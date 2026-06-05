# SpringAI 模拟面试平台 - 入门版 (MVP)

从0到1手动搭建的 AI 模拟面试平台最小闭环版本。

## 快速启动

### 后端 (Spring Boot + H2)
```bash
cd backend
# 确保 JAVA_HOME 指向 JDK 21+
export JAVA_HOME="C:/Program Files/Java/jdk-21"
./gradlew bootRun
# 后端启动于 http://localhost:8080
# H2 控制台: http://localhost:8080/h2-console
```

### 前端 (React + Vite + TypeScript)
```bash
cd frontend
pnpm install
pnpm dev
# 前端启动于 http://localhost:5173
# API 请求自动代理到后端 8080
```

### 验证
```bash
# 健康检查
curl http://localhost:8080/api/health

# 完整面试流程
curl -X POST http://localhost:8080/api/resumes -H "Content-Type: application/json" \
  -d '{"candidateName":"张三","targetPosition":"Java后端","resumeText":"..."}'
curl -X POST http://localhost:8080/api/interviews/sessions -H "Content-Type: application/json" \
  -d '{"resumeId":1,"roleType":"JAVA_BACKEND","questionCount":5}'
# ...逐题提交答案...
```

## 项目结构

```
springai-interview-beginner/
├── backend/                         # Spring Boot 3.5.0 + Java 21 + H2
│   └── src/main/java/com/example/interviewbeginner/
│       ├── common/                  # Result, ErrorCode, Exception, CORS
│       └── modules/
│           ├── health/              # 健康检查
│           ├── resume/              # 简历 CRUD
│           └── interview/           # 面试会话 + MockAI
└── frontend/                        # React 18 + Vite + TailwindCSS
    └── src/
        ├── api/                     # Axios 封装 + API 模块
        ├── types/                   # TypeScript 类型定义
        ├── pages/                   # Home, Resume, Interview, Result
        └── components/              # Layout, LoadingSpinner, ErrorMessage
```

## MVP 功能清单

- [x] 首页展示 + 跳转
- [x] 简历文本输入
- [x] 面试会话创建 + AI 出题 (Mock)
- [x] 逐题作答 + 进度显示
- [x] 综合评分（技术/沟通/逻辑）
- [x] 优点/不足/改进建议

## 后续扩展

1. 替换 MockAiService → Spring AI 接入真实 LLM
2. H2 → PostgreSQL + pgvector 向量检索
3. 知识库模块（文档上传、RAG 问答）
4. 面试安排模块
5. 语音面试（WebSocket + ASR/TTS）
6. LLM Provider 管理
