import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { listKbs, createKb, deleteKb } from '../api/knowledgebase'
import type { KbResponse } from '../types/knowledgebase'
import LoadingSpinner from '../components/LoadingSpinner'
import ErrorMessage from '../components/ErrorMessage'

export default function KnowledgeBaseListPage() {
  const navigate = useNavigate()
  const [kbs, setKbs] = useState<KbResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [name, setName] = useState('')
  const [desc, setDesc] = useState('')
  const [creating, setCreating] = useState(false)

  const fetchKbs = useCallback(async () => {
    setLoading(true)
    setError(null)
    try { setKbs(await listKbs()) }
    catch (e) { setError(e instanceof Error ? e.message : '加载失败') }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchKbs() }, [fetchKbs])

  const handleCreate = async () => {
    if (!name.trim()) return
    setCreating(true)
    try {
      await createKb({ name: name.trim(), description: desc.trim() })
      setName('')
      setDesc('')
      await fetchKbs()
    } catch (e) {
      setError(e instanceof Error ? e.message : '创建失败')
    } finally { setCreating(false) }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('确定删除该知识库？所有文档将被永久删除。')) return
    try { await deleteKb(id); await fetchKbs() }
    catch (e) { setError(e instanceof Error ? e.message : '删除失败') }
  }

  if (loading) return <LoadingSpinner text="加载知识库..." />
  if (error) return <ErrorMessage message={error} onRetry={fetchKbs} />

  return (
    <div>
      <h2 className="text-2xl font-bold text-slate-800 mb-2">知识库管理</h2>
      <p className="text-slate-500 mb-6">上传文档，基于知识库进行智能问答。</p>

      {/* 创建表单 */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-4 mb-6 flex gap-3">
        <input
          value={name}
          onChange={e => setName(e.target.value)}
          placeholder="知识库名称"
          className="flex-1 px-3 py-2 border border-slate-300 rounded-lg text-sm"
        />
        <input
          value={desc}
          onChange={e => setDesc(e.target.value)}
          placeholder="描述（可选）"
          className="flex-1 px-3 py-2 border border-slate-300 rounded-lg text-sm"
        />
        <button
          onClick={handleCreate}
          disabled={creating || !name.trim()}
          className="px-6 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700 disabled:bg-slate-300"
        >
          {creating ? '创建中...' : '创建'}
        </button>
      </div>

      {/* 列表 */}
      {kbs.length === 0 ? (
        <div className="text-center py-16 text-slate-400">暂无知识库，创建一个开始吧</div>
      ) : (
        <div className="space-y-3">
          {kbs.map(kb => (
            <div
              key={kb.id}
              className="bg-white rounded-xl shadow-sm border border-slate-200 p-5 flex items-center justify-between hover:border-blue-300 transition-colors"
            >
              <div className="flex-1 cursor-pointer" onClick={() => navigate(`/kb/${kb.id}`)}>
                <h3 className="font-semibold text-slate-800">{kb.name}</h3>
                <p className="text-sm text-slate-500 mt-1">
                  {kb.description || '暂无描述'} · {kb.docCount} 篇文档
                </p>
              </div>
              <div className="flex gap-2">
                <button
                  onClick={() => navigate(`/kb/${kb.id}`)}
                  className="px-3 py-1.5 bg-slate-100 text-slate-600 rounded-lg text-sm hover:bg-slate-200"
                >
                  管理
                </button>
                <button
                  onClick={() => navigate(`/kb/${kb.id}/chat`)}
                  className="px-3 py-1.5 bg-blue-50 text-blue-600 rounded-lg text-sm hover:bg-blue-100"
                >
                  问答
                </button>
                <button
                  onClick={() => handleDelete(kb.id)}
                  className="px-3 py-1.5 text-red-500 rounded-lg text-sm hover:bg-red-50"
                >
                  删除
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
