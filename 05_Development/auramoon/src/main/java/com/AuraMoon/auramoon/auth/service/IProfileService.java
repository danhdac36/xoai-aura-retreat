package com.AuraMoon.auramoon.auth.service;

import com.AuraMoon.auramoon.auth.dto.SensitiveProfileDto;
import com.AuraMoon.auramoon.auth.dto.PersonalProfileDto;

public interface IProfileService {
    SensitiveProfileDto getSensitiveProfile(Integer userId);
    void saveSensitiveProfile(SensitiveProfileDto inputDto, Integer userId);

    PersonalProfileDto getPersonalProfile(Integer userId);
    void savePersonalProfile(PersonalProfileDto dto, Integer userId);
}
