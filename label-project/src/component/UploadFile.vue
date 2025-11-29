<template>
  <div style="display: flex;height: 100vh;align-items: center; justify-content: center;">
    <div style="display: flex;">
      <el-upload v-model:file-list="fileList" class="upload-demo" :action="url + '/file/upload'" multiple
        :on-preview="handlePreview" :on-success="handleUploadSuccess" :on-remove="handleRemove"
        :before-remove="beforeRemove" :limit="fileListMaxSize" :on-exceed="handleExceed">
        <el-button type="primary">点击上传</el-button>
        <template #tip>
          <div class="el-upload__tip">
            文件大小应当小于10MB
          </div>
        </template>
      </el-upload>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox, ElUpload, ElButton } from 'element-plus'

import type { UploadProps, UploadUserFile } from 'element-plus'

const url = "http://127.0.0.1:8080";

const fileList = ref<UploadUserFile[]>([])

const fileListMaxSize = 4;

const handleRemove: UploadProps['onRemove'] = (file, uploadFiles) => {
  console.log(file, uploadFiles)
}

const handlePreview: UploadProps['onPreview'] = (uploadFile) => {
  console.log(uploadFile)
}

const handleExceed: UploadProps['onExceed'] = (files, uploadFiles) => {
  ElMessage.warning(
    `The limit is 3, you selected ${files.length} files this time, add up to ${files.length + uploadFiles.length
    } totally`
  )
}

const handleUploadSuccess: UploadProps['onSuccess'] = (res) => {
  alert(res);
  if (fileList.value.length === fileListMaxSize) fileList.value.shift();
}

const beforeRemove: UploadProps['beforeRemove'] = (uploadFile, uploadFiles) => {
  return ElMessageBox.confirm(
    `删除历史数据${uploadFile.name} ?`
  ).then(
    () => true,
    () => false
  )
}
</script>

<style scoped></style>
