package com.sb.mini_bank.model.enums;

public enum Currency {
    EUR("Euro"),
    LEKE("Leke"),
    USD("Dollar");

    private final String displayName;

    Currency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
