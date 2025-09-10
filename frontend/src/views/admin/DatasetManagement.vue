<template>
  <div class="admin-dataset">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h2>数据集管理</h2>
              <el-button type="primary" @click="uploadDialogVisible = true">
                <el-icon><Upload /></el-icon>
                上传共享数据集
              </el-button>
            </div>
          </template>
          
          <el-table :data="datasets" v-loading="loading" stripe>
            <el-table-column prop="originalFilename" label="文件名" min-width="200" />
            <el-table-column prop="ownerUsername" label="上传者" width="120" />
            <el-table-column prop="role" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.role === 'ADMIN' ? 'success' : 'info'">
                  {{ row.role === 'ADMIN' ? '共享' : '用户' }}
                </el-tag>
              </template>
            </el-table-column>
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
                  @click="handleDownload(row)"
                >
                  <el-icon><Download /></el-icon>
                  下载
                </el-button>
                <el-popconfirm
                  title="确定要删除此数据集吗？"
                  @confirm="handleDelete(row)"
                >
                  <template #reference>
                    <el-button type="danger" size="small">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- Upload Dialog -->
    <el-dialog v-model="uploadDialogVisible" title="上传共享数据集" width="500px">
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="选择文件" required>
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :file-list="fileList"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                共享数据集将对所有用户可见，支持各种格式，文件大小不超过100MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input 
            v-model="uploadForm.description" 
            type="textarea" 
            placeholder="请输入数据集描述（可选）"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="uploading" @click="handleUpload">
            上传
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
const uploading = ref(false)
const uploadDialogVisible = ref(false)

const datasets = ref([])
const fileList = ref([])

const uploadForm = reactive({
  description: ''
})

const loadDatasets = async () => {
  try {
    loading.value = true
    const response = await api.get('/admin/datasets')
    datasets.value = response.data.data
  } catch (error) {
    console.error('Failed to load datasets:', error)
  } finally {
    loading.value = false
  }
}

const handleFileChange = (file) => {
  fileList.value = [file]
}

const handleUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }
  
  try {
    uploading.value = true
    const formData = new FormData()
    formData.append('file', fileList.value[0].raw)
    if (uploadForm.description) {
      formData.append('description', uploadForm.description)
    }
    
    await api.post('/admin/datasets/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    ElMessage.success('共享数据集上传成功')
    uploadDialogVisible.value = false
    fileList.value = []
    uploadForm.description = ''
    loadDatasets()
  } catch (error) {
    console.error('Upload failed:', error)
  } finally {
    uploading.value = false
  }
}

const handleDownload = async (dataset) => {
  try {
    const response = await api.get(`/datasets/${dataset.id}/download`)
    const downloadUrl = response.data.data
    
    // Create a temporary link to download the file
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = dataset.originalFilename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('开始下载文件')
  } catch (error) {
    console.error('Download failed:', error)
  }
}

const handleDelete = async (dataset) => {
  try {
    await api.delete(`/admin/datasets/${dataset.id}`)
    ElMessage.success('数据集删除成功')
    loadDatasets()
  } catch (error) {
    console.error('Delete failed:', error)
  }
}

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

onMounted(() => {
  loadDatasets()
})
</script>

<style scoped>
.admin-dataset {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.text-muted {
  color: #909399;
}
</style>