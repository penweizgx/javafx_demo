package com.example.app.service;

import com.example.app.model.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface FinanceManageService {
    CompletableFuture<FinanceBrief> countBrief(Long schId);
    CompletableFuture<List<FinAccountVO>> listByCondition(StudentQO qo);
    CompletableFuture<FinAccountVO> financeAccount(Long studentId);
    CompletableFuture<List<BillTimeDTO>> chargeBills(Long studentId);
    CompletableFuture<ChargeFormBuildDTO> buildChargeForm(Long studentId);
    CompletableFuture<Long> charge(ChargeBillDTO dto);
    CompletableFuture<Void> feeScaleBind(Long studentId, List<Long> ids);
    CompletableFuture<Void> close(Long studentId);
    CompletableFuture<PaginationBillTimeDTO> listBills(FilterBillsPage filter);
    CompletableFuture<ChargeBillVO> chargeDetail(Long id);
    CompletableFuture<Void> invalidCharge(Long chargeBillId, String remark);
    CompletableFuture<Void> invalidCarry(Long billId, String remark);
    CompletableFuture<List<MonthAttendCarryOverVO>> listMonthAttendCarryOver(Long schId, Long clazzId, String month, boolean onlyrefund);
    CompletableFuture<Void> attendCarryOverBill(StudentMonthAttendDTO dto, String remark);
    CompletableFuture<Map<String, String>> confirmMonthAttendCarryOver(int year, int month, List<MonthAttendCarryOverVO> items);
    CompletableFuture<CarryOverConfig> attendCarryOverConfig(Long subjectId);
    CompletableFuture<Void> setAttendCarryOverConfig(Long subjectId, Number amount);
    CompletableFuture<List<SubjectWithFeeScaleDTO>> listSubjectWithFeeScale();
    CompletableFuture<Void> addSubject(SubjectDTO dto);
    CompletableFuture<Void> editSubject(SubjectDTO dto);
    CompletableFuture<Void> disableSubject(Long id);
    CompletableFuture<Void> removeSubject(Long id);
    CompletableFuture<Void> addFeeScale(Long subjectId, Number standardAmount, String unit);
    CompletableFuture<Void> changeFeeScaleName(Long id, String name);
    CompletableFuture<Void> disableFeeScale(Long id);
    CompletableFuture<Map<String, ChargeReportItem>> countChargeReport(Long schId, String month);
    CompletableFuture<List<ChargeSubjectReport>> sumChargeSubjectReport(Long schId, String month);
    CompletableFuture<List<CarryOverSubjectReport>> sumCarryOverSubjectReport(Long schId, String month);
    CompletableFuture<List<PayChancelReport>> countPayChancelReport(Long schId, String month);
    CompletableFuture<FinanceConfig> getConfig(Long schId);
    CompletableFuture<Void> saveConfig(Long schId, FinanceConfig config);
    CompletableFuture<BillNumberRule> getBillNumberRule(Long schId);
    CompletableFuture<Void> saveBillNumberRule(BillNumberRule rule);
    CompletableFuture<List<PayChancel>> listPayChancels(Long schId);
    CompletableFuture<Void> addPayChancel(Long schId, String name);
    CompletableFuture<Void> removePayChancel(Long schId, Long id);
    CompletableFuture<Map<String, String>> validate();
}
