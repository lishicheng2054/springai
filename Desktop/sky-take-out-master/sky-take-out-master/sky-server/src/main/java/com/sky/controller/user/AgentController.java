package com.sky.controller.user;

import com.sky.dto.AgentChatDTO;
import com.sky.result.Result;
import com.sky.service.AgentService;
import com.sky.vo.AgentChatVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userAgentController")
@RequestMapping("/user/agent")
@Api(tags = "AI Agent 相关接口")
@Slf4j
public class AgentController {

    @Autowired
    private AgentService agentService;

    @PostMapping("/chat")
    @ApiOperation("Agent 智能对话")
    public Result<AgentChatVO> chat(@RequestBody AgentChatDTO dto) {
        log.info("Agent 对话请求：sessionId={}, message={}", dto.getSessionId(), dto.getMessage());
        AgentChatVO vo = agentService.chat(dto);
        return Result.success(vo);
    }
}
