package com.example.CRMGym.models;
public enum TrainingType {
    CARDIO("Cardio"),
    STRENGTH_TRAINING("Strength Training"),
    YOGA("Yoga"),
    PILATES("Pilates"),
    CROSSFIT("CrossFit"),
    BOXING("Boxing"),
    DANCE("Dance"),
    HIIT("HIIT"),
    SPINNING("Spinning");

    private final String displayName;

    TrainingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
