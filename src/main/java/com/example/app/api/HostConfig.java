package com.example.app.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 微信接口地址域名部分的自定义设置信息.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * created on  2019-06-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostConfig implements Serializable {
  public static final String API_DEFAULT_HOST_URL = "https://doixiao.cn/api";

  /**
   * 对应于：https://api.weixin.qq.com
   */
  private String apiHost;

  public static String buildUrl(HostConfig hostConfig, String prefix, String path) {
    if (hostConfig == null) {
      return prefix + path;
    }

    if (hostConfig.getApiHost() != null && prefix.equals(API_DEFAULT_HOST_URL)) {
      return hostConfig.getApiHost() + path;
    }

    return prefix + path;
  }
}
