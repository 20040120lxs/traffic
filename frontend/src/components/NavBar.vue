<template>
  <el-menu mode="horizontal" :default-active="activeIndex" class="el-menu-demo" router>
    <el-menu-item class="logo" style="cursor: default;">
      <h2>DiffLight</h2>
    </el-menu-item>
    
    <el-menu-item index="/">首页</el-menu-item>
    <el-menu-item index="/datasets" v-if="authStore.isAuthenticated">数据集上传/下载</el-menu-item>
    <el-menu-item index="/parameters" v-if="authStore.isAuthenticated">参数设置</el-menu-item>
    <el-menu-item index="/profile" v-if="authStore.isAuthenticated">个人信息管理</el-menu-item>
    
    <!-- Admin menus -->
    <el-sub-menu index="admin" v-if="authStore.isAdmin">
      <template #title>管理员功能</template>
      <el-menu-item index="/admin/datasets">数据集管理</el-menu-item>
      <el-menu-item index="/admin/users">用户信息管理</el-menu-item>
    </el-sub-menu>
    
    <div class="flex-grow" />
    
    <!-- Auth buttons -->
    <div v-if="!authStore.isAuthenticated" class="auth-buttons">
      <el-button type="primary" @click="$router.push('/login')">登录</el-button>
      <el-button @click="$router.push('/register')">注册</el-button>
    </div>
    
    <div v-else class="user-info">
      <el-dropdown>
        <span class="user-name">
          <el-icon><User /></el-icon>
          {{ authStore.username }}
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/profile')">个人信息</el-dropdown-item>
            <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeIndex = computed(() => route.path)

const logout = () => {
  authStore.clearUser()
  ElMessage.success('退出登录成功')
  router.push('/')
}
</script>

<style scoped>
.el-menu-demo {
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo {
  pointer-events: none;
  margin-right: 20px;
}

.logo h2 {
  margin: 0;
  color: #409eff;
}

.flex-grow {
  flex-grow: 1;
}

.auth-buttons {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.user-info {
  margin-left: auto;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 10px;
}
</style>