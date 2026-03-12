package com.aliasforwarder.dto;

import com.aliasforwarder.model.SyncJob;
import com.aliasforwarder.model.SyncLog;

import java.time.Instant;
import java.util.List;

public class SyncJobResponse {

    private String id;
    private String originDomain;
    private String targetDomain;
    private String targetEmail;
    private SyncJob.SyncStatus status;
    private int aliasCount;
    private int createdCount;
    private int skippedCount;
    private int failedCount;
    private Instant startedAt;
    private Instant completedAt;
    private List<SyncLog> logs;

    public static SyncJobResponse fromJob(SyncJob job) {
        SyncJobResponse response = new SyncJobResponse();
        response.setId(job.getId());
        response.setOriginDomain(job.getOriginDomain());
        response.setTargetDomain(job.getTargetDomain());
        response.setTargetEmail(job.getTargetEmail());
        response.setStatus(job.getStatus());
        response.setAliasCount(job.getAliasCount());
        response.setCreatedCount(job.getCreatedCount());
        response.setSkippedCount(job.getSkippedCount());
        response.setFailedCount(job.getFailedCount());
        response.setStartedAt(job.getStartedAt());
        response.setCompletedAt(job.getCompletedAt());
        return response;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOriginDomain() { return originDomain; }
    public void setOriginDomain(String originDomain) { this.originDomain = originDomain; }

    public String getTargetDomain() { return targetDomain; }
    public void setTargetDomain(String targetDomain) { this.targetDomain = targetDomain; }

    public String getTargetEmail() { return targetEmail; }
    public void setTargetEmail(String targetEmail) { this.targetEmail = targetEmail; }

    public SyncJob.SyncStatus getStatus() { return status; }
    public void setStatus(SyncJob.SyncStatus status) { this.status = status; }

    public int getAliasCount() { return aliasCount; }
    public void setAliasCount(int aliasCount) { this.aliasCount = aliasCount; }

    public int getCreatedCount() { return createdCount; }
    public void setCreatedCount(int createdCount) { this.createdCount = createdCount; }

    public int getSkippedCount() { return skippedCount; }
    public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }

    public int getFailedCount() { return failedCount; }
    public void setFailedCount(int failedCount) { this.failedCount = failedCount; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public List<SyncLog> getLogs() { return logs; }
    public void setLogs(List<SyncLog> logs) { this.logs = logs; }
}
