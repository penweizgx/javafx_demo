package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class ChargeFormBuildDTO {
    private Long studentId;
    private String studentName;
    private String clazzName;
    private Number balancesAmount;
    private List<SubjectFeeSale> subjectFeeSales;
    private List<PayChancel> chancels;
    private List<StudentParent> parents;
}
