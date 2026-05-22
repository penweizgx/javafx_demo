package com.example.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.example.app.api.ApiService;
import com.example.app.api.ApiUrl;
import com.example.app.api.storage.InMemoryConfigStorage;
import com.example.app.model.Employee;
import com.example.app.model.EmployeeListReq;
import com.example.app.model.PageResult;
import com.example.app.service.EmployeeManageService;
import com.example.app.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class EmployeeManageServiceImpl implements EmployeeManageService {

    private final ApiService apiService;

    @Inject
    public EmployeeManageServiceImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public CompletableFuture<PageResult<Employee>> listEmployees(EmployeeListReq req) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = ApiUrl.Employee.LIST.getUrl(new InMemoryConfigStorage());
                String response = (String) apiService.get(url, BeanUtil.beanToMap(req,true,true));

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
                if (jsonObject.has("code") && jsonObject.get("code").getAsInt() != 200) {
                    throw new RuntimeException(jsonObject.get("message").getAsString());
                }

                // Response format: {code, message, resbody: [array]}
                JsonArray dataArray = jsonObject.has("resbody")
                        ? jsonObject.getAsJsonArray("resbody")
                        : jsonObject.has("data")
                        ? jsonObject.getAsJsonArray("data")
                        : jsonObject.getAsJsonArray();

                ArrayList<Employee> employees = new ArrayList<>();
                for (JsonElement elem : dataArray) {
                    JsonObject dto = elem.getAsJsonObject();
                    Employee emp = new Employee();
                    emp.setId(dto.has("id") && !dto.get("id").isJsonNull() ? dto.get("id").getAsLong() : null);
                    emp.setPhone(dto.has("phone") && !dto.get("phone").isJsonNull() ? dto.get("phone").getAsString() : "");
                    emp.setEmail(dto.has("email") && !dto.get("email").isJsonNull() ? dto.get("email").getAsString() : "");

                    // name is nested PersonName object
                    if (dto.has("name") && dto.get("name").isJsonObject()) {
                        JsonObject nameObj = dto.getAsJsonObject("name");
                        emp.setName(nameObj.has("name") && !nameObj.get("name").isJsonNull() ? nameObj.get("name").getAsString() : "");
                    } else if (dto.has("name") && !dto.get("name").isJsonNull()) {
                        emp.setName(dto.get("name").getAsString());
                    } else {
                        emp.setName("");
                    }

                    // sex is integer: 1=男, 2=女, 0=未知
                    if (dto.has("sex") && !dto.get("sex").isJsonNull()) {
                        int sex = dto.get("sex").getAsInt();
                        emp.setGender(sex == 1 ? "男" : sex == 2 ? "女" : "");
                    } else {
                        emp.setGender("");
                    }

                    // bornDate is nested BirthdayDate object or empty string
                    if (dto.has("bornDate") && dto.get("bornDate").isJsonObject()) {
                        JsonObject bornObj = dto.getAsJsonObject("bornDate");
                        emp.setBirthday(bornObj.has("date") && !bornObj.get("date").isJsonNull() ? bornObj.get("date").getAsString() : "");
                    } else {
                        emp.setBirthday("");
                    }

                    // period (DatePeriod) contains hire date range, or empty string
                    if (dto.has("period") && dto.get("period").isJsonObject()) {
                        JsonObject periodObj = dto.getAsJsonObject("period");
                        emp.setHireDate(periodObj.has("fromDate") && !periodObj.get("fromDate").isJsonNull() ? periodObj.get("fromDate").getAsString() : "");
                    } else {
                        emp.setHireDate("");
                    }

                    // active is boolean
                    if (dto.has("active") && !dto.get("active").isJsonNull()) {
                        boolean active = dto.get("active").getAsBoolean();
                        emp.setStatus(active ? 1 : 0);
                    } else {
                        emp.setStatus(0);
                    }

                    // orgName not in EmployeeDTO, use placeholder
                    emp.setOrgName("");

                    employees.add(emp);
                }

                PageResult<Employee> result = new PageResult<>();
                result.setRecords(employees);
                result.setTotal((long) employees.size());
                result.setPageNum(req.getPageNum());
                result.setPageSize(req.getPageSize());
                log.info("Loaded {} employees", employees.size());
                return result;
            } catch (Exception e) {
                log.error("Failed to load employees", e);
                throw new RuntimeException("加载员工列表失败: " + e.getMessage(), e);
            }
        });
    }
}