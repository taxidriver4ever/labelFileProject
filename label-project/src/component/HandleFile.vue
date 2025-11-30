<template>
  <div style="height: 100vh;display: flex;">
    <ElContainer style="display: flex;">
      <ElAside style="width: 18%;background-color:gold;padding: 20px;height: 100vh;">
        <h2>待处理的文件</h2>
        <br></br>
        <div v-for="(url, index) in urls" :key="index" style="font-size: 14px;">
          文件{{ index + 1 }}
        </div>
      </ElAside>
      <ElMain style="background-color: aquamarine;">
        <h2>需要处理的文件</h2>
        <div v-if="isConnected" style="font-size: 20px;font-weight: 200;">websocket已连接</div>
        <div v-else style="font-size: 20px;font-weight: 700;">websocket未连接</div>
        <br></br>
        <el-link href="/upload-file">点击此处回到上传文件</el-link>

        <br></br>
        <!-- 修复 el-link -->
        <el-link v-if="fileNeedToHandle" :href="fileNeedToHandle" target="_blank" type="primary"
          style="margin: 10px 0;">
          查看文件
        </el-link>
        <div v-else style="margin: 10px 0;">暂无文件需要处理</div>
        <br></br>
        <div>产生的n个向量序列</div>
        <ElInput style="width: 600px;" v-model="textVectorSequence"></ElInput>
        <br></br>
        <br></br>
        <div>完成标注后的n个偏移后向量序列</div>
        <ElInput style="width: 600px;" v-model="offsetVectorSequence"></ElInput>
        <br></br>
        <br></br>
        <br></br>
        <br></br>
        <ElButton style="width: 150px;">提交</ElButton>
      </ElMain>
    </ElContainer>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';
import { ElAside, ElButton, ElContainer, ElInput, ElLink, ElMain } from 'element-plus';

const stompClient = ref<Client | null>(null); // STOMP客户端
const websocketUrl = ref("http://localhost:8080/ws-upload")
const serverUrl = "http://localhost:8080"
const userUUID = localStorage.getItem("userUUID");
const loginUUID = localStorage.getItem("loginUUID");
const isConnected = ref(false)
const urls = ref<string[]>([]);
const fileNeedToHandle = ref<string | undefined>("");
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
  urls.value.push(fileUrl)
};

function getToDoUrls() {
  axios({
    url: serverUrl + "/file/urls",
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
      urls.value = res.data.data;
      fileNeedToHandle.value = urls.value.shift();
    }
    else console.log(res.data.msg);
  }).catch(e => {
    console.log(e);
  })
}

function commitTheVector(){
  
}

onMounted(() => {
  connect();
  getToDoUrls();
})
</script>

<style lang="css" scoped></style>
