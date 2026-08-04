package com.example.model;

public enum EventType {
    CREATE_ORDER("Create Order"),
    RECEIVE_PAYMENT("Receive Payment"),
    UPDATE_INVENTORY("Update Inventory"),
    SHIP_ORDER("Ship Order");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
