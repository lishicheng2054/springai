import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createResume } from '../api/resume'
import LoadingSpinner from '../components/LoadingSpinner'
import ErrorMessage from '../components/ErrorMessage'

/**
 * 简历输入页：填写姓名、岗位、简历文本。
 */
export default function ResumeInputPage() {
  const navigate = useNavigate()
  const [candidateName, setCandidateName] = useState('')
  const [targetPosition, setTargetPosition] = useState('')
  const [resumeText, setResumeText] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async () => {
    if (!candidateName.trim() || !targetPosition.trim() || !resumeText.trim()) {
      setError('请填写所有必填项')
      return
    }

    setLoading(true)
    setError(null)
    try {
      const result = await createResume({
        candidateName: candidateName.trim(),
        targetPosition: targetPosition.trim(),
        resumeText: resumeText.trim(),
        sourceType: 'TEXT',
      })
      // 跳转到面试页，携带 resumeId
      navigate(`/interview/${result.resumeId}`)
    } catch (e) {
      setError(e instanceof Error ? e.message : '创建简历失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <LoadingSpinner text="正在创建简历..." />
  if (error) return <ErrorMessage message={error} onRetry={() => setError(null)} />

  return (
    <div>
      <h2 className="text-2xl font-bold text-slate-800 mb-2">简历输入</h2>
      <p className="text-slate-500 mb-6">请输入你的简历信息，系统将根据简历内容智能生成面试题。</p>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 space-y-5">
        {/* 姓名 */}
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            姓名 <span className="text-red-400">*</span>
          </label>
          <input
            type="text"
            value={candidateName}
            onChange={(e) => setCandidateName(e.target.value)}
            placeholder="例如：张三"
            className="w-full px-4 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        {/* 岗位 */}
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            目标岗位 <span className="text-red-400">*</span>
          </label>
          <input
            type="text"
            value={targetPosition}
            onChange={(e) => setTargetPosition(e.target.value)}
            placeholder="例如：Java 后端开发"
            className="w-full px-4 py-2.5 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        {/* 简历文本 */}
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            简历内容 <span className="text-red-400">*</span>
          </label>
          <textarea
            value={resumeText}
            onChange={(e) => setResumeText(e.target.value)}
            placeholder="请粘贴或输入你的简历内容，包含工作经验、项目经历、技术栈等..."
            rows={10}
            className="w-full px-4 py-3 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-vertical"
          />
        </div>

        {/* 提交按钮 */}
        <button
          onClick={handleSubmit}
          className="w-full py-3 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 transition-colors shadow-sm"
        >
          开始面试
        </button>
      </div>
    </div>
  )
}
