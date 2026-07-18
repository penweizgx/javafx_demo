package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class PaginationBillTimeDTO {
    private Integer current;
    private Integer pageSize;
    private Long total;
    private Long pages;
    private List<BillTimeDTO> content;
}
