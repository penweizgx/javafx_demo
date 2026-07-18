package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class FilterBillsPage {
    private Integer current;
    private Integer pageSize;
    private Long total;
    private Long pages;
    private String searchWord;
    private Long schId;
    private Long clazzId;
    private DatePeriod period;
    private String billType;
    private Boolean filterInvalid;
}
