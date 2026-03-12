import { defineStore } from 'pinia'
import { ref } from 'vue'
import { syncApi, type SyncRequest, type SyncPreviewResponse, type SyncJobResponse } from '@/api/client'

export const useSyncStore = defineStore('sync', () => {
  const previewResult = ref<SyncPreviewResponse | null>(null)
  const syncResult = ref<SyncJobResponse | null>(null)
  const jobs = ref<SyncJobResponse[]>([])
  const jobDetail = ref<SyncJobResponse | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function preview(request: SyncRequest) {
    loading.value = true
    error.value = null
    previewResult.value = null
    try {
      const response = await syncApi.preview(request)
      previewResult.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || e.message || 'Preview failed'
    } finally {
      loading.value = false
    }
  }

  async function execute(request: SyncRequest) {
    loading.value = true
    error.value = null
    syncResult.value = null
    try {
      const response = await syncApi.execute(request)
      syncResult.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || e.message || 'Sync failed'
    } finally {
      loading.value = false
    }
  }

  async function fetchJobs() {
    loading.value = true
    error.value = null
    try {
      const response = await syncApi.listJobs()
      jobs.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || e.message || 'Failed to load jobs'
    } finally {
      loading.value = false
    }
  }

  async function fetchJobDetail(id: string) {
    loading.value = true
    error.value = null
    jobDetail.value = null
    try {
      const response = await syncApi.getJobDetail(id)
      jobDetail.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || e.message || 'Failed to load job detail'
    } finally {
      loading.value = false
    }
  }

  function clearResults() {
    previewResult.value = null
    syncResult.value = null
    error.value = null
  }

  return {
    previewResult, syncResult, jobs, jobDetail,
    loading, error,
    preview, execute, fetchJobs, fetchJobDetail, clearResults,
  }
})
