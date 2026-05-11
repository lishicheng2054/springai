package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentChatVO implements Serializable {
    private String sessionId;
    private String reply;
    private List<ToolCallVO> toolCalls;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCallVO implements Serializable {
        private String toolName;
        private String args;
        private String result;
    }
}
