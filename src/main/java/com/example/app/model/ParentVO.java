package com.example.app.model;

import lombok.Data;

@Data
public class ParentVO {
    private Long id;
    private String name;
    private String avatar;
    private String phone;
    private Boolean master;
    private Integer relationship;
    private Integer sex;
    private String idCard;
}
