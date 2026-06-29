package com.AuraMoon.auramoon.spa.service.impl;

import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleRequest;
import com.AuraMoon.auramoon.spa.dto.SpaScheduleResponse;
import com.AuraMoon.auramoon.spa.entity.Schedule;
import com.AuraMoon.auramoon.spa.entity.Therapist;
import com.AuraMoon.auramoon.spa.entity.TreatmentBooking;
import com.AuraMoon.auramoon.spa.entity.TreatmentRoom;
import com.AuraMoon.auramoon.spa.entity.TreatmentService;
import com.AuraMoon.auramoon.spa.exception.SpaBusinessException;
import com.AuraMoon.auramoon.spa.repository.ScheduleRepository;
import com.AuraMoon.auramoon.spa.repository.SpaBookingRepository;
import com.AuraMoon.auramoon.spa.repository.TherapistRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentBookingRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentRoomRepository;
import com.AuraMoon.auramoon.spa.repository.TreatmentServiceRepository;
import com.AuraMoon.auramoon.spa.service.BillingIntegrationService;
import com.AuraMoon.auramoon.spa.service.SpaManualBookingService;
import com.AuraMoon.auramoon.yoga.repository.YogaRegistrationRepository;
import com.AuraMoon.auramoon.yoga.entity.YogaRegistration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpaManualBookingServiceImpl implements SpaManualBookingService {

    private final SpaBookingRepository bookingRepository;
    private final TreatmentBookingRepository treatmentBookingRepository;
    private final TreatmentServiceRepository treatmentServiceRepository;
    private final TreatmentRoomRepository roomRepository;
    private final TherapistRepository therapistRepository;
    private final ScheduleRepository scheduleRepository;
    private final BillingIntegrationService billingService;
    private final YogaRegistrationRepository yogaRegistrationRepository;

    public SpaManualBookingServiceImpl(SpaBookingRepository bookingRepository,
            TreatmentBookingRepository treatmentBookingRepository,
            TreatmentServiceRepository treatmentServiceRepository,
            TreatmentRoomRepository roomRepository,
            TherapistRepository therapistRepository,
            ScheduleRepository scheduleRepository,
            BillingIntegrationService billingService,
            YogaRegistrationRepository yogaRegistrationRepository) {
        this.bookingRepository = bookingRepository;
        this.treatmentBookingRepository = treatmentBookingRepository;
        this.treatmentServiceRepository = treatmentServiceRepository;
        this.roomRepository = roomRepository;
        this.therapistRepository = therapistRepository;
        this.scheduleRepository = scheduleRepository;
        this.billingService = billingService;
        this.yogaRegistrationRepository = yogaRegistrationRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpaScheduleResponse bookAdditionalService(SpaScheduleRequest request, Integer receptionistUserId) {
        // 1. Validation: Kiểm tra booking tồn tại và status = 'Checked-In'
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new SpaBusinessException("SPA-001", "Không tìm thấy thông tin đặt phòng"));

        if (booking.getBookingStatus() == null ||
                (!"Checked-In".equalsIgnoreCase(booking.getBookingStatus())
                        && !"CHECKED_IN".equalsIgnoreCase(booking.getBookingStatus()))) {
            throw new SpaBusinessException("SPA-002",
                    "Chỉ cho phép đặt thêm Spa đối với Guest có trạng thái Checked-In.");
        }

        // 2. Kiểm tra service tồn tại
        TreatmentService service = treatmentServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new SpaBusinessException("SPA-001", "Không tìm thấy dịch vụ trị liệu"));

        // 3. Tìm folioId tương ứng với bookingId thông qua BillingIntegrationService
        Integer folioId = billingService.findFolioIdByBookingId(booking.getId())
                .orElseThrow(
                        () -> new SpaBusinessException("SPA-003", "Không tìm thấy tài khoản Folio của khách hàng"));

        // 4. Tìm Therapist + Room trống bằng Pessimistic Lock (BR-04)
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = startTime
                .plusMinutes(service.getDurationMinutes() != null ? service.getDurationMinutes() : 60);

        // 4.1 Kiểm tra trùng lịch Yoga: Khách không thể vừa học Yoga vừa làm Spa cùng lúc
        List<YogaRegistration> overlappingYoga = yogaRegistrationRepository
                .findOverlappingRegistrations(booking.getId(), startTime, endTime);
        if (!overlappingYoga.isEmpty()) {
            YogaRegistration conflict = overlappingYoga.get(0);
            String yogaTime = conflict.getSchedule().getStartTime().toLocalTime() + " - "
                    + conflict.getSchedule().getEndTime().toLocalTime();
            String yogaClass = conflict.getSchedule().getYogaClass().getClassName();
            throw new SpaBusinessException("SPA-014",
                    "Khách đã có lịch Yoga \"" + yogaClass + "\" từ " + yogaTime
                            + " trùng với khung giờ Spa được chọn. Vui lòng chọn thời gian khác.");
        }

        List<TreatmentRoom> availableRooms = roomRepository.findAvailableRoomsWithLock(startTime, endTime);
        if (availableRooms.isEmpty()) {
            throw new SpaBusinessException("SPA-010",
                    "Không tìm thấy Therapist hoặc Phòng điều trị khả dụng. Vui lòng chọn thời gian khác.");
        }

        // 3.5. Kiểm tra trùng lịch Spa dựa trên số lượng khách (totalGuests)
        List<Schedule> existingSchedules = scheduleRepository.findByTreatmentBookingBookingIdAndIsDeleteFalseOrderByStartTimeAsc(booking.getId());
        long overlappingCount = existingSchedules.stream()
                .filter(s -> s.getStartTime().isBefore(endTime) && s.getEndTime().isAfter(startTime))
                .count();

        int maxAllowedOverlapping = (booking.getTotalGuests() != null) ? booking.getTotalGuests() : 1;
        if (overlappingCount >= maxAllowedOverlapping) {
            if (maxAllowedOverlapping <= 1) {
                throw new SpaBusinessException("SPA-013",
                        "Quý khách không thể đặt 2 ca spa cùng một thời điểm.");
            } else {
                throw new SpaBusinessException("SPA-013",
                        "Số lượng ca spa trùng thời điểm vượt quá số lượng khách trong đơn đặt phòng (tối đa " + maxAllowedOverlapping + " người).");
            }
        }
        TreatmentRoom selectedRoom = availableRooms.get(0);

        List<Therapist> availableTherapists = therapistRepository.findAvailableTherapistsWithLock(startTime, endTime);
        if (availableTherapists.isEmpty()) {
            throw new SpaBusinessException("SPA-010",
                    "Không tìm thấy Therapist hoặc Phòng điều trị khả dụng. Vui lòng chọn thời gian khác.");
        }
        
        // Cân bằng công việc: Lựa chọn Therapist có số ca làm việc ít nhất trong ngày
        Therapist selectedTherapist = availableTherapists.get(0);
        long minWorkload = Long.MAX_VALUE;
        LocalDateTime startOfDay = startTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startTime.toLocalDate().atTime(java.time.LocalTime.MAX);
        for (Therapist t : availableTherapists) {
            long workload = scheduleRepository.countDailySchedulesForTherapist(t.getId(), startOfDay, endOfDay);
            if (workload < minWorkload) {
                minWorkload = workload;
                selectedTherapist = t;
            }
        }

        // 5. Lưu TreatmentBooking (với status = 'Scheduled' và folio_id liên kết)
        TreatmentBooking treatmentBooking = TreatmentBooking.builder()
                .bookingId(booking.getId())
                .folioId(folioId)
                .treatmentService(service)
                .note(request.getNote())
                .status("Scheduled")
                .build();
        treatmentBooking = treatmentBookingRepository.save(treatmentBooking);

        // 6. Lưu Schedule
        Schedule schedule = Schedule.builder()
                .treatmentBooking(treatmentBooking)
                .therapist(selectedTherapist)
                .room(selectedRoom)
                .startTime(startTime)
                .endTime(endTime)
                .isDelete(false)
                .build();
        schedule = scheduleRepository.save(schedule);

        // 7. Ghi nợ vào Folio (Dual-Write)
        try {
            String description = "Dịch vụ Spa: " + service.getServiceName() + " (" + service.getDurationMinutes()
                    + " phút)";
            billingService.createFolioItem(
                    folioId,
                    treatmentBooking.getId(),
                    "Extra Spa",
                    description,
                    service.getPrice(),
                    receptionistUserId);
        } catch (Exception e) {
            // Ném lỗi SPA-019 để kích hoạt Transaction Rollback
            throw new SpaBusinessException("SPA-019",
                    "Đã xảy ra lỗi hệ thống trong quá trình xử lý giao dịch. Vui lòng thử lại.");
        }

        // 8. Trả về response
        SpaScheduleResponse response = new SpaScheduleResponse();
        response.setScheduleId(schedule.getId());
        response.setTherapistCode(selectedTherapist.getTherapistCode());
        response.setRoomId(selectedRoom.getId());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());

        return response;
    }
}
