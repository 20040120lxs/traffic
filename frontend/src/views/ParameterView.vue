<template>
  <div class="parameter">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h2>参数设置</h2>
            </div>
          </template>
          
          <el-form :model="parameterForm" label-width="150px" size="large">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="交通文件">
                  <el-select v-model="parameterForm.trafficFile" placeholder="请选择交通文件" style="width: 100%">
                    <el-option
                      v-for="option in parameterOptions.traffic_file || []"
                      :key="option"
                      :label="option"
                      :value="option"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="路网文件">
                  <el-select v-model="parameterForm.roadnetFile" placeholder="请选择路网文件" style="width: 100%">
                    <el-option
                      v-for="option in parameterOptions.roadnet_file || []"
                      :key="option"
                      :label="option"
                      :value="option"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="交叉口数量">
                  <el-select v-model="parameterForm.numIntersections" placeholder="请选择交叉口数量" style="width: 100%">
                    <el-option
                      v-for="option in parameterOptions.NUM_INTERSECTIONS || []"
                      :key="option"
                      :label="option"
                      :value="parseInt(option)"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="缺失模式">
                  <el-select v-model="parameterForm.missingPattern" placeholder="请选择缺失模式" style="width: 100%">
                    <el-option
                      v-for="option in parameterOptions.missing_pattern || []"
                      :key="option"
                      :label="option"
                      :value="option"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="缺失率">
              <el-select v-model="parameterForm.missingRate" placeholder="请选择缺失率" style="width: 300px">
                <el-option
                  v-for="option in filteredMissingRates"
                  :key="option"
                  :label="option"
                  :value="option"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="queryResults">
                <el-icon><Search /></el-icon>
                查询结果
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <!-- Results Display -->
        <el-card v-if="queryResult" style="margin-top: 20px;">
          <template #header>
            <h3>查询结果</h3>
          </template>
          
          <!-- Parameter Table -->
          <el-table :data="[queryResult.parameters]" border style="margin-bottom: 20px;">
            <el-table-column prop="trafficFile" label="交通文件" />
            <el-table-column prop="roadnetFile" label="路网文件" />
            <el-table-column prop="numIntersections" label="交叉口数量" />
            <el-table-column prop="missingPattern" label="缺失模式" />
            <el-table-column prop="missingRate" label="缺失率" />
          </el-table>
          
          <!-- Images Carousel -->
          <div v-if="queryResult.images && queryResult.images.length > 0">
            <h4>结果图片</h4>
            <el-carousel height="400px" indicator-position="outside">
              <el-carousel-item v-for="image in queryResult.images" :key="image.type">
                <div class="image-container">
                  <h4>{{ image.title || getImageTypeTitle(image.type) }}</h4>
                  <img :src="image.path" :alt="image.title" @error="handleImageError" />
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
          
          <div v-else class="no-results">
            <el-alert
              title="暂无对应结果"
              type="info"
              description="当前参数组合暂无对应的结果图片，请尝试其他参数组合或联系管理员添加结果。"
              show-icon
              :closable="false"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const loading = ref(false)
const parameterOptions = ref({})
const queryResult = ref(null)

const parameterForm = reactive({
  trafficFile: '',
  roadnetFile: '',
  numIntersections: null,
  missingPattern: '',
  missingRate: ''
})

// Filter missing rates based on traffic file and missing pattern
const filteredMissingRates = computed(() => {
  const { trafficFile, missingPattern } = parameterForm
  const allRates = parameterOptions.value.missing_rate || []
  
  if (!trafficFile || !missingPattern) {
    return allRates
  }
  
  // Based on the requirements, filter rates by traffic file and pattern
  if (trafficFile === 'hangzhou' && missingPattern === 'random_missing') {
    return allRates.filter(rate => ['10%', '30%', '50%'].includes(rate))
  }
  if (trafficFile === 'hangzhou' && missingPattern === 'kriging missing') {
    return allRates.filter(rate => ['6.25%', '12.5%', '18.75%'].includes(rate))
  }
  if (trafficFile === 'jinan' && missingPattern === 'random_missing') {
    return allRates.filter(rate => ['10%', '30%', '50%'].includes(rate))
  }
  if (trafficFile === 'jinan' && missingPattern === 'kriging missing') {
    return allRates.filter(rate => ['8.33%', '16.67%', '25%'].includes(rate))
  }
  
  return allRates
})

const loadParameterOptions = async () => {
  try {
    const response = await api.get('/params/options')
    parameterOptions.value = response.data.data
  } catch (error) {
    console.error('Failed to load parameter options:', error)
  }
}

const queryResults = async () => {
  if (!validateForm()) {
    return
  }
  
  try {
    loading.value = true
    const response = await api.post('/params/query', parameterForm)
    queryResult.value = response.data.data
    
    if (queryResult.value.images.length === 0) {
      ElMessage.info('当前参数组合暂无结果图片')
    } else {
      ElMessage.success('查询结果成功')
    }
  } catch (error) {
    console.error('Query failed:', error)
  } finally {
    loading.value = false
  }
}

const validateForm = () => {
  if (!parameterForm.trafficFile) {
    ElMessage.warning('请选择交通文件')
    return false
  }
  if (!parameterForm.roadnetFile) {
    ElMessage.warning('请选择路网文件')
    return false
  }
  if (!parameterForm.numIntersections) {
    ElMessage.warning('请选择交叉口数量')
    return false
  }
  if (!parameterForm.missingPattern) {
    ElMessage.warning('请选择缺失模式')
    return false
  }
  if (!parameterForm.missingRate) {
    ElMessage.warning('请选择缺失率')
    return false
  }
  return true
}

const resetForm = () => {
  Object.assign(parameterForm, {
    trafficFile: '',
    roadnetFile: '',
    numIntersections: null,
    missingPattern: '',
    missingRate: ''
  })
  queryResult.value = null
}

const getImageTypeTitle = (type) => {
  const titleMap = {
    'imputation': '填补结果图',
    'noise': '加噪过程图', 
    'denoise': '去噪过程图'
  }
  return titleMap[type] || type
}

const handleImageError = (event) => {
  event.target.src = '/placeholder-image.png'
  event.target.alt = '图片加载失败'
}

onMounted(() => {
  loadParameterOptions()
})
</script>

<style scoped>
.parameter {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.image-container {
  text-align: center;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 20px;
}

.image-container img {
  max-width: 100%;
  max-height: 300px;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-container h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.no-results {
  text-align: center;
  padding: 40px;
}

.el-carousel__item {
  background-color: #f5f7fa;
}
</style>