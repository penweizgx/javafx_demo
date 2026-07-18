package com.example.app.model;

import lombok.Data;

@Data
public class FinanceBrief {
    private Long schId;
    private String dataTime;
    private Integer expirePerson;
    private Integer expireSubject;
    private Integer remindPerson;
    private Integer remindSubject;
    private Long depositNum;
    private Number depositAmount;
    private Integer freePerson;
}
