// stores/FileURL.ts
import { defineStore } from 'pinia'

export const useFileUrlStore = defineStore('fileUrl', {
  state() {
    return {
      fileUrl: '',
    }
  },
})
