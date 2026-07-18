package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class SubjectWithFeeScaleDTO {
    private Long id;
    private String name;
    private String type;
    private Integer day;
    private Integer alloc;
    private String remark;
    private Boolean disabled;
    private String refund;
    private List<FeeScaleDTO> feeScales;
}
