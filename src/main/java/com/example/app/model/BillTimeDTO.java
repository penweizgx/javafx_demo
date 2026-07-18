package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class BillTimeDTO {
    private Long id;
    private Long schId;
    private Long studentId;
    private Number totalAmount;
    private Long wokerId;
    private String bizDate;
    private InvalidBill invalid;
    private String remark;
    private Long createdAt;
    private String type;
    private String payUserName;
    private Number deductionAmount;
    private Number realAmount;
    private String studentName;
    private String clazzName;
    private Long chargeBillId;
    private Number balancesAmount;
    private List<SubjectItem> subjectItems;
    private List<ChancelAmount> payChancels;
    private List<CarryItem> carryOverSubjectItems;
}
