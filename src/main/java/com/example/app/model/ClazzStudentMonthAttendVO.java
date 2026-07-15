package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class ClazzStudentMonthAttendVO {
    private Long clazzId;
    private String month;
    private List<StudentMonthAttend> students;
}
