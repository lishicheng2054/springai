<!-- Improved README for GitHub -->
<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen?logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20AI-1.1.2-blue?logo=spring" alt="Spring AI">
  <img src="https://img.shields.io/badge/React-18-61DAFB?logo=react" alt="React 18">
  <img src="https://img.shields.io/badge/TypeScript-5.6-3178C6?logo=typescript" alt="TypeScript">
  <img src="https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker" alt="Docker">
</div>

<hr>

# 🎯 SpringAI 模拟面试平台

<p align="center">
  <strong>AI 驱动的全栈面试训练平台</strong> — 从简历录入到 AI 出题、智能评分、多维分析，一站式面试备战
</p>

<p align="center">
  <a href="#-快速开始">快速开始</a> ·
  <a href="#-功能概览">功能概览</a> ·
  <a href="#-技术栈">技术栈</a> ·
  <a href="#-项目结构">项目结构</a> ·
  <a href="#-API接口">API 接口</a> ·
  <a href="#-部署">部署</a>
</p>

<hr>

## ✨ 功能概览

<table>
<tr>
<td width="50%">

### 🤖 AI 面试引擎
- **真实 LLM 驱动** — 集成 DeepSeek，根据简历智能出题
- **多维评分** — 技术能力/沟通表达/逻辑思维 三维评分
- **Mock 降级** — AI 不可用时自动回退本地 Mock 服务
- **面试配置化** — 可选岗位 & 题目数量 & 面试模板
- **语音面试** — WebSocket + 浏览器语音识别，边说边作答
- **计时器** — 每题 120 秒倒计时，超时自动提交

### 📊 数据分析看板
- **SVG 图表** — 分数趋势折线图 / 能力维度柱状图 / 岗位分布图
- **雷达图对比** — 多维度能力雷达图，最多 5 次面试横向对比
- **历史回顾** — 面试记录列表 + 统计卡片 + 趋势分析

</td>
<td width="50%">

### 📝 简历管理
- **双模式录入** — 文本输入 + PDF/Word/TXT 文件上传
- **AI 简历分析** — 技能提取 / 优势亮点 / 风险提示 / 改进建议
- **简历列表管理** — 增删查改 + Tika 文档解析

### 📚 知识库 (RAG)
- **文档管理** — 上传文档，自动解析
- **智能问答** — PG 全文检索 + LLM 生成答案 + 引用来源

### ⚙️ 平台功能
- **Provider 管理** — 多 LLM 服务商管理，API Key AES-256 加密存储
- **面试日程** — 日历式面试安排管理
- **题库 + 模板** — 内置分类题库，自定义面试模板
- **PDF 导出** — 面试评估报告一键导出
- **中断恢复** — 未完成面试自动保存，下次继续

</td>
</tr>
</table>

<hr>

## 🚀 快速开始

### 前置要求

- **JDK 21+**
- **Docker Desktop** (运行 PostgreSQL)
- **pnpm** (前端包管理)

### 1. 启动 PostgreSQL

```bash
docker compose up -d postgres
```

### 2. 启动后端

```bash
cd backend
./gradlew bootRun    # Windows: gradlew.bat bootRun
```

注册 DeepSeek Provider (首次运行):

```bash
curl -X POST http://localhost:8080/api/llm-providers \
  -H "Content-Type: application/json" \
  -d '{"id":"deepseek","baseUrl":"https://api.deepseek.com","apiKey":"sk-你的Key","model":"deepseek-chat"}'
```

### 3. 启动前端

```bash
cd frontend
pnpm install
pnpm dev
```

浏览器访问 **http://localhost:5173**

### 一键 Docker 部署

```bash
docker compose up -d
```

<hr>

## 🛠 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **后端框架** | Spring Boot 3.5 + Java 21 | 虚拟线程 |
| **AI 引擎** | Spring AI + DeepSeek (OpenAI 协议) | 可切换多种 LLM |
| **数据库** | PostgreSQL 16 + Hibernate JPA | 关系数据存储 |
| **文档解析** | Apache Tika 2.9 | PDF/Word/TXT 解析 |
| **PDF 生成** | OpenPDF 2.0 | 面试报告导出 |
| **加密** | AES-256-GCM | API Key 加密存储 |
| **前端框架** | React 18 + TypeScript | Hooks + 函数组件 |
| **构建工具** | Vite 8 | 极速 HMR |
| **样式** | TailwindCSS 4 + Lucide Icons | 现代企业风 |
| **图表** | 纯 SVG 实现 | 无第三方图表库依赖 |
| **部署** | Docker Compose | 一键启动 |


<hr>

## 📁 项目结构

