package com.example.app.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class FinAccountVO {
    private Long id;
    private Integer version;
    private Long modifiedAt;
    private Long createdAt;
    private Long studentId;
    private Number refundAmount;
    private Number totalAmount;
    private Number balancesAmount;
    private List<FeeScale> feeScales;
    private StudentVO student;
    private Map<String, String> subjectExpireDate;
    private Boolean closed;
}
