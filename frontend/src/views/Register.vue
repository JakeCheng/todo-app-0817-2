<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { NCard, NForm, NFormItem, NInput, NButton, NSpace } from 'naive-ui'
import { userApi } from '@/api/user'

const router = useRouter()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: ['blur', 'input'] },
    { min: 3, max: 32, message: '用户名长度 3-32', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: ['blur', 'input'] },
    { min: 6, max: 32, message: '密码长度 6-32', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: ['blur', 'input'] },
    {
      validator: (rule, value) => value === form.password,
      message: '两次密码不一致',
      trigger: ['blur', 'input']
    }
  ]
}

// 注册：成功后自动调用登录接口（后端注册接口已直接返回 token，无需再调登录）
async function handleRegister() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const res = await userApi.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      email: form.email
    })
    // 注册成功后直接保存 token，免登录跳转主页
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data.user))
    window.$message.success('注册成功，已自动登录')
    router.push({ name: 'Todo' })
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-container">
    <n-card class="register-card" title="用户注册" :bordered="false" size="large">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="top" size="large">
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="3-32 位" clearable />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="form.password"
            type="password"
            show-password-on="click"
            placeholder="6-32 位"
            clearable
          />
        </n-form-item>
        <n-form-item label="确认密码" path="confirmPassword">
          <n-input
            v-model:value="form.confirmPassword"
            type="password"
            show-password-on="click"
            placeholder="再次输入密码"
            clearable
          />
        </n-form-item>
        <n-form-item label="昵称（可选）" path="nickname">
          <n-input v-model:value="form.nickname" placeholder="留空则默认为用户名" clearable />
        </n-form-item>
        <n-form-item label="邮箱（可选）" path="email">
          <n-input v-model:value="form.email" placeholder="请输入邮箱" clearable />
        </n-form-item>
        <n-button type="primary" block size="large" :loading="loading" @click="handleRegister">
          注册并登录
        </n-button>
      </n-form>

      <n-space justify="center" style="margin-top: 16px">
        <n-button text type="primary" @click="router.push('/login')">已有账号？去登录</n-button>
      </n-space>
    </n-card>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 400px;
  max-width: 90vw;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}
</style>