```
springai-interview-beginner/
├── backend/                                # Spring Boot 后端
│   ├── Dockerfile
│   └── src/main/java/.../interviewbeginner/
│       ├── common/                         # 公共层
│       │   ├── ai/                         #   LlmProviderRegistry, ApiKeyEncryption
│       │   ├── config/                     #   CORS, WebSocket
│       │   ├── exception/                  #   GlobalExceptionHandler, ErrorCode
│       │   └── result/                     #   Result<T> 统一响应
│       ├── infrastructure/
│       │   └── export/                     #   PdfExportService
│       └── modules/
│           ├── dashboard/                  # 📊 数据看板
│           ├── health/                     # ❤️ 健康检查
│           ├── interview/                  # 🎯 模拟面试 (核心)
│           ├── interviewschedule/          # 📅 面试安排
│           ├── knowledgebase/             # 📚 知识库 RAG
│           ├── llmprovider/               # ⚙️ LLM Provider 管理
│           ├── questionbank/              # 📝 题库管理
│           ├── resume/                    # 📄 简历管理 + AI 分析
│           ├── template/                  # 📋 面试模板
│           └── voiceinterview/            # 🎙️ 语音面试 WebSocket
│
├── frontend/                               # React 前端
│   ├── Dockerfile + nginx.conf
│   └── src/
│       ├── api/                            # Axios 封装 + 11 个 API 模块
│       ├── components/                     # Layout, LoadingSpinner, ErrorMessage
│       ├── pages/                          # 16 个功能页面
│       │   ├── HomePage                    # 🏠 深色 Hero 首页
│       │   ├── DashboardPage               # 📊 SVG 图表看板
│       │   ├── ResumeListPage / ResumeInputPage / ResumeDetailPage
│       │   ├── InterviewPage / VoiceInterviewPage
│       │   ├── ResultPage / HistoryPage / ComparePage
│       │   ├── KnowledgeBaseListPage / KnowledgeBaseDetailPage / KnowledgeBaseChatPage
│       │   ├── QuestionBankPage / TemplateListPage
│       │   ├── InterviewSchedulePage / SettingsPage
│       │   └── ComparePage
│       └── types/                          # TypeScript 类型定义
│
└── docker-compose.yml                      # 一键部署编排
```

<hr>

## 🔌 API 接口

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| **健康** | `GET` | `/api/health` | 健康检查 |
| **简历** | `POST` `GET` `DELETE` | `/api/resumes[/{id}]` | 简历 CRUD |
| | `POST` | `/api/resumes/upload` | 文件上传简历 |
| | `POST` `GET` | `/api/resumes/{id}/analyze` `/analysis` | AI 简历分析 |
| **面试** | `POST` | `/api/interviews/sessions` | 创建面试会话 |
| | `POST` | `/api/interviews/sessions/{id}/answers` | 提交答案 |
| | `GET` | `/api/interviews/sessions/{id}/result` | 获取评分 |
| | `GET` | `/api/interviews/history` | 面试历史 |
| | `GET` | `/api/interviews/compare?ids=...` | 多维度对比 |
| | `GET` | `/api/interviews/sessions/{id}/export` | 导出 PDF |
| | `GET` | `/api/interviews/unfinished/{resumeId}` | 中断恢复 |
| **知识库** | `POST` `GET` `DELETE` | `/api/knowledgebases` | 知识库 CRUD |
| | `POST` `GET` `DELETE` | `/api/knowledgebases/{id}/documents` | 文档管理 |
| | `POST` | `/api/knowledgebases/{id}/chat` | RAG 问答 |
| **题库** | `GET` `POST` `DELETE` | `/api/question-bank` | 题库 CRUD |
| **模板** | `GET` `POST` `DELETE` | `/api/templates` | 模板 CRUD |
| | `POST` | `/api/templates/{id}/apply` | 应用模板 |
| **日程** | `GET` `POST` `PUT` `PATCH` `DELETE` | `/api/interview-schedules` | 日程管理 |
| **Provider** | `GET` `POST` `PUT` `DELETE` | `/api/llm-providers` | Provider 管理 |
| | `POST` | `/api/llm-providers/{id}/test` | 连通性测试 |
| **看板** | `GET` | `/api/dashboard/stats` | 聚合统计数据 |
| **语音** | `WS` | `/ws/voice-interview/{sessionId}` | WebSocket 语音面试 |

<hr>

## 🚢 部署

### Docker Compose 一键启动

```bash
# 启动全部服务 (PostgreSQL + 后端 + 前端)
docker compose up -d

# 查看日志
docker compose logs -f

# 停止
docker compose down
```

- 前端: `http://localhost:80`
- 后端: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

### 手动构建

```bash
# 后端
cd backend && ./gradlew bootJar && docker build -t interview-backend .

# 前端
cd frontend && pnpm build && docker build -t interview-frontend .
```

<hr>

## 📈 项目演进

| 阶段 | 内容 |
|------|------|
| **MVP** | 健康检查 + 简历文本 + MockAI 面试 |
| **第二阶段** | PostgreSQL + DeepSeek 真实 AI + Provider 管理 |
| **第三阶段** | 知识库 (Tika 解析 + PG 全文搜索 + RAG 问答) |
| **第四阶段** | 数据分析看板 + 面试对比 + PDF 导出 |
| **第五阶段** | 语音面试 + 题库模板 + 面试安排 + 中断恢复 |
| **当前** | 全功能平台，持续打磨 UI/UX |

<hr>

<div align="center">
  <sub>Built with ❤️ by SpringAI Interview Platform Team</sub>
</div>
