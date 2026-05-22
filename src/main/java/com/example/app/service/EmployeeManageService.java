package com.example.app.service;

import com.example.app.model.Employee;
import com.example.app.model.EmployeeListReq;
import com.example.app.model.PageResult;

import java.util.concurrent.CompletableFuture;

public interface EmployeeManageService {
    CompletableFuture<PageResult<Employee>> listEmployees(EmployeeListReq req);
}