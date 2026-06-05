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
  fileName?: string
  fileType?: string
  fileSize?: number
  createdAt: string
}

/** AI简历分析结果 */
export interface ResumeAnalysisResponse {
  resumeId: number
  summary: string
  skills: string[]
  advantages: string[]
  risks: string[]
  suggestions: string[]
  analysisStatus: string
}
