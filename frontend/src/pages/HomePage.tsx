import { useNavigate } from 'react-router-dom'

/**
 * 首页：展示产品介绍和入口按钮。
 */
export default function HomePage() {
  const navigate = useNavigate()

  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <div className="text-6xl mb-6">🎯</div>
      <h1 className="text-3xl font-bold text-slate-800 mb-4">
        AI 驱动的模拟面试平台
      </h1>
      <p className="text-slate-500 max-w-md mb-10 leading-relaxed">
        输入简历，AI 自动生成面试题。逐题作答，获取智能评分和改进建议。
        无需注册，即刻开始你的面试训练之旅。
      </p>

      <div className="flex flex-col gap-3 w-64">
        <button
          onClick={() => navigate('/resume')}
          className="w-full px-6 py-3 bg-blue-600 text-white rounded-xl text-lg font-medium hover:bg-blue-700 transition-colors shadow-sm"
        >
          开始面试
        </button>
        <button
          onClick={() => navigate('/kb')}
          className="w-full px-6 py-3 bg-slate-100 text-slate-700 rounded-xl text-sm font-medium hover:bg-slate-200 transition-colors"
        >
          知识库管理
        </button>
      </div>

      <div className="mt-16 grid grid-cols-4 gap-6 text-center">
        {[
          { icon: '📝', title: '简历输入', desc: '粘贴或输入你的简历' },
          { icon: '🤖', title: 'AI 出题', desc: '根据简历智能生成面试题' },
          { icon: '📊', title: '评分反馈', desc: '逐题评分 + 综合建议' },
          { icon: '📚', title: '知识库', desc: '文档上传 + AI 问答' },
        ].map((item) => (
          <div key={item.title}>
            <div className="text-3xl mb-2">{item.icon}</div>
            <div className="font-medium text-slate-700 text-sm">{item.title}</div>
            <div className="text-xs text-slate-400 mt-1">{item.desc}</div>
          </div>
        ))}
      </div>
    </div>
  )
}
