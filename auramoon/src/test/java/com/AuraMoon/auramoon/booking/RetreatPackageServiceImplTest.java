package com.AuraMoon.auramoon.booking;

import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.entity.RetreatPackage;
import com.AuraMoon.auramoon.booking.repository.RetreatPackageRepository;
import com.AuraMoon.auramoon.booking.service.impl.RetreatPackageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RetreatPackageServiceImpl
 * Features covered:
 *   - Hiển thị danh sách gói retreat
 *   - Lọc theo mục tiêu sức khỏe (typePackage)
 *   - Lọc theo số ngày (durationDays)
 *   - Lọc theo mức giá (priceRange)
 *   - Xem chi tiết từng gói
 *   - Hiển thị gợi ý khi không có kết quả
 */
@ExtendWith(MockitoExtension.class)
class RetreatPackageServiceImplTest {

    @Mock
    private RetreatPackageRepository retreatPackageRepository;

    @InjectMocks
    private RetreatPackageServiceImpl retreatPackageService;

    private RetreatPackage pkg1;
    private RetreatPackage pkg2;
    private RetreatPackage pkg3;

    @BeforeEach
    void setUp() {
        pkg1 = RetreatPackage.builder()
                .id(1).typePackage("Yoga").packageName("Yoga Basic")
                .durationDays(3).price(new BigDecimal("3000000"))
                .isActive(true).build();
        pkg1.setIsDelete(false);

        pkg2 = RetreatPackage.builder()
                .id(2).typePackage("Detox").packageName("Detox Premium")
                .durationDays(7).price(new BigDecimal("8000000"))
                .isActive(true).build();
        pkg2.setIsDelete(false);

        pkg3 = RetreatPackage.builder()
                .id(3).typePackage("Spa").packageName("Spa Luxury")
                .durationDays(5).price(new BigDecimal("12000000"))
                .isActive(true).build();
        pkg3.setIsDelete(false);
    }

    // ========== TC-SV-01: Hiển thị danh sách gói retreat ==========

    @Test
    void getAllActivePackages_returnsAllActivePackages() {
        when(retreatPackageRepository.findByIsActiveTrueAndIsDeleteFalse())
                .thenReturn(List.of(pkg1, pkg2, pkg3));

        List<RetreatPackageDTO> result = retreatPackageService.getAllActivePackages();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(RetreatPackageDTO::getPackageName)
                .containsExactlyInAnyOrder("Yoga Basic", "Detox Premium", "Spa Luxury");
    }

    @Test
    void getAllActivePackages_returnsEmptyList_whenNoActivePackages() {
        when(retreatPackageRepository.findByIsActiveTrueAndIsDeleteFalse())
                .thenReturn(Collections.emptyList());

        List<RetreatPackageDTO> result = retreatPackageService.getAllActivePackages();

        assertThat(result).isEmpty();
    }

    // ========== TC-SV-02: Lọc theo mục tiêu sức khỏe (typePackage) ==========

    @Test
    void searchPackages_filterByType_returnsMatchingPackages() {
        when(retreatPackageRepository.searchPackages("Yoga", null, null))
                .thenReturn(List.of(pkg1));

        List<RetreatPackageDTO> result = retreatPackageService.searchPackages("Yoga", null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTypePackage()).isEqualTo("Yoga");
    }

