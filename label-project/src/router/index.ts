import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/component/Login.vue'
import UploadFile from '@/component/UploadFile.vue'
import HandleFile from '@/component/HandleFile.vue'
import axios from 'axios'
const serverUrl = 'http://localhost:8080'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/upload-file', // 默认重定向到首页
    },
    {
      path: '/handle-file',
      component: HandleFile,
    },
    {
      path: '/upload-file',
      component: UploadFile,
    },
    {
      path: '/login',
      component: Login,
    },
  ],
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  // 检查用户是否已登录（这里需要根据你的实际登录状态来判断）
  const userUUID = localStorage.getItem('userUUID') // 或者使用你的状态管理
  const loginUUID = localStorage.getItem('loginUUID') // 或者使用你的状态管理

  axios({
    url: serverUrl + '/file/nothing',
    method: 'GET',
    headers: {
      userUUID: userUUID,
      loginUUID: loginUUID,
    },
  })
    .then((res) => {
      if (res.data !== 'Not logged in') next()
      else {
        localStorage.removeItem('userUUID')
        localStorage.removeItem('loginUUID')
        next('/login')
      }
    })
    .catch((e) => {
      console.log(e)
      next('/login')
    })
  // 如果访问的不是登录页且未登录，重定向到登录页
  if (to.path !== '/login' && (!userUUID || !loginUUID)) {
    next('/login')
  } else next()
})

export default router
