package com.example.app.model;

import lombok.Data;

@Data
public class CarryItem {
    private Long subjectId;
    private String subjectName;
    private Long billId;
    private Number defalutAmount;
    private Number realAmount;
}
