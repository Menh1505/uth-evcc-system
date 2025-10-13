package com.evcc.group.entity;

public enum MembershipStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PENDING("Pending"),
    LEFT("Left"),
    KICKED("Kicked"),
    BANNED("Banned");
    
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
