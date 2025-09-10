<template>
  <div class="profile">
    <el-row :gutter="20">
      <el-col :span="16" :offset="4">
        <el-card>
          <template #header>
            <div class="card-header">
              <h2>个人信息管理</h2>
            </div>
          </template>
          
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="rules"
            label-width="120px"
            size="large"
          >
            <el-form-item label="当前用户名">
              <el-input v-model="currentUsername" disabled />
            </el-form-item>
            
            <el-form-item label="新用户名" prop="username">
              <el-input 
                v-model="profileForm.username" 
                placeholder="如需修改用户名请输入新用户名"
                clearable 
              />
            </el-form-item>
            
            <el-form-item label="原密码" prop="oldPassword" v-if="profileForm.newPassword">
              <el-input
                v-model="profileForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="profileForm.newPassword"
                type="password"
                placeholder="如需修改密码请输入新密码"
                show-password
                clearable
              />
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" clearable />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleUpdate">
                更新信息
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const authStore = useAuthStore()

const profileFormRef = ref()
const loading = ref(false)
const currentUsername = ref('')

const profileForm = reactive({
  username: '',
  oldPassword: '',
  newPassword: '',
  phone: ''
})

const validateOldPassword = (rule, value, callback) => {
  if (profileForm.newPassword && !value) {
    callback(new Error('修改密码时必须输入原密码'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { min: 3, max: 20, message: '用户名长度必须在3-20个字符之间', trigger: 'blur' }
  ],
  oldPassword: [
    { validator: validateOldPassword, trigger: 'blur' }
  ],
  newPassword: [
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

const loadUserInfo = async () => {
  try {
    const response = await api.get('/auth/me')
    const user = response.data.data
    currentUsername.value = user.username
    profileForm.phone = user.phone || ''
  } catch (error) {
    console.error('Failed to load user info:', error)
  }
}

const handleUpdate = async () => {
  if (!profileFormRef.value) return
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        
        const updateData = {}
        if (profileForm.username && profileForm.username !== currentUsername.value) {
          updateData.username = profileForm.username
        }
        if (profileForm.newPassword) {
          updateData.oldPassword = profileForm.oldPassword
          updateData.newPassword = profileForm.newPassword
        }
        if (profileForm.phone) {
          updateData.phone = profileForm.phone
        }
        
        if (Object.keys(updateData).length === 0) {
          ElMessage.warning('没有需要更新的信息')
          return
        }
        
        await api.put('/auth/me', updateData)
        ElMessage.success('个人信息更新成功')
        
        // 如果用户名发生变化，需要重新登录
        if (updateData.username) {
          ElMessage.info('用户名已更新，请重新登录')
          authStore.clearUser()
          window.location.href = '/login'
        } else {
          loadUserInfo()
          resetForm()
        }
      } catch (error) {
        console.error('Update failed:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = () => {
  profileForm.username = ''
  profileForm.oldPassword = ''
  profileForm.newPassword = ''
  loadUserInfo()
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}
</style>