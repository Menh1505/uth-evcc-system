package com.evcc.evcc.entity;

public enum GroupRole {
    OWNER("Owner"),
    ADMIN("Administrator"), 
    MEMBER("Member"),
    VIEWER("Viewer");
    
    private final String displayName;
    
    GroupRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
