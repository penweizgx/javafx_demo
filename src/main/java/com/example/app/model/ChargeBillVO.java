package com.example.app.model;

import lombok.Data;
import java.util.List;

@Data
public class ChargeBillVO {
    private ChargeBill bill;
    private List<CarryOverBillDTO> carryOverBillList;
}
