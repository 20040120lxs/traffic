<template>
  <div class="dataset-list">
    <el-table :data="datasets" v-loading="loading" stripe>
      <el-table-column prop="originalFilename" label="文件名" min-width="200" />
      <el-table-column prop="ownerUsername" label="上传者" width="120" />
      <el-table-column prop="size" label="文件大小" width="120">
        <template #default="{ row }">
          {{ formatFileSize(row.size) }}
        </template>
      </el-table-column>
      <el-table-column prop="uploadTime" label="上传时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.uploadTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="200">
        <template #default="{ row }">
          <span v-if="row.description">{{ row.description }}</span>
          <span v-else class="text-muted">无描述</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            size="small" 
            @click="$emit('download', row)"
          >
            <el-icon><Download /></el-icon>
            下载
          </el-button>
          <el-button 
            v-if="allowDelete"
            type="danger" 
            size="small" 
            @click="$emit('delete', row)"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div v-if="datasets.length === 0 && !loading" class="empty-state">
      <el-empty description="暂无数据集" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  datasets: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  allowDelete: {
    type: Boolean,
    default: false
  }
})

defineEmits(['download', 'delete', 'refresh'])

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 40px;
}

.text-muted {
  color: #909399;
}
</style>