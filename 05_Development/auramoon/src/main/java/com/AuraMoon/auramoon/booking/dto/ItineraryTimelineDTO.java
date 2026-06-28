package com.AuraMoon.auramoon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryTimelineDTO {
    private Integer bookingId;
    private String guestName;
    private String packageName;
    private String villaName;
    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
    private String bookingStatus;
    private boolean hasReviewed;
    private List<TimelineEvent> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimelineEvent {
        private String eventName;
        private LocalDateTime time;
        private String location;
        private String description;
    }
}
