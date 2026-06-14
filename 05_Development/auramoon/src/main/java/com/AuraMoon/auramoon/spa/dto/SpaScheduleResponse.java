package com.AuraMoon.auramoon.spa.dto;

import java.time.LocalDateTime;

public class SpaScheduleResponse {
    private Integer scheduleId;
    private String therapistCode;
    private Integer roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }

    public String getTherapistCode() { return therapistCode; }
    public void setTherapistCode(String therapistCode) { this.therapistCode = therapistCode; }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
