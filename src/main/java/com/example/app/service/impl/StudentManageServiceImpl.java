package com.example.app.service.impl;

import com.example.app.api.ApiException;
import com.example.app.api.okhttp.StudentApiServiceImpl;
import com.example.app.exception.ExceptionHandler;
import com.example.app.model.*;
import com.example.app.service.StudentManageService;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class StudentManageServiceImpl implements StudentManageService {

    private final StudentApiServiceImpl studentApiService;

    @Inject
    public StudentManageServiceImpl(StudentApiServiceImpl studentApiService) {
        this.studentApiService = studentApiService;
    }

    @Override
    public CompletableFuture<List<ClazzWithCountVO>> listClazzWithCount(Long schId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return studentApiService.listClazzWithCount(schId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load classes");
                throw new RuntimeException("加载班级列表失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<ClazzDayAttendVO>> countClazzDay(String day) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return studentApiService.countClazzDay(day);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load attendance");
                throw new RuntimeException("加载考勤数据失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<StudentVO>> listWithClazz(Long clazzId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return studentApiService.listWithClazz(clazzId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load students");
                throw new RuntimeException("加载学生列表失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<StudentVO>> listByCondition(StudentQO qo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return studentApiService.listByCondition(qo);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to search students");
                throw new RuntimeException("搜索学生失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<StudentVO> detail(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return studentApiService.detail(id);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load student detail");
                throw new RuntimeException("加载学生详情失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> create(StudentFO fo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                studentApiService.create(fo);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to create student");
                throw new RuntimeException("创建学生失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> change(StudentFO fo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                studentApiService.change(fo);
                return null;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to update student");
                throw new RuntimeException("更新学生失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<ClazzOptionVO>> listActiveClazz(Long schId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return studentApiService.listActiveClazz(schId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load active classes");
                throw new RuntimeException("加载可选班级失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<ClazzStudentMonthAttendVO> listMonthAttend(Long clazzId, String month) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return studentApiService.listMonthAttend(clazzId, month);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load month attendance");
                throw new RuntimeException("加载月考勤失败: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<FinAccountVO> financeAccount(Long studentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return studentApiService.financeAccount(studentId);
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load finance info");
                throw new RuntimeException("加载财务信息失败: " + e.getMessage(), e);
            }
        });
    }
}
