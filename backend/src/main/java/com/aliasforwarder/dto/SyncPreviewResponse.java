package com.aliasforwarder.dto;

import java.util.List;

public class SyncPreviewResponse {

    private String originDomain;
    private String targetDomain;
    private String targetEmail;
    private int totalAliases;
    private List<AliasDto> aliases;

    public SyncPreviewResponse() {}

    public String getOriginDomain() { return originDomain; }
    public void setOriginDomain(String originDomain) { this.originDomain = originDomain; }

    public String getTargetDomain() { return targetDomain; }
    public void setTargetDomain(String targetDomain) { this.targetDomain = targetDomain; }

    public String getTargetEmail() { return targetEmail; }
    public void setTargetEmail(String targetEmail) { this.targetEmail = targetEmail; }

    public int getTotalAliases() { return totalAliases; }
    public void setTotalAliases(int totalAliases) { this.totalAliases = totalAliases; }

    public List<AliasDto> getAliases() { return aliases; }
    public void setAliases(List<AliasDto> aliases) { this.aliases = aliases; }
}
