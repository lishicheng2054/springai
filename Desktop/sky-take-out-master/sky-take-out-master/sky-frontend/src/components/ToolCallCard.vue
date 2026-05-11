<template>
  <div class="tool-card">
    <button class="tool-card-header" @click="isExpanded = !isExpanded">
      <div class="tool-card-left">
        <svg class="tool-icon" width="14" height="14" viewBox="0 0 24 24" fill="none">
          <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"
                stroke="#888" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span class="tool-name">{{ toolNameMap[toolCall.toolName] || toolCall.toolName }}</span>
        <span class="tool-status-badge success">完成</span>
      </div>
      <svg class="collapse-arrow" :class="{ expanded: isExpanded }"
           width="14" height="14" viewBox="0 0 14 14" fill="none">
        <path d="M3 5l4 4 4-4" stroke="#888" stroke-width="1.5" stroke-linecap="round"/>
      </svg>
    </button>
    <div class="tool-card-body" :class="{ expanded: isExpanded }">
      <div class="tool-args" v-if="toolCall.args && toolCall.args !== '{}'">
        <span class="label">参数：</span>{{ toolCall.args }}
      </div>
      <pre class="tool-result">{{ formatResult(toolCall.result) }}</pre>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  toolCall: { type: Object, required: true }
})

const isExpanded = ref(false)

const toolNameMap = {
  queryDish: '查询菜品',
  addToCart: '加入购物车',
  queryOrder: '查询订单'
}

function formatResult(result) {
  if (!result) return ''
  try {
    return JSON.stringify(JSON.parse(result), null, 2)
  } catch {
    return result
  }
}
</script>

<style scoped>
.tool-card {
  margin-top: 8px;
  background: #f0f1f3;
  border: 1px solid #e2e4e8;
  border-radius: 8px;
  overflow: hidden;
}

.tool-card-header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 10px;
  background: transparent;
  border: none;
  cursor: pointer;
  text-align: left;
}

.tool-card-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tool-name {
  font-size: 12px;
  font-weight: 500;
  color: #666;
}

.tool-status-badge {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 10px;
  font-weight: 500;
}

.tool-status-badge.success {
  background: #f0fff0;
  color: #52c41a;
}

.collapse-arrow {
  transition: transform 0.25s ease;
  flex-shrink: 0;
}

.collapse-arrow.expanded {
  transform: rotate(180deg);
}

.tool-card-body {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.tool-card-body.expanded {
  max-height: 200px;
  border-top: 1px solid #e2e4e8;
}

.tool-args {
  padding: 6px 10px 0;
  font-size: 11px;
  color: #888;
}

.label {
  font-weight: 500;
}

.tool-result {
  padding: 6px 10px 8px;
  font-size: 11px;
  font-family: 'SF Mono', Consolas, monospace;
  color: #555;
  white-space: pre-wrap;
  word-break: break-all;
  overflow-y: auto;
  max-height: 160px;
}
</style>
