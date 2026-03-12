import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/client'

interface UserInfo {
  sub: string
  name: string
  email: string
  authenticated: boolean
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserInfo | null>(null)
  const loading = ref(true)

  const isAuthenticated = computed(() => user.value?.authenticated === true)

  async function checkAuth() {
    loading.value = true
    try {
      const response = await authApi.getUserInfo()
      if (response.data.authenticated) {
        user.value = response.data
      } else {
        user.value = null
      }
    } catch {
      user.value = null
    } finally {
      loading.value = false
    }
  }

  async function login() {
    try {
      const response = await authApi.getLoginUrl()
      window.location.href = response.data.loginUrl
    } catch {
      // Fallback to the default OIDC login URL
      window.location.href = '/oauth2/authorization/oidc-provider'
    }
  }

  function logout() {
    window.location.href = '/api/auth/logout'
  }

  return { user, loading, isAuthenticated, checkAuth, login, logout }
})
