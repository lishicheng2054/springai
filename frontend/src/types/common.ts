/** 统一响应体 */
export interface Result<T> {
  code: number
  message: string
  data: T
  success: boolean
}

/** 面试状态 */
export type SessionStatus = 'NEW' | 'IN_PROGRESS' | 'COMPLETED' | 'EVALUATED'

/** 面试模式 */
export type InterviewMode = 'TEXT'
