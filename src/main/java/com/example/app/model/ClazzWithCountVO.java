package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class ClazzWithCountVO {
    private Long id;
    private String name;
    private Object capacity;
    private List<TeachDTO> teachers;
    private Object period;
    private Object vacancy;
    private Integer activeNum;
    private Integer leaveNum;
    private Integer regNum;
    private String status;
}
