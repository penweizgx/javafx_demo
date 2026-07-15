package com.example.app.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class StudentQO {
    @SerializedName("校区id")
    private Long schId;

    @SerializedName("班级id")
    private Long clazzId;

    @SerializedName("按关键词查找——姓名、手机号、拼音")
    private String keyword;

    @SerializedName("是否包含已离开的学生，period is null时有效")
    private Boolean includeGraduated;

    @SerializedName("查询时段： claazz不为空则为班级期内，否则为在校时间")
    private DatePeriod period;
}
