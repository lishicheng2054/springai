package com.example.interviewbeginner.modules.llmprovider;

import com.example.interviewbeginner.common.ai.ApiKeyEncryptionService;
import com.example.interviewbeginner.common.ai.LlmProviderRegistry;
import com.example.interviewbeginner.common.exception.BusinessException;
import com.example.interviewbeginner.common.exception.ErrorCode;
import com.example.interviewbeginner.modules.llmprovider.dto.CreateProviderRequest;
import com.example.interviewbeginner.modules.llmprovider.dto.ProviderDTO;
import com.example.interviewbeginner.modules.llmprovider.dto.ProviderTestResult;
import com.example.interviewbeginner.modules.llmprovider.dto.UpdateProviderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Provider 配置服务（简化版）。
 * <p>
 * 提供 Provider 的 CRUD、连通性测试和默认 Provider 管理。
 * 不做 YAML 文件编辑，所有配置存数据库。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmProviderConfigService {

    private final LlmProviderRepository providerRepository;
    private final ApiKeyEncryptionService encryptionService;
    private final LlmProviderRegistry registry;

    // ===== 查询 =====

    public List<ProviderDTO> listProviders() {
        return providerRepository.findAll().stream()
                .map(p -> new ProviderDTO(
                        p.getId(),
                        p.getBaseUrl(),
                        maskApiKey(decrypt(p)),
                        p.getModel(),
                        p.getTemperature(),
                        p.isEnabled(),
                        p.getCreatedAt(),
                        p.getUpdatedAt()
                ))
                .toList();
    }

    public ProviderDTO getProvider(String id) {
        LlmProviderEntity p = getEntityOrThrow(id);
        return new ProviderDTO(
                p.getId(), p.getBaseUrl(), maskApiKey(decrypt(p)),
                p.getModel(), p.getTemperature(), p.isEnabled(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }

    // ===== 写入 =====

    @Transactional
    public void createProvider(CreateProviderRequest req) {
        if (providerRepository.existsById(req.id())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "Provider 已存在: " + req.id());
        }
        var encrypted = encryptionService.encrypt(req.apiKey());
        providerRepository.save(LlmProviderEntity.builder()
                .id(req.id())
                .baseUrl(req.baseUrl())
                .apiKeyNonce(encrypted.nonce())
                .apiKeyCiphertext(encrypted.ciphertext())
                .model(req.model())
                .temperature(req.temperature())
                .enabled(true)
                .build());
        registry.reload();
        log.info("Provider created: {}", req.id());
    }

    @Transactional
    public void updateProvider(String id, UpdateProviderRequest req) {
        LlmProviderEntity p = getEntityOrThrow(id);
        if (req.baseUrl() != null) p.setBaseUrl(req.baseUrl());
        if (req.model() != null) p.setModel(req.model());
        if (req.temperature() != null) p.setTemperature(req.temperature());
        if (req.enabled() != null) p.setEnabled(req.enabled());
        if (req.apiKey() != null) {
            var encrypted = encryptionService.encrypt(req.apiKey());
            p.setApiKeyNonce(encrypted.nonce());
            p.setApiKeyCiphertext(encrypted.ciphertext());
        }
        providerRepository.save(p);
        registry.reload();
        log.info("Provider updated: {}", id);
    }

    @Transactional
    public void deleteProvider(String id) {
        getEntityOrThrow(id);
        providerRepository.deleteById(id);
        registry.reload();
        log.info("Provider deleted: {}", id);
    }

    // ===== 连通性测试 =====

    public ProviderTestResult testProvider(String id) {
        LlmProviderEntity p = getEntityOrThrow(id);
        String apiKey = decrypt(p);
        try {
            var factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(10000);

            RestClient client = RestClient.builder()
                    .defaultHeader("Authorization", "Bearer " + apiKey)
                    .requestFactory(factory)
                    .build();

            Map<String, Object> body = Map.of(
                    "model", p.getModel(),
                    "messages", List.of(Map.of("role", "user", "content", "Reply with OK only.")),
                    "max_tokens", 1
            );

            client.post()
                    .uri(URI.create(p.getBaseUrl() + "/chat/completions"))
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);

            return new ProviderTestResult(true, "连接成功", p.getModel());
        } catch (Exception e) {
            log.warn("Provider test failed: id={}, error={}", id, e.getMessage());
            return new ProviderTestResult(false, "连接失败: " + e.getMessage(), p.getModel());
        }
    }

    // ===== 私有方法 =====

    private LlmProviderEntity getEntityOrThrow(String id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Provider 不存在: " + id));
    }

    private String decrypt(LlmProviderEntity p) {
        return encryptionService.decrypt(p.getApiKeyNonce(), p.getApiKeyCiphertext());
    }

    private String maskApiKey(String key) {
        if (key == null || key.length() <= 6) return "***";
        return key.substring(0, 3) + "***" + key.substring(key.length() - 3);
    }
}
