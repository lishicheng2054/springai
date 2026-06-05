/** 创建会话请求 */
export interface CreateSessionRequest {
  resumeId: number
  roleType: string
  questionCount: number
}

/** 创建会话响应 */
export interface CreateSessionResponse {
  sessionId: string
  firstQuestion: {
    questionId: number
    content: string
  }
  currentStep: number
  totalStep: number
}

/** 提交答案请求 */
export interface SubmitAnswerRequest {
  questionId: number
  answerText: string
}

/** 提交答案响应 */
export interface SubmitAnswerResponse {
  hasNext: boolean
  nextQuestion: {
    questionId: number
    content: string
  } | null
  resultId: number | null
}

/** 面试结果 */
export interface InterviewResultResponse {
  sessionId: number
  status: string
  totalScore: number
  techScore: number
  communicationScore: number
  logicScore: number
  strengths: string[]
  weaknesses: string[]
  improvements: string[]
  summary: string
}
