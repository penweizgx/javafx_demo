package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class StudentMonthAttendDTO {
    private Long studentId;
    private LocalMonth month;
    private Boolean monthArchive;
    private Long archiveId;
    private Integer refund;
    private List<CarryItem> items;
}
