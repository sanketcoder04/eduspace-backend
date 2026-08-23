package com.example.eduspace.opportunity.enums;

public enum OpportunityStatus {
    OPEN,
    PARTIALLY_FILLED, // batch with some seats taken
    CLOSED,           // fully filled or manually closed by author
    EXPIRED
}