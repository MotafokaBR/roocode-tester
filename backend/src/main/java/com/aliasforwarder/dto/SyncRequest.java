package com.aliasforwarder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SyncRequest {

    @NotBlank(message = "Addy.io API key is required")
    private String addyApiKey;

    @NotBlank(message = "MXroute API key is required")
    private String mxrouteApiKey;

    @NotBlank(message = "Origin domain is required")
    private String originDomain;

    @NotBlank(message = "Target domain is required")
    private String targetDomain;

    @NotBlank(message = "Target email is required")
    @Email(message = "Target email must be a valid email address")
    private String targetEmail;

    public SyncRequest() {}

    public String getAddyApiKey() { return addyApiKey; }
    public void setAddyApiKey(String addyApiKey) { this.addyApiKey = addyApiKey; }

    public String getMxrouteApiKey() { return mxrouteApiKey; }
    public void setMxrouteApiKey(String mxrouteApiKey) { this.mxrouteApiKey = mxrouteApiKey; }

    public String getOriginDomain() { return originDomain; }
    public void setOriginDomain(String originDomain) { this.originDomain = originDomain; }

    public String getTargetDomain() { return targetDomain; }
    public void setTargetDomain(String targetDomain) { this.targetDomain = targetDomain; }

    public String getTargetEmail() { return targetEmail; }
    public void setTargetEmail(String targetEmail) { this.targetEmail = targetEmail; }
}
