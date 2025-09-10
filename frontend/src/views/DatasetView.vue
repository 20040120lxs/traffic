<template>
  <div class="dataset">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h2>数据集上传/下载</h2>
              <el-button type="primary" @click="uploadDialogVisible = true">
                <el-icon><Upload /></el-icon>
                上传数据集
              </el-button>
            </div>
          </template>
          
          <el-tabs v-model="activeTab">
            <el-tab-pane label="我的数据集" name="my">
              <dataset-list 
                :datasets="myDatasets" 
                :loading="loading"
                @download="handleDownload"
                @delete="handleDelete"
                @refresh="loadMyDatasets"
                allow-delete
              />
            </el-tab-pane>
            
            <el-tab-pane label="共享数据集" name="shared">
              <dataset-list 
                :datasets="sharedDatasets" 
                :loading="loading"
                @download="handleDownload"
                @refresh="loadSharedDatasets"
              />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- Upload Dialog -->
    <el-dialog v-model="uploadDialogVisible" title="上传数据集" width="500px">
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
                支持各种格式的数据文件，文件大小不超过100MB
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
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'
import DatasetList from '@/components/DatasetList.vue'

const activeTab = ref('my')
const loading = ref(false)
const uploading = ref(false)
const uploadDialogVisible = ref(false)

const myDatasets = ref([])
const sharedDatasets = ref([])
const fileList = ref([])

const uploadForm = reactive({
  description: ''
})

const loadMyDatasets = async () => {
  try {
    loading.value = true
    const response = await api.get('/datasets/my')
    myDatasets.value = response.data.data
  } catch (error) {
    console.error('Failed to load my datasets:', error)
  } finally {
    loading.value = false
  }
}

const loadSharedDatasets = async () => {
  try {
    loading.value = true
    const response = await api.get('/datasets/shared')
    sharedDatasets.value = response.data.data
  } catch (error) {
    console.error('Failed to load shared datasets:', error)
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
    
    await api.post('/datasets/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    ElMessage.success('数据集上传成功')
    uploadDialogVisible.value = false
    fileList.value = []
    uploadForm.description = ''
    loadMyDatasets()
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
    await ElMessageBox.confirm(
      `确定要删除数据集 "${dataset.originalFilename}" 吗？此操作不可撤销。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await api.delete(`/datasets/${dataset.id}`)
    ElMessage.success('数据集删除成功')
    loadMyDatasets()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete failed:', error)
    }
  }
}

onMounted(() => {
  loadMyDatasets()
  loadSharedDatasets()
})
</script>

<style scoped>
.dataset {
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
</style>