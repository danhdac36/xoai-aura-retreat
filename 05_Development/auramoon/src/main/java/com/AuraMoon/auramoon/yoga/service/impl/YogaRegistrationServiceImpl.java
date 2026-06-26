package com.AuraMoon.auramoon.yoga.service.impl;

import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.spa.entity.PhysicalHealthProfile;
import com.AuraMoon.auramoon.spa.repository.PhysicalHealthProfileRepository;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.auth.config.AesDataEncryptor;
import com.AuraMoon.auramoon.yoga.entity.YogaRegistration;
import com.AuraMoon.auramoon.yoga.entity.YogaSchedule;
import com.AuraMoon.auramoon.yoga.repository.YogaRegistrationRepository;
import com.AuraMoon.auramoon.yoga.repository.YogaScheduleRepository;
import com.AuraMoon.auramoon.yoga.dto.YogaRegistrationRequest;
import com.AuraMoon.auramoon.yoga.dto.YogaRegistrationResponse;
import com.AuraMoon.auramoon.yoga.dto.YogaParticipantResponse;
import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.yoga.exception.YogaBusinessException;
import com.AuraMoon.auramoon.yoga.exception.HealthWarningException;
import com.AuraMoon.auramoon.yoga.service.IYogaRegistrationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class YogaRegistrationServiceImpl implements IYogaRegistrationService {

    private final YogaScheduleRepository yogaScheduleRepository;
    private final YogaRegistrationRepository yogaRegistrationRepository;
    private final BookingRepository bookingRepository;
    private final PhysicalHealthProfileRepository physicalHealthProfileRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public YogaRegistrationServiceImpl(
            YogaScheduleRepository yogaScheduleRepository,
            YogaRegistrationRepository yogaRegistrationRepository,
            BookingRepository bookingRepository,
            PhysicalHealthProfileRepository physicalHealthProfileRepository,
            ScheduleRepository scheduleRepository,
            UserRepository userRepository) {
        this.yogaScheduleRepository = yogaScheduleRepository;
        this.yogaRegistrationRepository = yogaRegistrationRepository;
        this.bookingRepository = bookingRepository;
        this.physicalHealthProfileRepository = physicalHealthProfileRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public YogaRegistrationResponse registerYogaClass(YogaRegistrationRequest request) {
        if (request.getBookingId() == null || request.getYogaScheduleId() == null) {
            throw new YogaBusinessException("YOGA-001", "Dữ liệu đầu vào không hợp lệ");
        }

        // 1. Kiểm tra trạng thái Checked-In (BR-YOGA-01)
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new YogaBusinessException("YOGA-001", "Thông tin đặt phòng không tồn tại"));

        String bStatus = booking.getBookingStatus();
        if (bStatus == null || (!"CHECKED_IN".equalsIgnoreCase(bStatus) && !"Checked-In".equalsIgnoreCase(bStatus))) {
            throw new YogaBusinessException("YOGA-006", "Chỉ cho phép khách hàng Checked-In đăng ký");
        }

        // 2. Tìm kiếm lịch học Yoga và Lock hàng để tránh Race Condition (Pessimistic
        // Lock - ADR-YOGA-001)
        YogaSchedule schedule = yogaScheduleRepository.findByIdForUpdate(request.getYogaScheduleId())
                .orElseThrow(() -> new YogaBusinessException("YOGA-001", "Lịch học Yoga không tồn tại"));

        if (Boolean.TRUE.equals(schedule.getIsDelete())) {
            throw new YogaBusinessException("YOGA-001", "Lịch học Yoga đã bị xóa");
        }

        if (schedule.getStartTime().isBefore(LocalDateTime.now())) {
            throw new YogaBusinessException("YOGA-001", "Lịch học Yoga đã diễn ra hoặc không khả dụng");
        }

        // 3. Kiểm tra nếu khách đã đăng ký thành công lớp này rồi
        Optional<YogaRegistration> existingReg = yogaRegistrationRepository
                .findByBookingIdAndSchedule_IdAndStatus(request.getBookingId(), schedule.getId(), "REGISTERED");
        if (existingReg.isPresent()) {
            throw new YogaBusinessException("YOGA-007", "Bạn đã đăng ký lớp học này rồi");
        }

        // 4. Kiểm tra sĩ số lớp tối đa (BR-YOGA-02)
        long count = yogaRegistrationRepository.countBySchedule_IdAndStatus(schedule.getId(), "REGISTERED");
        if (count >= schedule.getMaxCapacity()) {
            throw new YogaBusinessException("YOGA-002", "Lớp học đã đủ sĩ số tối đa");
        }

        // 5. Kiểm tra trùng lịch hoạt động chéo (BR-YOGA-04)
        // 5.1 Kiểm tra trùng các lớp Yoga khác
        List<YogaRegistration> overlapYoga = yogaRegistrationRepository.findOverlappingRegistrations(
                request.getBookingId(), schedule.getStartTime(), schedule.getEndTime());
        if (!overlapYoga.isEmpty()) {
            throw new YogaBusinessException("YOGA-003", "Bạn đã đăng ký một hoạt động khác trùng vào khung giờ này");
        }

        // 5.2 Kiểm tra trùng lịch các buổi trị liệu Spa
        List<Schedule> spaSchedules = scheduleRepository
                .findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(request.getBookingId());
        for (Schedule spa : spaSchedules) {
            if (spa.getTreatmentBooking() != null
                    && !"CANCELLED".equalsIgnoreCase(spa.getTreatmentBooking().getStatus())) {
                if (spa.getStartTime().isBefore(schedule.getEndTime())
                        && spa.getEndTime().isAfter(schedule.getStartTime())) {
                    throw new YogaBusinessException("YOGA-003",
                            "Bạn đã đăng ký một hoạt động khác trùng vào khung giờ này");
                }
            }
        }

        // 6. Kiểm tra Hồ sơ Sức khỏe PII (Decree 356/2025 - ADR-YOGA-002)
        if (request.getConfirmHealthWarning() == null || !request.getConfirmHealthWarning()) {
            Integer userId = booking.getGuestId();
            PhysicalHealthProfile hp = physicalHealthProfileRepository.findByUserId(userId).orElse(null);
            if (hp != null) {
                AesDataEncryptor encryptor = new AesDataEncryptor();
                String decryptedMed = encryptor.convertToEntityAttribute(hp.getMedicalConditions());
                String decryptedInj = encryptor.convertToEntityAttribute(hp.getInjuries());

                StringBuilder checkBuilder = new StringBuilder();
                if (decryptedMed != null)
                    checkBuilder.append(decryptedMed.toLowerCase()).append(" ");
                if (decryptedInj != null)
                    checkBuilder.append(decryptedInj.toLowerCase());
                String checkText = checkBuilder.toString();

                if (checkText.contains("knee") || checkText.contains("khớp gối") || checkText.contains("đầu gối")
                        || checkText.contains("gối")) {
                    throw new HealthWarningException("YOGA-005", "KNEE",
                            "Hệ thống ghi nhận bạn đang có tình trạng chấn thương hoặc bệnh lý khớp gối. Bạn có chắc chắn thể chất phù hợp không?");
                } else if (checkText.contains("spine") || checkText.contains("cột sống") || checkText.contains("lưng")
                        || checkText.contains("back")) {
                    throw new HealthWarningException("YOGA-005", "SPINE",
                            "Hệ thống ghi nhận bạn đang có tình trạng chấn thương hoặc bệnh lý cột sống/lưng. Bạn có chắc chắn thể chất phù hợp không?");
                } else if (checkText.contains("heart") || checkText.contains("tim mạch") || checkText.contains("cardio")
                        || checkText.contains("huyết áp")) {
                    throw new HealthWarningException("YOGA-005", "HEART",
                            "Hệ thống ghi nhận bạn đang có tình trạng chấn thương hoặc bệnh lý tim mạch/huyết áp. Bạn có chắc chắn thể chất phù hợp không?");
                }
            }
        }

        // 7. Tạo bản ghi đăng ký mới
        YogaRegistration registration = YogaRegistration.builder()
                .bookingId(request.getBookingId())
                .schedule(schedule)
                .registeredAt(LocalDateTime.now())
                .status("REGISTERED")
                .build();

        YogaRegistration saved = yogaRegistrationRepository.save(registration);

        return YogaRegistrationResponse.builder()
                .registrationId(saved.getId())
                .bookingId(saved.getBookingId())
                .yogaScheduleId(schedule.getId())
                .className(schedule.getYogaClass().getClassName())
                .location(schedule.getLocation())
                .startTime(schedule.getStartTime())
                .status(saved.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<YogaSchedule> getAvailableSchedules(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return yogaScheduleRepository.findSchedulesByDateRange(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<YogaSchedule> getInstructorSchedules(Integer instructorUserId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return yogaScheduleRepository.findByInstructorAndDateRange(instructorUserId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<YogaParticipantResponse> getParticipantsForSchedule(Integer scheduleId, Integer instructorUserId) {
        YogaSchedule schedule = yogaScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new YogaBusinessException("YOGA-001", "Lịch học Yoga không tồn tại"));

        if (!schedule.getInstructor().getInstructorId().equals(instructorUserId)) {
            throw new YogaBusinessException("YOGA-008",
                    "Bạn không có quyền truy cập danh sách học viên của lớp học này.");
        }

        List<YogaRegistration> regs = yogaRegistrationRepository.findBySchedule_IdAndStatus(scheduleId, "REGISTERED");
        List<YogaParticipantResponse> result = new java.util.ArrayList<>();
        AesDataEncryptor encryptor = new AesDataEncryptor();

        for (YogaRegistration reg : regs) {
            Booking booking = bookingRepository.findById(reg.getBookingId()).orElse(null);
            if (booking == null)
                continue;

            User guest = userRepository.findById(booking.getGuestId()).orElse(null);
            if (guest == null)
                continue;

            String villaCode = booking.getAssignedVilla() != null ? booking.getAssignedVilla().getVillaCode()
                    : "Chưa xếp phòng";

            String injuriesNote = "Không";
            String medicalConditionsNote = "Không";

            PhysicalHealthProfile hp = physicalHealthProfileRepository.findByUserId(booking.getGuestId()).orElse(null);
            if (hp != null) {
                String decryptedMed = encryptor.convertToEntityAttribute(hp.getMedicalConditions());
                String decryptedInj = encryptor.convertToEntityAttribute(hp.getInjuries());

                injuriesNote = filterMovementHealthNote(decryptedInj);
                medicalConditionsNote = filterMovementHealthNote(decryptedMed);
            }

            result.add(YogaParticipantResponse.builder()
                    .registrationId(reg.getId())
                    .bookingId(reg.getBookingId())
                    .guestName(guest.getFullName())
                    .villaName(villaCode)
                    .injuriesNote(injuriesNote)
                    .medicalConditionsNote(medicalConditionsNote)
                    .build());
        }

        return result;
    }

    private String filterMovementHealthNote(String note) {
        if (note == null || note.trim().isEmpty() || "không".equalsIgnoreCase(note.trim())) {
            return "Không";
        }
        String[] parts = note.split("[,;.\\n]");
        List<String> keywords = List.of(
                "gối", "knee", "spine", "cột sống", "lưng", "back", "heart", "tim", "cardio",
                "huyết áp", "pressure", "vai", "shoulder", "cổ", "neck", "khớp", "joint", "xương", "bone");
        StringBuilder filtered = new StringBuilder();
        for (String part : parts) {
            String lower = part.toLowerCase();
            boolean matches = false;
            for (String kw : keywords) {
                if (lower.contains(kw)) {
                    matches = true;
                    break;
                }
            }
            if (matches) {
                if (filtered.length() > 0) {
                    filtered.append(", ");
                }
                filtered.append(part.trim());
            }
        }
        return filtered.length() > 0 ? filtered.toString() : "Không";
    }
}
