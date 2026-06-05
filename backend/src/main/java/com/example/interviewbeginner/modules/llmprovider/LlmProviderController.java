package com.example.interviewbeginner.modules.llmprovider;

import com.example.interviewbeginner.common.result.Result;
import com.example.interviewbeginner.modules.llmprovider.dto.CreateProviderRequest;
import com.example.interviewbeginner.modules.llmprovider.dto.ProviderDTO;
import com.example.interviewbeginner.modules.llmprovider.dto.ProviderTestResult;
import com.example.interviewbeginner.modules.llmprovider.dto.UpdateProviderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * LLM Provider 管理接口。
 */
@RestController
@RequestMapping("/api/llm-providers")
@RequiredArgsConstructor
public class LlmProviderController {

    private final LlmProviderConfigService configService;

    /** 查询所有 Provider */
    @GetMapping
    public Result<List<ProviderDTO>> listProviders() {
        return Result.success(configService.listProviders());
    }

    /** 查询单个 Provider */
    @GetMapping("/{providerId}")
    public Result<ProviderDTO> getProvider(@PathVariable String providerId) {
        return Result.success(configService.getProvider(providerId));
    }

    /** 新增 Provider */
    @PostMapping
    public Result<Map<String, String>> createProvider(
            @Valid @RequestBody CreateProviderRequest request) {
        configService.createProvider(request);
        return Result.success(Map.of("status", "created", "id", request.id()));
    }

    /** 更新 Provider */
    @PutMapping("/{providerId}")
    public Result<Map<String, String>> updateProvider(
            @PathVariable String providerId,
            @RequestBody UpdateProviderRequest request) {
        configService.updateProvider(providerId, request);
        return Result.success(Map.of("status", "updated", "id", providerId));
    }

    /** 删除 Provider */
    @DeleteMapping("/{providerId}")
    public Result<Map<String, String>> deleteProvider(@PathVariable String providerId) {
        configService.deleteProvider(providerId);
        return Result.success(Map.of("status", "deleted", "id", providerId));
    }

    /** 连通性测试 */
    @PostMapping("/{providerId}/test")
    public Result<ProviderTestResult> testProvider(@PathVariable String providerId) {
        return Result.success(configService.testProvider(providerId));
    }
}
