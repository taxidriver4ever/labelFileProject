<template>
  <div style="display: flex; align-items: center; height: 100vh; flex-direction: column;background-color:thistle;">
    <div style="margin-right: auto;margin-bottom: 12%;">
      <ElButton type="text" style="color: black;margin: 15px;" @click="backToLogin"><ElIcon><Back/></ElIcon>&nbsp;&nbsp;退出登录</ElButton>
    </div>
    <h2 style="font-size: 40px;">上传数据</h2>
    <br></br>
    <el-upload v-model:file-list="fileList" :headers="{ userUUID: userUUID, loginUUID: loginUUID }"
      :on-success="handleUploadSuccess" ref="uploadRef" class="upload-demo" :on-error-="handleUploadError"
      :action="url + '/file/upload'" :auto-upload="false" multiple>
      <template #trigger>
        <el-button type="primary" style="border-radius: 20px;font-size: 25px;height: 60px;" size="large">选择文件</el-button>
      </template>

      <el-button style="margin-left: 20px;font-size: 25px;height: 60px;border-radius: 20px;margin-bottom: 5px;" class="ml-3" type="success"
        @click="uploadFile">
        上传文件
      </el-button>

    </el-upload>
    <div class="el-upload__tip">
      传输文件小于10MB
    </div>
    <br></br>
    <ElLink href="/handle-file">点击跳转处理数据</ElLink>

  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElUpload, ElButton, ElLink, ElIcon, ElMessage } from 'element-plus'
import type { UploadProps, UploadUserFile, UploadInstance } from 'element-plus'
import { Back } from '@element-plus/icons-vue';

const fileList = ref<UploadUserFile[]>([])
const uploadRef = ref<UploadInstance>()
const url = "http://127.0.0.1:8080";
const fileListMaxSize = 4;
const userUUID = localStorage.getItem("userUUID");
const loginUUID = localStorage.getItem("loginUUID");

// 发送消息
function uploadFile() {
  uploadRef.value!.submit()
}

const handleUploadSuccess: UploadProps['onSuccess'] = (res) => {
  ElMessage.success(res);
  if (fileList.value.length === fileListMaxSize) fileList.value.shift();
}
const handleUploadError: UploadProps['onError'] = () => {
  alert("文件上传失败可能是因为文件过大")
}
function backToLogin(){
  window.location.href = "/login"
  localStorage.removeItem("userUUID");
  localStorage.removeItem("loginUUID");
}
</script>
