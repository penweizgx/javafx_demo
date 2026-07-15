package com.example.app.model;

import lombok.Data;

@Data
public class TeachDTO {
    private Long id;
    private String name;
    private String avatar;
    private Integer sex;
    private Object orgUnit;
    private Long clazzId;
    private String clazzName;
    private String type;
}
