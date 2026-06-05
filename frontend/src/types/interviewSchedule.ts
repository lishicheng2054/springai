export type ScheduleStatus = 'PENDING' | 'READY' | 'DONE' | 'CANCELLED'

export interface ScheduleResponse {
  id: number
  title: string
  companyName: string
  roleType: string
  startTime: string
  endTime: string
  status: ScheduleStatus
  location: string
  meetingLink: string
  notes: string
  createdAt: string
}

export interface CreateScheduleRequest {
  title: string
  companyName: string
  roleType: string
  startTime: string
  endTime: string
  location: string
  meetingLink: string
  notes: string
}
