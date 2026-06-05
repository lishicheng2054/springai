import { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getResult } from '../api/interview'
import type { InterviewResultResponse } from '../types/interview'
import LoadingSpinner from '../components/LoadingSpinner'
import ErrorMessage from '../components/ErrorMessage'

/**
 * 结果页：展示总分、各维度评分、优点、建议。
 */
export default function ResultPage() {
  const { sessionId } = useParams<{ sessionId: string }>()
  const navigate = useNavigate()

  const [result, setResult] = useState<InterviewResultResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchResult = useCallback(async () => {
    if (!sessionId) return
    setLoading(true)
    setError(null)
    try {
      const data = await getResult(sessionId)
      setResult(data)
    } catch (e) {
      setError(e instanceof Error ? e.message : '获取结果失败')
    } finally {
      setLoading(false)
    }
  }, [sessionId])

  useEffect(() => {
    fetchResult()
  }, [fetchResult])

  if (loading) return <LoadingSpinner text="正在加载评估结果..." />
  if (error) return <ErrorMessage message={error} onRetry={fetchResult} />
  if (!result) return null

  const scoreColor = (score: number) => {
    if (score >= 75) return 'text-green-600'
    if (score >= 50) return 'text-yellow-600'
    return 'text-red-500'
  }

  return (
    <div>
      <h2 className="text-2xl font-bold text-slate-800 mb-2 text-center">面试评估报告</h2>
      <p className="text-slate-500 mb-8 text-center">
        状态：{result.status === 'EVALUATED' ? '✅ 已完成' : result.status}
      </p>

      {/* 总分 */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-8 mb-6 text-center">
        <div className="text-sm text-slate-500 mb-2">综合总分</div>
        <div className={`text-6xl font-bold ${scoreColor(result.totalScore)}`}>
          {result.totalScore}
        </div>
        <div className="text-sm text-slate-400 mt-1">满分 100</div>
      </div>

      {/* 各维度 */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        {[
          { label: '技术能力', score: result.techScore },
          { label: '沟通表达', score: result.communicationScore },
          { label: '逻辑思维', score: result.logicScore },
        ].map((dim) => (
          <div key={dim.label} className="bg-white rounded-xl shadow-sm border border-slate-200 p-4 text-center">
            <div className="text-xs text-slate-500 mb-1">{dim.label}</div>
            <div className={`text-2xl font-bold ${scoreColor(dim.score)}`}>{dim.score}</div>
          </div>
        ))}
      </div>

      {/* 总结 */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 mb-6">
        <h3 className="font-semibold text-slate-700 mb-3">总结评语</h3>
        <p className="text-slate-600 leading-relaxed">{result.summary}</p>
      </div>

      {/* 优点/不足/建议 */}
      <div className="space-y-4 mb-8">
        {[
          { title: '优点', items: result.strengths, color: 'text-green-700', icon: '✅' },
          { title: '不足', items: result.weaknesses, color: 'text-amber-700', icon: '⚠️' },
          { title: '改进建议', items: result.improvements, color: 'text-blue-700', icon: '💡' },
        ].map((section) => (
          <div key={section.title} className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
            <h3 className="font-semibold text-slate-700 mb-3">{section.icon} {section.title}</h3>
            <ul className="space-y-2">
              {section.items.map((item, idx) => (
                <li key={idx} className={`text-sm ${section.color} flex gap-2`}>
                  <span>•</span> <span>{item}</span>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>

      <button
        onClick={() => navigate('/')}
        className="w-full py-3 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 transition-colors shadow-sm"
      >
        返回首页
      </button>
    </div>
  )
}
