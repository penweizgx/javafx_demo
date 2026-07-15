package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class StudentVO {
    private Long id;
    private String name;
    private String nickname;
    private String avatar;
    private Integer sex;
    private String bornDate;
    private String regTime;
    private String indate;
    private String status;
    private String outdate;
    private Long clazzId;
    private String clazzname;
    private List<ParentVO> parents;
    private List<StudentClazzHistoryVO> clazzs;
    private String address;
    private String danger;
    private String phone;
}
