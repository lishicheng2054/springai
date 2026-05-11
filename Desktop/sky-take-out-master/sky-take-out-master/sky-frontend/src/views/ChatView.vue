<template>
  <div class="chat-view">
    <!-- 顶部标题栏 -->
    <header class="chat-header">
      <div class="header-left">
        <div class="bot-avatar">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <rect x="5" y="8" width="14" height="10" rx="3" fill="white"/>
            <circle cx="9" cy="13" r="1.5" fill="#FF6B35"/>
            <circle cx="15" cy="13" r="1.5" fill="#FF6B35"/>
            <rect x="10" y="4" width="4" height="4" rx="1" fill="white"/>
            <line x1="12" y1="4" x2="12" y2="8" stroke="white" stroke-width="1.5"/>
          </svg>
        </div>
        <div>
          <div class="header-title">AI 点餐助手</div>
          <div class="header-subtitle">智能推荐 · 快速下单</div>
        </div>
      </div>
      <button class="clear-btn" @click="clearMessages" title="清空对话">
        <svg width="18" height="18" viewBox="0 0 20 20" fill="none">
          <path d="M4 6h12M8 6V4h4v2M7 9v6M13 9v6M5 6l1 10h8l1-10H5z"
                stroke="white" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
      </button>
    </header>

    <!-- Token 输入（首次使用） -->
    <div class="token-bar" v-if="!hasToken">
      <input v-model="tokenInput" class="token-input" placeholder="请输入用户 JWT Token（authentication）" />
      <button class="token-btn" @click="saveToken">确认</button>
    </div>

    <!-- 消息列表 -->
    <main class="message-list" ref="messageListRef">
      <div v-if="messages.length === 0" class="empty-hint">
        <div class="empty-icon">🍜</div>
        <div>你好！我是 AI 点餐助手</div>
        <div class="empty-sub">可以问我菜品推荐、帮你加购物车或查询订单</div>
      </div>

      <div v-for="msg in messages" :key="msg.id" class="message-item" :class="msg.role">
        <!-- AI 消息 -->
        <template v-if="msg.role === 'ai'">
          <div class="ai-avatar">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <rect x="5" y="8" width="14" height="10" rx="3" fill="white"/>
              <circle cx="9" cy="13" r="1.5" fill="#FF6B35"/>
              <circle cx="15" cy="13" r="1.5" fill="#FF6B35"/>
            </svg>
          </div>
          <div class="bubble-wrapper">
            <div class="bubble ai-bubble">
              <p class="bubble-text">{{ msg.content }}</p>
              <ToolCallCard
                v-for="(tc, idx) in msg.toolCalls"
                :key="idx"
                :toolCall="tc"
              />
            </div>
            <span class="timestamp">{{ msg.time }}</span>
          </div>
        </template>

        <!-- 用户消息 -->
        <template v-else>
          <div class="bubble-wrapper user-wrapper">
            <div class="bubble user-bubble">
              <p class="bubble-text">{{ msg.content }}</p>
            </div>
            <span class="timestamp">{{ msg.time }}</span>
          </div>
        </template>
      </div>

      <!-- 打字动画 -->
      <div class="message-item ai" v-if="isLoading">
        <div class="ai-avatar">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <rect x="5" y="8" width="14" height="10" rx="3" fill="white"/>
            <circle cx="9" cy="13" r="1.5" fill="#FF6B35"/>
            <circle cx="15" cy="13" r="1.5" fill="#FF6B35"/>
          </svg>
        </div>
        <div class="bubble ai-bubble typing-bubble">
          <span class="dot"></span>
          <span class="dot"></span>
          <span class="dot"></span>
        </div>
      </div>
    </main>

    <!-- 快捷提示 -->
    <div class="quick-replies">
      <button v-for="tip in quickTips" :key="tip" class="quick-chip"
              @click="sendQuickMessage(tip)" :disabled="isLoading">
        {{ tip }}
      </button>
    </div>

    <!-- 输入区域 -->
    <footer class="input-area">
      <input
        v-model="inputText"
        class="text-input"
        type="text"
        placeholder="问我任何关于菜品的问题..."
        maxlength="200"
        @keydown.enter="sendMessage"
        :disabled="isLoading"
      />
      <button class="send-btn" @click="sendMessage"
              :disabled="!inputText.trim() || isLoading">
        <svg width="18" height="18" viewBox="0 0 20 20" fill="none">
          <path d="M3 10L17 10M17 10L11 4M17 10L11 16"
                stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
    </footer>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import ToolCallCard from '../components/ToolCallCard.vue'
import { agentChat, setToken, getToken } from '../api/agent.js'

const messages = ref([])
const inputText = ref('')
const isLoading = ref(false)
const sessionId = ref('')
const messageListRef = ref(null)
const tokenInput = ref('')
const hasToken = ref(false)

const quickTips = ['推荐菜品', '查看我的订单', '帮我加宫保鸡丁']

onMounted(() => {
  hasToken.value = !!getToken()
})

function saveToken() {
  if (tokenInput.value.trim()) {
    setToken(tokenInput.value.trim())
    hasToken.value = true
    tokenInput.value = ''
  }
}

