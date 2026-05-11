package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class AgentChatDTO implements Serializable {
    private String sessionId;
    private String message;
}
