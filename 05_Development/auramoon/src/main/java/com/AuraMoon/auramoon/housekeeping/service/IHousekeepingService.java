package com.AuraMoon.auramoon.housekeeping.service;

import com.AuraMoon.auramoon.booking.entity.Villa;

import java.util.List;

public interface IHousekeepingService {

    /**
     * Lấy danh sách các Villa đang cần dọn dẹp (DIRTY) hoặc đang dọn (CLEANING).
     */
    List<Villa> getDirtyAndCleaningVillas();

    /**
     * Phân công nhân viên dọn dẹp cho một Villa.
     * Chuyển cleaning_status từ DIRTY → CLEANING.
     */
    void assignHousekeeper(Integer villaId, String keeperName, Integer actorId);

    /**
     * Nghiệm thu và duyệt Villa đã sạch (Approve).
     * Chuyển cleaning_status → CLEAN, villa_status → AVAILABLE.
     */
    void approveAndUpdateToClean(Integer villaId, Integer actorId);

    /**
     * Từ chối nghiệm thu, yêu cầu dọn lại.
     * Chuyển cleaning_status: CLEANING → DIRTY.
     */
    void rejectCleaning(Integer villaId, Integer actorId);
}
