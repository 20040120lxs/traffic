<template>
  <div id="app">
    <el-container>
      <el-header>
        <NavBar />
      </el-header>
      <el-main>
        <RouterView />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  authStore.initializeAuth()
})
</script>

<style>
#app {
  font-family: 'Microsoft YaHei', sans-serif;
}

.el-header {
  padding: 0;
  border-bottom: 1px solid #ebeef5;
}

.el-main {
  padding: 20px;
}
</style>