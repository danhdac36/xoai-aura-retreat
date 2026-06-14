package com.AuraMoon.auramoon.common.service.impl;

import com.AuraMoon.auramoon.common.service.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    // Sử dụng khóa AES 128-bit (16 ký tự) mặc định để chạy thử nghiệm, hoặc cấu hình từ application.properties
    @Value("${app.encryption.key:AuraMoonSecretKey}")
    private String secretKey;

    private static final String ALGORITHM = "AES";

    @Override
    public String encrypt(String data) {
        if (data == null) {
            return null;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa dữ liệu", e);
        }
    }

    @Override
    public String decrypt(String encryptedData) {
        if (encryptedData == null) {
            return null;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi giải mã dữ liệu", e);
        }
    }
}

