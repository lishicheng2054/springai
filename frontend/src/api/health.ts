import request from './request'

export interface HealthStatus {
  status: string
}

export async function checkHealth(): Promise<HealthStatus> {
  const { data } = await request.get<HealthStatus>('/api/health')
  return data
}
