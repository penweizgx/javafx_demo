package com.example.app.model;

import lombok.Data;

@Data
public class FeeScale {
    private Long id;
    private Integer version;
    private Long modifiedAt;
    private Long createdAt;
    private Subject subject;
    private String name;
    private Number standardAmount;
    private Number specialAmount;
    private String unit;
    private Boolean disabled;
    private String remark;
}
