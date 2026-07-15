package com.example.app.api;

import com.example.app.api.storage.ConfigStorage;
import com.example.app.api.storage.HostConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import static com.example.app.api.storage.HostConfig.API_DEFAULT_HOST_URL;
import static com.example.app.api.storage.HostConfig.buildUrl;

/**
 * <pre>
 *  公众号接口api地址
 *  Created by BinaryWang on 2019-06-03.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
public interface ApiUrl {

  /**
   * 得到api完整地址.
   *
   * @param config 微信公众号配置
   * @return api地址
   */
  default String getUrl(ConfigStorage config) {
    HostConfig hostConfig = null;
    if (config != null) {
      hostConfig = config.getApiHost();
    }
    return buildUrl(hostConfig, this.getPrefix(), this.getPath());

  }

  /**
   * the path
   *
   * @return path
   */
  String getPath();

  /**
   * the prefix
   *
   * @return prefix
   */
  String getPrefix();

  @AllArgsConstructor
  @Getter
  enum Authenticate implements ApiUrl {
    /**
     */
    PUBLIC_KEY(API_DEFAULT_HOST_URL, "/auth/getPublickey"),
    LOGIN_WITH_PASSWORD(API_DEFAULT_HOST_URL, "/auth/loginWithPassword"),
    LOGOUT(API_DEFAULT_HOST_URL, "/auth/logout"),
    CURRENT_USER(API_DEFAULT_HOST_URL, "/auth/currentUser"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Employee implements ApiUrl {
    LIST(API_DEFAULT_HOST_URL, "/employee/list"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Student implements ApiUrl {
    LIST_WITH_CLAZZ(API_DEFAULT_HOST_URL, "/student/listWithClazz"),
    LIST_BY_CONDITION(API_DEFAULT_HOST_URL, "/student/listByCondition"),
    DETAIL(API_DEFAULT_HOST_URL, "/student/detail"),
    CREATE(API_DEFAULT_HOST_URL, "/student/create"),
    CHANGE(API_DEFAULT_HOST_URL, "/student/change"),
    GROUP_CLAZZ_STUDENT(API_DEFAULT_HOST_URL, "/student/groupClazzStudent"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Org implements ApiUrl {
    CLAZZ_LIST_WITH_COUNT(API_DEFAULT_HOST_URL, "/org/clazz/listWithCount"),
    CLAZZ_LIST_ACTIVE(API_DEFAULT_HOST_URL, "/org/clazz/listActive"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Attend implements ApiUrl {
    COUNT_CLAZZ_DAY(API_DEFAULT_HOST_URL, "/attend/countClazzDay"),
    LIST_MONTH_ATTEND_STUDENT(API_DEFAULT_HOST_URL, "/attend/listMonthAttend"),
    ;

    private final String prefix;
    private final String path;
  }

  @AllArgsConstructor
  @Getter
  enum Finance implements ApiUrl {
    ACCOUNT(API_DEFAULT_HOST_URL, "/finance/account"),
    ;

    private final String prefix;
    private final String path;
  }

}
