package com.AuraMoon.auramoon.auth.service;

import com.AuraMoon.auramoon.auth.dto.AccountDTO;
import com.AuraMoon.auramoon.auth.entity.User;
import java.util.List;

public interface AccountService {
    void createAccount(AccountDTO input, Integer actorId);
    void updateAccount(Integer id, AccountDTO input, Integer actorId);
    AccountDTO getAccountById(Integer id);
    void deactivateAccount(Integer userId, Integer actorId);
    void restoreAccount(Integer userId, Integer actorId);
    List<User> getAllAccounts(String email, boolean isDeleted);
}
