package com.example.app.api.okhttp;

import com.example.app.api.ApiException;
import com.example.app.api.ApiUrl;
import com.example.app.model.Employee;
import com.example.app.model.EmployeeListReq;
import com.example.app.model.PageResult;
import com.example.app.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工模块ApiServiceImpl - 继承OkHttpApiServiceImpl复用HTTP基础设施。
 * 使用父类的get()方法发起请求，extractResBodyAs()解析响应。
 */
@Slf4j
public class EmployeeApiServiceImpl extends OkHttpApiServiceImpl {

    private final Gson gson = new Gson();

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

        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) {
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

    private JsonElement extractResBodyJsonElement(String responseContent) throws ApiException {
        JsonObject jsonObject = gson.fromJson(responseContent, JsonObject.class);
        if (jsonObject.has("code") && jsonObject.get("code").getAsInt() != 200) {
            String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "未知错误";
            throw new ApiException(message, jsonObject.get("code").getAsInt());
        }
        return jsonObject.get("resbody");
    }

    /**
     * 将嵌套的EmployeeDTO JsonObject映射为扁平的Employee bean
     */
    private Employee mapEmployeeDTOtoEmployee(JsonObject dto) {
        Employee emp = new Employee();

        // id
        if (dto.has("id") && !dto.get("id").isJsonNull()) {
            emp.setId(dto.get("id").getAsLong());
        }

        // phone
        if (dto.has("phone") && !dto.get("phone").isJsonNull()) {
            emp.setPhone(dto.get("phone").getAsString());
        }

        // email
        if (dto.has("email") && !dto.get("email").isJsonNull()) {
            emp.setEmail(dto.get("email").getAsString());
        }

        // name - nested PersonName object
        if (dto.has("name") && dto.get("name").isJsonObject()) {
            JsonObject nameObj = dto.getAsJsonObject("name");
            if (nameObj.has("name") && !nameObj.get("name").isJsonNull()) {
                emp.setName(nameObj.get("name").getAsString());
            }
        } else if (dto.has("name") && !dto.get("name").isJsonNull()) {
            emp.setName(dto.get("name").getAsString());
        }

        // sex - integer: 1=男, 2=女, 0=未知
        if (dto.has("sex") && !dto.get("sex").isJsonNull()) {
            int sex = dto.get("sex").getAsInt();
            emp.setGender(sex == 1 ? "男" : sex == 2 ? "女" : "");
        }

        // bornDate - nested BirthdayDate object
        if (dto.has("bornDate") && dto.get("bornDate").isJsonObject()) {
            JsonObject bornObj = dto.getAsJsonObject("bornDate");
            if (bornObj.has("date") && !bornObj.get("date").isJsonNull()) {
                emp.setBirthday(bornObj.get("date").getAsString());
            }
        }

        // period - DatePeriod object contains hire date
        if (dto.has("period") && dto.get("period").isJsonObject()) {
            JsonObject periodObj = dto.getAsJsonObject("period");
            if (periodObj.has("fromDate") && !periodObj.get("fromDate").isJsonNull()) {
                emp.setHireDate(periodObj.get("fromDate").getAsString());
            }
        }

        // active - boolean
        if (dto.has("active") && !dto.get("active").isJsonNull()) {
            boolean active = dto.get("active").getAsBoolean();
            emp.setStatus(active ? 1 : 0);
        }

        // orgName - not in EmployeeDTO, use empty string
        emp.setOrgName("");

        return emp;
    }
}