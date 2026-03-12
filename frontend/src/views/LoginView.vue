<template>
  <div class="login-page">
    <div class="login-card card">
      <h1>Alias Forwarder</h1>
      <p>Sync your Addy.io aliases to MXroute forwarders</p>
      <button class="btn btn-primary" @click="authStore.login()">
        Sign in with OIDC
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 80vh;
}

.login-card {
  text-align: center;
  max-width: 400px;
  width: 100%;
}

.login-card h1 {
  font-size: 1.75rem;
  margin-bottom: 0.5rem;
  color: var(--primary);
}

.login-card p {
  color: var(--text-muted);
  margin-bottom: 1.5rem;
}

.login-card .btn {
  width: 100%;
  justify-content: center;
  padding: 0.75rem;
  font-size: 1rem;
}
</style>
