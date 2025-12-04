<template>
  <div style="display: flex;flex-direction: column;">
    <ElContainer style="display: flex;height: 100vh;">
      <ElMain style="background-color: aquamarine; display: flex; flex-direction: column;align-items: center;">
        <h2 style="margin-top: 70px;font-size: 30px;">需要处理的文件</h2>
        <div v-if="isConnected" style="font-size: 18px;font-weight: 200;">实时接收连接成功</div>
        <div v-else style="font-size: 20px;font-weight: 200;">实时接收连接失败</div>
        <br></br>
        <el-link href="/upload-file">点击此处回到上传文件</el-link>
        <br></br>
        <div v-if="fileNeedToHandle === ''" style="margin: 10px 0;">暂无文件需要处理</div>
        <div v-else style="margin: 10px 0;">有文件需要处理，往下滑可以查看</div>
        <br></br>
        <ElInput type="textarea" resize="none" :autosize="{ minRows: 4, maxRows: 6 }" placeholder="产生的n个向量序列"
          style="width: 600px;" v-model="textVectorSequence"></ElInput>
        <br></br>
        <br></br>
        <ElInput type="textarea" resize="none" :autosize="{ minRows: 4, maxRows: 6 }" placeholder="完成标注后的n个偏移后向量序列"
          style="width: 600px;" v-model="offsetVectorSequence"></ElInput>
        <br></br>
        <br></br>
        <ElButton @click="commitTheVector" style="width: 150px;">提交</ElButton>
        <br></br>
        <ElButton @click="exportExcel">导出当前数据库表</ElButton>
      </ElMain>
    </ElContainer>
    <div class="container">
      <h1>文档查看器</h1>
      <!-- 使用文档查看器组件 -->
      <DocxViewer :docx-url="fileNeedToHandle" :auto-load="false" ref="viewerRef" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios, { all } from 'axios';
import { ElAside, ElButton, ElContainer, ElInput, ElLink, ElMain } from 'element-plus';
import DocxViewer from './DocxViewer.vue'

const viewerRef = ref<InstanceType<typeof DocxViewer> | null>(null)
const stompClient = ref<Client | null>(null); // STOMP客户端
const websocketUrl = ref("http://26.46.22.92:8080/ws-upload")
const serverUrl = "http://26.46.22.92:8080"
const userUUID = localStorage.getItem("userUUID");
const loginUUID = localStorage.getItem("loginUUID");
const isConnected = ref(false)
const fileNeedToHandle = ref<string>("");
const textVectorSequence = ref("")
const offsetVectorSequence = ref("")

const connect = () => {
  try {
    stompClient.value = new Client({
      webSocketFactory: () => new SockJS(websocketUrl.value),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      connectHeaders: {
        "userUUID": userUUID || "",
        "loginUUID": loginUUID || ""
      },
      onConnect: () => {
        console.log('STOMP连接已建立');
        // 订阅私聊消息
        stompClient.value?.subscribe(
          `/user/queue/upload.private`,
          (fileUrl) => {
            handleIncomingMessage(fileUrl.body);
          }
        );

        isConnected.value = true;
      },
      onStompError: (frame) => {
        console.error('STOMP协议错误:', frame);
        alert('STOMP协议错误: ' + frame.headers['message']);
      },
      onWebSocketError: (event) => {
        console.error('WebSocket错误:', event);
        alert('WebSocket连接错误，请检查URL和服务器状态');
      },
      onDisconnect: () => {
        console.log('STOMP连接已断开');
      }
    });

    // 激活客户端
    stompClient.value.activate();
  } catch (error) {
    console.error('连接失败:', error);
  }
};

// 处理收到的消息
const handleIncomingMessage = (fileUrl: string) => {
  fileNeedToHandle.value = fileUrl;
};

function getToDoUrls() {
  axios({
    url: serverUrl + "/file/url",
    method: "POST",
    headers: {
      "userUUID": userUUID,
      "loginUUID": loginUUID
    },
    params: {
      userUUID: userUUID
    }
  }).then(res => {
    if (res.data.code === 200) {
      fileNeedToHandle.value = res.data.data;
    }
    else console.log(res.data.msg);
  }).catch(e => {
    console.log(e);
  })
}

async function commitTheVector() {
  if (fileNeedToHandle.value === "") {
    alert("目前暂无处理文件")
    return
  }
  if (textVectorSequence.value === "") {
    alert("请输入完整")
    return
  }
  if (offsetVectorSequence.value === "") {
    alert("请输入完整")
    return
  }
  await axios({
    url: serverUrl + "/file/vector",
    method: "POST",
    headers: {
      "userUUID": userUUID,
      "loginUUID": loginUUID
    },
    params: {
      userUUID: localStorage.getItem("userUUID"),
      textVectorSequence: textVectorSequence.value,
      offsetVectorSequence: offsetVectorSequence.value,
      fileUrl: fileNeedToHandle.value
    }
  }).then(res => {
    if (res.data.code === 200) {
      alert(res.data.data);
      window.location.href = "/handle-file"
    }
    else {
      alert("未知错误");
    }
  }).catch(e => {
    console.log(e)
  })
}

async function exportExcel() {
  await fetch(serverUrl + '/file/excel', {
    method: 'GET',
    headers: {
      "userUUID": userUUID || "",
      "loginUUID": loginUUID || ""
    }
  })
    .then(response => response.blob())
    .then(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = '文件识别结果数据.xlsx';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    });
}

onMounted(() => {
  connect();
  getToDoUrls();
})
</script>

<style lang="css" scoped>
.container {
  background-color: palegoldenrod;
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  color: #2c3e50;
  margin-bottom: 30px;
  text-align: center;
}

.url-input {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.url-input-field {
  flex: 1;
  padding: 12px 15px;
  border: 2px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.url-input-field:focus {
  outline: none;
  border-color: #3498db;
}

.load-btn {
  padding: 12px 30px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.load-btn:hover {
  background-color: #2980b9;
}
</style>
