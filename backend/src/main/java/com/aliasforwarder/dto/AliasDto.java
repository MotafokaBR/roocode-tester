package com.aliasforwarder.dto;

public class AliasDto {

    private String email;
    private String description;
    private boolean active;

    public AliasDto() {}

    public AliasDto(String email, String description, boolean active) {
        this.email = email;
        this.description = description;
        this.active = active;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
