<template>
  <div>
    <div class="page-header">
      <h1>Sync Dashboard</h1>
      <p>Enter your API keys and domains to sync aliases from Addy.io to MXroute forwarders</p>
    </div>

    <div v-if="syncStore.error" class="alert alert-error">
      {{ syncStore.error }}
    </div>

    <div v-if="syncStore.syncResult" class="alert alert-success">
      Sync completed: {{ syncStore.syncResult.createdCount }} created,
      {{ syncStore.syncResult.skippedCount }} skipped,
      {{ syncStore.syncResult.failedCount }} failed
    </div>

    <!-- Sync Form -->
    <div class="card">
      <h2 style="margin-bottom: 1rem; font-size: 1.125rem;">Configuration</h2>

      <div class="form-row">
        <div class="form-group">
          <label for="addyApiKey">Addy.io API Key</label>
          <input id="addyApiKey" v-model="form.addyApiKey" type="password"
                 placeholder="Your Addy.io API key (session only, never stored)" />
        </div>
        <div class="form-group">
          <label for="mxrouteApiKey">MXroute API Key</label>
          <input id="mxrouteApiKey" v-model="form.mxrouteApiKey" type="password"
                 placeholder="Your MXroute API key (session only, never stored)" />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="originDomain">Origin Domain (Addy.io)</label>
          <input id="originDomain" v-model="form.originDomain" type="text"
                 placeholder="e.g., privacy.example.com" />
        </div>
        <div class="form-group">
          <label for="targetDomain">Target Domain (MXroute)</label>
          <input id="targetDomain" v-model="form.targetDomain" type="text"
                 placeholder="e.g., mail.example.com" />
        </div>
      </div>

      <div class="form-group">
        <label for="targetEmail">Target Email (forwarding destination)</label>
        <input id="targetEmail" v-model="form.targetEmail" type="email"
               placeholder="e.g., inbox@example.com" />
      </div>

      <div class="btn-group">
        <button class="btn btn-secondary" @click="handlePreview" :disabled="syncStore.loading || !isFormValid">
          <span v-if="syncStore.loading && previewing" class="spinner"></span>
          Preview
        </button>
        <button class="btn btn-primary" @click="handleExecute"
                :disabled="syncStore.loading || !isFormValid || !syncStore.previewResult">
          <span v-if="syncStore.loading && !previewing" class="spinner"></span>
          Sync Now
        </button>
      </div>
    </div>

    <!-- Preview Results -->
    <div v-if="syncStore.previewResult" class="card">
      <h2 style="margin-bottom: 1rem; font-size: 1.125rem;">
        Preview: {{ syncStore.previewResult.totalAliases }} aliases found
      </h2>
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Alias Email</th>
              <th>Description</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="alias in syncStore.previewResult.aliases" :key="alias.email">
              <td>{{ alias.email }}</td>
              <td>{{ alias.description || '-' }}</td>
              <td>
                <span :class="alias.active ? 'badge badge-success' : 'badge badge-warning'">
                  {{ alias.active ? 'Active' : 'Inactive' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Sync Result Logs -->
    <div v-if="syncStore.syncResult?.logs?.length" class="card">
      <h2 style="margin-bottom: 1rem; font-size: 1.125rem;">Sync Results</h2>
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Alias Email</th>
              <th>Status</th>
              <th>Error</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="log in syncStore.syncResult.logs" :key="log.id">
              <td>{{ log.aliasEmail }}</td>
              <td>
                <span :class="statusBadgeClass(log.status)">{{ log.status }}</span>
              </td>
              <td>{{ log.errorMessage || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import { useSyncStore } from '@/stores/sync'

const syncStore = useSyncStore()
const previewing = ref(false)

const form = reactive({
  addyApiKey: '',
  mxrouteApiKey: '',
  originDomain: '',
  targetDomain: '',
  targetEmail: '',
})

const isFormValid = computed(() =>
  form.addyApiKey && form.mxrouteApiKey && form.originDomain &&
  form.targetDomain && form.targetEmail
)

async function handlePreview() {
  previewing.value = true
  syncStore.clearResults()
  await syncStore.preview({ ...form })
  previewing.value = false
}

async function handleExecute() {
  previewing.value = false
  await syncStore.execute({ ...form })
}

function statusBadgeClass(status: string): string {
  switch (status) {
    case 'CREATED': return 'badge badge-success'
    case 'SKIPPED': return 'badge badge-info'
    case 'FAILED': return 'badge badge-danger'
    default: return 'badge'
  }
}
</script>

<style scoped>
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.btn-group {
  display: flex;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.table-wrapper {
  overflow-x: auto;
}

@media (max-width: 640px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
