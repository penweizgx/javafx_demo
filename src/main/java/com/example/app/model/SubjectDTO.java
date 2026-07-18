package com.example.app.model;

import lombok.Data;

@Data
public class SubjectDTO {
    private Long id;
    private String name;
    private String type;
    private Integer day;
    private Integer alloc;
    private String remark;
    private Boolean disabled;
    private String refund;
}
