package com.example.app.model;

import lombok.Data;

@Data
public class SubjectItem {
    private Long feeScaleId;
    private DatePeriod period;
    private Integer unitAmount;
    private Number receivableAmount;
    private Number payAmount;
    private Number balancesAmount;
}
