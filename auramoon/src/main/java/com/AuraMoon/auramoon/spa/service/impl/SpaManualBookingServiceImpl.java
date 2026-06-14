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

    public SpaManualBookingServiceImpl(SpaBookingRepository bookingRepository,
                                       TreatmentBookingRepository treatmentBookingRepository,
                                       TreatmentServiceRepository treatmentServiceRepository,
                                       TreatmentRoomRepository roomRepository,
                                       TherapistRepository therapistRepository,
                                       ScheduleRepository scheduleRepository,
                                       BillingIntegrationService billingService) {
        this.bookingRepository = bookingRepository;
        this.treatmentBookingRepository = treatmentBookingRepository;
        this.treatmentServiceRepository = treatmentServiceRepository;
        this.roomRepository = roomRepository;
        this.therapistRepository = therapistRepository;
        this.scheduleRepository = scheduleRepository;
        this.billingService = billingService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpaScheduleResponse bookAdditionalService(SpaScheduleRequest request, Integer receptionistUserId) {
        // 1. Validation: Kiểm tra booking tồn tại và status = 'Checked-In'
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new SpaBusinessException("SPA-001", "Không tìm thấy thông tin đặt phòng"));
                
        if (booking.getBookingStatus() == null || 
            (!"Checked-In".equalsIgnoreCase(booking.getBookingStatus()) && !"CHECKED_IN".equalsIgnoreCase(booking.getBookingStatus()))) {
            throw new SpaBusinessException("SPA-002", "Chỉ cho phép đặt thêm Spa đối với Guest có trạng thái Checked-In.");
        }

        // 2. Kiểm tra service tồn tại
        TreatmentService service = treatmentServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new SpaBusinessException("SPA-001", "Không tìm thấy dịch vụ trị liệu"));

        // 3. Tìm folioId tương ứng với bookingId thông qua BillingIntegrationService
        Integer folioId = billingService.findFolioIdByBookingId(booking.getId())
                .orElseThrow(() -> new SpaBusinessException("SPA-003", "Không tìm thấy tài khoản Folio của khách hàng"));

        // 4. Tìm Therapist + Room trống bằng Pessimistic Lock (BR-04)
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(service.getDurationMinutes() != null ? service.getDurationMinutes() : 60);

        List<TreatmentRoom> availableRooms = roomRepository.findAvailableRoomsWithLock(startTime, endTime);
        if (availableRooms.isEmpty()) {
            throw new SpaBusinessException("SPA-010", "Không tìm thấy Therapist hoặc Phòng điều trị khả dụng. Vui lòng chọn thời gian khác.");
        }
        TreatmentRoom selectedRoom = availableRooms.get(0);

        List<Therapist> availableTherapists = therapistRepository.findAvailableTherapistsWithLock(startTime, endTime);
        if (availableTherapists.isEmpty()) {
            throw new SpaBusinessException("SPA-010", "Không tìm thấy Therapist hoặc Phòng điều trị khả dụng. Vui lòng chọn thời gian khác.");
        }
        Therapist selectedTherapist = availableTherapists.get(0);

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
            String description = "Dịch vụ Spa: " + service.getServiceName() + " (" + service.getDurationMinutes() + " phút)";
            billingService.createFolioItem(
                    folioId,
                    treatmentBooking.getId(),
                    "Extra Spa",
                    description,
                    service.getPrice(),
                    receptionistUserId
            );
        } catch (Exception e) {
            // Ném lỗi SPA-019 để kích hoạt Transaction Rollback
            throw new SpaBusinessException("SPA-019", "Đã xảy ra lỗi hệ thống trong quá trình xử lý giao dịch. Vui lòng thử lại.");
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
