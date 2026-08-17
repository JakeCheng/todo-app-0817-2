<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  NLayout,
  NLayoutHeader,
  NLayoutContent,
  NCard,
  NSpace,
  NInput,
  NButton,
  NDataTable,
  NTag,
  NPopconfirm,
  NAvatar,
  NText,
  NStatistic,
  NGrid,
  NGi
} from 'naive-ui'
import { todoApi } from '@/api/todo'
import { userApi } from '@/api/user'

const router = useRouter()

// 当前登录用户信息
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

// 待办列表
const todoList = ref([])
const loading = ref(false)

// 新增表单
const newTodo = reactive({
  title: '',
  completed: false
})

// 加载列表
async function loadList() {
  loading.value = true
  try {
    const res = await todoApi.list()
    todoList.value = res.data || []
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 新增待办
async function handleAdd() {
  if (!newTodo.title.trim()) {
    window.$message.warning('请输入待办内容')
    return
  }
  try {
    const res = await todoApi.create({
      title: newTodo.title.trim(),
      completed: false
    })
    todoList.value.unshift(res.data)
    newTodo.title = ''
    window.$message.success('新增成功')
  } catch (err) {
    console.error(err)
  }
}

// 切换完成状态
async function handleToggle(row) {
  try {
    const res = await todoApi.toggle(row.id)
    // 直接更新当前行
    const idx = todoList.value.findIndex((item) => item.id === row.id)
    if (idx !== -1) {
      todoList.value[idx] = res.data
    }
  } catch (err) {
    console.error(err)
  }
}

// 删除待办
async function handleDelete(row) {
  try {
    await todoApi.remove(row.id)
    todoList.value = todoList.value.filter((item) => item.id !== row.id)
    window.$message.success('删除成功')
  } catch (err) {
    console.error(err)
  }
}

// 退出登录
function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  window.$message.success('已退出登录')
  router.push({ name: 'Login' })
}

// 表格列定义
const columns = [
  {
    title: '状态',
    key: 'completed',
    width: 100,
    render(row) {
      return row.completed
        ? h(NTag, { type: 'success', size: 'small' }, { default: () => '已完成' })
        : h(NTag, { type: 'warning', size: 'small' }, { default: () => '进行中' })
    }
  },
  {
    title: '标题',
    key: 'title',
    render(row) {
      return h(
        NText,
        { delete: row.completed, type: row.completed ? 'tertiary' : 'default' },
        { default: () => row.title }
      )
    }
  },
  {
    title: '创建时间',
    key: 'createdAt',
    width: 180
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    render(row) {
      return h(NSpace, null, {
        default: () => [
          h(
            NButton,
            {
              size: 'small',
              type: row.completed ? 'default' : 'primary',
              onClick: () => handleToggle(row)
            },
            { default: () => (row.completed ? '标记未完成' : '标记完成') }
          ),
          h(
            NPopconfirm,
            {
              onPositiveClick: () => handleDelete(row)
            },
            {
              default: () => '确认删除该待办？',
              trigger: () =>
                h(
                  NButton,
                  { size: 'small', type: 'error', ghost: true },
                  { default: () => '删除' }
                )
            }
          )
        ]
      })
    }
  }
]

// 渲染函数（n-data-table 的 columns 需要 h）
import { h } from 'vue'

onMounted(() => {
  loadList()
})
</script>

<template>
  <n-layout style="min-height: 100vh">
    <!-- 顶部 -->
    <n-layout-header bordered style="padding: 12px 24px; display: flex; align-items: center; justify-content: space-between;">
      <n-space align="center">
        <n-avatar round color="#18a058">{{ userInfo.nickname?.charAt(0) || 'U' }}</n-avatar>
        <div>
          <n-text strong>{{ userInfo.nickname || userInfo.username }}</n-text>
          <br />
          <n-text depth="3" style="font-size: 12px">{{ userInfo.username }}</n-text>
        </div>
      </n-space>
      <n-button type="error" ghost @click="handleLogout">退出登录</n-button>
    </n-layout-header>

    <!-- 内容 -->
    <n-layout-content style="padding: 24px; background: #f5f5f5;">
      <div style="max-width: 960px; margin: 0 auto;">
        <!-- 统计 -->
        <n-grid :cols="3" :x-gap="16" style="margin-bottom: 16px;">
          <n-gi>
            <n-card>
              <n-statistic label="待办总数" :value="todoList.length" />
            </n-card>
          </n-gi>
          <n-gi>
            <n-card>
              <n-statistic
                label="已完成"
                :value="todoList.filter((t) => t.completed).length"
              />
            </n-card>
          </n-gi>
          <n-gi>
            <n-card>
              <n-statistic
                label="进行中"
                :value="todoList.filter((t) => !t.completed).length"
              />
            </n-card>
          </n-gi>
        </n-grid>

        <!-- 新增待办 -->
        <n-card title="新增待办" style="margin-bottom: 16px;">
          <n-space>
            <n-input
              v-model:value="newTodo.title"
              placeholder="请输入待办内容，按回车快速新增"
              style="width: 600px"
              clearable
              @keyup.enter="handleAdd"
            />
            <n-button type="primary" @click="handleAdd">新增</n-button>
          </n-space>
        </n-card>

        <!-- 待办列表 -->
        <n-card title="待办列表">
          <n-data-table
            :columns="columns"
            :data="todoList"
            :loading="loading"
            :bordered="false"
            :row-key="(row) => row.id"
          />
        </n-card>
      </div>
    </n-layout-content>
  </n-layout>
</template>

<style scoped>
:deep(.n-data-table .n-data-table-th) {
  font-weight: 600;
}
</style>
