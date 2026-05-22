package com.example.app.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeListReq {
    private Integer pageNum;
    private Integer pageSize;
    private String name;
    private String phone;
}