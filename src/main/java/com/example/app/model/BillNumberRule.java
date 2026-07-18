package com.example.app.model;

import lombok.Data;

@Data
public class BillNumberRule {
    private Long schId;
    private Integer length;
    private String prefix;
    private String start;
    private Long serialno;
}
