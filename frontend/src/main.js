import { createApp } from 'vue'
import { createDiscreteApi } from 'naive-ui'
import App from './App.vue'
import router from './router'

// 全局 discrete API（message / dialog / notification）
const { message, dialog, notification } = createDiscreteApi(['message', 'dialog', 'notification'])

// 挂载到全局，方便 axios 拦截器中使用
window.$message = message
window.$dialog = dialog
window.$notification = notification

const app = createApp(App)
app.use(router)
app.mount('#app')
