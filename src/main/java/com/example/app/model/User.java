package com.example.app.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String avatar;
    private OrgBound orgBound;
    private String orgName;
    private String email;
    private String phone;
    private String status;
    private String role;
    private String createTime;
}
