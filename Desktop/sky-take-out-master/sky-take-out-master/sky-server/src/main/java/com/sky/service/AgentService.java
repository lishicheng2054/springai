package com.sky.service;

import com.sky.dto.AgentChatDTO;
import com.sky.vo.AgentChatVO;

public interface AgentService {
    AgentChatVO chat(AgentChatDTO dto);
}
