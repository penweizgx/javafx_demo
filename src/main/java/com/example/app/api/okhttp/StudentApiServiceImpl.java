package com.example.app.api.okhttp;

import com.example.app.api.ApiException;
import com.example.app.api.ApiUrl;
import com.example.app.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class StudentApiServiceImpl extends OkHttpApiServiceImpl {

    private final Gson gson = new Gson();

    @Inject
    public StudentApiServiceImpl() {
        initHttp();
    }

    public List<StudentVO> listWithClazz(Long clazzId) throws ApiException {
        String url = ApiUrl.Student.LIST_WITH_CLAZZ.getUrl(configStorage) + "/" + clazzId;
        String response = (String) this.get(url);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<StudentVO>>() {}.getType();
        List<StudentVO> students = gson.fromJson(resbody, listType);
        log.info("Loaded {} students for clazz {}", students.size(), clazzId);
        return students;
    }

    public List<StudentVO> listByCondition(StudentQO qo) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (qo.getSchId() != null) queryParams.put("schId", qo.getSchId());
        if (qo.getClazzId() != null) queryParams.put("clazzId", qo.getClazzId());
        if (qo.getKeyword() != null && !qo.getKeyword().isEmpty()) queryParams.put("keyword", qo.getKeyword());
        if (qo.getIncludeGraduated() != null) queryParams.put("includeGraduated", qo.getIncludeGraduated());

        String url = ApiUrl.Student.LIST_BY_CONDITION.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<StudentVO>>() {}.getType();
        List<StudentVO> students = gson.fromJson(resbody, listType);
        log.info("Loaded {} students by condition", students.size());
        return students;
    }

    public StudentVO detail(Long id) throws ApiException {
        String url = ApiUrl.Student.DETAIL.getUrl(configStorage) + "/" + id;
        String response = (String) this.get(url);
        return extractResBodyAs(response, StudentVO.class);
    }

    public void create(StudentFO fo) throws ApiException {
        Map<String, Object> params = toParamMap(fo);
        String url = ApiUrl.Student.CREATE.getUrl(configStorage);
        this.postJSON(url, params);
        log.info("Created student: {}", fo.getName());
    }

    public void change(StudentFO fo) throws ApiException {
        Map<String, Object> params = toParamMap(fo);
        String url = ApiUrl.Student.CHANGE.getUrl(configStorage);
        this.postJSON(url, params);
        log.info("Changed student: {}", fo.getName());
    }

    public List<ClazzStudentDTO> groupClazzStudent(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (schId != null) queryParams.put("schId", schId);
        String url = ApiUrl.Student.GROUP_CLAZZ_STUDENT.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<ClazzStudentDTO>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public List<ClazzWithCountVO> listClazzWithCount(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("includeLeaved", false);
        if (schId != null) queryParams.put("schId", schId);
        String url = ApiUrl.Org.CLAZZ_LIST_WITH_COUNT.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<ClazzWithCountVO>>() {}.getType();
        List<ClazzWithCountVO> result = gson.fromJson(resbody, listType);
        log.info("Loaded {} classes", result.size());
        return result;
    }

    public List<ClazzOptionVO> listActiveClazz(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (schId != null) queryParams.put("schId", schId);
        String url = ApiUrl.Org.CLAZZ_LIST_ACTIVE.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<ClazzOptionVO>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public List<ClazzDayAttendVO> countClazzDay(String day) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (day != null) queryParams.put("day", day);
        String url = ApiUrl.Attend.COUNT_CLAZZ_DAY.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        JsonElement resbody = extractResBodyJsonElement(response);
        if (resbody == null || resbody.isJsonNull()) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<ClazzDayAttendVO>>() {}.getType();
        return gson.fromJson(resbody, listType);
    }

    public ClazzStudentMonthAttendVO listMonthAttend(Long clazzId, String month) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (month != null) queryParams.put("month", month);
        String url = ApiUrl.Attend.LIST_MONTH_ATTEND_STUDENT.getUrl(configStorage) + "/" + clazzId;
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        return extractResBodyAs(response, ClazzStudentMonthAttendVO.class);
    }

    private JsonElement extractResBodyJsonElement(String responseContent) throws ApiException {
        return gson.fromJson(responseContent, JsonElement.class)
                .getAsJsonObject().get("resbody");
    }

    private Map<String, Object> toParamMap(StudentFO fo) {
        Map<String, Object> params = new HashMap<>();
        if (fo.getId() != null) params.put("id", fo.getId());
        if (fo.getName() != null) params.put("name", fo.getName());
        if (fo.getNickname() != null) params.put("nickname", fo.getNickname());
        if (fo.getAvatar() != null) params.put("avatar", fo.getAvatar());
        if (fo.getSex() != null) params.put("sex", fo.getSex());
        if (fo.getClazzId() != null) params.put("clazzId", fo.getClazzId());
        if (fo.getBornDate() != null) params.put("bornDate", fo.getBornDate());
        if (fo.getAddress() != null) params.put("address", fo.getAddress());
        if (fo.getDanger() != null) params.put("danger", fo.getDanger());
        if (fo.getRegDate() != null) params.put("regDate", fo.getRegDate());
        if (fo.getPhone() != null) params.put("phone", fo.getPhone());
        if (fo.getPid() != null) params.put("pid", fo.getPid());
        if (fo.getPname() != null) params.put("pname", fo.getPname());
        if (fo.getPrelationship() != null) params.put("prelationship", fo.getPrelationship());
        return params;
    }
}
