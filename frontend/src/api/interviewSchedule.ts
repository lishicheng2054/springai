import request from './request'
import type { ScheduleResponse, ScheduleStatus, CreateScheduleRequest } from '../types/interviewSchedule'

export async function listSchedules(): Promise<ScheduleResponse[]> {
  const r = await request.get<ScheduleResponse[]>('/api/interview-schedules')
  return r.data
}

export async function createSchedule(data: CreateScheduleRequest): Promise<ScheduleResponse> {
  const r = await request.post<ScheduleResponse>('/api/interview-schedules', data)
  return r.data
}

export async function updateSchedule(id: number, data: Partial<CreateScheduleRequest>): Promise<ScheduleResponse> {
  const r = await request.put<ScheduleResponse>(`/api/interview-schedules/${id}`, data)
  return r.data
}

export async function updateStatus(id: number, status: ScheduleStatus): Promise<ScheduleResponse> {
  const r = await request.patch<ScheduleResponse>(`/api/interview-schedules/${id}/status`, { status })
  return r.data
}

export async function deleteSchedule(id: number): Promise<void> {
  await request.delete(`/api/interview-schedules/${id}`)
}
