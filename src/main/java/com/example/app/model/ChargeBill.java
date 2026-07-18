package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class ChargeBill {
    private Long id;
    private Integer version;
    private Long modifiedAt;
    private Long createdAt;
    private Long schId;
    private Long studentId;
    private Number totalAmount;
    private Long wokerId;
    private String bizDate;
    private InvalidBill invalid;
    private String remark;
    private Number payAmount;
    private Number deductionAmount;
    private String payUserId;
    private String payUserName;
    private List<SubjectItem> subjectItems;
    private List<ChancelAmount> payChancels;
    private Boolean valid;
}
