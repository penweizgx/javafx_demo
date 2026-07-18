package com.example.app.api.okhttp;

import com.example.app.api.ApiException;
import com.example.app.api.ApiUrl;
import com.example.app.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class FinanceApiServiceImpl extends OkHttpApiServiceImpl {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Integer.class, new LenientIntegerAdapter())
            .registerTypeAdapter(Long.class, new LenientLongAdapter())
            .registerTypeAdapter(int.class, new LenientIntegerAdapter())
            .registerTypeAdapter(long.class, new LenientLongAdapter())
            .create();

    @Inject
    public FinanceApiServiceImpl() {
        initHttp();
    }

    public FinanceBrief countBrief(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("schId", schId);
        String url = ApiUrl.Finance.COUNT_BRIEF.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        return extractResBodyAs(response, FinanceBrief.class);
    }

    public List<FinAccountVO> listByCondition(StudentQO qo) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (qo.getSchId() != null) queryParams.put("schId", qo.getSchId());
        if (qo.getClazzId() != null) queryParams.put("clazzId", qo.getClazzId());
        if (qo.getKeyword() != null && !qo.getKeyword().isEmpty()) queryParams.put("keyword", qo.getKeyword());
        if (qo.getIncludeGraduated() != null) queryParams.put("includeGraduated", qo.getIncludeGraduated());
        String url = ApiUrl.Finance.LIST_BY_CONDITION.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyList();
        if (!resbody.isJsonArray()) {
            log.warn("listByCondition resbody is not an array: {}", resbody);
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<FinAccountVO>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public FinAccountVO financeAccount(Long studentId) throws ApiException {
        String url = ApiUrl.Finance.ACCOUNT.getUrl(configStorage) + "/" + studentId;
        String response = (String) this.get(url);
        return extractResBodyAs(response, FinAccountVO.class);
    }

    public List<BillTimeDTO> chargeBills(Long studentId) throws ApiException {
        String url = ApiUrl.Finance.CHARGE_BILLS.getUrl(configStorage) + "/" + studentId;
        String response = (String) this.get(url);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyList();
        Type listType = new TypeToken<List<BillTimeDTO>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public ChargeFormBuildDTO buildChargeForm(Long studentId) throws ApiException {
        String url = ApiUrl.Finance.BUILD_CHARGE_FORM.getUrl(configStorage) + "/" + studentId;
        String response = (String) this.get(url);
        return extractResBodyAs(response, ChargeFormBuildDTO.class);
    }

    public Long charge(ChargeBillDTO dto) throws ApiException {
        Map<String, Object> params = toChargeBillParamMap(dto);
        String url = ApiUrl.Finance.CHARGE.getUrl(configStorage);
        String response = (String) this.postJSON(url, params);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody != null && !resbody.isJsonNull()) {
            return resbody.getAsLong();
        }
        return null;
    }

    public void feeScaleBind(Long studentId, List<Long> ids) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", studentId);
        params.put("ids", ids);
        String url = ApiUrl.Finance.FEE_SCALE_BIND.getUrl(configStorage);
        this.post(url, params);
    }

    public void close(Long studentId) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", studentId);
        String url = ApiUrl.Finance.CLOSE.getUrl(configStorage);
        this.post(url, params);
    }

    public PaginationBillTimeDTO listBills(FilterBillsPage filter) throws ApiException {
        Map<String, Object> params = toFilterBillsParamMap(filter);
        String url = ApiUrl.Finance.BILLS_LIST.getUrl(configStorage);
        String response = (String) this.postJSON(url, params);
        return extractResBodyAs(response, PaginationBillTimeDTO.class);
    }

    public ChargeBillVO chargeDetail(Long id) throws ApiException {
        String url = ApiUrl.Finance.CHARGE_DETAIL.getUrl(configStorage) + "/" + id;
        String response = (String) this.get(url);
        return extractResBodyAs(response, ChargeBillVO.class);
    }

    public void invalidCharge(Long chargeBillId, String remark) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("chargeBillId", chargeBillId);
        params.put("remark", remark);
        String url = ApiUrl.Finance.INVALID_CHARGE.getUrl(configStorage);
        this.post(url, params);
    }

    public void invalidCarry(Long billId, String remark) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("billId", billId);
        params.put("remark", remark);
        String url = ApiUrl.Finance.INVALID_CARRY.getUrl(configStorage);
        this.post(url, params);
    }

    public List<MonthAttendCarryOverVO> listMonthAttendCarryOver(Long schId, Long clazzId, String month, boolean onlyrefund) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("schId", schId);
        queryParams.put("clazzId", clazzId);
        queryParams.put("month", month);
        queryParams.put("onlyrefund", onlyrefund);
        String url = ApiUrl.Finance.LIST_MONTH_ATTEND_CARRY_OVER.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyList();
        Type listType = new TypeToken<List<MonthAttendCarryOverVO>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public void attendCarryOverBill(StudentMonthAttendDTO dto, String remark) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("monthAttendDTO", dto);
        params.put("remark", remark);
        String url = ApiUrl.Finance.ATTEND_CARRY_OVER_BILL.getUrl(configStorage);
        this.post(url, params);
    }

    public Map<String, String> confirmMonthAttendCarryOver(int year, int month, List<MonthAttendCarryOverVO> items) throws ApiException {
        String url = ApiUrl.Finance.CONFIRM_MONTH_ATTEND_CARRY_OVER.getUrl(configStorage) + "/" + year + "/" + month;
        Map<String, Object> params = new HashMap<>();
        params.put("items", items);
        String response = (String) this.postJSON(url, params);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyMap();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(resbody, mapType);
    }

    public CarryOverConfig attendCarryOverConfig(Long subjectId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("subjectId", subjectId);
        String url = ApiUrl.Finance.ATTEND_CARRY_OVER_CONFIG.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        return extractResBodyAs(response, CarryOverConfig.class);
    }

    public void setAttendCarryOverConfig(Long subjectId, Number amount) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("subjectId", subjectId);
        params.put("amount", amount);
        String url = ApiUrl.Finance.ATTEND_CARRY_OVER_CONFIG.getUrl(configStorage);
        this.post(url, params);
    }

    public List<SubjectWithFeeScaleDTO> listSubjectWithFeeScale() throws ApiException {
        String url = ApiUrl.Finance.SUBJECT_LIST.getUrl(configStorage);
        String response = (String) this.get(url);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyList();
        Type listType = new TypeToken<List<SubjectWithFeeScaleDTO>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public void addSubject(SubjectDTO dto) throws ApiException {
        Map<String, Object> params = toSubjectDTOParamMap(dto);
        String url = ApiUrl.Finance.SUBJECT_ADD.getUrl(configStorage);
        this.post(url, params);
    }

    public void editSubject(SubjectDTO dto) throws ApiException {
        Map<String, Object> params = toSubjectDTOParamMap(dto);
        String url = ApiUrl.Finance.SUBJECT_EDIT.getUrl(configStorage);
        this.post(url, params);
    }

    public void disableSubject(Long id) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        String url = ApiUrl.Finance.SUBJECT_DISABLE.getUrl(configStorage);
        this.post(url, params);
    }

    public void removeSubject(Long id) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        String url = ApiUrl.Finance.SUBJECT_REMOVE.getUrl(configStorage);
        this.post(url, params);
    }

    public void addFeeScale(Long subjectId, Number standardAmount, String unit) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("subjectId", subjectId);
        params.put("standardAmount", standardAmount);
        params.put("unit", unit);
        String url = ApiUrl.Finance.FEES_SCALE_ADD.getUrl(configStorage);
        this.post(url, params);
    }

    public void changeFeeScaleName(Long id, String name) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", name);
        String url = ApiUrl.Finance.FEES_SCALE_CHANGE_NAME.getUrl(configStorage);
        this.post(url, params);
    }

    public void disableFeeScale(Long id) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        String url = ApiUrl.Finance.FEE_SCALE_DISABLE.getUrl(configStorage);
        this.post(url, params);
    }

    public void batchBindFeeScale(List<Long> accountIds, List<Long> ids) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("accountIds", accountIds);
        params.put("ids", ids);
        String url = ApiUrl.Finance.FEE_SCALE_BATCH_BIND.getUrl(configStorage);
        this.post(url, params);
    }

    public Map<String, ChargeReportItem> countChargeReport(Long schId, String month) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("schId", schId);
        queryParams.put("month", month);
        String url = ApiUrl.Finance.COUNT_CHARGE_REPORT.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyMap();
        Type mapType = new TypeToken<Map<String, ChargeReportItem>>() {}.getType();
        return gson.fromJson(resbody, mapType);
    }

    public List<ChargeSubjectReport> sumChargeSubjectReport(Long schId, String month) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("schId", schId);
        queryParams.put("month", month);
        String url = ApiUrl.Finance.SUM_CHARGE_SUBJECT_REPORT.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyList();
        Type listType = new TypeToken<List<ChargeSubjectReport>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public List<CarryOverSubjectReport> sumCarryOverSubjectReport(Long schId, String month) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("schId", schId);
        queryParams.put("month", month);
        String url = ApiUrl.Finance.SUM_CARRY_OVER_SUBJECT_REPORT.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyList();
        Type listType = new TypeToken<List<CarryOverSubjectReport>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public List<PayChancelReport> countPayChancelReport(Long schId, String month) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("schId", schId);
        queryParams.put("month", month);
        String url = ApiUrl.Finance.COUNT_PAY_CHANCEL_REPORT.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyList();
        Type listType = new TypeToken<List<PayChancelReport>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public FinanceConfig getConfig(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("schId", schId);
        String url = ApiUrl.Finance.CONFIG.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        return extractResBodyAs(response, FinanceConfig.class);
    }

    public void saveConfig(Long schId, FinanceConfig config) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("schId", schId);
        params.put("config", config);
        String url = ApiUrl.Finance.CONFIG.getUrl(configStorage);
        this.post(url, params);
    }

    public BillNumberRule getBillNumberRule(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("schId", schId);
        String url = ApiUrl.Finance.BILL_NUMBER_RULE.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        return extractResBodyAs(response, BillNumberRule.class);
    }

    public void saveBillNumberRule(BillNumberRule rule) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("rule", rule);
        String url = ApiUrl.Finance.BILL_NUMBER_RULE_SAVE.getUrl(configStorage);
        this.post(url, params);
    }

    public List<PayChancel> listPayChancels(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("schId", schId);
        String url = ApiUrl.Finance.PAY_CHANCELS_LIST.getUrl(configStorage);
        String response = (String) this.get(url, queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyList();
        Type listType = new TypeToken<List<PayChancel>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public void addPayChancel(Long schId, String name) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("schId", schId);
        params.put("name", name);
        String url = ApiUrl.Finance.PAY_CHANCEL_ADD.getUrl(configStorage);
        this.post(url, params);
    }

    public void removePayChancel(Long schId, Long id) throws ApiException {
        Map<String, Object> params = new HashMap<>();
        params.put("schId", schId);
        params.put("id", id);
        String url = ApiUrl.Finance.PAY_CHANCEL_REMOVE.getUrl(configStorage);
        this.post(url, params);
    }

    public Map<String, String> validate() throws ApiException {
        String url = ApiUrl.Finance.VALIDATE.getUrl(configStorage);
        String response = (String) this.get(url);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) return Collections.emptyMap();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(resbody, mapType);
    }

    private JsonElement extractResBodyJsonElement(String responseContent) throws ApiException {
        return gson.fromJson(responseContent, JsonElement.class)
                .getAsJsonObject().get("resbody");
    }

    private Map<String, Object> toChargeBillParamMap(ChargeBillDTO dto) {
        Map<String, Object> params = new HashMap<>();
        if (dto.getStudentId() != null) params.put("studentId", dto.getStudentId());
        if (dto.getTotalAmount() != null) params.put("totalAmount", dto.getTotalAmount());
        if (dto.getPayAmount() != null) params.put("payAmount", dto.getPayAmount());
        if (dto.getDeductionAmount() != null) params.put("deductionAmount", dto.getDeductionAmount());
        if (dto.getBizDate() != null) params.put("bizDate", dto.getBizDate());
        if (dto.getPayUserId() != null) params.put("payUserId", dto.getPayUserId());
        if (dto.getPayUserName() != null) params.put("payUserName", dto.getPayUserName());
        if (dto.getRemark() != null) params.put("remark", dto.getRemark());
        if (dto.getSubjectItems() != null) params.put("subjectItems", dto.getSubjectItems());
        if (dto.getPayChancels() != null) params.put("payChancels", dto.getPayChancels());
        return params;
    }

    private Map<String, Object> toFilterBillsParamMap(FilterBillsPage filter) {
        Map<String, Object> params = new HashMap<>();
        if (filter.getCurrent() != null) params.put("current", filter.getCurrent());
        if (filter.getPageSize() != null) params.put("pageSize", filter.getPageSize());
        if (filter.getSearchWord() != null) params.put("searchWord", filter.getSearchWord());
        if (filter.getSchId() != null) params.put("schId", filter.getSchId());
        if (filter.getClazzId() != null) params.put("clazzId", filter.getClazzId());
        if (filter.getPeriod() != null) params.put("period", filter.getPeriod());
        if (filter.getBillType() != null) params.put("billType", filter.getBillType());
        if (filter.getFilterInvalid() != null) params.put("filterInvalid", filter.getFilterInvalid());
        return params;
    }

    private Map<String, Object> toSubjectDTOParamMap(SubjectDTO dto) {
        Map<String, Object> params = new HashMap<>();
        if (dto.getId() != null) params.put("id", dto.getId());
        if (dto.getName() != null) params.put("name", dto.getName());
        if (dto.getType() != null) params.put("type", dto.getType());
        if (dto.getDay() != null) params.put("day", dto.getDay());
        if (dto.getAlloc() != null) params.put("alloc", dto.getAlloc());
        if (dto.getRemark() != null) params.put("remark", dto.getRemark());
        if (dto.getDisabled() != null) params.put("disabled", dto.getDisabled());
        if (dto.getRefund() != null) params.put("refund", dto.getRefund());
        return params;
    }

    private static class LenientIntegerAdapter extends TypeAdapter<Integer> {
        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            if (value == null) out.nullValue();
            else out.value(value);
        }

        @Override
        public Integer read(JsonReader in) throws IOException {
            try {
                return in.nextInt();
            } catch (NumberFormatException | IllegalStateException e) {
                in.skipValue();
                return null;
            }
        }
    }

    private static class LenientLongAdapter extends TypeAdapter<Long> {
        @Override
        public void write(JsonWriter out, Long value) throws IOException {
            if (value == null) out.nullValue();
            else out.value(value);
        }

        @Override
        public Long read(JsonReader in) throws IOException {
            try {
                return in.nextLong();
            } catch (NumberFormatException | IllegalStateException e) {
                in.skipValue();
                return null;
            }
        }
    }
}
