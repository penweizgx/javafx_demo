package com.example.app.model;

import lombok.Data;

@Data
public class StudentMonthAttend {
    private Long studentId;
    private String studentName;
    private Integer attendDays;
    private Integer leaveDays;
    private Integer absentDays;
}
