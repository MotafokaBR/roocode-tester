package com.aliasforwarder.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "sync_jobs")
public class SyncJob {

    @Id
    private String id;
    private String userId;
    private String originDomain;
    private String targetDomain;
    private String targetEmail;
    private SyncStatus status;
    private int aliasCount;
    private int createdCount;
    private int skippedCount;
    private int failedCount;
    private Instant startedAt;
    private Instant completedAt;

    public enum SyncStatus {
        PENDING, RUNNING, COMPLETED, FAILED
    }

    public SyncJob() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getOriginDomain() { return originDomain; }
    public void setOriginDomain(String originDomain) { this.originDomain = originDomain; }

    public String getTargetDomain() { return targetDomain; }
    public void setTargetDomain(String targetDomain) { this.targetDomain = targetDomain; }

    public String getTargetEmail() { return targetEmail; }
    public void setTargetEmail(String targetEmail) { this.targetEmail = targetEmail; }

    public SyncStatus getStatus() { return status; }
    public void setStatus(SyncStatus status) { this.status = status; }

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
}
