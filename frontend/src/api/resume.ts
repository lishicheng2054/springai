import request from './request'
import type { ResumeRequest, ResumeResponse } from '../types/resume'

export async function createResume(
  req: ResumeRequest
): Promise<{ resumeId: number }> {
  const { data } = await request.post<{ resumeId: number }>('/api/resumes', req)
  return data
}

export async function getResume(id: number): Promise<ResumeResponse> {
  const { data } = await request.get<ResumeResponse>(`/api/resumes/${id}`)
  return data
}

export async function listResumes(): Promise<ResumeResponse[]> {
  const { data } = await request.get<ResumeResponse[]>('/api/resumes')
  return data
}
