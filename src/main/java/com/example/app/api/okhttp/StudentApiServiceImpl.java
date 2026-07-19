package com.example.app.api.okhttp;

import cn.hutool.core.bean.BeanUtil;
import com.example.app.api.ApiException;
import com.example.app.api.ApiUrl;
import com.example.app.model.*;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class StudentApiServiceImpl extends OkHttpApiServiceImpl {

    @Inject
    public StudentApiServiceImpl() {
        initHttp();
    }

    public List<StudentVO> listWithClazz(Long clazzId) throws ApiException {
        String url = ApiUrl.Student.LIST_WITH_CLAZZ.getUrl(configStorage) + "/" + clazzId;
        String response = (String) this.get(url);
        Type listType = new TypeToken<List<StudentVO>>() {}.getType();
        List<StudentVO> students = extractResBodyAsNullable(response, listType);
        log.info("Loaded {} students for clazz {}", students != null ? students.size() : 0, clazzId);
        return students != null ? students : Collections.emptyList();
    }

    public List<StudentVO> listByCondition(StudentQO qo) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (qo.getSchId() != null) queryParams.put("schId", qo.getSchId());
        if (qo.getClazzId() != null) queryParams.put("clazzId", qo.getClazzId());
        if (qo.getKeyword() != null && !qo.getKeyword().isEmpty()) queryParams.put("keyword", qo.getKeyword());
        if (qo.getIncludeGraduated() != null) queryParams.put("includeGraduated", qo.getIncludeGraduated());

        String url = ApiUrl.Student.LIST_BY_CONDITION.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        Type listType = new TypeToken<List<StudentVO>>() {}.getType();
        List<StudentVO> students = extractResBodyAsNullable(response, listType);
        log.info("Loaded {} students by condition", students != null ? students.size() : 0);
        return students != null ? students : Collections.emptyList();
    }

    public StudentVO detail(Long id) throws ApiException {
        String url = ApiUrl.Student.DETAIL.getUrl(configStorage) + "/" + id;
        String response = (String) this.get(url);
        return extractResBodyAs(response, StudentVO.class);
    }

    public void create(StudentFO fo) throws ApiException {
        String url = ApiUrl.Student.CREATE.getUrl(configStorage);
        this.postJSON(url, BeanUtil.beanToMap(fo, false, true));
        log.info("Created student: {}", fo.getName());
    }

    public void change(StudentFO fo) throws ApiException {
        String url = ApiUrl.Student.CHANGE.getUrl(configStorage);
        this.postJSON(url, BeanUtil.beanToMap(fo, false, true));
        log.info("Changed student: {}", fo.getName());
    }

    public List<ClazzStudentDTO> groupClazzStudent(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (schId != null) queryParams.put("schId", schId);
        String url = ApiUrl.Student.GROUP_CLAZZ_STUDENT.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        Type listType = new TypeToken<List<ClazzStudentDTO>>() {}.getType();
        List<ClazzStudentDTO> result = extractResBodyAsNullable(response, listType);
        return result != null ? result : Collections.emptyList();
    }

    public List<ClazzWithCountVO> listClazzWithCount(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("includeLeaved", false);
        if (schId != null) queryParams.put("schId", schId);
        String url = ApiUrl.Org.CLAZZ_LIST_WITH_COUNT.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        Type listType = new TypeToken<List<ClazzWithCountVO>>() {}.getType();
        List<ClazzWithCountVO> result = extractResBodyAsNullable(response, listType);
        log.info("Loaded {} classes", result != null ? result.size() : 0);
        return result != null ? result : Collections.emptyList();
    }

    public List<ClazzOptionVO> listActiveClazz(Long schId) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (schId != null) queryParams.put("schId", schId);
        String url = ApiUrl.Org.CLAZZ_LIST_ACTIVE.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        Type listType = new TypeToken<List<ClazzOptionVO>>() {}.getType();
        List<ClazzOptionVO> result = extractResBodyAsNullable(response, listType);
        return result != null ? result : Collections.emptyList();
    }

    public List<ClazzDayAttendVO> countClazzDay(String day) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (day != null) queryParams.put("day", day);
        String url = ApiUrl.Attend.COUNT_CLAZZ_DAY.getUrl(configStorage);
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        Type listType = new TypeToken<List<ClazzDayAttendVO>>() {}.getType();
        List<ClazzDayAttendVO> result = extractResBodyAsNullable(response, listType);
        return result != null ? result : Collections.emptyList();
    }

    public ClazzStudentMonthAttendVO listMonthAttend(Long clazzId, String month) throws ApiException {
        Map<String, Object> queryParams = new HashMap<>();
        if (month != null) queryParams.put("month", month);
        String url = ApiUrl.Attend.LIST_MONTH_ATTEND_STUDENT.getUrl(configStorage) + "/" + clazzId;
        String response = (String) this.get(url, queryParams.isEmpty() ? null : queryParams);
        return extractResBodyAs(response, ClazzStudentMonthAttendVO.class);
    }
}
