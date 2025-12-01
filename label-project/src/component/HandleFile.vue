<template>
  <div style="height: 100vh;display: flex;">
    <ElContainer style="display: flex;">
      <ElMain style="background-color: aquamarine; display: flex; flex-direction: column;align-items: center;">
        <h2 style="margin-top: 80px;">需要处理的文件</h2>
        <div v-if="isConnected" style="font-size: 20px;font-weight: 200;">websocket已连接</div>
        <div v-else style="font-size: 20px;font-weight: 200;">websocket未连接</div>
        <br></br>
        <el-link href="/upload-file">点击此处回到上传文件</el-link>

        <br></br>
        <el-link v-if="fileNeedToHandle !== ''" :href="fileNeedToHandle" target="_blank" type="primary"
          style="margin: 10px 0;">
          查看文件
        </el-link>
        <div v-else style="margin: 10px 0;">暂无文件需要处理</div>
        <br></br>
        <ElInput placeholder="产生的n个向量序列" style="width: 600px;" v-model="textVectorSequence"></ElInput>
        <br></br>
        <br></br>
        <ElInput placeholder="完成标注后的n个偏移后向量序列" style="width: 600px;" v-model="offsetVectorSequence"></ElInput>
        <br></br>
        <br></br>
        <br></br>
        <br></br>
        <ElButton @click="commitTheVector" style="width: 150px;">提交</ElButton>
      </ElMain>
    </ElContainer>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios, { all } from 'axios';
import { ElAside, ElButton, ElContainer, ElInput, ElLink, ElMain } from 'element-plus';

const stompClient = ref<Client | null>(null); // STOMP客户端
const websocketUrl = ref("http://26.46.22.92:8080/ws-upload")
const serverUrl = "http://26.46.22.92:8080"
const userUUID = localStorage.getItem("userUUID");
const loginUUID = localStorage.getItem("loginUUID");
const isConnected = ref(false)
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

onMounted(() => {
  connect();
  getToDoUrls();
})
</script>

<style lang="css" scoped></style>
