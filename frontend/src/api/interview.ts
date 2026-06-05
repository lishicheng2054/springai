import request from './request'
import type {
  CreateSessionRequest,
  CreateSessionResponse,
  SubmitAnswerRequest,
  SubmitAnswerResponse,
  InterviewResultResponse,
} from '../types/interview'

export async function createSession(
  req: CreateSessionRequest
): Promise<CreateSessionResponse> {
  const { data } = await request.post<CreateSessionResponse>(
    '/api/interviews/sessions',
    req
  )
  return data
}

export async function submitAnswer(
  sessionId: string,
  req: SubmitAnswerRequest
): Promise<SubmitAnswerResponse> {
  const { data } = await request.post<SubmitAnswerResponse>(
    `/api/interviews/sessions/${sessionId}/answers`,
    req
  )
  return data
}

export async function getResult(
  sessionId: string
): Promise<InterviewResultResponse> {
  const { data } = await request.get<InterviewResultResponse>(
    `/api/interviews/sessions/${sessionId}/result`
  )
  return data
}
