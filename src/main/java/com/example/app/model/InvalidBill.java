package com.example.app.model;

import lombok.Data;

@Data
public class InvalidBill {
    private Long wokerId;
    private String date;
    private String remark;
}
