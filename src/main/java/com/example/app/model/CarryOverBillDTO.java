package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class CarryOverBillDTO {
    private Long id;
    private Number totalAmount;
    private Long wokerId;
    private String wokerName;
    private String bizDate;
    private InvalidBill invalid;
    private List<CarryItem> subjectItems;
    private Object monthAttend;
    private String createTime;
    private String remark;
}
