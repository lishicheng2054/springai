package com.example.interviewbeginner.modules.interview;

import com.example.interviewbeginner.modules.interview.InterviewQuestionEntity.QuestionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock AI 服务。
 * <p>
 * 第一版使用预置题目和启发式评分替代真实 AI 调用。
 * 方法签名与真实 AI 服务保持一致，后续只需替换实现类即可接入 Spring AI。
 */
@Slf4j
@Service
public class MockAiService {

    /**
     * 根据简历和岗位生成面试题目。
     *
     * @param resumeText     简历文本
     * @param targetPosition 目标岗位
     * @param questionCount  题目数量
     * @return 题目内容列表
     */
    public List<String> generateQuestions(String resumeText, String targetPosition, int questionCount) {
        log.info("MockAi: generating {} questions for position={}", questionCount, targetPosition);

        List<String> pool = new ArrayList<>();
        pool.add("请做一个简短的自我介绍，包括你的技术背景和项目经验。");
        pool.add("请介绍一个你最有成就感的项目，你在其中担任什么角色？遇到的最大挑战是什么？");
        pool.add("你对" + targetPosition + "这个岗位的理解是什么？需要具备哪些核心能力？");
        pool.add("请描述一次你解决技术难题的经历。你用了什么方法和工具？");
        pool.add("在团队协作中，你遇到过意见分歧吗？你是如何处理的？");
        pool.add("请谈谈你对Spring Boot框架的理解，为什么选择用它？");
        pool.add("数据库优化方面你有哪些经验？比如慢SQL调优、索引设计等。");
        pool.add("你未来的职业规划是什么？希望在技术方向还是管理方向发展？");

        int actualCount = Math.min(Math.max(questionCount, 1), pool.size());
        return pool.subList(0, actualCount);
    }

    /**
     * 根据题目类型池生成面试题目（包含类型信息）。
     *
     * @param resumeText     简历文本
     * @param targetPosition 目标岗位
     * @param questionCount  题目数量
     * @return 带类型的题目列表
     */
    public List<QuestionWithType> generateQuestionsWithType(
            String resumeText, String targetPosition, int questionCount) {

        List<QuestionWithType> pool = new ArrayList<>();
        pool.add(new QuestionWithType("请做一个简短的自我介绍。", QuestionType.INTRO));
        pool.add(new QuestionWithType(
                "请介绍一个你最有成就感的项目，你在其中担任什么角色？", QuestionType.PROJECT));
        pool.add(new QuestionWithType(
                "你对" + targetPosition + "岗位需要哪些核心技能？", QuestionType.TECH));
        pool.add(new QuestionWithType(
                "请描述一次你排查线上故障的经历。", QuestionType.TECH));
        pool.add(new QuestionWithType(
                "你的职业规划是什么？为什么选择这个方向？", QuestionType.HR));

        int actualCount = Math.min(Math.max(questionCount, 1), pool.size());
        return pool.subList(0, actualCount);
    }

    /**
     * 评估单题答案。
     *
     * @param question  题目内容
     * @param userAnswer 用户答案
     * @return 评估结果（分数 + 反馈）
     */
    public AnswerEval evaluateAnswer(String question, String userAnswer) {
        // 简单启发式：基于答案长度和关键词匹配评分
        int lengthScore = Math.min(userAnswer.length() / 5, 60);
        int keywordBonus = countKeywords(userAnswer) * 5;
        int score = Math.min(lengthScore + keywordBonus, 95);

        String feedback;
        if (score >= 75) {
            feedback = "回答较为完整，能够清晰地表达核心观点。建议可以补充更多具体的技术细节和实际案例。";
        } else if (score >= 50) {
            feedback = "回答有一定内容，但深度和完整度可以进一步提高。建议围绕STAR法则展开回答。";
        } else {
            feedback = "回答过于简短，建议详细展开。可以从背景、行动、结果等方面进行描述。";
        }

        return new AnswerEval(score, feedback);
    }

    /**
     * 统计关键词命中数。
     */
    private int countKeywords(String answer) {
        String[] keywords = {"项目", "技术", "经验", "实现", "设计", "优化", "方案",
                "团队", "架构", "性能", "数据库", "Spring", "Java", "微服务"};
        int count = 0;
        for (String kw : keywords) {
            if (answer.contains(kw)) {
                count++;
            }
        }
        return Math.min(count, 8); // 最多 8 个
    }

    /**
     * 生成最终面试评估报告。
     *
     * @param answers 所有答案记录
     * @return 评估实体
     */
    public InterviewEvaluationEntity generateFinalEvaluation(
            InterviewSessionEntity session,
            List<InterviewAnswerEntity> answers) {

        double avgScore = answers.stream()
                .mapToInt(a -> a.getScore() != null ? a.getScore() : 50)
                .average()
                .orElse(50);

        int total = (int) Math.round(avgScore);
        int tech = Math.min((int) Math.round(avgScore * 1.05), 100);
        int comm = Math.min((int) Math.round(avgScore * 0.95), 100);
        int logic = Math.min((int) Math.round(avgScore * 0.98), 100);

        return InterviewEvaluationEntity.builder()
                .session(session)
                .totalScore(total)
                .techScore(tech)
                .communicationScore(comm)
                .logicScore(logic)
                .strengths("[\"表达流畅，能够清楚描述项目经历\"]")
                .weaknesses("[\"部分技术问题可以回答得更深入\", \"建议结合更多实际案例\"]")
                .improvements("[\"建议深入学习系统设计相关知识\", \"多练习行为面试题\"]")
                .summary("整体表现" + (total >= 75 ? "良好" : total >= 50 ? "中等" : "有待提升")
                        + "，建议针对性地加强技术深度和沟通表达。")
                .build();
    }

    // ========== 内部类 ==========

    /**
     * 带类型的题目。
     */
    public record QuestionWithType(String content, QuestionType questionType) {
    }

    /**
     * 单题评估结果。
     */
    public record AnswerEval(int score, String feedback) {
    }
}
