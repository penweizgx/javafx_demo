package com.example.app.model;

import lombok.Data;

@Data
public class Subject {
    private Long id;
    private String name;
    private String type;
    private Long schId;
    private Integer version;
    private Long modifiedAt;
    private Long createdAt;
    private Integer day;
    private Integer alloc;
    private String remark;
    private Boolean disabled;
}
