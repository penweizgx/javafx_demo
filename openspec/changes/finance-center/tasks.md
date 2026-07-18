## 1. Foundation: Models & API Layer

- [x] 1.1 Create PeriodUnit enum (ONCE/M/Q/T/HY/FY/Y with getLabel() and getMonthCount())
- [x] 1.2 Expand FinAccountVO with feeScales (List<FeeScale>), student (StudentVO), subjectExpireDate (Map<String,String>), id, version, createdAt, modifiedAt fields
- [x] 1.3 Create FeeScale model (id, name, standardAmount, specialAmount, unit, disabled, remark, subject ref)
- [x] 1.4 Create SubjectWithFeeScaleDTO model (id, name, type, day, alloc, remark, disabled, refund, feeScales list)
- [x] 1.5 Create FeeScaleDTO model (id, name, standardAmount, specialAmount, unit, disabled, remark, used)
- [x] 1.6 Create FinanceBrief model (schId, expirePerson, expireSubject, remindPerson, remindSubject, depositNum, depositAmount, freePerson)
- [x] 1.7 Create ChargeFormBuildDTO model (studentId, studentName, clazzName, balancesAmount, subjectFeeSales list, chancels list, parents list)
- [x] 1.8 Create SubjectFeeSale model (id, subjectName, subjectType, amount, unit, lastDate)
- [x] 1.9 Create ChargeBillDTO model (studentId, totalAmount, payAmount, deductionAmount, bizDate, payUserId, payUserName, subjectItems list, payChancels list, remark)
- [x] 1.10 Create SubjectItem model (feeScaleId, period, unitAmount, receivableAmount, payAmount, balancesAmount)
- [x] 1.11 Create ChancelAmount model (id, amount)
- [x] 1.12 Create FilterBillsPage model (current, pageSize, total, pages, searchWord, schId, clazzId, period, billType, filterInvalid)
- [x] 1.13 Create BillTimeDTO model (id, studentId, studentName, clazzName, type, totalAmount, realAmount, deductionAmount, balancesAmount, bizDate, payUserName, subjectItems, payChancels, carryOverSubjectItems, invalid, remark)
- [x] 1.14 Create PaginationBillTimeDTO model (current, pageSize, total, pages, content list)
- [x] 1.15 Create ChargeBillVO model (bill ChargeBill, carryOverBillList list)
- [x] 1.16 Create MonthAttendCarryOverVO model (archiveId, studentId, studentName, clazzName, miss, leave, refund, items list)
- [x] 1.17 Create CarryItem model (subjectId, subjectName, billId, defalutAmount, realAmount)
- [x] 1.18 Create StudentMonthAttendDTO model (studentId, month LocalMonth, monthArchive, archiveId, refund, items list)
- [x] 1.19 Create LocalMonth model (year, month, datePeriod, endDay)
- [x] 1.20 Create SubjectDTO model (id, name, type, day, alloc, remark, disabled, refund)
- [x] 1.21 Create FinanceConfig model (remind, continuous)
- [x] 1.22 Create BillNumberRule model (schId, length, prefix, start, serialno)
- [x] 1.23 Create PayChancel model (id, name, hide)
- [x] 1.24 Create ChargeSubjectReport model (subjectId, subjectName, receivableAmount, payAmount, balancesAmount)
- [x] 1.25 Create CarryOverSubjectReport model (subjectId, subjectName, receivableAmount)
- [x] 1.26 Create PayChancelReport model (id, name, amount)
- [x] 1.27 Create ChargeReportItem model (billnumber, studentNum, receivableAmount, payAmount, balancesAmount, amount)
- [x] 1.28 Create StuentParent model (id, name)
- [x] 1.29 Add Finance enum entries to ApiUrl.java
- [x] 1.30 Create FinanceApiServiceImpl extending OkHttpApiServiceImpl with all finance API methods

## 2. Service Layer

- [x] 2.1 Create FinanceManageService interface with all finance business methods (returning CompletableFuture)
- [x] 2.2 Create FinanceManageServiceImpl with async wrapping and error handling (following StudentManageServiceImpl pattern)
- [x] 2.3 Migrate financeAccount() from StudentApiServiceImpl/StudentManageService to FinanceApiServiceImpl/FinanceManageService
- [x] 2.4 Update StudentApiServiceImpl to remove financeAccount() method
- [x] 2.5 Update StudentManageServiceImpl to delegate financeAccount() to FinanceManageService
- [x] 2.6 Add Guice bindings in AppModule (FinanceApiServiceImpl, FinanceManageService)

## 3. Navigation & Configuration

