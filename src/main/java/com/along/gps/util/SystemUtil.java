package com.along.gps.util;

import com.along.gps.entity.GpsDescData;
import org.springframework.scheduling.annotation.Scheduled;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    @Scheduled(cron ="0 0 0 * * ?")
    public void clearMap(){
        ORDERMAP = new ConcurrentHashMap<>();
    }
	//存储发送ws的数据
	public static ConcurrentLinkedQueue<GpsDescData> gpsDatalist = new ConcurrentLinkedQueue<>();
	//存储保存脚扣相关命令
	public static ConcurrentLinkedQueue<String> orderloglist = new ConcurrentLinkedQueue<>();
	//存储保存gps相关命令
	public static ConcurrentLinkedQueue<String> gpsloglist = new ConcurrentLinkedQueue<>();
	//存储保存gps数据
	public static ConcurrentLinkedQueue<GpsDescData> gpsJsonlist = new ConcurrentLinkedQueue<>();

    public static int FLAG=0;
}
