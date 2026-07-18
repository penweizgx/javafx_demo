package com.example.app.model;

import lombok.Data;

@Data
public class ChargeReportItem {
    private Integer billnumber;
    private Integer studentNum;
    private Number receivableAmount;
    private Number payAmount;
    private Number balancesAmount;
    private Number amount;
}
