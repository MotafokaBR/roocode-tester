<template>
  <div>
    <nav v-if="authStore.isAuthenticated">
      <div class="container">
        <router-link to="/" class="nav-brand">Alias Forwarder</router-link>
        <ul class="nav-links">
          <li><router-link to="/">Dashboard</router-link></li>
          <li><router-link to="/history">History</router-link></li>
          <li>
            <a href="#" @click.prevent="authStore.logout()">
              Logout ({{ authStore.user?.name || authStore.user?.email }})
            </a>
          </li>
        </ul>
      </div>
    </nav>
    <main class="container">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  authStore.checkAuth()
})
</script>
