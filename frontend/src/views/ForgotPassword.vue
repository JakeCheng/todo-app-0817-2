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
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  username: { required: true, message: '请输入用户名', trigger: ['blur', 'input'] },
  oldPassword: { required: true, message: '请输入原始密码', trigger: ['blur', 'input'] },
  newPassword: [
    { required: true, message: '请输入新密码', trigger: ['blur', 'input'] },
    { min: 6, max: 32, message: '新密码长度 6-32', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: ['blur', 'input'] },
    {
      validator: (rule, value) => value === form.newPassword,
      message: '两次新密码不一致',
      trigger: ['blur', 'input']
    }
  ]
}

// 修改密码
async function handleSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    await userApi.updatePassword({
      username: form.username,
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    window.$message.success('密码修改成功，请使用新密码登录')
    // 清理旧 token，跳转登录页
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push({ name: 'Login' })
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="forgot-container">
    <n-card class="forgot-card" title="忘记密码" :bordered="false" size="large">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="top" size="large">
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="请输入用户名" clearable />
        </n-form-item>
        <n-form-item label="原始密码" path="oldPassword">
          <n-input
            v-model:value="form.oldPassword"
            type="password"
            show-password-on="click"
            placeholder="请输入原始密码"
            clearable
          />
        </n-form-item>
        <n-form-item label="新密码" path="newPassword">
          <n-input
            v-model:value="form.newPassword"
            type="password"
            show-password-on="click"
            placeholder="6-32 位"
            clearable
          />
        </n-form-item>
        <n-form-item label="确认新密码" path="confirmPassword">
          <n-input
            v-model:value="form.confirmPassword"
            type="password"
            show-password-on="click"
            placeholder="再次输入新密码"
            clearable
          />
        </n-form-item>
        <n-button type="primary" block size="large" :loading="loading" @click="handleSubmit">
          确认修改
        </n-button>
      </n-form>

      <n-space justify="center" style="margin-top: 16px">
        <n-button text type="primary" @click="router.push('/login')">返回登录</n-button>
      </n-space>
    </n-card>
  </div>
</template>

<style scoped>
.forgot-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.forgot-card {
  width: 400px;
  max-width: 90vw;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}
</style>
