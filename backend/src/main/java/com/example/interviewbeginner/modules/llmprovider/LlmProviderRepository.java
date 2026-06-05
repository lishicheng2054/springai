package com.example.interviewbeginner.modules.llmprovider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * LLM Provider Repository。
 */
@Repository
public interface LlmProviderRepository extends JpaRepository<LlmProviderEntity, String> {

    /**
     * 查找第一个启用的 Provider（作为默认提供者）。
     */
    Optional<LlmProviderEntity> findFirstByEnabledTrue();
}
