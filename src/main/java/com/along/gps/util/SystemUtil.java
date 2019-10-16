package com.along.gps.util;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: why
 * @Data:2019/6/12
 * @Deacription:
 */
public class SystemUtil {
    //记录请求用户角色
    public static Map<String, Set<Session>> sessionmap = new ConcurrentHashMap<>();

    //记录用户发送命令
    public static Map<String,String> ORDERMAP = new ConcurrentHashMap<>();
    //设备异常命令

}
