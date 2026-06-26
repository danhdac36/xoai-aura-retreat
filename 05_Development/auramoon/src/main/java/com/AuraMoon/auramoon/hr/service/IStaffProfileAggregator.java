package com.AuraMoon.auramoon.hr.service;
import com.AuraMoon.auramoon.hr.dto.FullStaffProfileDTO;
public interface IStaffProfileAggregator {
    FullStaffProfileDTO getAggregatedProfile(Long id);
}
