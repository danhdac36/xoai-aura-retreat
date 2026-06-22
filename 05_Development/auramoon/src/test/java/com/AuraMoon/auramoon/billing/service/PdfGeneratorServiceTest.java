package com.AuraMoon.auramoon.billing.service;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.repository.UserRepository;
import com.AuraMoon.auramoon.billing.entity.FolioItem;
import com.AuraMoon.auramoon.billing.entity.GuestFolio;
import com.AuraMoon.auramoon.billing.repository.FolioItemRepository;
import com.AuraMoon.auramoon.billing.repository.GuestFolioRepository;
import com.AuraMoon.auramoon.billing.service.impl.PdfGeneratorServiceImpl;
import com.AuraMoon.auramoon.booking.entity.Booking;
import com.AuraMoon.auramoon.booking.entity.Villa;
import com.AuraMoon.auramoon.booking.repository.BookingRepository;
import com.AuraMoon.auramoon.booking.repository.VillaRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PdfGeneratorServiceTest {

    @Mock
    private GuestFolioRepository guestFolioRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private FolioItemRepository folioItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VillaRepository villaRepository;

    @InjectMocks
    private PdfGeneratorServiceImpl pdfGeneratorService;

    private GuestFolio mockFolio;
    private Booking mockBooking;
    private User mockUser;
    private Villa mockVilla;

    @BeforeEach
    public void setup() {
        mockFolio = new GuestFolio();
        mockFolio.setFolioId(1001);
        mockFolio.setBookingId(2001);
        mockFolio.setFinalAmount(new BigDecimal("5500000"));

        mockBooking = new Booking();
        mockBooking.setId(2001);
        mockBooking.setGuestId(3001);
        Villa tempVilla = new Villa();
        tempVilla.setId(4001);
        mockBooking.setAssignedVilla(tempVilla);

        mockUser = new User();
        mockUser.setId(3001);
        mockUser.setEmail("test@qa.com");

        mockVilla = new Villa();
        mockVilla.setId(4001);
    }

    private void setupMocks() {
        when(guestFolioRepository.findById(1001)).thenReturn(Optional.of(mockFolio));
        when(bookingRepository.findById(2001)).thenReturn(Optional.of(mockBooking));
        when(userRepository.findById(3001)).thenReturn(Optional.of(mockUser));
        when(villaRepository.findById(4001)).thenReturn(Optional.of(mockVilla));
        
        FolioItem item = new FolioItem();
        item.setGuestFolio(mockFolio);
        item.setServiceCategory("RoomCharge");
        item.setAmount(new BigDecimal("5000000"));
        when(folioItemRepository.findAll()).thenReturn(List.of(item));
    }

    // MOD5-TC-001: Kết xuất PDF In-Memory thành công
    @Test
    public void generateConsolidatedInvoice_Success_ReturnsByteArray() throws Exception {
        setupMocks();

        byte[] result = pdfGeneratorService.generateConsolidatedInvoice(1001);

        assertNotNull(result);
        assertTrue(result.length > 0);
        String pdfSignature = new String(result, StandardCharsets.UTF_8).substring(0, 5);
        assertEquals("%PDF-", pdfSignature);
    }

    // MOD5-TC-005: Nội dung PDF phải chứa đủ trường chuẩn Bộ Tài chính (BR-21)
    @Test
    public void generateConsolidatedInvoice_ContainsRequiredFields_BR21() throws Exception {
        setupMocks();

        byte[] result = pdfGeneratorService.generateConsolidatedInvoice(1001);

        try (PDDocument document = Loader.loadPDF(result)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains("Xoai Aura Retreat"), "Missing Company Name");
            assertTrue(text.contains("MST:"), "Missing Tax ID");
            assertTrue(text.contains("VAT 10%:"), "Missing VAT 10%");
            assertTrue(text.contains("Total in words:"), "Missing Total in words");
            assertTrue(text.contains("RoomCharge"), "Missing Service Item");
        }
    }

    // MOD5-TC-006: PDF Generator phải query đủ 4 bảng nguồn dữ liệu gộp
    @Test
    public void generateConsolidatedInvoice_QueriesAllDataSources() throws Exception {
        setupMocks();

        pdfGeneratorService.generateConsolidatedInvoice(1001);

        verify(guestFolioRepository, times(1)).findById(1001);
        verify(bookingRepository, times(1)).findById(2001);
        verify(userRepository, times(1)).findById(3001);
        verify(folioItemRepository, times(1)).findAll();
        verify(villaRepository, times(1)).findById(4001);
    }
}
