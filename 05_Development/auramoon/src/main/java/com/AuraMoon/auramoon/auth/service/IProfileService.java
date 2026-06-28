package com.AuraMoon.auramoon.auth.service;

import com.AuraMoon.auramoon.auth.dto.SensitiveProfileDto;
import com.AuraMoon.auramoon.auth.dto.PersonalProfileDto;
import com.AuraMoon.auramoon.auth.dto.MyAccountDto;
import com.AuraMoon.auramoon.auth.dto.ChangePasswordDto;

public interface IProfileService {
    SensitiveProfileDto getSensitiveProfile(Integer userId);
    void saveSensitiveProfile(SensitiveProfileDto inputDto, Integer userId);

    PersonalProfileDto getPersonalProfile(Integer userId);
    void savePersonalProfile(PersonalProfileDto dto, Integer userId);

    MyAccountDto getMyAccountInfo(Integer userId);
    void changePassword(Integer userId, ChangePasswordDto dto);
}
