package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VillaDisplayDTO {
    private Integer id;
    private String villaCode;
    private String villaTypeName; // Tên loại phòng
    private Integer limitPerson;
    private String villaStatus;
    private String cleaningStatus;
}
