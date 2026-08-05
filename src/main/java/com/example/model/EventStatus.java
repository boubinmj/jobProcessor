package com.example.model;

public enum EventStatus {
    QUEUED("Queued"),
    RUNNING("Running"),
    COMPLETED("Completed");

    private final String displayName;

    EventStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
