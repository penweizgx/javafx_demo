package com.example.app.model;

import lombok.Data;

@Data
public class FeeScale {
    private Long id;
    private String version;
    private String modifiedAt;
    private String createdAt;
    private Subject subject;
    private String name;
    private Number standardAmount;
    private Number specialAmount;
    private String unit;
    private Boolean disabled;
    private String remark;
}
