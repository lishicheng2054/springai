import request from './request'
import type { KbResponse, DocumentResponse, ChatResponse } from '../types/knowledgebase'

export async function createKb(data: { name: string; description?: string }): Promise<{ kbId: number }> {
  const r = await request.post<{ kbId: number }>('/api/knowledgebases', data)
  return r.data
}

export async function listKbs(): Promise<KbResponse[]> {
  const r = await request.get<KbResponse[]>('/api/knowledgebases')
  return r.data
}

export async function getKb(kbId: number): Promise<KbResponse> {
  const r = await request.get<KbResponse>(`/api/knowledgebases/${kbId}`)
  return r.data
}

export async function deleteKb(kbId: number): Promise<void> {
  await request.delete(`/api/knowledgebases/${kbId}`)
}

export async function uploadDocument(kbId: number, file: File): Promise<DocumentResponse> {
  const formData = new FormData()
  formData.append('file', file)
  const r = await request.post<DocumentResponse>(`/api/knowledgebases/${kbId}/documents`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000,
  })
  return r.data
}

export async function listDocuments(kbId: number): Promise<DocumentResponse[]> {
  const r = await request.get<DocumentResponse[]>(`/api/knowledgebases/${kbId}/documents`)
  return r.data
}

export async function deleteDocument(kbId: number, docId: number): Promise<void> {
  await request.delete(`/api/knowledgebases/${kbId}/documents/${docId}`)
}

export async function chatWithKb(kbId: number, question: string): Promise<ChatResponse> {
  const r = await request.post<ChatResponse>(`/api/knowledgebases/${kbId}/chat`, { question })
  return r.data
}
