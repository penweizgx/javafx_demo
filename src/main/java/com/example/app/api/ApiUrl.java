package com.example.app.api;

import com.example.app.api.storage.ConfigStorage;
import com.example.app.api.storage.HostConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import static com.example.app.api.storage.HostConfig.API_DEFAULT_HOST_URL;
import static com.example.app.api.storage.HostConfig.buildUrl;

/**
 * <pre>
 *  公众号接口api地址
 *  Created by BinaryWang on 2019-06-03.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
public interface ApiUrl {

  /**
   * 得到api完整地址.
   *
   * @param config 微信公众号配置
   * @return api地址
   */
  default String getUrl(ConfigStorage config) {
    HostConfig hostConfig = null;
    if (config != null) {
      hostConfig = config.getApiHost();
    }
    return buildUrl(hostConfig, this.getPrefix(), this.getPath());

  }

  /**
   * the path
   *
   * @return path
   */
  String getPath();

  /**
   * the prefix
   *
   * @return prefix
   */
  String getPrefix();

  @AllArgsConstructor
  @Getter
  enum Authenticate implements ApiUrl {
    /**
     */
    PUBLIC_KEY(API_DEFAULT_HOST_URL, "/auth/getPublickey"),
    LOGIN_WITH_PASSWORD(API_DEFAULT_HOST_URL, "/auth/loginWithPassword"),
    LOGOUT(API_DEFAULT_HOST_URL, "/auth/logout"),
    CURRENT_USER(API_DEFAULT_HOST_URL, "/auth/currentUser"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Employee implements ApiUrl {
    LIST(API_DEFAULT_HOST_URL, "/employee/list"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Student implements ApiUrl {
    LIST_WITH_CLAZZ(API_DEFAULT_HOST_URL, "/student/listWithClazz"),
    LIST_BY_CONDITION(API_DEFAULT_HOST_URL, "/student/listByCondition"),
    DETAIL(API_DEFAULT_HOST_URL, "/student/detail"),
    CREATE(API_DEFAULT_HOST_URL, "/student/create"),
    CHANGE(API_DEFAULT_HOST_URL, "/student/change"),
    GROUP_CLAZZ_STUDENT(API_DEFAULT_HOST_URL, "/student/groupClazzStudent"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Org implements ApiUrl {
    CLAZZ_LIST_WITH_COUNT(API_DEFAULT_HOST_URL, "/org/clazz/listWithCount"),
    CLAZZ_LIST_ACTIVE(API_DEFAULT_HOST_URL, "/org/clazz/listActive"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Attend implements ApiUrl {
    COUNT_CLAZZ_DAY(API_DEFAULT_HOST_URL, "/attend/countClazzDay"),
    LIST_MONTH_ATTEND_STUDENT(API_DEFAULT_HOST_URL, "/attend/listMonthAttend"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Finance implements ApiUrl {
    ACCOUNT(API_DEFAULT_HOST_URL, "/finance/account"),
    COUNT_BRIEF(API_DEFAULT_HOST_URL, "/finance/countBrief"),
    LIST_BY_CONDITION(API_DEFAULT_HOST_URL, "/finance/listByCondition"),
    CHARGE_BILLS(API_DEFAULT_HOST_URL, "/finance/chargeBills"),
    BUILD_CHARGE_FORM(API_DEFAULT_HOST_URL, "/finance/buildChargeForm"),
    CHARGE(API_DEFAULT_HOST_URL, "/finance/charge"),
    FEE_SCALE_BIND(API_DEFAULT_HOST_URL, "/finance/feeScale/bind"),
    CLOSE(API_DEFAULT_HOST_URL, "/finance/close"),
    IMPORT_CHARGE(API_DEFAULT_HOST_URL, "/finance/importCharge"),
    EXPORT_FINANCE_ACCOUNT(API_DEFAULT_HOST_URL, "/finance/exportFinanceAccount"),
    STUDENTS(API_DEFAULT_HOST_URL, "/finance/students"),
    BILLS_LIST(API_DEFAULT_HOST_URL, "/finance/bills/list"),
    CHARGE_DETAIL(API_DEFAULT_HOST_URL, "/finance/charge"),
    INVALID_CHARGE(API_DEFAULT_HOST_URL, "/finance/invalidCharge"),
    INVALID_CARRY(API_DEFAULT_HOST_URL, "/finance/invalidCarry"),
    EXPORT_CHARGE_BILL(API_DEFAULT_HOST_URL, "/finance/exportChargeBill"),
    LIST_MONTH_ATTEND_CARRY_OVER(API_DEFAULT_HOST_URL, "/finance/listMonthAttendCarryOver"),
    ATTEND_CARRY_OVER_BILL(API_DEFAULT_HOST_URL, "/finance/attendCarryOverBill"),
    CONFIRM_MONTH_ATTEND_CARRY_OVER(API_DEFAULT_HOST_URL, "/finance/confirmMonthAttendCarryOver"),
    ATTEND_CARRY_OVER_CONFIG(API_DEFAULT_HOST_URL, "/finance/attendCarryOverConfig"),
    SUBJECT_LIST(API_DEFAULT_HOST_URL, "/finance/subject/list"),
    SUBJECT_ADD(API_DEFAULT_HOST_URL, "/finance/subject/add"),
    SUBJECT_EDIT(API_DEFAULT_HOST_URL, "/finance/subject/edit"),
    SUBJECT_DISABLE(API_DEFAULT_HOST_URL, "/finance/subject/disable"),
    SUBJECT_REMOVE(API_DEFAULT_HOST_URL, "/finance/subject/remove"),
    FEES_SCALE_ADD(API_DEFAULT_HOST_URL, "/finance/feesScale/add"),
    FEES_SCALE_CHANGE_NAME(API_DEFAULT_HOST_URL, "/finance/feesScale/changeName"),
    FEE_SCALE_DISABLE(API_DEFAULT_HOST_URL, "/finance/feeScale/disable"),
    FEE_SCALE_BATCH_BIND(API_DEFAULT_HOST_URL, "/finance/feeScale/batchBind"),
    COUNT_CHARGE_REPORT(API_DEFAULT_HOST_URL, "/finance/countChargeReport"),
    SUM_CHARGE_SUBJECT_REPORT(API_DEFAULT_HOST_URL, "/finance/sumChargeSubjectReport"),
    SUM_CARRY_OVER_SUBJECT_REPORT(API_DEFAULT_HOST_URL, "/finance/sumCarryOverSubjectReport"),
    COUNT_PAY_CHANCEL_REPORT(API_DEFAULT_HOST_URL, "/finance/countPayChancelReport"),
    CONFIG(API_DEFAULT_HOST_URL, "/finance/config"),
    BILL_NUMBER_RULE(API_DEFAULT_HOST_URL, "/finance/billNumberRule"),
    BILL_NUMBER_RULE_SAVE(API_DEFAULT_HOST_URL, "/finance/billNumberRule/save"),
    PAY_CHANCELS_LIST(API_DEFAULT_HOST_URL, "/finance/payChancels/list"),
    PAY_CHANCEL_ADD(API_DEFAULT_HOST_URL, "/finance/payChancel/add"),
    PAY_CHANCEL_REMOVE(API_DEFAULT_HOST_URL, "/finance/payChancel/remove"),
    DEPOSIT(API_DEFAULT_HOST_URL, "/finance/deposit"),
    REFUND(API_DEFAULT_HOST_URL, "/finance/refund"),
    REFUND_INVALID(API_DEFAULT_HOST_URL, "/finance/refund/invalid"),
    CARRY_OVER_CHARGE(API_DEFAULT_HOST_URL, "/finance/carryOverCharge"),
    TRANSPORT_DEPOSIT(API_DEFAULT_HOST_URL, "/finance/transportDeposit"),
    BATCH_CHARGE(API_DEFAULT_HOST_URL, "/finance/batchCharge"),
    VALIDATE(API_DEFAULT_HOST_URL, "/finance/validate"),
    EXPORT_IMPORT_CHARGE_ERROR(API_DEFAULT_HOST_URL, "/finance/exportImportChargeError"),
    ;

    private final String prefix;
    private final String path;
  }

}
