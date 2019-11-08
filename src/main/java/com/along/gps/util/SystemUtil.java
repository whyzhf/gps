package com.along.gps.util;

import com.along.gps.entity.*;
import io.netty.channel.ChannelHandlerContext;
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
	public static Map<String, Set<org.yeauty.pojo.Session>> NETTYSESSIONMAP = new ConcurrentHashMap<>();
    //记录用户发送命令
    public static Map<String,String> ORDERMAP = new ConcurrentHashMap<>();
    //设备异常命令
    @Scheduled(cron ="0 0 0 * * ?")
    public void clearMap(){
        ORDERMAP = new ConcurrentHashMap<>();
    }
	//存储发送ws的数据
	public static ConcurrentLinkedQueue<GpsDescData> gpsDatalist = new ConcurrentLinkedQueue<>();
	public static ConcurrentLinkedQueue<WSgpsData> WSGPSLIST = new ConcurrentLinkedQueue<>();
	//存储保存脚扣相关命令
	public static ConcurrentLinkedQueue<String> orderloglist = new ConcurrentLinkedQueue<>();
	//存储保存gps相关命令
	public static ConcurrentLinkedQueue<String> gpsloglist = new ConcurrentLinkedQueue<>();
	//存储保存gps数据
	public static ConcurrentLinkedQueue<GpsDescData> gpsJsonlist = new ConcurrentLinkedQueue<>();
	public static ConcurrentLinkedQueue<NgpsData> GPSDATALIST = new ConcurrentLinkedQueue<>();
	//保存每个设备的连接通道
	public static Map<ChannelHandlerContext, GpsStatusData> ContextMap=null;

    public static int FLAG=0;
}
