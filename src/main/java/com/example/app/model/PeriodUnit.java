package com.example.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PeriodUnit {
    ONCE(0, "次"),
    M(1, "月"),
    Q(3, "季度"),
    T(5, "学期"),
    HY(6, "半年"),
    FY(12, "年"),
    Y(12, "年");

    private final int monthCount;
    private final String label;

    public static PeriodUnit fromString(String value) {
        if (value == null) return null;
        for (PeriodUnit u : values()) {
            if (u.name().equalsIgnoreCase(value)) return u;
        }
        return null;
    }

    public String formatAmount(Number amount) {
        if (amount == null) return "-";
        return amount + "/" + label;
    }
}
