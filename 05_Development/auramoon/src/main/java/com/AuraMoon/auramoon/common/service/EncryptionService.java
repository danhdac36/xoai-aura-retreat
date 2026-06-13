package com.AuraMoon.auramoon.common.service;

public interface EncryptionService {
    String encrypt(String data);
    String decrypt(String encryptedData);
}
