import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
})

// Auth API
export const authApi = {
  getUserInfo: () => api.get('/auth/userinfo'),
  getLoginUrl: () => api.get('/auth/login'),
}

// Sync request payload
export interface SyncRequest {
  addyApiKey: string
  mxrouteApiKey: string
  originDomain: string
  targetDomain: string
  targetEmail: string
}

export interface AliasDto {
  email: string
  description: string
  active: boolean
}

export interface SyncPreviewResponse {
  originDomain: string
  targetDomain: string
  targetEmail: string
  totalAliases: number
  aliases: AliasDto[]
}

export interface SyncJobResponse {
  id: string
  originDomain: string
  targetDomain: string
  targetEmail: string
  status: string
  aliasCount: number
  createdCount: number
  skippedCount: number
  failedCount: number
  startedAt: string
  completedAt: string
  logs?: SyncLogEntry[]
}

export interface SyncLogEntry {
  id: string
  syncJobId: string
  aliasEmail: string
  forwarderCreated: boolean
  status: string
  errorMessage: string | null
  timestamp: string
}

// Sync API
export const syncApi = {
  preview: (data: SyncRequest) => api.post<SyncPreviewResponse>('/sync/preview', data),
  execute: (data: SyncRequest) => api.post<SyncJobResponse>('/sync/execute', data),
  listJobs: () => api.get<SyncJobResponse[]>('/sync/jobs'),
  getJobDetail: (id: string) => api.get<SyncJobResponse>(`/sync/jobs/${id}`),
}

export default api
