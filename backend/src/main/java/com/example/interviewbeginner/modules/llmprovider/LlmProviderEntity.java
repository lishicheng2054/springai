package com.example.interviewbeginner.modules.llmprovider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * LLM Provider 配置实体。
 * 存储 DeepSeek、OpenAI 等 Provider 的连接信息，API Key 加密存储。
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "llm_provider")
public class LlmProviderEntity {

    /** Provider 唯一标识（如 "deepseek"、"openai"） */
    @Id
    @Column(length = 64)
    private String id;

    /** API 地址 */
    @Column(name = "base_url", nullable = false, length = 512)
    private String baseUrl;

    /** API Key 密文（AES-GCM 加密后 Base64） */
    @Column(name = "api_key_ciphertext", nullable = false, length = 4096)
    private String apiKeyCiphertext;

    /** 加密 nonce（Base64） */
    @Column(name = "api_key_nonce", nullable = false, length = 64)
    private String apiKeyNonce;

    /** 模型名称 */
    @Column(nullable = false, length = 128)
    private String model;

    /** 温度参数 */
    private Double temperature;

    /** 是否启用 */
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
