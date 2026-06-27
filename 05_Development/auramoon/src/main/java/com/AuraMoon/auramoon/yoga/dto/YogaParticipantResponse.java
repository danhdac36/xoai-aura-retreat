package com.AuraMoon.auramoon.yoga.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaParticipantResponse {
    private Integer registrationId;
    private Integer bookingId;
    private String guestName;
    private String villaName;
    private String injuriesNote;
    private String medicalConditionsNote;
}
