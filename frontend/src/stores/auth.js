import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const userRole = ref(localStorage.getItem('userRole') || '')
  const phone = ref(localStorage.getItem('phone') || '')

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => userRole.value === 'ADMIN')

  function setUser(loginResponse) {
    token.value = loginResponse.token
    username.value = loginResponse.username
    userRole.value = loginResponse.role
    phone.value = loginResponse.phone || ''
    
    localStorage.setItem('token', loginResponse.token)
    localStorage.setItem('username', loginResponse.username)
    localStorage.setItem('userRole', loginResponse.role)
    localStorage.setItem('phone', loginResponse.phone || '')
    
    // Set default authorization header
    api.defaults.headers.common['Authorization'] = `Bearer ${loginResponse.token}`
  }

  function clearUser() {
    token.value = ''
    username.value = ''
    userRole.value = ''
    phone.value = ''
    
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('userRole')
    localStorage.removeItem('phone')
    
    delete api.defaults.headers.common['Authorization']
  }

  function initializeAuth() {
    if (token.value) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
    }
  }

  return {
    token,
    username,
    userRole,
    phone,
    isAuthenticated,
    isAdmin,
    setUser,
    clearUser,
    initializeAuth
  }
})