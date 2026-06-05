package com.example.interviewbeginner.common.ai;

import com.example.interviewbeginner.modules.llmprovider.LlmProviderEntity;
import com.example.interviewbeginner.modules.llmprovider.LlmProviderRepository;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM Provider 注册中心。
 * <p>
 * 管理 ChatClient 缓存，支持动态创建和重建。
 * 所有 ChatClient 通过 OpenAI 兼容协议构建，因此可以对接 DeepSeek、Kimi 等。
 */
@Slf4j
@Component
public class LlmProviderRegistry {

    private final LlmProviderRepository providerRepository;
    private final ApiKeyEncryptionService encryptionService;
    private final Map<String, ChatClient> clientCache = new ConcurrentHashMap<>();

    public LlmProviderRegistry(LlmProviderRepository providerRepository,
                               ApiKeyEncryptionService encryptionService) {
        this.providerRepository = providerRepository;
        this.encryptionService = encryptionService;
    }

    /**
     * 根据 provider ID 获取 ChatClient（带缓存）。
     */
    public ChatClient getChatClient(String providerId) {
        return clientCache.computeIfAbsent(providerId, id -> {
            log.info("Building ChatClient for provider: {}", id);
            return buildChatClient(id);
        });
    }

    /**
     * 获取默认 Provider 的 ChatClient。
     */
    public ChatClient getDefaultChatClient() {
        return getChatClient(resolveDefaultProviderId());
    }

    /**
     * 清空所有缓存，下次访问时重新构建。
     */
    public void reload() {
        int count = clientCache.size();
        clientCache.clear();
        log.info("ChatClient cache cleared ({} entries)", count);
    }

    private ChatClient buildChatClient(String providerId) {
        LlmProviderEntity provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found: " + providerId));

        String apiKey = encryptionService.decrypt(
                provider.getApiKeyNonce(), provider.getApiKeyCiphertext());

        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(provider.getBaseUrl())
                .apiKey(apiKey)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(provider.getModel())
                .temperature(provider.getTemperature() != null ? provider.getTemperature() : 0.7)
                .build();

        OpenAiChatModel chatModel = new OpenAiChatModel(
                openAiApi,
                options,
                ToolCallingManager.builder().build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE,
                ObservationRegistry.NOOP
        );

        return ChatClient.builder(chatModel).build();
    }

    private String resolveDefaultProviderId() {
        return providerRepository.findFirstByEnabledTrue()
                .map(LlmProviderEntity::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "No enabled LLM provider configured. "
                        + "Create one via POST /api/llm-providers"));
    }
}
