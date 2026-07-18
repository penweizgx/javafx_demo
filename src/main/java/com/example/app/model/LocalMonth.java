package com.example.app.model;

import lombok.Data;

@Data
public class LocalMonth {
    private Integer year;
    private Integer month;
    private DatePeriod datePeriod;
    private Integer endDay;
}
