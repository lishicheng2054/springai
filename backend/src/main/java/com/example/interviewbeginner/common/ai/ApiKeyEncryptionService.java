package com.example.interviewbeginner.common.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * API Key 加密服务。
 * <p>
 * 使用 AES-256-GCM 加密，每次加密生成随机 nonce（12 字节），
 * 密文和 nonce 分别存储到数据库的不同列。
 */
@Slf4j
@Service
public class ApiKeyEncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int KEY_SIZE = 256;

    private final SecretKey secretKey;

    public ApiKeyEncryptionService() {
        this.secretKey = loadOrGenerateKey();
    }

    /**
     * 加密 API Key。
     */
    public EncryptedValue encrypt(String plaintext) {
        try {
            byte[] nonce = new byte[GCM_IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(nonce);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, nonce));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            return new EncryptedValue(
                    Base64.getEncoder().encodeToString(nonce),
                    Base64.getEncoder().encodeToString(ciphertext)
            );
        } catch (Exception e) {
            throw new RuntimeException("API Key encryption failed", e);
        }
    }

    /**
     * 解密 API Key。
     */
    public String decrypt(String nonceBase64, String ciphertextBase64) {
        try {
            byte[] nonce = Base64.getDecoder().decode(nonceBase64);
            byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, nonce));
            byte[] plaintext = cipher.doFinal(ciphertext);

            return new String(plaintext, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("API Key decryption failed", e);
        }
    }

    /**
     * 加载或生成密钥。
     * 生产环境中密钥应从环境变量或密钥管理服务加载。
     */
    private SecretKey loadOrGenerateKey() {
        String envKey = System.getenv("APP_ENCRYPTION_KEY");
        if (envKey != null && !envKey.isBlank()) {
            byte[] keyBytes = Base64.getDecoder().decode(envKey);
            log.info("Encryption key loaded from environment");
            return new SecretKeySpec(keyBytes, ALGORITHM);
        }
        // 开发环境：自动生成（重启后之前加密的数据将无法解密）
        try {
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            generator.init(KEY_SIZE);
            SecretKey key = generator.generateKey();
            log.warn("No APP_ENCRYPTION_KEY set — generated ephemeral key. "
                    + "Previously encrypted data will be lost on restart.");
            return key;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate encryption key", e);
        }
    }

    /**
     * 加密结果。
     */
    public record EncryptedValue(String nonce, String ciphertext) {
    }
}
