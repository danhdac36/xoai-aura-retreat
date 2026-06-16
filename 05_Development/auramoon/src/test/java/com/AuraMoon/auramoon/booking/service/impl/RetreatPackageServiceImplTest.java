package com.AuraMoon.auramoon.booking.service.impl;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.repository.RetreatPackageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetreatPackageServiceImplTest {

    @Mock
    private RetreatPackageRepository retreatPackageRepository;

    @InjectMocks
    private RetreatPackageServiceImpl retreatPackageService;

    @Test
    @DisplayName("UC06-TC-001 - Lấy tất cả gói nghỉ dưỡng đang hoạt động thành công")
    void getAllActivePackages_Success() {
        // Arrange
        RetreatPackage pkg = RetreatPackage.builder()
                .id(1)
                .packageName("Gói Trị Liệu Thư Giãn 3 Ngày")
                .typePackage("Wellness")
                .durationDays(3)
                .price(new BigDecimal("15000000"))
                .build();

        when(retreatPackageRepository.findByIsActiveTrueAndIsDeleteFalse())
                .thenReturn(Collections.singletonList(pkg));

        // Act
        List<RetreatPackageDTO> result = retreatPackageService.getAllActivePackages();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gói Trị Liệu Thư Giãn 3 Ngày", result.get(0).getPackageName());
    }

    @Test
    @DisplayName("UC06-TC-002 - Lọc gói nghỉ dưỡng theo loại (Type) thành công")
    void getPackagesByType_Success() {
        // Arrange
        String type = "Detox";
        RetreatPackage pkg = RetreatPackage.builder()
                .id(2)
                .packageName("Gói Thanh Lọc 5 Ngày")
                .typePackage(type)
                .durationDays(5)
                .price(new BigDecimal("25000000"))
                .build();

        when(retreatPackageRepository.findByTypePackageAndIsActiveTrueAndIsDeleteFalse(type))
                .thenReturn(Collections.singletonList(pkg));

        // Act
        List<RetreatPackageDTO> result = retreatPackageService.getPackagesByType(type);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(type, result.get(0).getTypePackage());
    }

    @Test
    @DisplayName("UC06-TC-003 - Tìm kiếm nâng cao các gói nghỉ dưỡng thành công")
    void searchPackages_Success() {
        // Arrange
        RetreatPackage pkg = RetreatPackage.builder()
                .id(1)
                .packageName("Gói Stress Relief")
                .typePackage("Stress")
                .durationDays(3)
                .price(new BigDecimal("12000000"))
                .build();

        when(retreatPackageRepository.searchPackages("Stress", 2, 5, 10000000.0, 20000000.0))
                .thenReturn(Collections.singletonList(pkg));

        // Act
        List<RetreatPackageDTO> result = retreatPackageService.searchPackages("Stress", 2, 5, 10000000.0, 20000000.0);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gói Stress Relief", result.get(0).getPackageName());
    }

    @Test
    @DisplayName("UC06-TC-004 - Lấy chi tiết gói nghỉ dưỡng theo ID thành công")
    void getPackageById_Success() {
        // Arrange
        Integer packageId = 1;
        RetreatPackage pkg = RetreatPackage.builder()
                .id(packageId)
                .packageName("Gói Trị Liệu")
                .isActive(true)
                .build();

        when(retreatPackageRepository.findByIdAndIsActiveTrueAndIsDeleteFalse(packageId))
                .thenReturn(Optional.of(pkg));

        // Act
        RetreatPackageDTO result = retreatPackageService.getPackageById(packageId);

        // Assert
        assertNotNull(result);
        assertEquals(packageId, result.getId());
    }

    @Test
    @DisplayName("UC06-TC-005 - Lấy chi tiết gói nghỉ dưỡng không tồn tại -> Exception")
    void getPackageById_NotFound_ThrowsException() {
        // Arrange
        Integer packageId = 999;
        when(retreatPackageRepository.findByIdAndIsActiveTrueAndIsDeleteFalse(packageId))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            retreatPackageService.getPackageById(packageId);
        });

        assertTrue(exception.getMessage().contains("Retreat package not found"));
    }

    @Test
    @DisplayName("UC06-TC-006 - Lấy danh sách gói nổi bật thành công")
    void getPopularPackages_Success() {
        // Arrange
        RetreatPackage pkg1 = RetreatPackage.builder().id(1).packageName("Gói 1").build();
        RetreatPackage pkg2 = RetreatPackage.builder().id(2).packageName("Gói 2").build();

        when(retreatPackageRepository.findTop3ByIsActiveTrueAndIsDeleteFalseOrderByIdAsc())
                .thenReturn(Arrays.asList(pkg1, pkg2));

        // Act
        List<RetreatPackageDTO> result = retreatPackageService.getPopularPackages();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("UC06-TC-007 - Lấy danh sách tất cả loại gói nghỉ dưỡng đang hoạt động")
    void getAllActivePackageTypes_Success() {
        // Arrange
        List<String> types = Arrays.asList("Wellness", "Detox", "Stress");
        when(retreatPackageRepository.findDistinctTypePackageByIsActiveTrue()).thenReturn(types);

        // Act
        List<String> result = retreatPackageService.getAllActivePackageTypes();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Wellness"));
    }
}
