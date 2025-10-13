package com.evcc.group.entity;

public enum GroupRole {
    CO_OWNER("CoOwner"),
    GROUP_ADMIN("GroupAdmin");
    
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
