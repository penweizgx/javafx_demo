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

}
