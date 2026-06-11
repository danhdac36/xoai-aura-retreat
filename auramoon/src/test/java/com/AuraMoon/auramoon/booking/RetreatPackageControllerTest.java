package com.AuraMoon.auramoon.booking;

import com.AuraMoon.auramoon.booking.controller.RetreatPackageController;
import com.AuraMoon.auramoon.booking.dto.RetreatPackageDTO;
import com.AuraMoon.auramoon.booking.service.RetreatPackageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for RetreatPackageController
 * Features covered:
 *   - Hiển thị danh sách gói retreat
 *   - Lọc theo mục tiêu sức khỏe (typePackage)
 *   - Lọc theo số ngày (durationDays)
 *   - Lọc theo mức giá (priceRange)
 *   - Xem chi tiết từng gói
 *   - Hiển thị gợi ý khi không có kết quả
 */
@WebMvcTest(RetreatPackageController.class)
class RetreatPackageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RetreatPackageService retreatPackageService;

    private RetreatPackageDTO buildDTO(int id, String type, int days, BigDecimal price) {
        return RetreatPackageDTO.builder()
                .id(id).typePackage(type).packageName(type + " Package")
                .durationDays(days).price(price).build();
    }

    // ========== TC-CT-01: Hiển thị danh sách gói retreat ==========

    @Test
    void listPackages_rendersPackagesView_withPackagesList() throws Exception {
        List<RetreatPackageDTO> packages = List.of(
                buildDTO(1, "Yoga", 3, new BigDecimal("3000000")),
                buildDTO(2, "Detox", 7, new BigDecimal("8000000"))
        );
        when(retreatPackageService.searchPackages(null, null, null)).thenReturn(packages);
        when(retreatPackageService.getAllActivePackageTypes()).thenReturn(List.of("Yoga", "Detox"));

        mockMvc.perform(get("/packages"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/packages"))
                .andExpect(model().attributeExists("packages"))
                .andExpect(model().attribute("packages", packages));
    }

    @Test
    void listPackages_setsSelectedTypeToAll_whenNoTypeParam() throws Exception {
        when(retreatPackageService.searchPackages(null, null, null)).thenReturn(Collections.emptyList());
        when(retreatPackageService.getAllActivePackageTypes()).thenReturn(Collections.emptyList());
        when(retreatPackageService.getPopularPackages()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/packages"))
                .andExpect(model().attribute("selectedType", "All"));
    }

    // ========== TC-CT-02: Lọc theo mục tiêu sức khỏe ==========

    @Test
    void listPackages_filterByType_passesTypeToService() throws Exception {
        List<RetreatPackageDTO> filtered = List.of(buildDTO(1, "Yoga", 3, new BigDecimal("3000000")));
        when(retreatPackageService.searchPackages("Yoga", null, null)).thenReturn(filtered);
        when(retreatPackageService.getAllActivePackageTypes()).thenReturn(List.of("Yoga", "Detox"));

        mockMvc.perform(get("/packages").param("type", "Yoga"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("packages", filtered))
                .andExpect(model().attribute("selectedType", "Yoga"));

        verify(retreatPackageService).searchPackages("Yoga", null, null);
    }

    @Test
    void listPackages_filterByType_setsSelectedTypeInModel() throws Exception {
        when(retreatPackageService.searchPackages("Detox", null, null)).thenReturn(Collections.emptyList());
        when(retreatPackageService.getAllActivePackageTypes()).thenReturn(List.of("Detox"));
        when(retreatPackageService.getPopularPackages()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/packages").param("type", "Detox"))
                .andExpect(model().attribute("selectedType", "Detox"));
    }

    // ========== TC-CT-03: Lọc theo số ngày ==========

    @Test
    void listPackages_filterByDurationDays_passesDurationToService() throws Exception {
        List<RetreatPackageDTO> filtered = List.of(buildDTO(2, "Detox", 7, new BigDecimal("8000000")));
        when(retreatPackageService.searchPackages(null, 7, null)).thenReturn(filtered);
        when(retreatPackageService.getAllActivePackageTypes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/packages").param("durationDays", "7"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("packages", filtered))
                .andExpect(model().attribute("selectedDurationDays", 7));

        verify(retreatPackageService).searchPackages(null, 7, null);
    }

    // ========== TC-CT-04: Lọc theo mức giá ==========

    @Test
    void listPackages_filterByPriceRange_passesPriceRangeToService() throws Exception {
        List<RetreatPackageDTO> filtered = List.of(buildDTO(1, "Yoga", 3, new BigDecimal("3000000")));
        when(retreatPackageService.searchPackages(null, null, "UNDER_5")).thenReturn(filtered);
        when(retreatPackageService.getAllActivePackageTypes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/packages").param("priceRange", "UNDER_5"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("packages", filtered))
                .andExpect(model().attribute("selectedPriceRange", "UNDER_5"));

        verify(retreatPackageService).searchPackages(null, null, "UNDER_5");
    }

    @Test
    void listPackages_filterByAllCriteria_passesAllParamsToService() throws Exception {
        List<RetreatPackageDTO> filtered = List.of(buildDTO(2, "Detox", 7, new BigDecimal("8000000")));
        when(retreatPackageService.searchPackages("Detox", 7, "FROM_5_TO_10")).thenReturn(filtered);
        when(retreatPackageService.getAllActivePackageTypes()).thenReturn(List.of("Detox"));

        mockMvc.perform(get("/packages")
                        .param("type", "Detox")
                        .param("durationDays", "7")
                        .param("priceRange", "FROM_5_TO_10"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("packages", filtered));

        verify(retreatPackageService).searchPackages("Detox", 7, "FROM_5_TO_10");
    }

    // ========== TC-CT-05: Xem chi tiết từng gói ==========

    @Test
    void packageDetail_rendersDetailView_withPackageData() throws Exception {
        RetreatPackageDTO dto = buildDTO(1, "Yoga", 3, new BigDecimal("3000000"));
        when(retreatPackageService.getPackageById(1)).thenReturn(dto);

        mockMvc.perform(get("/packages/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/package-detail"))
                .andExpect(model().attribute("pkg", dto));
    }

    @Test
    void packageDetail_throwsException_whenPackageNotFound() throws Exception {
        when(retreatPackageService.getPackageById(99))
                .thenThrow(new RuntimeException("Retreat package not found"));

        mockMvc.perform(get("/packages/99"))
                .andExpect(status().is5xxServerError());
    }

    // ========== TC-CT-06: Hiển thị gợi ý khi không có kết quả ==========

    @Test
    void listPackages_addsPopularPackages_whenResultIsEmpty() throws Exception {
        List<RetreatPackageDTO> popular = List.of(
                buildDTO(1, "Yoga", 3, new BigDecimal("3000000")),
                buildDTO(2, "Detox", 7, new BigDecimal("8000000"))
        );
        when(retreatPackageService.searchPackages("Unknown", null, null)).thenReturn(Collections.emptyList());
        when(retreatPackageService.getAllActivePackageTypes()).thenReturn(Collections.emptyList());
        when(retreatPackageService.getPopularPackages()).thenReturn(popular);

        mockMvc.perform(get("/packages").param("type", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("popularPackages"))
                .andExpect(model().attribute("popularPackages", popular));

        verify(retreatPackageService).getPopularPackages();
    }

    @Test
    void listPackages_doesNotAddPopularPackages_whenResultIsNotEmpty() throws Exception {
        List<RetreatPackageDTO> packages = List.of(buildDTO(1, "Yoga", 3, new BigDecimal("3000000")));
        when(retreatPackageService.searchPackages(null, null, null)).thenReturn(packages);
        when(retreatPackageService.getAllActivePackageTypes()).thenReturn(List.of("Yoga"));

        mockMvc.perform(get("/packages"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("popularPackages"));

        verify(retreatPackageService, never()).getPopularPackages();
    }
}
