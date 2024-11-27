package com.parse.demo.web.config;

/**
 * @author: sunyixin@kanzhun.com
 * @date: 2019/3/27 2:15 PM
 * @description:
 */
public interface MomentUrlConfig {
    @Deprecated
    String WAP_URL_PREFIX = "/wap/moment";
    String WAP_URL_PREFIX_NEW = "/wapi/moment";
    String APP_URL_PREFIX = "/api/moment";
    String APP_URL_PREFIX_MINIAPP = "/wapi/moment/miniapp";
}
