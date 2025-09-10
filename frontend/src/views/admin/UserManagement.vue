<template>
  <div class="admin-user">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h2>用户信息管理</h2>
            </div>
          </template>
          
          <el-table :data="users" v-loading="loading" stripe>
            <el-table-column prop="username" label="用户名" width="150" />
            <el-table-column prop="passwordHash" label="密码" width="120">
              <template #default>
                <el-text type="info">已加密</el-text>
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" width="150">
              <template #default="{ row }">
                <span v-if="row.phone">{{ row.phone }}</span>
                <span v-else class="text-muted">未设置</span>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="注册时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="updatedAt" label="更新时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.updatedAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="editUser(row)"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-popconfirm
                  title="确定要重置此用户密码吗？"
                  @confirm="resetPassword(row)"
                >
                  <template #reference>
                    <el-button type="warning" size="small">
                      <el-icon><Key /></el-icon>
                      重置密码
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- Edit User Dialog -->
    <el-dialog v-model="editDialogVisible" title="编辑用户信息" width="500px">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="editForm.newPassword" 
            type="password" 
            placeholder="如需修改密码请输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="updating" @click="handleUpdate">
            更新
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const loading = ref(false)
const updating = ref(false)
const editDialogVisible = ref(false)

const users = ref([])
const editFormRef = ref()
const currentUserId = ref(null)

const editForm = reactive({
  username: '',
  phone: '',
  newPassword: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度必须在3-20个字符之间', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  newPassword: [
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ]
}

const loadUsers = async () => {
  try {
    loading.value = true
    // Note: This endpoint needs to be implemented in the backend
    const response = await api.get('/admin/users')
    users.value = response.data.data
  } catch (error) {
    console.error('Failed to load users:', error)
    // For demo purposes, we'll use mock data
    users.value = []
    ElMessage.warning('用户管理功能需要后端完善相关接口')
  } finally {
    loading.value = false
  }
}

const editUser = (user) => {
  currentUserId.value = user.id
  editForm.username = user.username
  editForm.phone = user.phone || ''
  editForm.newPassword = ''
  editDialogVisible.value = true
}

const handleUpdate = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        updating.value = true
        
        const updateData = {
          username: editForm.username,
          phone: editForm.phone
        }
        
        if (editForm.newPassword) {
          updateData.newPassword = editForm.newPassword
        }
        
        // Note: This endpoint needs to be implemented in the backend
        await api.put(`/admin/users/${currentUserId.value}`, updateData)
        
        ElMessage.success('用户信息更新成功')
        editDialogVisible.value = false
        loadUsers()
      } catch (error) {
        console.error('Update failed:', error)
        ElMessage.warning('用户管理功能需要后端完善相关接口')
      } finally {
        updating.value = false
      }
    }
  })
}

const resetPassword = async (user) => {
  try {
    // Note: This endpoint needs to be implemented in the backend
    await api.post(`/admin/users/${user.id}/reset-password`)
    ElMessage.success('密码重置成功，新密码已发送到用户手机')
  } catch (error) {
    console.error('Reset password failed:', error)
    ElMessage.warning('密码重置功能需要后端完善相关接口')
  }
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-user {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.text-muted {
  color: #909399;
}
</style>