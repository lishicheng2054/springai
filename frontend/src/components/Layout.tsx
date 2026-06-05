import { Outlet } from 'react-router-dom'

/**
 * 简单页面布局：顶部标题栏 + 内容区。
 */
export default function Layout() {
  return (
    <div className="min-h-screen bg-slate-50">
      {/* 顶部栏 */}
      <header className="bg-white shadow-sm border-b border-slate-200">
        <div className="max-w-3xl mx-auto px-4 py-4 flex items-center justify-between">
          <a href="/" className="text-xl font-bold text-slate-800 no-underline">
            SpringAI 模拟面试平台
          </a>
          <span className="text-sm text-slate-500">AI 驱动的面试训练助手</span>
        </div>
      </header>

      {/* 主内容 */}
      <main className="max-w-3xl mx-auto px-4 py-8">
        <Outlet />
      </main>

      {/* 底部 */}
      <footer className="text-center py-6 text-sm text-slate-400">
        SpringAI 模拟面试平台 · MVP v0.1
      </footer>
    </div>
  )
}
