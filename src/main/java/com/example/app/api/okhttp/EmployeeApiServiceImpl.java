package com.example.app.api.okhttp;

import com.example.app.api.ApiException;
import com.example.app.api.ApiUrl;
import com.example.app.model.Employee;
import com.example.app.model.EmployeeListReq;
import com.example.app.model.PageResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class EmployeeApiServiceImpl extends OkHttpApiServiceImpl {

    @Inject
    public EmployeeApiServiceImpl() {
        initHttp();
    }

    public PageResult<Employee> listEmployees(EmployeeListReq req) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryParams.put("name", req.getName());
        }
        if (req.getPhone() != null && !req.getPhone().isEmpty()) {
            queryParams.put("phone", req.getPhone());
        }

        String url = ApiUrl.Employee.LIST.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);

        JsonElement resbody = extractResBodyElement(response);
        if (resbody == null) {
            throw new ApiException("员工列表响应数据为空");
        }

        JsonArray dataArray = resbody.getAsJsonArray();
        ArrayList<Employee> allEmployees = new ArrayList<>(dataArray.size());

        for (JsonElement elem : dataArray) {
            allEmployees.add(mapEmployeeDTOtoEmployee(elem.getAsJsonObject()));
        }

        long total = allEmployees.size();
        int pageSize = req.getPageSize() != null ? req.getPageSize() : 10;
        int pageNum = req.getPageNum() != null ? req.getPageNum() : 1;
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allEmployees.size());

        List<Employee> pageRecords;
        if (fromIndex >= allEmployees.size()) {
            pageRecords = new ArrayList<>();
        } else {
            pageRecords = allEmployees.subList(fromIndex, toIndex);
        }

        PageResult<Employee> result = new PageResult<>();
        result.setRecords(new ArrayList<>(pageRecords));
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        log.info("Loaded {} employees (page {}/{}, total {})", pageRecords.size(), pageNum, (total + pageSize - 1) / pageSize, total);
        return result;
    }

    private Employee mapEmployeeDTOtoEmployee(JsonObject dto) {
        Employee emp = new Employee();

        if (dto.has("id") && !dto.get("id").isJsonNull()) {
            emp.setId(dto.get("id").getAsLong());
        }

        if (dto.has("phone") && !dto.get("phone").isJsonNull()) {
            emp.setPhone(dto.get("phone").getAsString());
        }

        if (dto.has("email") && !dto.get("email").isJsonNull()) {
            emp.setEmail(dto.get("email").getAsString());
        }

        if (dto.has("name") && dto.get("name").isJsonObject()) {
            JsonObject nameObj = dto.getAsJsonObject("name");
            if (nameObj.has("name") && !nameObj.get("name").isJsonNull()) {
                emp.setName(nameObj.get("name").getAsString());
            }
        } else if (dto.has("name") && !dto.get("name").isJsonNull()) {
            emp.setName(dto.get("name").getAsString());
        }

        if (dto.has("sex") && !dto.get("sex").isJsonNull()) {
            int sex = dto.get("sex").getAsInt();
            emp.setGender(sex == 1 ? "男" : sex == 2 ? "女" : "");
        }

        if (dto.has("bornDate") && dto.get("bornDate").isJsonObject()) {
            JsonObject bornObj = dto.getAsJsonObject("bornDate");
            if (bornObj.has("date") && !bornObj.get("date").isJsonNull()) {
                emp.setBirthday(bornObj.get("date").getAsString());
            }
        }

        if (dto.has("period") && dto.get("period").isJsonObject()) {
            JsonObject periodObj = dto.getAsJsonObject("period");
            if (periodObj.has("fromDate") && !periodObj.get("fromDate").isJsonNull()) {
                emp.setHireDate(periodObj.get("fromDate").getAsString());
            }
        }

        if (dto.has("active") && !dto.get("active").isJsonNull()) {
            boolean active = dto.get("active").getAsBoolean();
            emp.setStatus(active ? 1 : 0);
        }

        emp.setOrgName("");

        return emp;
    }
}
