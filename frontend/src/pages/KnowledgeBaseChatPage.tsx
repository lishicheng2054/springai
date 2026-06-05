import { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getKb, chatWithKb } from '../api/knowledgebase'
import type { KbResponse, ChatResponse } from '../types/knowledgebase'
import LoadingSpinner from '../components/LoadingSpinner'

export default function KnowledgeBaseChatPage() {
  const { kbId } = useParams<{ kbId: string }>()
  const navigate = useNavigate()
  const [kb, setKb] = useState<KbResponse | null>(null)
  const [question, setQuestion] = useState('')
  const [result, setResult] = useState<ChatResponse | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (kbId) getKb(Number(kbId)).then(setKb).catch(() => {})
  }, [kbId])

  const handleAsk = async () => {
    if (!kbId || !question.trim()) return
    setLoading(true); setError(null); setResult(null)
    try {
      const r = await chatWithKb(Number(kbId), question.trim())
      setResult(r)
    } catch (e) {
      setError(e instanceof Error ? e.message : '问答失败')
    } finally { setLoading(false) }
  }

  return (
    <div>
      <div className="flex items-center gap-3 mb-6">
        <button onClick={() => navigate(`/kb/${kbId}`)} className="text-slate-400 hover:text-slate-600">← 返回</button>
        <h2 className="text-2xl font-bold text-slate-800">{kb?.name} · 问答</h2>
      </div>

      {/* 输入区 */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-4 mb-6 flex gap-3">
        <input
          value={question}
          onChange={e => setQuestion(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleAsk()}
          placeholder="输入问题，基于知识库内容回答..."
          className="flex-1 px-3 py-2 border border-slate-300 rounded-lg text-sm"
        />
        <button
          onClick={handleAsk}
          disabled={loading || !question.trim()}
          className="px-6 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700 disabled:bg-slate-300"
        >
          {loading ? '思考中...' : '提问'}
        </button>
      </div>

      {/* 错误 */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-4 text-sm text-red-600">{error}</div>
      )}

      {/* 加载 */}
      {loading && <LoadingSpinner text="AI 正在分析文档..." />}

      {/* 结果 */}
      {result && (
        <div className="space-y-4">
          <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
            <h3 className="font-semibold text-slate-700 mb-3">回答</h3>
            <p className="text-slate-700 leading-relaxed whitespace-pre-wrap">{result.answer}</p>
          </div>

          {result.sources.length > 0 && (
            <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
              <h3 className="font-semibold text-slate-700 mb-3">参考来源</h3>
              <div className="space-y-3">
                {result.sources.map((s, i) => (
                  <div key={i} className="border-l-2 border-blue-300 pl-3">
                    <div className="text-xs font-medium text-blue-600">{s.fileName}</div>
                    <div className="text-xs text-slate-500 mt-1">{s.snippet}</div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
