package com.along.gps.util;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: why
 * @Data:2019/6/12
 * @Deacription:
 */
public class SystemUtil {
    //记录请求用户角色
    public static Map<String,Session> sessionmap = new ConcurrentHashMap<>();
}
