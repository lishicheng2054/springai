package com.sky.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sky.agent.AgentToolFunctions;
import com.sky.context.BaseContext;
import com.sky.dto.AgentChatDTO;
import com.sky.service.AgentService;
import com.sky.vo.AgentChatVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {

    @Value("${sky.ai.api-key}")
    private String apiKey;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AgentToolFunctions toolFunctions;

    private static final String SESSION_KEY_PREFIX = "agent:session:";
    private static final int SESSION_TTL_MINUTES = 30;
    private static final int MAX_HISTORY_MESSAGES = 20;

    private static final String SYSTEM_PROMPT =
            "你是苍穹外卖的智能点餐助手。你可以帮用户查询菜品、将菜品加入购物车、查询订单。" +
            "请用友好、简洁的语言回复用户。当用户询问菜品时，调用 queryDish 工具；" +
            "当用户要点餐或加购物车时，调用 addToCart 工具；" +
            "当用户询问订单时，调用 queryOrder 工具。";

    // 工具定义（JSON 格式，通义千问 Function Calling 格式）
    private static final String TOOLS_JSON = "[" +
        "{\"type\":\"function\",\"function\":{" +
            "\"name\":\"queryDish\"," +
            "\"description\":\"查询菜品列表，可按关键词筛选\"," +
            "\"parameters\":{\"type\":\"object\",\"properties\":{" +
                "\"categoryName\":{\"type\":\"string\",\"description\":\"菜品名称关键词，为空则查全部\"}" +
            "},\"required\":[]}" +
        "}}," +
        "{\"type\":\"function\",\"function\":{" +
            "\"name\":\"addToCart\"," +
            "\"description\":\"将指定菜品加入购物车\"," +
            "\"parameters\":{\"type\":\"object\",\"properties\":{" +
                "\"dishName\":{\"type\":\"string\",\"description\":\"要加入购物车的菜品名称\"}" +
            "},\"required\":[\"dishName\"]}" +
        "}}," +
        "{\"type\":\"function\",\"function\":{" +
            "\"name\":\"queryOrder\"," +
            "\"description\":\"查询当前用户最近的订单列表\"," +
            "\"parameters\":{\"type\":\"object\",\"properties\":{}}" +
        "}}" +
    "]";

    @Override
    public AgentChatVO chat(AgentChatDTO dto) {
        String sessionId = dto.getSessionId();
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        Long userId = BaseContext.getCurrentId();
        String redisKey = SESSION_KEY_PREFIX + (userId != null ? userId : sessionId);

        // 加载历史消息
        List<Message> history = loadHistory(redisKey);

        // 添加用户消息
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(dto.getMessage())
                .build();
        history.add(userMsg);

        List<AgentChatVO.ToolCallVO> toolCallResults = new ArrayList<>();
        String finalReply;

        try {
            // 第一次调用 AI（带工具定义）
            GenerationResult result = callAI(history);
            Message assistantMsg = result.getOutput().getChoices().get(0).getMessage();

            // 检查是否有工具调用
            String toolCallsStr = extractToolCalls(assistantMsg);
            if (toolCallsStr != null) {
                history.add(assistantMsg);

                // 执行工具并收集结果
                JSONArray toolCalls = JSON.parseArray(toolCallsStr);
                for (int i = 0; i < toolCalls.size(); i++) {
                    JSONObject tc = toolCalls.getJSONObject(i);
                    String toolName = tc.getString("function") != null
                            ? tc.getJSONObject("function").getString("name")
                            : tc.getString("name");
                    String argsStr = tc.getJSONObject("function") != null
                            ? tc.getJSONObject("function").getString("arguments")
                            : "{}";
                    String toolCallId = tc.getString("id");

                    String toolResult = executeTool(toolName, argsStr);
                    toolCallResults.add(AgentChatVO.ToolCallVO.builder()
                            .toolName(toolName)
                            .args(argsStr)
                            .result(toolResult)
                            .build());

                    // 将工具结果加入历史
                    Message toolResultMsg = new Message();
                    toolResultMsg.setRole("tool");
                    toolResultMsg.setContent(toolResult);
                    if (toolCallId != null) {
                        toolResultMsg.setToolCallId(toolCallId);
                    }
                    history.add(toolResultMsg);
                }

                // 第二次调用 AI，获取最终回复
                GenerationResult finalResult = callAI(history);
                finalReply = finalResult.getOutput().getChoices().get(0).getMessage().getContent();
                history.add(Message.builder()
                        .role(Role.ASSISTANT.getValue())
                        .content(finalReply)
                        .build());
            } else {
                finalReply = assistantMsg.getContent();
                history.add(assistantMsg);
            }
        } catch (Exception e) {
            log.error("Agent 调用失败", e);
            finalReply = "抱歉，服务暂时不可用，请稍后再试。";
        }

        // 保存历史（最多保留 MAX_HISTORY_MESSAGES 条）
        saveHistory(redisKey, history);

        return AgentChatVO.builder()
                .sessionId(sessionId)
                .reply(finalReply)
                .toolCalls(toolCallResults)
                .build();
    }

    private GenerationResult callAI(List<Message> messages) throws Exception {
        // 构建消息列表（加入 system prompt）
        List<Message> fullMessages = new ArrayList<>();
        fullMessages.add(Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(SYSTEM_PROMPT)
                .build());
        fullMessages.addAll(messages);

        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model("qwen-turbo")
                .messages(fullMessages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        Generation gen = new Generation();
        return gen.call(param);
    }

    private String extractToolCalls(Message msg) {
        // 通义千问返回的 tool_calls 在 content 中以特定格式存在
        // 尝试从消息内容中解析工具调用
        if (msg == null) return null;
        String content = msg.getContent();
        if (content == null) return null;

        // 检查是否包含工具调用标记
        if (content.contains("\"tool_calls\"") || content.contains("tool_calls")) {
            try {
                JSONObject obj = JSON.parseObject(content);
                if (obj.containsKey("tool_calls")) {
                    return obj.getString("tool_calls");
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private String executeTool(String toolName, String argsStr) {
        try {
            JSONObject args = argsStr != null && !argsStr.isEmpty()
                    ? JSON.parseObject(argsStr) : new JSONObject();
            switch (toolName) {
                case "queryDish":
                    return toolFunctions.queryDish(args.getString("categoryName"));
                case "addToCart":
                    return toolFunctions.addToCart(args.getString("dishName"));
                case "queryOrder":
                    return toolFunctions.queryOrder();
                default:
                    return "未知工具：" + toolName;
            }
        } catch (Exception e) {
            log.error("执行工具 {} 失败", toolName, e);
            return "工具执行失败：" + e.getMessage();
        }
    }

    private List<Message> loadHistory(String redisKey) {
        try {
            String json = redisTemplate.opsForValue().get(redisKey);
            if (json == null || json.isEmpty()) return new ArrayList<>();
            JSONArray arr = JSON.parseArray(json);
            List<Message> messages = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                messages.add(Message.builder()
                        .role(obj.getString("role"))
                        .content(obj.getString("content"))
                        .build());
            }
            return messages;
        } catch (Exception e) {
            log.warn("加载会话历史失败", e);
            return new ArrayList<>();
        }
    }

    private void saveHistory(String redisKey, List<Message> messages) {
        try {
            // 只保留最近 MAX_HISTORY_MESSAGES 条
            List<Message> toSave = messages;
            if (messages.size() > MAX_HISTORY_MESSAGES) {
                toSave = messages.subList(messages.size() - MAX_HISTORY_MESSAGES, messages.size());
            }
            JSONArray arr = new JSONArray();
            for (Message msg : toSave) {
                JSONObject obj = new JSONObject();
                obj.put("role", msg.getRole());
                obj.put("content", msg.getContent());
                arr.add(obj);
            }
            redisTemplate.opsForValue().set(redisKey, arr.toJSONString(),
                    SESSION_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("保存会话历史失败", e);
        }
    }
}
