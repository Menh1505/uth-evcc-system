package com.evcc.group.entity;

public enum MembershipStatus {
    ACTIVE("Active"),
    LEFT("Left"),
    KICKED("Kicked");
    
    private final String displayName;
    
    MembershipStatus(String displayName) {
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
