package com.aliasforwarder.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "alias_cache")
public class AliasCache {

    @Id
    private String id;
    private String userId;
    private String originDomain;
    private String aliasEmail;
    private String description;
    private boolean active;
    private Instant lastSeenAt;

    public AliasCache() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getOriginDomain() { return originDomain; }
    public void setOriginDomain(String originDomain) { this.originDomain = originDomain; }

    public String getAliasEmail() { return aliasEmail; }
    public void setAliasEmail(String aliasEmail) { this.aliasEmail = aliasEmail; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(Instant lastSeenAt) { this.lastSeenAt = lastSeenAt; }
}
