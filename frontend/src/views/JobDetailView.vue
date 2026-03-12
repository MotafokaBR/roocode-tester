<template>
  <div>
    <div class="page-header">
      <h1>Job Detail</h1>
      <router-link to="/history" class="btn btn-secondary" style="margin-top: 0.5rem;">
        Back to History
      </router-link>
    </div>

    <div v-if="syncStore.error" class="alert alert-error">
      {{ syncStore.error }}
    </div>

    <div v-if="syncStore.loading" class="card" style="text-align: center; padding: 2rem;">
      <div class="spinner" style="border-color: rgba(79,70,229,0.3); border-top-color: var(--primary);"></div>
    </div>

    <template v-else-if="syncStore.jobDetail">
      <div class="card">
        <h2 style="margin-bottom: 1rem; font-size: 1.125rem;">Summary</h2>
        <div class="summary-grid">
          <div><strong>Origin Domain:</strong> {{ syncStore.jobDetail.originDomain }}</div>
          <div><strong>Target Domain:</strong> {{ syncStore.jobDetail.targetDomain }}</div>
          <div><strong>Target Email:</strong> {{ syncStore.jobDetail.targetEmail }}</div>
          <div>
            <strong>Status:</strong>
            <span :class="statusBadgeClass(syncStore.jobDetail.status)" style="margin-left: 0.5rem;">
              {{ syncStore.jobDetail.status }}
            </span>
          </div>
          <div><strong>Total Aliases:</strong> {{ syncStore.jobDetail.aliasCount }}</div>
          <div><strong>Created:</strong> {{ syncStore.jobDetail.createdCount }}</div>
          <div><strong>Skipped:</strong> {{ syncStore.jobDetail.skippedCount }}</div>
          <div><strong>Failed:</strong> {{ syncStore.jobDetail.failedCount }}</div>
          <div><strong>Started:</strong> {{ formatDate(syncStore.jobDetail.startedAt) }}</div>
          <div><strong>Completed:</strong> {{ formatDate(syncStore.jobDetail.completedAt) }}</div>
        </div>
      </div>

      <div v-if="syncStore.jobDetail.logs?.length" class="card">
        <h2 style="margin-bottom: 1rem; font-size: 1.125rem;">Per-Alias Results</h2>
        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Alias Email</th>
                <th>Status</th>
                <th>Forwarder Created</th>
                <th>Error</th>
                <th>Time</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="log in syncStore.jobDetail.logs" :key="log.id">
                <td>{{ log.aliasEmail }}</td>
                <td>
                  <span :class="logStatusBadgeClass(log.status)">{{ log.status }}</span>
                </td>
                <td>{{ log.forwarderCreated ? 'Yes' : 'No' }}</td>
                <td>{{ log.errorMessage || '-' }}</td>
                <td>{{ formatDate(log.timestamp) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useSyncStore } from '@/stores/sync'

const route = useRoute()
const syncStore = useSyncStore()

onMounted(() => {
  const id = route.params.id as string
  syncStore.fetchJobDetail(id)
})

function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString()
}

function statusBadgeClass(status: string): string {
  switch (status) {
    case 'COMPLETED': return 'badge badge-success'
    case 'RUNNING': return 'badge badge-warning'
    case 'FAILED': return 'badge badge-danger'
    default: return 'badge badge-info'
  }
}

function logStatusBadgeClass(status: string): string {
  switch (status) {
    case 'CREATED': return 'badge badge-success'
    case 'SKIPPED': return 'badge badge-info'
    case 'FAILED': return 'badge badge-danger'
    default: return 'badge'
  }
}
</script>

<style scoped>
.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  font-size: 0.875rem;
}

.table-wrapper {
  overflow-x: auto;
}

@media (max-width: 640px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
