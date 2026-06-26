package com.AuraMoon.auramoon.yoga.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class YogaManagerDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YogaClassRequest {
        @NotBlank(message = "Tên lớp học không được để trống")
        @Size(max = 100, message = "Tên lớp học không vượt quá 100 ký tự")
        private String className;

        private String description;

        @NotNull(message = "Thời lượng lớp học không được để trống")
        @Min(value = 15, message = "Thời lượng lớp học tối thiểu phải là 15 phút")
        private Integer durationMinutes;

        @Size(max = 255, message = "Đường dẫn ảnh không vượt quá 255 ký tự")
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YogaClassResponse {
        private Integer classId;
        private String className;
        private String description;
        private Integer durationMinutes;
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YogaScheduleRequest {
        @NotNull(message = "Lớp học không được để trống")
        private Integer classId;

        @NotNull(message = "Huấn luyện viên không được để trống")
        private Integer instructorId;

        @NotBlank(message = "Địa điểm không được để trống")
        @Size(max = 100, message = "Địa điểm không vượt quá 100 ký tự")
        private String location;

        @NotNull(message = "Thời gian bắt đầu không được để trống")
        @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startTime;

        @NotNull(message = "Thời gian kết thúc không được để trống")
        @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endTime;

        @NotNull(message = "Sĩ số tối đa không được để trống")
        @Min(value = 1, message = "Sĩ số tối đa phải ít nhất là 1 học viên")
        private Integer maxCapacity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YogaScheduleResponse {
        private Integer scheduleId;
        private Integer classId;
        private String className;
        private Integer durationMinutes;
        private Integer instructorId;
        private String instructorName;
        private String location;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer maxCapacity;
        private Integer countRegistered;
    }
}
