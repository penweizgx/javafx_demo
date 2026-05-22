package com.example.app.service.impl;

import com.example.app.api.ApiException;
import com.example.app.api.okhttp.EmployeeApiServiceImpl;
import com.example.app.exception.ExceptionHandler;
import com.example.app.model.Employee;
import com.example.app.model.EmployeeListReq;
import com.example.app.model.PageResult;
import com.example.app.service.EmployeeManageService;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class EmployeeManageServiceImpl implements EmployeeManageService {

    private final EmployeeApiServiceImpl employeeApiService;

    @Inject
    public EmployeeManageServiceImpl(EmployeeApiServiceImpl employeeApiService) {
        this.employeeApiService = employeeApiService;
    }

    @Override
    public CompletableFuture<PageResult<Employee>> listEmployees(EmployeeListReq req) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PageResult<Employee> result = employeeApiService.listEmployees(req);
                log.info("Loaded {} employees", result.getRecords().size());
                return result;
            } catch (ApiException e) {
                ExceptionHandler.handle(e, "Failed to load employees");
                throw new RuntimeException("加载员工列表失败: " + e.getMessage(), e);
            } catch (Exception e) {
                ExceptionHandler.handle(e, "Failed to load employees");
                throw new RuntimeException("加载员工列表失败: " + e.getMessage(), e);
            }
        });
    }
}
