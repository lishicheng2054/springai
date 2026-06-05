/** 创建简历请求 */
export interface ResumeRequest {
  candidateName: string
  targetPosition: string
  resumeText: string
  sourceType?: string
}

/** 简历响应 */
export interface ResumeResponse {
  id: number
  candidateName: string
  targetPosition: string
  resumeText: string
  sourceType: string
  createdAt: string
}
