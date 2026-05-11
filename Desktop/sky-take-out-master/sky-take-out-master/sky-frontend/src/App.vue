<template>
  <div id="app-root">
    <ChatView />
  </div>
</template>

<script setup>
import ChatView from './views/ChatView.vue'
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  background: #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  font-family: -apple-system, 'PingFang SC', 'Helvetica Neue', sans-serif;
}

#app-root {
  width: 100%;
  max-width: 480px;
  height: 100vh;
  background: #fff;
  box-shadow: 0 0 20px rgba(0,0,0,0.1);
}
</style>
