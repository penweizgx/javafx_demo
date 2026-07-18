package com.example.app.service.impl;

import com.example.app.api.ApiException;
import com.example.app.api.okhttp.FinanceApiServiceImpl;
import com.example.app.exception.ExceptionHandler;
import com.example.app.model.*;
import com.example.app.service.FinanceManageService;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class FinanceManageServiceImpl implements FinanceManageService {

    private final FinanceApiServiceImpl financeApiService;

    @Inject
    public FinanceManageServiceImpl(FinanceApiServiceImpl financeApiService) {
        this.financeApiService = financeApiService;
    }

    @Override
    public CompletableFuture<FinanceBrief> countBrief(Long schId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.countBrief(schId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load finance brief");
                throw new RuntimeException("加载财务简报失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<FinAccountVO>> listByCondition(StudentQO qo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.listByCondition(qo);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load finance accounts");
                throw new RuntimeException("加载学生账户列表失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<FinAccountVO> financeAccount(Long studentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.financeAccount(studentId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load finance account");
                throw new RuntimeException("加载财务信息失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<BillTimeDTO>> chargeBills(Long studentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.chargeBills(studentId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load charge bills");
                throw new RuntimeException("加载收费记录失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<ChargeFormBuildDTO> buildChargeForm(Long studentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.buildChargeForm(studentId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to build charge form");
                throw new RuntimeException("加载收费表单失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Long> charge(ChargeBillDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.charge(dto);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to submit charge");
                throw new RuntimeException("提交收费失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> feeScaleBind(Long studentId, List<Long> ids) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.feeScaleBind(studentId, ids);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to bind fee scale");
                throw new RuntimeException("设置收费标准失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> close(Long studentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.close(studentId);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to close account");
                throw new RuntimeException("离校归档失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<PaginationBillTimeDTO> listBills(FilterBillsPage filter) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.listBills(filter);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load bills");
                throw new RuntimeException("加载费用记录失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<ChargeBillVO> chargeDetail(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.chargeDetail(id);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load charge detail");
                throw new RuntimeException("加载收费详情失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> invalidCharge(Long chargeBillId, String remark) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.invalidCharge(chargeBillId, remark);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to invalidate charge");
                throw new RuntimeException("作废收费单失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> invalidCarry(Long billId, String remark) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.invalidCarry(billId, remark);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to invalidate carry");
                throw new RuntimeException("作废结转单失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<MonthAttendCarryOverVO>> listMonthAttendCarryOver(Long schId, Long clazzId, String month, boolean onlyrefund) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.listMonthAttendCarryOver(schId, clazzId, month, onlyrefund);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load attendance carry-over");
                throw new RuntimeException("加载考勤退费失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> attendCarryOverBill(StudentMonthAttendDTO dto, String remark) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.attendCarryOverBill(dto, remark);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to create attendance carry-over bill");
                throw new RuntimeException("生成退费单失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Map<String, String>> confirmMonthAttendCarryOver(int year, int month, List<MonthAttendCarryOverVO> items) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.confirmMonthAttendCarryOver(year, month, items);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to confirm carry-over");
                throw new RuntimeException("确认退费结转失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<CarryOverConfig> attendCarryOverConfig(Long subjectId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.attendCarryOverConfig(subjectId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load carry-over config");
                throw new RuntimeException("加载退费标准失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> setAttendCarryOverConfig(Long subjectId, Number amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.setAttendCarryOverConfig(subjectId, amount);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to set carry-over config");
                throw new RuntimeException("设置退费标准失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<SubjectWithFeeScaleDTO>> listSubjectWithFeeScale() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.listSubjectWithFeeScale();
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load subjects");
                throw new RuntimeException("加载收费项目失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> addSubject(SubjectDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.addSubject(dto);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to add subject");
                throw new RuntimeException("新增收费项失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> editSubject(SubjectDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.editSubject(dto);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to edit subject");
                throw new RuntimeException("修改收费项失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> disableSubject(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.disableSubject(id);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to disable subject");
                throw new RuntimeException("停用收费项失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> removeSubject(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.removeSubject(id);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to remove subject");
                throw new RuntimeException("删除收费项失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> addFeeScale(Long subjectId, Number standardAmount, String unit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.addFeeScale(subjectId, standardAmount, unit);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to add fee scale");
                throw new RuntimeException("新增收费标准失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> changeFeeScaleName(Long id, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.changeFeeScaleName(id, name);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to rename fee scale");
                throw new RuntimeException("修改标准名称失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> disableFeeScale(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.disableFeeScale(id);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to disable fee scale");
                throw new RuntimeException("停用收费标准失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Map<String, ChargeReportItem>> countChargeReport(Long schId, String month) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.countChargeReport(schId, month);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load charge report");
                throw new RuntimeException("加载收费统计失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<ChargeSubjectReport>> sumChargeSubjectReport(Long schId, String month) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.sumChargeSubjectReport(schId, month);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load subject report");
                throw new RuntimeException("加载收费项统计失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<CarryOverSubjectReport>> sumCarryOverSubjectReport(Long schId, String month) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.sumCarryOverSubjectReport(schId, month);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load carry-over report");
                throw new RuntimeException("加载退费结转统计失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<PayChancelReport>> countPayChancelReport(Long schId, String month) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.countPayChancelReport(schId, month);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load pay chancel report");
                throw new RuntimeException("加载支付方式统计失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<FinanceConfig> getConfig(Long schId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.getConfig(schId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load finance config");
                throw new RuntimeException("加载财务配置失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveConfig(Long schId, FinanceConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.saveConfig(schId, config);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to save finance config");
                throw new RuntimeException("保存财务配置失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<BillNumberRule> getBillNumberRule(Long schId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.getBillNumberRule(schId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load bill number rule");
                throw new RuntimeException("加载票据号规则失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveBillNumberRule(BillNumberRule rule) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.saveBillNumberRule(rule);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to save bill number rule");
                throw new RuntimeException("保存票据号规则失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<PayChancel>> listPayChancels(Long schId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.listPayChancels(schId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load pay chancels");
                throw new RuntimeException("加载支付方式失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> addPayChancel(Long schId, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.addPayChancel(schId, name);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to add pay chancel");
                throw new RuntimeException("新增支付方式失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> removePayChancel(Long schId, Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                financeApiService.removePayChancel(schId, id);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to remove pay chancel");
                throw new RuntimeException("删除支付方式失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Map<String, String>> validate() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return financeApiService.validate();
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to validate accounts");
                throw new RuntimeException("核查账户失败: " + e.getMessage(), e);
            }
        });
    }
}
