package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class MonthAttendCarryOverVO {
    private Long archiveId;
    private Long studentId;
    private String studentName;
    private String clazzName;
    private Integer miss;
    private Integer leave;
    private Integer refund;
    private List<CarryItem> items;
}
