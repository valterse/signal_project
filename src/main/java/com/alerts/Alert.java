package com.alerts;

// Represents an alert
public class Alert {
    private String patientId;
    private String condition;
    private long timestamp;
    private int priority; // Added for Decorator Pattern
    private boolean triggered; // Added for Decorator Pattern

    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
        this.priority = 1; // Default priority
        this.triggered = false;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isTriggered() {
        return triggered;
    }


    //added a trigger function
    public void trigger() {
        this.triggered = true;
        System.out.println("ALERT: Patient ID " + patientId +
                " triggered condition '" + condition +
                "' at timestamp " + timestamp +
                " with priority " + priority);
    }
}