<template>
  <div style="display: flex;height: 100vh;width: 100%;">
    <ElContainer style="display: flex;height: 100vh;width: 100%;">
      <ElMain style="display: flex; height: 100vh;flex: 1;overflow: hidden;flex-direction: column;
            align-items: center; justify-content: center;">
        <div style="display: flex;flex-direction: column;width: 100%;justify-content: center;align-items: center;">
          <h2 style="display: flex;margin-bottom: 20px;font-size: 30px;">登录进入</h2>
          <div class="user-information-container">
            <div class="user-information-input-tips" v-show="isUserNameInputFocus">请输入用户名</div>
            <input @focusout="userNameFocusOutEvent" @focusin="userNameFocusInEvent" v-model="userName"
              :placeholder="userNamePlaceHolder" class="user-information-input" />
          </div>
          <div class="user-information-container">
            <div class="user-information-input-tips" v-show="isUserPasswordInputFocus">请输入密码</div>
            <input @focusout="userPasswordFocusOutEvent" type="password" @focusin="userPasswordFocusInEvent"
              v-model="userPassword" :placeholder="userPasswordPlaceHolder" class="user-information-input" />
          </div>
        </div>
        <button class="sign-up-button" @click="login">登录</button>
        <h5>注册即表示您同意<a href="#">条款与条件</a>以及<a href="#">隐私政策</a></h5>
      </ElMain>
      <ElAside
        style="background-position: center;background-size: cover;background-repeat: no-repeat;overflow: hidden;display: flex;height: 100vh;flex: 1;background-image: url('/src/image/right.png');">
      </ElAside>
    </ElContainer>
  </div>
</template>

<script setup lang="ts">
import { ElContainer, ElAside, ElMain } from 'element-plus';
import { ref } from 'vue';
import axios from 'axios';

const userPassword = ref("")
const userName = ref("")
const userNamePlaceHolder = ref("请输入用户名");
const userPasswordPlaceHolder = ref("请输入密码");
const isUserNameInputFocus = ref(false);
const isUserPasswordInputFocus = ref(false);
const serverUrl = "http://26.46.22.92:8080"
const userPasswordFocusInEvent = () => {
  isUserPasswordInputFocus.value = true;
  userPasswordPlaceHolder.value = "";
}
const userPasswordFocusOutEvent = () => {
  isUserPasswordInputFocus.value = false;
  if (userPassword.value === "") userPasswordPlaceHolder.value = "请输入密码";
}
const userNameFocusInEvent = () => {
  isUserNameInputFocus.value = true;
  userNamePlaceHolder.value = "";
}
const userNameFocusOutEvent = () => {
  isUserNameInputFocus.value = false;
  if (userName.value === "") userNamePlaceHolder.value = "请输入用户名";
}


async function login() {

  if (userName.value === '') {
    alert('请输入名称');
    return;
  }

  if (userPassword.value === '') {
    alert("请输入密码");
    return;
  }

  await axios({
    url: serverUrl + "/login/name-password",
    method: "POST",
    data: {
      userName: userName.value,
      userPassword: userPassword.value
    }
  }).then(res => {
    if (res.data.code === 200) {
      localStorage.setItem("userUUID", res.data.data.userUUID);
      localStorage.setItem("loginUUID", res.data.data.loginUUID);
      alert("登录成功")
      window.location.href = "/upload-file"
    }
    else alert("登录失败");
  }).catch(e => {
    alert(e);
  })
}
</script>


<style scoped>
.user-information-container {
  display: flex;
  flex-direction: column;
  width: 50vh;
}

.user-information-input-tips {
  padding-left: 5px;
  padding-right: 5px;
  position: absolute;
  background-color: white;
  color: #9005b0;
  margin-left: 10px;
}

.user-information-input {
  display: flex;
  border-radius: 5px;
  padding: 10px;
  height: 50px;
  font-size: 18px;
  margin-top: 15px;
  margin-bottom: 8px;
  border: 1px solid grey;
}

.verification-input {
  margin-right: auto;
  display: flex;
  border-radius: 5px;
  padding: 10px;
  height: 30px;
  font-size: 18px;
  margin-top: 15px;
  margin-bottom: 8px;
  border: 1px solid grey;
}

.captcha-button {
  height: 50px;
  width: 120px;
  margin-left: auto;
  padding: 8px 12px;
  background: #f3f3f3;
  border: 1px solid #ccc;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  color: #333;
}

.captcha-button:hover {
  border-color: #9005b0;
  color: #9005b0;
}

.user-information-input:focus,
.verification-input:focus {
  outline: none;
  border: 2px solid #9005b0;
}

.user-information-input:focus {
  outline: none;
  border: 2px solid #9005b0;
}

.sign-up-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 200px;
  height: 50px;
  background-color: #9005b0;
  color: white;
  border-radius: 5px;
  font-size: 18px;
  cursor: pointer;
  margin-top: 20px;
  margin-bottom: 20px;
}

.sign-up-button:hover {
  background-color: #a40ad3;
}
</style>
