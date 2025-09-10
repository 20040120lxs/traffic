<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h1>DiffLight</h1>
              <p class="subtitle">交通流量数据分析与预测平台</p>
            </div>
          </template>
          
          <div class="content-area">
            <el-alert
              title="欢迎使用 DiffLight 平台"
              type="info"
              description="本平台提供交通流量数据的上传、分析和结果展示功能。您可以上传数据集、配置参数、查看分析结果。"
              show-icon
              :closable="false">
            </el-alert>
            
            <el-divider></el-divider>
            
            <div class="rich-text-area">
              <h3>功能介绍</h3>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-card shadow="hover">
                    <template #header>
                      <el-icon><Upload /></el-icon>
                      数据集管理
                    </template>
                    <p>上传和管理您的交通数据集，支持多种格式，便于后续分析处理。</p>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card shadow="hover">
                    <template #header>
                      <el-icon><Setting /></el-icon>
                      参数配置
                    </template>
                    <p>灵活配置分析参数，包括交通文件、路网文件、缺失模式等多项设置。</p>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card shadow="hover">
                    <template #header>
                      <el-icon><PictureRounded /></el-icon>
                      结果展示
                    </template>
                    <p>查看分析结果图表，包括填补结果图、加噪过程图、去噪过程图等。</p>
                  </el-card>
                </el-col>
              </el-row>
            </div>
            
            <el-divider></el-divider>
            
            <div class="getting-started" v-if="!authStore.isAuthenticated">
              <h3>开始使用</h3>
              <p>请先注册账号或登录以使用完整功能：</p>
              <el-space>
                <el-button type="primary" @click="$router.push('/register')">
                  <el-icon><UserFilled /></el-icon>
                  注册账号
                </el-button>
                <el-button @click="$router.push('/login')">
                  <el-icon><User /></el-icon>
                  登录
                </el-button>
              </el-space>
            </div>
            
            <div class="quick-actions" v-else>
              <h3>快速操作</h3>
              <el-space>
                <el-button type="primary" @click="$router.push('/datasets')">
                  <el-icon><Folder /></el-icon>
                  管理数据集
                </el-button>
                <el-button type="success" @click="$router.push('/parameters')">
                  <el-icon><Tools /></el-icon>
                  参数设置
                </el-button>
              </el-space>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  text-align: center;
}

.card-header h1 {
  margin: 0;
  color: #409eff;
  font-size: 2.5rem;
}

.subtitle {
  margin: 10px 0 0 0;
  color: #666;
  font-size: 1.1rem;
}

.content-area {
  margin-top: 20px;
}

.rich-text-area h3,
.getting-started h3,
.quick-actions h3 {
  color: #303133;
  margin-bottom: 15px;
}

.el-card .el-card__header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
}
</style>