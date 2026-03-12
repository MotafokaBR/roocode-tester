package com.aliasforwarder.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "sync_logs")
public class SyncLog {

    @Id
    private String id;
    private String syncJobId;
    private String aliasEmail;
    private boolean forwarderCreated;
    private String status;
    private String errorMessage;
    private Instant timestamp;

    public SyncLog() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSyncJobId() { return syncJobId; }
    public void setSyncJobId(String syncJobId) { this.syncJobId = syncJobId; }

    public String getAliasEmail() { return aliasEmail; }
    public void setAliasEmail(String aliasEmail) { this.aliasEmail = aliasEmail; }

    public boolean isForwarderCreated() { return forwarderCreated; }
    public void setForwarderCreated(boolean forwarderCreated) { this.forwarderCreated = forwarderCreated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
