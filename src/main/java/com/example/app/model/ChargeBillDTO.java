package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class ChargeBillDTO {
    private Long id;
    private Long schId;
    private Long studentId;
    private Number totalAmount;
    private Long wokerId;
    private String bizDate;
    private InvalidBill invalid;
    private String remark;
    private Long createdAt;
    private Number payAmount;
    private Number deductionAmount;
    private String payUserId;
    private String payUserName;
    private List<SubjectItem> subjectItems;
    private List<ChancelAmount> payChancels;
    private Long depositBillId;
}
