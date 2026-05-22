package com.example.app.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Long id;
    private String name;
    private String gender;
    private String birthday;
    private String phone;
    private String email;
    private String orgName;
    private String hireDate;
    private Integer status;
}