    @Test
    void searchPackages_filterByType_returnsEmpty_whenNoMatch() {
        when(retreatPackageRepository.searchPackages("Meditation", null, null))
                .thenReturn(Collections.emptyList());

        List<RetreatPackageDTO> result = retreatPackageService.searchPackages("Meditation", null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void searchPackages_noTypeFilter_returnsAllPackages() {
        when(retreatPackageRepository.searchPackages(null, null, null))
                .thenReturn(List.of(pkg1, pkg2, pkg3));

        List<RetreatPackageDTO> result = retreatPackageService.searchPackages(null, null, null);

        assertThat(result).hasSize(3);
    }

    // ========== TC-SV-03: Lọc theo số ngày ==========

    @Test
    void searchPackages_filterByDurationDays_returnsMatchingPackages() {
        when(retreatPackageRepository.searchPackages(null, 7, null))
                .thenReturn(List.of(pkg2));

        List<RetreatPackageDTO> result = retreatPackageService.searchPackages(null, 7, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDurationDays()).isEqualTo(7);
    }

    @Test
    void searchPackages_filterByDurationDays_returnsEmpty_whenNoMatch() {
        when(retreatPackageRepository.searchPackages(null, 14, null))
                .thenReturn(Collections.emptyList());

        List<RetreatPackageDTO> result = retreatPackageService.searchPackages(null, 14, null);

        assertThat(result).isEmpty();
    }

    // ========== TC-SV-04: Lọc theo mức giá ==========

    @Test
    void searchPackages_filterByPriceRangeUnder5_returnsMatchingPackages() {
        when(retreatPackageRepository.searchPackages(null, null, "UNDER_5"))
                .thenReturn(List.of(pkg1));

        List<RetreatPackageDTO> result = retreatPackageService.searchPackages(null, null, "UNDER_5");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrice()).isLessThan(new BigDecimal("5000000"));
    }

    @Test
    void searchPackages_filterByPriceRangeFrom5To10_returnsMatchingPackages() {
        when(retreatPackageRepository.searchPackages(null, null, "FROM_5_TO_10"))
                .thenReturn(List.of(pkg2));

        List<RetreatPackageDTO> result = retreatPackageService.searchPackages(null, null, "FROM_5_TO_10");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrice()).isBetween(new BigDecimal("5000000"), new BigDecimal("10000000"));
    }

    @Test
    void searchPackages_filterByPriceRangeOver10_returnsMatchingPackages() {
        when(retreatPackageRepository.searchPackages(null, null, "OVER_10"))
                .thenReturn(List.of(pkg3));

        List<RetreatPackageDTO> result = retreatPackageService.searchPackages(null, null, "OVER_10");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrice()).isGreaterThan(new BigDecimal("10000000"));
    }

    @Test
    void searchPackages_combinedFilters_returnsCorrectPackage() {
        when(retreatPackageRepository.searchPackages("Detox", 7, "FROM_5_TO_10"))
                .thenReturn(List.of(pkg2));

        List<RetreatPackageDTO> result = retreatPackageService.searchPackages("Detox", 7, "FROM_5_TO_10");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTypePackage()).isEqualTo("Detox");
        assertThat(result.get(0).getDurationDays()).isEqualTo(7);
    }

    // ========== TC-SV-05: Xem chi tiết từng gói ==========

    @Test
    void getPackageById_returnsCorrectDTO() {
        when(retreatPackageRepository.findByIdAndIsActiveTrueAndIsDeleteFalse(1))
                .thenReturn(Optional.of(pkg1));

        RetreatPackageDTO result = retreatPackageService.getPackageById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getPackageName()).isEqualTo("Yoga Basic");
        assertThat(result.getTypePackage()).isEqualTo("Yoga");
        assertThat(result.getPrice()).isEqualByComparingTo("3000000");
    }

    @Test
    void getPackageById_throwsException_whenNotFound() {
        when(retreatPackageRepository.findByIdAndIsActiveTrueAndIsDeleteFalse(99))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> retreatPackageService.getPackageById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Retreat package not found");
    }

    // ========== TC-SV-06: Hiển thị gợi ý khi không có kết quả ==========

    @Test
    void getPopularPackages_returnsTop3Packages() {
        when(retreatPackageRepository.findTop3ByIsActiveTrueAndIsDeleteFalseOrderByIdAsc())
                .thenReturn(List.of(pkg1, pkg2, pkg3));

        List<RetreatPackageDTO> result = retreatPackageService.getPopularPackages();

        assertThat(result).hasSize(3);
    }

    @Test
    void getPopularPackages_returnsEmpty_whenNoActivePackages() {
        when(retreatPackageRepository.findTop3ByIsActiveTrueAndIsDeleteFalseOrderByIdAsc())
                .thenReturn(Collections.emptyList());

        List<RetreatPackageDTO> result = retreatPackageService.getPopularPackages();

        assertThat(result).isEmpty();
    }
}
