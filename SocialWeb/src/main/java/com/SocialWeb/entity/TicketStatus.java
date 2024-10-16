package com.SocialWeb.entity;


public enum TicketStatus {
    IN_PROGRESS("In progress"),
    CLOSED("Closed");

    private final String displayValue;

    TicketStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public String toString() {
        return this.displayValue;
    }
}
