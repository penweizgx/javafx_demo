package com.example.app.model;

import lombok.Data;

@Data
public class User {
    /**
     * 员工名字
     */
    private String name;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 机构范围
     */
    private OrgBound orgBound;

    /**
     * 机构名称
     */
    private String orgName;
}
