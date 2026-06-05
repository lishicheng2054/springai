import { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getKb, listDocuments, uploadDocument, deleteDocument } from '../api/knowledgebase'
import type { KbResponse, DocumentResponse } from '../types/knowledgebase'
import LoadingSpinner from '../components/LoadingSpinner'
import ErrorMessage from '../components/ErrorMessage'

export default function KnowledgeBaseDetailPage() {
  const { kbId } = useParams<{ kbId: string }>()
  const navigate = useNavigate()
  const [kb, setKb] = useState<KbResponse | null>(null)
  const [docs, setDocs] = useState<DocumentResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [uploading, setUploading] = useState(false)

  const fetchData = useCallback(async () => {
    if (!kbId) return
    setLoading(true); setError(null)
    try {
      const [kbData, docData] = await Promise.all([getKb(Number(kbId)), listDocuments(Number(kbId))])
      setKb(kbData); setDocs(docData)
    } catch (e) { setError(e instanceof Error ? e.message : '加载失败') }
    finally { setLoading(false) }
  }, [kbId])

  useEffect(() => { fetchData() }, [fetchData])

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file || !kbId) return
    setUploading(true); setError(null)
    try { await uploadDocument(Number(kbId), file); await fetchData() }
    catch (err) { setError(err instanceof Error ? err.message : '上传失败') }
    finally { setUploading(false) }
  }

  const handleDeleteDoc = async (docId: number) => {
    if (!kbId || !confirm('确定删除该文档？')) return
    try { await deleteDocument(Number(kbId), docId); await fetchData() }
    catch (e) { setError(e instanceof Error ? e.message : '删除失败') }
  }

  if (loading) return <LoadingSpinner text="加载文档..." />
  if (error) return <ErrorMessage message={error} onRetry={fetchData} />

  return (
    <div>
      <div className="flex items-center gap-3 mb-6">
        <button onClick={() => navigate('/kb')} className="text-slate-400 hover:text-slate-600">← 返回</button>
        <h2 className="text-2xl font-bold text-slate-800">{kb?.name}</h2>
        <span className="text-sm text-slate-500">{docs.length} 篇文档</span>
      </div>

      {/* 上传 */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-4 mb-6">
        <label className="flex items-center gap-3 cursor-pointer">
          <span className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">
            {uploading ? '上传中...' : '选择文件'}
          </span>
          <span className="text-sm text-slate-500">支持 PDF、Word、TXT，最大 20MB</span>
          <input type="file" onChange={handleUpload} className="hidden" accept=".pdf,.doc,.docx,.txt" />
        </label>
      </div>

      {/* 文档列表 */}
      {docs.length === 0 ? (
        <div className="text-center py-16 text-slate-400">暂无文档，上传一个试试</div>
      ) : (
        <div className="space-y-3">
          {docs.map(doc => (
            <div key={doc.id} className="bg-white rounded-xl shadow-sm border border-slate-200 p-4 flex justify-between items-start">
              <div className="flex-1">
                <div className="font-medium text-slate-800 text-sm">{doc.fileName}</div>
                <div className="text-xs text-slate-400 mt-1">
                  {doc.contentType} · {doc.fileSize ? (doc.fileSize / 1024).toFixed(1) + 'KB' : '未知大小'}
                </div>
                <div className="text-xs text-slate-500 mt-2 line-clamp-2">{doc.contentPreview}</div>
              </div>
              <button
                onClick={() => handleDeleteDoc(doc.id)}
                className="text-red-400 hover:text-red-600 text-sm ml-4 mt-1"
              >
                删除
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