- [x] 3.1 Add finance-mgmt navigation node to navigation.yaml with 6 children (finance-account, finance-bills, finance-attend, finance-subject, finance-reports, finance-config)
- [x] 3.2 Add i18n keys to messages_zh_CN.properties for finance center navigation and page labels
- [x] 3.3 Add i18n keys to messages_en_US.properties for finance center navigation and page labels

## 4. Student Account Page (finance-account)

- [x] 4.1 Create FinanceAccountListViewModel (dashboard data, student list, loading state, filter properties)
- [x] 4.2 Create finance_account_list.fxml (dashboard cards, filter bar, student table, pagination)
- [x] 4.3 Create FinanceAccountListController (dashboard loading, table setup, filter handlers, navigation to detail/charge/settings)
- [x] 4.4 Create FinanceAccountDetailModal component (basic info, fee items table, charge history table, action buttons)
- [x] 4.5 Create ChargeModal component (fee item selection, amount/period input, balance deduction, payment method selection, payer selection, submit)
- [x] 4.6 Create FeeScaleBindModal component (TabPane by subject, fee scale checkboxes, submit with feeScale/bind)
- [x] 4.7 Add import charge dialog to FinanceAccountListController
- [x] 4.8 Add export functionality to FinanceAccountListController

## 5. Bills Page (finance-bills)

- [x] 5.1 Create FinanceBillsViewModel (bill list, pagination state, filter properties)
- [x] 5.2 Create finance_bills.fxml (filter bar, bill table, pagination)
- [x] 5.3 Create FinanceBillsController (filter handlers, table setup with BillTimeDTO columns, pagination)
- [x] 5.4 Create BillDetailModal component (charge bill detail with subject items, payment channels, carry-over bills)
- [x] 5.5 Add invalidate charge/carry-over actions to FinanceBillsController
- [x] 5.6 Add export bills functionality

## 6. Attendance Refund Page (finance-attend)

- [x] 6.1 Create FinanceAttendViewModel (refund list, month/clazz filter, onlyRefund property)
- [x] 6.2 Create finance_attend.fxml (month picker, class dropdown, onlyRefund checkbox, refund table)
- [x] 6.3 Create FinanceAttendController (filter handlers, table setup with MonthAttendCarryOverVO columns, anomaly display)
- [x] 6.4 Add confirm carry-over action
- [x] 6.5 Add manual refund action

## 7. Subject Page (finance-subject)

- [x] 7.1 Create FinanceSubjectViewModel (subject list, type filter)
- [x] 7.2 Create finance_subject.fxml (type filter, subject table with expand rows)
- [x] 7.3 Create FinanceSubjectController (table setup with expandable rows, sub-table rendering)
- [x] 7.4 Create SubjectAddModal component (name, type radio, remind period, remark)
- [x] 7.5 Create SubjectEditModal component (pre-filled form)
- [x] 7.6 Create RefundSettingModal component (subject name, amount, unit)
- [x] 7.7 Create FeeScaleAddModal component (standardAmount, unit dropdown, name, remark)
- [x] 7.8 Add disable/remove subject actions
- [x] 7.9 Add rename/disable fee scale actions in sub-table
- [x] 7.10 Add batch charge action stub for fee scale rows

## 8. Reports Page (finance-reports)

- [x] 8.1 Create FinanceReportsViewModel (summary data, subject reports, carry-over reports, payment reports, selected month)
- [x] 8.2 Create finance_reports.fxml (month picker, summary table, subject analysis table+chart, carry-over table+chart, payment table+chart)
- [x] 8.3 Create FinanceReportsController (data loading, chart rendering with BarChart/PieChart, dimension selector)

## 9. Config Page (finance-config)

- [x] 9.1 Create FinanceConfigViewModel (config data, payment methods, bill number rule)
- [x] 9.2 Create finance_config.fxml (payment method section, bill number rule section, remind period section, attendance refund section with validate button)
- [x] 9.3 Create FinanceConfigController (load/save config, payment method CRUD, bill number rule save, validate account)

## 10. Integration & Verification

- [x] 10.1 Compile and verify no errors (mvn compile)
- [ ] 10.2 Verify navigation to all 6 finance pages works
- [ ] 10.3 Verify student account page loads dashboard and student list with real API data
- [ ] 10.4 Verify charge modal opens and submits successfully
- [ ] 10.5 Verify bills page loads and paginates with real API data
- [ ] 10.6 Verify subject page loads with expandable fee scales
- [ ] 10.7 Verify reports page loads charts with real API data
- [ ] 10.8 Verify config page loads and saves settings
