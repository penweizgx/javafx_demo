package com.example.app.model;

import lombok.Data;

@Data
public class ClazzDayAttendVO {
    private Long clazzId;
    private String clazzName;
    private Integer total;
    private Integer attend;
    private Integer leave;
    private Integer absent;
}