function getTime() {
  const now = new Date()
  return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || isLoading.value) return

  messages.value.push({ id: Date.now(), role: 'user', content: text, time: getTime() })
  inputText.value = ''
  isLoading.value = true
  await scrollToBottom()

  try {
    const data = await agentChat(sessionId.value, text)
    sessionId.value = data.sessionId
    messages.value.push({
      id: Date.now(),
      role: 'ai',
      content: data.reply,
      toolCalls: data.toolCalls || [],
      time: getTime()
    })
  } catch (e) {
    messages.value.push({
      id: Date.now(),
      role: 'ai',
      content: '抱歉，服务暂时不可用，请稍后再试。',
      toolCalls: [],
      time: getTime()
    })
  } finally {
    isLoading.value = false
    await scrollToBottom()
  }
}

function sendQuickMessage(tip) {
  inputText.value = tip
  sendMessage()
}

function clearMessages() {
  messages.value = []
  sessionId.value = ''
}

async function scrollToBottom() {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}
</script>

<style scoped>
:root {
  --primary: #FF6B35;
}

.chat-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f7f8fa;
}

/* 标题栏 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 16px;
  background: #FF6B35;
  box-shadow: 0 2px 8px rgba(255, 107, 53, 0.3);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.bot-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  line-height: 1.3;
}

.header-subtitle {
  font-size: 11px;
  color: rgba(255,255,255,0.75);
}

.clear-btn {
  width: 34px;
  height: 34px;
  border: none;
  background: rgba(255,255,255,0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.clear-btn:hover { background: rgba(255,255,255,0.25); }

/* Token 输入栏 */
.token-bar {
  display: flex;
  gap: 8px;
  padding: 8px 12px;
  background: #fff3ee;
  border-bottom: 1px solid #ffd4c2;
  flex-shrink: 0;
}

.token-input {
  flex: 1;
  height: 34px;
  padding: 0 10px;
  border: 1px solid #ffd4c2;
  border-radius: 6px;
  font-size: 12px;
  outline: none;
}

.token-btn {
  padding: 0 14px;
  height: 34px;
  background: #FF6B35;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
}

/* 消息列表 */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  scroll-behavior: smooth;
}

.empty-hint {
  text-align: center;
  color: #aaa;
  margin-top: 60px;
  font-size: 14px;
  line-height: 2;
}

.empty-icon { font-size: 40px; margin-bottom: 8px; }
.empty-sub { font-size: 12px; color: #bbb; }

/* 消息条目 */
.message-item {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.ai-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #FF6B35;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bubble-wrapper {
  display: flex;
  flex-direction: column;
  gap: 3px;
  max-width: 72%;
}

.user-wrapper { align-items: flex-end; }

.bubble {
  padding: 10px 14px;
  border-radius: 12px;
  word-break: break-word;
  line-height: 1.6;
}

.ai-bubble {
  background: #fff;
  color: #1a1a1a;
  font-size: 14px;
  border-radius: 4px 12px 12px 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.user-bubble {
  background: #FF6B35;
  color: #fff;
  font-size: 14px;
  border-radius: 12px 4px 12px 12px;
}

.bubble-text { margin: 0; white-space: pre-wrap; }

.timestamp {
  font-size: 11px;
  color: #bbb;
  padding: 0 4px;
}

/* 打字动画 */
.typing-bubble {
  padding: 14px 18px;
  display: flex;
  align-items: center;
  gap: 5px;
  min-width: 60px;
}

.dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #FF6B35;
  opacity: 0.6;
  animation: typing-bounce 1.2s infinite ease-in-out;
}

.dot:nth-child(1) { animation-delay: 0s; }
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing-bounce {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-6px); opacity: 1; }
}

/* 快捷提示 */
.quick-replies {
  display: flex;
  gap: 8px;
  padding: 6px 12px 8px;
  overflow-x: auto;
  flex-shrink: 0;
  background: #f7f8fa;
}

.quick-chip {
  flex-shrink: 0;
  padding: 5px 14px;
  background: #fff1eb;
  border: 1px solid #ffd4c2;
  border-radius: 16px;
  font-size: 12px;
  color: #FF6B35;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s;
}

.quick-chip:hover:not(:disabled) { background: #ffd4c2; }
.quick-chip:disabled { opacity: 0.5; cursor: not-allowed; }

/* 输入区域 */
.input-area {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: #fff;
  border-top: 1px solid #ebebeb;
  flex-shrink: 0;
}

.text-input {
  flex: 1;
  height: 44px;
  padding: 0 16px;
  background: #f7f8fa;
  border: 1px solid transparent;
  border-radius: 22px;
  font-size: 15px;
  color: #1a1a1a;
  outline: none;
  transition: border-color 0.2s;
}

.text-input::placeholder { color: #bbb; }
.text-input:focus { border-color: #ffd4c2; background: #fff; }
.text-input:disabled { opacity: 0.6; }

.send-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #FF6B35;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  transition: background 0.2s, transform 0.1s;
}

.send-btn:hover:not(:disabled) { background: #e55a25; }
.send-btn:active:not(:disabled) { transform: scale(0.92); }
.send-btn:disabled { background: #ddd; cursor: not-allowed; }
</style>
