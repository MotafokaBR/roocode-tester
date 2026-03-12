<template>
  <div>
    <div class="page-header">
      <h1>Sync History</h1>
      <p>View past sync operations and their results</p>
    </div>

    <div v-if="syncStore.error" class="alert alert-error">
      {{ syncStore.error }}
    </div>

    <div v-if="syncStore.loading" class="card" style="text-align: center; padding: 2rem;">
      <div class="spinner" style="border-color: rgba(79,70,229,0.3); border-top-color: var(--primary);"></div>
      <p style="margin-top: 0.5rem; color: var(--text-muted);">Loading...</p>
    </div>

    <div v-else-if="syncStore.jobs.length === 0" class="card" style="text-align: center; padding: 2rem;">
      <p style="color: var(--text-muted);">No sync jobs found. Run your first sync from the Dashboard.</p>
    </div>

    <div v-else class="card">
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Origin Domain</th>
              <th>Target</th>
              <th>Status</th>
              <th>Created</th>
              <th>Skipped</th>
              <th>Failed</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="job in syncStore.jobs" :key="job.id">
              <td>{{ formatDate(job.startedAt) }}</td>
              <td>{{ job.originDomain }}</td>
              <td>{{ job.targetEmail }}</td>
              <td>
                <span :class="statusBadgeClass(job.status)">{{ job.status }}</span>
              </td>
              <td>{{ job.createdCount }}</td>
              <td>{{ job.skippedCount }}</td>
              <td>{{ job.failedCount }}</td>
              <td>
                <router-link :to="`/history/${job.id}`" class="btn btn-secondary" style="padding: 0.25rem 0.75rem;">
                  Details
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useSyncStore } from '@/stores/sync'

const syncStore = useSyncStore()

onMounted(() => {
  syncStore.fetchJobs()
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
    case 'PENDING': return 'badge badge-info'
    default: return 'badge'
  }
}
</script>

<style scoped>
.table-wrapper {
  overflow-x: auto;
}
</style>
