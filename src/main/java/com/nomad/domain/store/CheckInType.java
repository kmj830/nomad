package com.nomad.domain.store;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CheckInType {
    BLE,
    BLE_BEACON,
    BEACON,
    NFC,
    QR,
    MANUAL,
    AUTO;

    @JsonCreator
    public static CheckInType fromString(String key) {
        if (key == null || key.isBlank()) {
            return MANUAL;
        }
        String upper = key.trim().toUpperCase();
        if (upper.contains("BLE") || upper.contains("BEACON")) {
            return BLE;
        }
        if (upper.contains("NFC")) {
            return NFC;
        }
        if (upper.contains("QR")) {
            return QR;
        }
        if (upper.contains("AUTO")) {
            return AUTO;
        }
        for (CheckInType type : values()) {
            if (type.name().equalsIgnoreCase(upper)) {
                return type;
            }
        }
        return MANUAL;
    }
}
