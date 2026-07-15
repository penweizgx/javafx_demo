package com.example.app.service;

import com.example.app.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface StudentManageService {
    CompletableFuture<List<ClazzWithCountVO>> listClazzWithCount(Long schId);
    CompletableFuture<List<ClazzDayAttendVO>> countClazzDay(String day);
    CompletableFuture<List<StudentVO>> listWithClazz(Long clazzId);
    CompletableFuture<List<StudentVO>> listByCondition(StudentQO qo);
    CompletableFuture<StudentVO> detail(Long id);
    CompletableFuture<Void> create(StudentFO fo);
    CompletableFuture<Void> change(StudentFO fo);
    CompletableFuture<List<ClazzOptionVO>> listActiveClazz(Long schId);
    CompletableFuture<ClazzStudentMonthAttendVO> listMonthAttend(Long clazzId, String month);
    CompletableFuture<FinAccountVO> financeAccount(Long studentId);
}
