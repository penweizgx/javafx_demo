package com.example.app.model;

import lombok.Data;

@Data
public class ChargeSubjectReport {
    private Long subjectId;
    private String subjectName;
    private Number receivableAmount;
    private Number payAmount;
    private Number balancesAmount;
}
