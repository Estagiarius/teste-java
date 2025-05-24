package com.teacheragenda.model;

public enum Priority {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High");

    private final int value;
    private final String displayName;

    Priority(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Priority fromValue(int value) {
        for (Priority priority : values()) {
            if (priority.value == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority value: " + value);
    }

    public static Priority fromDisplayName(String displayName) {
        for (Priority priority : values()) {
            if (priority.displayName.equalsIgnoreCase(displayName)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority display name: " + displayName);
    }
}
