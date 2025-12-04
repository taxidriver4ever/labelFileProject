<template>
  <div class="docx-viewer">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>正在加载文档...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error">
      <p>❌ {{ error }}</p>
      <button @click="loadDocument" class="retry-btn">重试</button>
    </div>

    <!-- 文档内容 -->
    <div v-else class="document-content" ref="contentRef">
      <div v-if="!htmlContent" class="empty">
        <p>暂无内容</p>
        <button @click="loadDocument" class="load-btn">加载文档</button>
      </div>
      <div v-else v-html="htmlContent"></div>
    </div>

    <!-- 操作按钮 -->
    <div v-if="!loading && !error" class="controls">
      <button @click="loadDocument" class="refresh-btn">
        🔄 刷新
      </button>
      <button @click="downloadDocument" class="download-btn" v-if="docxUrl">
        ⬇️ 下载文档
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import * as mammoth from 'mammoth'

interface Props {
  docxUrl: string
  autoLoad?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  autoLoad: true
})

// 响应式数据
const loading = ref(false)
const error = ref<string | null>(null)
const htmlContent = ref<string>('')
const contentRef = ref<HTMLElement | null>(null)

// 加载并显示docx文档
const loadDocument = async () => {
  if (!props.docxUrl) {
    error.value = '文档URL为空'
    return
  }

  loading.value = true
  error.value = null
  htmlContent.value = ''

  try {
    // 获取docx文件
    const response = await fetch(props.docxUrl)

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }

    const arrayBuffer = await response.arrayBuffer()

    // 使用mammoth将docx转换为HTML
    const result = await mammoth.convertToHtml({ arrayBuffer })

    htmlContent.value = result.value

    // 应用自定义样式（如果有的话）
    if (result.messages && result.messages.length > 0) {
      console.warn('文档转换消息:', result.messages)
    }

  } catch (err: any) {
    error.value = err.message || '加载文档失败'
    console.error('加载文档失败:', err)
  } finally {
    loading.value = false
  }
}

// 下载原始文档
const downloadDocument = () => {
  if (!props.docxUrl) return

  const link = document.createElement('a')
  link.href = props.docxUrl
  link.download = 'document.docx'
  link.target = '_blank'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 自动滚动到顶部
const scrollToTop = () => {
  if (contentRef.value) {
    contentRef.value.scrollTop = 0
  }
}

// 监听URL变化
watch(() => props.docxUrl, () => {
  if (props.autoLoad) {
    loadDocument()
  }
})

// 组件挂载时加载文档
onMounted(() => {
  if (props.autoLoad && props.docxUrl) {
    loadDocument()
  }
})

// 暴露方法给父组件
defineExpose({
  loadDocument,
  downloadDocument,
  scrollToTop
})
</script>

<style scoped>
.docx-viewer {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.loading {
  text-align: center;
  padding: 50px;
  color: #666;
}

.spinner {
  display: inline-block;
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.error {
  text-align: center;
  padding: 30px;
  color: #e74c3c;
  background-color: #ffeaea;
  border-radius: 8px;
  margin: 20px 0;
}

.retry-btn {
  margin-top: 15px;
  padding: 8px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.retry-btn:hover {
  background-color: #2980b9;
}

.empty {
  text-align: center;
  padding: 50px;
  color: #999;
  border: 2px dashed #ddd;
  border-radius: 8px;
}

.load-btn {
  margin-top: 15px;
  padding: 10px 25px;
  background-color: #2ecc71;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.load-btn:hover {
  background-color: #27ae60;
}

.document-content {
  background-color: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  min-height: 300px;
  max-height: 80vh;
  overflow-y: auto;
}

.controls {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 20px;
}

.refresh-btn,
.download-btn {
  padding: 8px 16px;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.refresh-btn:hover {
  background-color: #e2e6ea;
}

.download-btn:hover {
  background-color: #d1ecf1;
  border-color: #bee5eb;
}

/* 增强文档内容的样式 */
.document-content :deep(h1) {
  color: #2c3e50;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.document-content :deep(h2) {
  color: #34495e;
  margin: 25px 0 15px;
}

.document-content :deep(p) {
  line-height: 1.6;
  margin-bottom: 15px;
  color: #333;
}

.document-content :deep(ul),
.document-content :deep(ol) {
  margin: 15px 0;
  padding-left: 30px;
}

.document-content :deep(li) {
  margin-bottom: 8px;
}

.document-content :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 20px 0;
}

.document-content :deep(th),
.document-content :deep(td) {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

.document-content :deep(th) {
  background-color: #f8f9fa;
  font-weight: 600;
}

.document-content :deep(strong) {
  color: #2c3e50;
}

.document-content :deep(a) {
  color: #3498db;
  text-decoration: none;
}

.document-content :deep(a:hover) {
  text-decoration: underline;
}
</style>
