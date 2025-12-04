<template>
  <div style="display: flex; align-items: center; height: 100vh; flex-direction: column;background-color:thistle;">
    <div style="margin-right: auto;margin-bottom: 12%;">
      <ElButton type="text" style="color: black;margin: 15px;" @click="backToLogin">
        <ElIcon>
          <Back />
        </ElIcon>&nbsp;&nbsp;退出登录
      </ElButton>
    </div>
    <h2 style="font-size: 40px;">上传数据</h2>
    <br></br>
    <ElLink href="/handle-file">点击跳转处理数据</ElLink>
    <br></br>
    <el-upload accept=".docx" v-model:file-list="fileList" :limit="fileListMaxSize" :on-success="handleSuccess"
      :headers="{ userUUID: userUUID, loginUUID: loginUUID }" ref="uploadRef" class="upload-demo"
      :on-error-="handleUploadError" :action="url + '/file/upload'" :on-exceed="handleExceed" :auto-upload="false"
      multiple>
      <template #trigger>
        <el-button type="primary" style="border-radius: 20px;font-size: 20px;height: 50px;"
          size="large">选择文件</el-button>
      </template>

      <el-button style="margin-left: 20px;font-size: 20px;height: 50px;border-radius: 20px;margin-bottom: 5px;"
        class="ml-3" type="success" @click="uploadFile">
        上传文件
      </el-button>

    </el-upload>
    <div class="el-upload__tip">
      传输文件小于10MB，传输的文件应为docx文件
    </div>
    <br></br>

  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElUpload, ElButton, ElLink, ElIcon, ElMessage } from 'element-plus'
import type { UploadProps, UploadUserFile, UploadInstance } from 'element-plus'
import { Back } from '@element-plus/icons-vue';

const fileList = ref<UploadUserFile[]>([])
const uploadRef = ref<UploadInstance>()
const url = "http://26.46.22.92:8080";
const fileListMaxSize = 5;
const userUUID = localStorage.getItem("userUUID");
const loginUUID = localStorage.getItem("loginUUID");

// 发送消息
function uploadFile() {
  uploadRef.value!.submit()
}

const handleUploadError: UploadProps['onError'] = () => {
  alert("文件上传失败可能是因为文件过大")
}

const handleExceed: UploadProps['onExceed'] = () => {
  ElMessage.error("文件上传过多，最多仅支持4个文件")
}

const handleSuccess: UploadProps['onSuccess'] = (res) => {
  if (res.data === "Not logged in") {
    ElMessage.info(res);
    window.location.href = "/";
    return;
  }
  ElMessage.success(res);
  for (let i = 0; i < fileList.value.length; i++)
    fileList.value.pop();
}

function backToLogin() {
  window.location.href = "/login"
  localStorage.removeItem("userUUID");
  localStorage.removeItem("loginUUID");
}
</script>
