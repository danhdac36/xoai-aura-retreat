package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.billing.dto.NightAuditResultDTO;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FolioConsolidationServiceTest {

    @Mock
    private GuestFolioRepository guestFolioRepository;

    @Mock
    private FolioItemRepository folioItemRepository;

    @InjectMocks
    private FolioConsolidationServiceImpl folioConsolidationService;

    @BeforeEach
    void setUp() {
    }

    private static final int FOLIO_ID_A = 1;
    private static final int BOOKING_ID_A = 101;
    private static final int FOLIO_ID_B = 2;
    private static final int BOOKING_ID_B = 102;
    private static final int ACTOR_ID = 999;

    /**
     * UC26-TC-003 — Skip lỗi từng Folio (SRS E3)
     */
    @Test
    void consolidateAllActiveFolios_SkipsFailedFolio_WhenDBErrorOccurs() {
        // Arrange: Thiết lập môi trường giả lập (Test Fixtures)
        GuestFolio folioA = new GuestFolio();
        folioA.setFolioId(FOLIO_ID_A);
        folioA.setBookingId(BOOKING_ID_A);

        GuestFolio folioB = new GuestFolio();
        folioB.setFolioId(FOLIO_ID_B);
        folioB.setBookingId(BOOKING_ID_B);

        // Giả lập Repository trả về 2 Folio đang mở
        when(guestFolioRepository.findByStatus("OPEN"))
                .thenReturn(Arrays.asList(folioA, folioB));

        // Giả lập Folio A: Các câu lệnh DB Insert chạy thành công trả về 1 record
        when(folioItemRepository.consolidateFnbCharges(eq(BOOKING_ID_A), eq(FOLIO_ID_A), anyInt()))
                .thenReturn(1);
        when(folioItemRepository.consolidateSpaCharges(eq(BOOKING_ID_A), eq(FOLIO_ID_A), anyInt()))
                .thenReturn(1);

        // Giả lập Folio B: Câu lệnh DB văng lỗi khi chạy
        when(folioItemRepository.consolidateFnbCharges(eq(BOOKING_ID_B), eq(FOLIO_ID_B), anyInt()))
                .thenThrow(new DataAccessException("Simulated DB Connection Error for Folio B") {});

        // Act: Kích hoạt luồng nghiệp vụ
        NightAuditResultDTO result = folioConsolidationService.consolidateAllActiveFolios(ACTOR_ID);

        // Assert: Xác minh logic vòng lặp
        assertNotNull(result);
        
        // Vòng lặp phải chạy qua lỗi của Folio B, tiếp tục chạy và tính điểm cho Folio A
        assertEquals(1, result.getTotalActiveFolios(), "Hệ thống chỉ được phép tính Folio A là thành công");
        
        // Xác minh Spa của Folio A được chạy
        verify(folioItemRepository, times(1)).consolidateSpaCharges(eq(BOOKING_ID_A), eq(FOLIO_ID_A), anyInt());
        
        // Xác minh vòng lặp đã Skip Folio B ngay khi lỗi F&B, nên lệnh Spa của B KHÔNG BAO GIỜ được gọi
        verify(folioItemRepository, never()).consolidateSpaCharges(eq(BOOKING_ID_B), eq(FOLIO_ID_B), anyInt());
    }
}
