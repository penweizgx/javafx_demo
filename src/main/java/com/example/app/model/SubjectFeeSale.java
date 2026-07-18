package com.example.app.model;

import lombok.Data;

@Data
public class SubjectFeeSale {
    private Long id;
    private String subjectName;
    private String subjectType;
    private Number amount;
    private String unit;
    private String lastDate;
}
