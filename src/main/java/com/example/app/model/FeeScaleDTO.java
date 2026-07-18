package com.example.app.model;

import lombok.Data;

@Data
public class FeeScaleDTO {
    private Long id;
    private String name;
    private Number standardAmount;
    private Number specialAmount;
    private String unit;
    private Boolean disabled;
    private String remark;
    private Integer used;
}
