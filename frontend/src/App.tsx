import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import HomePage from './pages/HomePage'
import ResumeInputPage from './pages/ResumeInputPage'
import InterviewPage from './pages/InterviewPage'
import ResultPage from './pages/ResultPage'
import KnowledgeBaseListPage from './pages/KnowledgeBaseListPage'
import KnowledgeBaseDetailPage from './pages/KnowledgeBaseDetailPage'
import KnowledgeBaseChatPage from './pages/KnowledgeBaseChatPage'
import InterviewSchedulePage from './pages/InterviewSchedulePage'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="resume" element={<ResumeInputPage />} />
        <Route path="interview/:resumeId" element={<InterviewPage />} />
        <Route path="result/:sessionId" element={<ResultPage />} />
        <Route path="kb" element={<KnowledgeBaseListPage />} />
        <Route path="kb/:kbId" element={<KnowledgeBaseDetailPage />} />
        <Route path="kb/:kbId/chat" element={<KnowledgeBaseChatPage />} />
        <Route path="schedule" element={<InterviewSchedulePage />} />
      </Route>
    </Routes>
  )
}
