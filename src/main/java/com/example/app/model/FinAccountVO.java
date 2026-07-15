package com.example.app.model;

import lombok.Data;

@Data
public class FinAccountVO {
    private Long studentId;
    private String totalAmount;
    private String balancesAmount;
    private String refundAmount;
    private Boolean closed;
}
