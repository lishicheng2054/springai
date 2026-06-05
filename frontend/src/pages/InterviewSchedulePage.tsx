import { useState, useEffect, useCallback } from 'react'
import { listSchedules, createSchedule, updateStatus, deleteSchedule } from '../api/interviewSchedule'
import type { ScheduleResponse, ScheduleStatus } from '../types/interviewSchedule'
import LoadingSpinner from '../components/LoadingSpinner'
import ErrorMessage from '../components/ErrorMessage'

const STATUS_LABELS: Record<ScheduleStatus, { label: string; color: string }> = {
  PENDING:  { label: '待面试', color: 'bg-yellow-100 text-yellow-700' },
  READY:    { label: '已准备', color: 'bg-blue-100 text-blue-700' },
  DONE:     { label: '已完成', color: 'bg-green-100 text-green-700' },
  CANCELLED:{ label: '已取消', color: 'bg-slate-100 text-slate-500' },
}

export default function InterviewSchedulePage() {
  const [schedules, setSchedules] = useState<ScheduleResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ title: '', companyName: '', roleType: '', startTime: '', endTime: '', location: '', meetingLink: '', notes: '' })

  const fetch = useCallback(async () => {
    setLoading(true); setError(null)
    try { setSchedules(await listSchedules()) }
    catch (e) { setError(e instanceof Error ? e.message : '加载失败') }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetch() }, [fetch])

  const handleCreate = async () => {
    if (!form.title || !form.startTime) return
    try {
      await createSchedule({
        title: form.title, companyName: form.companyName, roleType: form.roleType,
        startTime: form.startTime, endTime: form.endTime || form.startTime,
        location: form.location, meetingLink: form.meetingLink, notes: form.notes,
      })
      setShowForm(false); setForm({ title: '', companyName: '', roleType: '', startTime: '', endTime: '', location: '', meetingLink: '', notes: '' })
      await fetch()
    } catch (e) { setError(e instanceof Error ? e.message : '创建失败') }
  }

  const handleStatus = async (id: number, status: ScheduleStatus) => {
    try { await updateStatus(id, status); await fetch() }
    catch (e) { setError(e instanceof Error ? e.message : '更新失败') }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('确定删除？')) return
    try { await deleteSchedule(id); await fetch() }
    catch (e) { setError(e instanceof Error ? e.message : '删除失败') }
  }

  if (loading) return <LoadingSpinner text="加载面试安排..." />
  if (error) return <ErrorMessage message={error} onRetry={fetch} />

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-800">面试安排</h2>
          <p className="text-slate-500 text-sm">管理你的面试日程</p>
        </div>
        <button onClick={() => setShowForm(!showForm)}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">
          {showForm ? '取消' : '+ 新建'}
        </button>
      </div>

      {/* 创建表单 */}
      {showForm && (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-4 mb-6 grid grid-cols-2 gap-3">
          <input placeholder="面试标题 *" value={form.title} onChange={e => setForm({...form, title: e.target.value})}
            className="col-span-2 px-3 py-2 border border-slate-300 rounded-lg text-sm" />
          <input placeholder="公司名称" value={form.companyName} onChange={e => setForm({...form, companyName: e.target.value})}
            className="px-3 py-2 border border-slate-300 rounded-lg text-sm" />
          <input placeholder="岗位" value={form.roleType} onChange={e => setForm({...form, roleType: e.target.value})}
            className="px-3 py-2 border border-slate-300 rounded-lg text-sm" />
          <input type="datetime-local" value={form.startTime} onChange={e => setForm({...form, startTime: e.target.value})}
            className="px-3 py-2 border border-slate-300 rounded-lg text-sm" />
          <input type="datetime-local" value={form.endTime} onChange={e => setForm({...form, endTime: e.target.value})}
            className="px-3 py-2 border border-slate-300 rounded-lg text-sm" />
          <input placeholder="地点" value={form.location} onChange={e => setForm({...form, location: e.target.value})}
            className="px-3 py-2 border border-slate-300 rounded-lg text-sm" />
          <input placeholder="会议链接" value={form.meetingLink} onChange={e => setForm({...form, meetingLink: e.target.value})}
            className="px-3 py-2 border border-slate-300 rounded-lg text-sm" />
          <input placeholder="备注" value={form.notes} onChange={e => setForm({...form, notes: e.target.value})}
            className="col-span-2 px-3 py-2 border border-slate-300 rounded-lg text-sm" />
          <button onClick={handleCreate}
            className="col-span-2 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">
            创建
          </button>
        </div>
      )}

      {/* 列表 */}
      {schedules.length === 0 ? (
        <div className="text-center py-16 text-slate-400">暂无面试安排</div>
      ) : (
        <div className="space-y-3">
          {schedules.map(s => {
            const st = STATUS_LABELS[s.status] || STATUS_LABELS.PENDING
            return (
              <div key={s.id} className="bg-white rounded-xl shadow-sm border border-slate-200 p-4">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <h3 className="font-semibold text-slate-800">{s.title}</h3>
                      <span className={`text-xs px-2 py-0.5 rounded-full ${st.color}`}>{st.label}</span>
                    </div>
                    <div className="text-sm text-slate-500 mt-1">
                      {s.companyName}{s.roleType && ` · ${s.roleType}`}
                    </div>
                    <div className="text-xs text-slate-400 mt-1">
                      {new Date(s.startTime).toLocaleString('zh-CN')}
                      {s.endTime && ` ~ ${new Date(s.endTime).toLocaleString('zh-CN')}`}
                      {s.location && ` · ${s.location}`}
                    </div>
                    {s.meetingLink && (
                      <a href={s.meetingLink} target="_blank" rel="noreferrer"
                        className="text-xs text-blue-500 mt-1 inline-block">🔗 会议链接</a>
                    )}
                    {s.notes && <div className="text-xs text-slate-400 mt-1">{s.notes}</div>}
                  </div>
                  <div className="flex gap-1 ml-4">
                    {s.status === 'PENDING' && (
                      <button onClick={() => handleStatus(s.id, 'READY')}
                        className="px-2 py-1 text-xs bg-blue-50 text-blue-600 rounded hover:bg-blue-100">准备</button>
                    )}
                    {(s.status === 'PENDING' || s.status === 'READY') && (
                      <button onClick={() => handleStatus(s.id, 'DONE')}
                        className="px-2 py-1 text-xs bg-green-50 text-green-600 rounded hover:bg-green-100">完成</button>
                    )}
                    {s.status !== 'CANCELLED' && (
                      <button onClick={() => handleStatus(s.id, 'CANCELLED')}
                        className="px-2 py-1 text-xs bg-red-50 text-red-500 rounded hover:bg-red-100">取消</button>
                    )}
                    <button onClick={() => handleDelete(s.id)}
                      className="px-2 py-1 text-xs text-slate-400 hover:text-red-500">删除</button>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
