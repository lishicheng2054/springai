export interface KbResponse {
  id: number
  name: string
  description: string
  status: string
  docCount: number
  createdAt: string
}

export interface DocumentResponse {
  id: number
  kbId: number
  fileName: string
  contentType: string
  fileSize: number
  contentPreview: string
  parseStatus: string
  createdAt: string
}

export interface ChatResponse {
  answer: string
  sources: {
    documentId: number
    fileName: string
    snippet: string
  }[]
}
