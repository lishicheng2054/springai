import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createResume, uploadResume } from '../api/resume'
import LoadingSpinner from '../components/LoadingSpinner'
import ErrorMessage from '../components/ErrorMessage'

/**
 * 简历输入页：支持文本输入和文件上传两种方式。
 */
export default function ResumeInputPage() {
  const navigate = useNavigate()
  const [mode, setMode] = useState<'text' | 'file'>('text')
  const [candidateName, setCandidateName] = useState('')
  const [targetPosition, setTargetPosition] = useState('')
  const [resumeText, setResumeText] = useState('')
  const [file, setFile] = useState<File | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleTextSubmit = async () => {
    if (!candidateName.trim() || !targetPosition.trim() || !resumeText.trim()) {
      setError('请填写所有必填项'); return
    }
    setLoading(true); setError(null)
    try {
      const result = await createResume({
        candidateName: candidateName.trim(), targetPosition: targetPosition.trim(),
        resumeText: resumeText.trim(), sourceType: 'TEXT',
      })
      navigate(`/interview/${result.resumeId}`)
    } catch (e) { setError(e instanceof Error ? e.message : '创建失败') }
    finally { setLoading(false) }
  }

  const handleFileSubmit = async () => {
    if (!candidateName.trim() || !targetPosition.trim() || !file) {
      setError('请填写姓名、岗位并选择文件'); return
    }
    setLoading(true); setError(null)
    try {
      const result = await uploadResume(candidateName.trim(), targetPosition.trim(), file)
      navigate(`/interview/${result.id}`)
    } catch (e) { setError(e instanceof Error ? e.message : '上传失败') }
    finally { setLoading(false) }
  }

  if (loading) return <LoadingSpinner text="正在处理简历..." />
  if (error) return <ErrorMessage message={error} onRetry={() => setError(null)} />

  return (
    <div>
      <h2 className="text-2xl font-bold text-slate-800 mb-2">简历输入</h2>
      <p className="text-slate-500 mb-6">录入简历，AI自动生成面试题并给出分析建议。</p>

      {/* 模式切换 */}
      <div className="flex gap-2 mb-6">
        {(['text', 'file'] as const).map(m => (
          <button key={m}
            onClick={() => setMode(m)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${mode === m ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'}`}
          >
            {m === 'text' ? '📝 文本输入' : '📎 文件上传'}
          </button>
        ))}
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 space-y-5">
        {/* 姓名 + 岗位（两种模式共用） */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">姓名 *</label>
            <input type="text" value={candidateName}
              onChange={e => setCandidateName(e.target.value)}
              placeholder="例如：张三"
              className="w-full px-3 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">目标岗位 *</label>
            <input type="text" value={targetPosition}
              onChange={e => setTargetPosition(e.target.value)}
              placeholder="例如：Java 后端开发"
              className="w-full px-3 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
        </div>

        {mode === 'text' ? (
          <>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">简历内容 *</label>
              <textarea value={resumeText}
                onChange={e => setResumeText(e.target.value)}
                placeholder="粘贴或输入简历内容，包含工作经验、项目经历、技术栈等..."
                rows={10}
                className="w-full px-3 py-3 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-vertical" />
            </div>
            <button onClick={handleTextSubmit}
              className="w-full py-3 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 transition-colors">
              开始面试
            </button>
          </>
        ) : (
          <>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">上传简历文件 *</label>
              <div className="border-2 border-dashed border-slate-300 rounded-lg p-8 text-center hover:border-blue-400 transition-colors">
                <input type="file" accept=".pdf,.doc,.docx,.txt"
                  onChange={e => setFile(e.target.files?.[0] || null)}
                  className="text-sm text-slate-600 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" />
                <p className="text-xs text-slate-400 mt-3">
                  {file ? `已选择: ${file.name} (${(file.size / 1024).toFixed(1)} KB)` : '支持 PDF、Word、TXT，最大 20MB'}
                </p>
              </div>
            </div>
            <button onClick={handleFileSubmit}
              disabled={!file}
              className="w-full py-3 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 disabled:bg-slate-300 transition-colors">
              上传并开始面试
            </button>
          </>
        )}
      </div>
    </div>
  )
}
