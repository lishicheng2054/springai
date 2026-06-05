import { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { createSession, submitAnswer } from '../api/interview'
import type { CreateSessionResponse, SubmitAnswerResponse } from '../types/interview'
import LoadingSpinner from '../components/LoadingSpinner'
import ErrorMessage from '../components/ErrorMessage'

/**
 * 面试页：展示题目、输入回答、查看进度。
 */
export default function InterviewPage() {
  const { resumeId } = useParams<{ resumeId: string }>()
  const navigate = useNavigate()

  const [session, setSession] = useState<CreateSessionResponse | null>(null)
  const [answerText, setAnswerText] = useState('')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const startSession = useCallback(async () => {
    if (!resumeId) return
    setLoading(true)
    setError(null)
    try {
      const data = await createSession({
        resumeId: Number(resumeId),
        roleType: 'JAVA_BACKEND',
        questionCount: 5,
      })
      setSession(data)
    } catch (e) {
      setError(e instanceof Error ? e.message : '创建面试会话失败')
    } finally {
      setLoading(false)
    }
  }, [resumeId])

  useEffect(() => {
    startSession()
  }, [startSession])

  const handleSubmit = async () => {
    if (!session || !answerText.trim()) return
    const currentQuestion = session.firstQuestion
    if (!currentQuestion) return

    setSubmitting(true)
    setError(null)
    try {
      const result: SubmitAnswerResponse = await submitAnswer(session.sessionId, {
        questionId: currentQuestion.questionId,
        answerText: answerText.trim(),
      })

      if (result.hasNext && result.nextQuestion) {
        setSession({
          ...session,
          firstQuestion: result.nextQuestion,
          currentStep: session.currentStep + 1,
        })
      } else {
        navigate(`/result/${session.sessionId}`)
      }
      setAnswerText('')
    } catch (e) {
      setError(e instanceof Error ? e.message : '提交答案失败')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <LoadingSpinner text="正在生成面试题..." />
  if (error) return <ErrorMessage message={error} onRetry={startSession} />
  if (!session) return null

  return (
    <div>
      {/* 进度条 */}
      <div className="mb-6">
        <div className="flex justify-between text-sm text-slate-500 mb-2">
          <span>第 {session.currentStep} / {session.totalStep} 题</span>
          <span>{Math.round((session.currentStep / session.totalStep) * 100)}%</span>
        </div>
        <div className="w-full bg-slate-200 rounded-full h-2">
          <div
            className="bg-blue-600 h-2 rounded-full transition-all duration-300"
            style={{ width: `${(session.currentStep / session.totalStep) * 100}%` }}
          />
        </div>
      </div>

      {/* 题目 */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 mb-6">
        <div className="text-xs text-blue-600 font-medium mb-3 uppercase tracking-wide">
          当前问题
        </div>
        <p className="text-lg text-slate-800 leading-relaxed">
          {session.firstQuestion.content}
        </p>
      </div>

      {/* 答案输入 */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
        <label className="block text-sm font-medium text-slate-700 mb-2">
          你的回答
        </label>
        <textarea
          value={answerText}
          onChange={(e) => setAnswerText(e.target.value)}
          placeholder="请输入你的回答...（内容越详细，评分越准确）"
          rows={6}
          className="w-full px-4 py-3 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-vertical mb-4"
          disabled={submitting}
        />
        <button
          onClick={handleSubmit}
          disabled={submitting || !answerText.trim()}
          className="w-full py-3 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 disabled:bg-slate-300 disabled:cursor-not-allowed transition-colors shadow-sm"
        >
          {submitting ? '提交中...' : '提交答案'}
        </button>
      </div>
    </div>
  )
}
