import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/component/Login.vue'
import UploadFile from '@/component/UploadFile.vue'
import Home from '@/component/Home.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/home',
      component: Home,
      children: [
        {
          path: '/upload-file',
          component: UploadFile,
        }
      ]
    },
    {
      path: '/login',
      component: Login,
    }
  ]
})

export default router
