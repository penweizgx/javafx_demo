package com.example.app.model;

import lombok.Data;

@Data
public class StudentClazzHistoryVO {
    private Long clazzId;
    private DatePeriod period;
    private String clazzName;
    private String remark;
}
