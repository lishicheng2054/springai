import axios from 'axios'

const TOKEN_KEY = 'sky_token'

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export async function agentChat(sessionId, message) {
  const res = await axios.post('/user/agent/chat', { sessionId, message }, {
    headers: { authentication: getToken() },
    timeout: 30000
  })
  if (res.data.code === 1) {
    return res.data.data
  }
  throw new Error(res.data.msg || '请求失败')
}
