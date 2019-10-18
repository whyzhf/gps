package com.along.gps.util.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorMsg {
	public static Map<String,String> ERRORMAP = new ConcurrentHashMap<>();

	public ErrorMsg() {
		ERRORMAP.put("10","主锁异常打开");
		ERRORMAP.put("11","主锁异常恢复");
		ERRORMAP.put("12","电池仓异常打开");
		ERRORMAP.put("13","电池仓异常恢复");
		ERRORMAP.put("14","绷带异常断开");
		ERRORMAP.put("15","绷带异常恢复");
		ERRORMAP.put("16","设备电量不足");
		ERRORMAP.put("17","设备充满电");
		ERRORMAP.put("18","电击点遮挡");
		ERRORMAP.put("19","电击点遮挡取消");
		ERRORMAP.put("1A","电击失败");
		ERRORMAP.put("1B","恢复出厂设置");
		ERRORMAP.put("1C","添加分组");
		ERRORMAP.put("1D","删除分组");
		ERRORMAP.put("1E","删除分组ID");
		ERRORMAP.put("1F","分组布防");
		ERRORMAP.put("20","分组撤防");
		ERRORMAP.put("21","设置防脱逃模式（不启动）");
		ERRORMAP.put("22","启动防脱逃模式（广播方式）");
		ERRORMAP.put("23","停止防脱逃模式（广播方式）");
		ERRORMAP.put("24","脚铐从当前遥控器的防脱逃分组中剔除");
		ERRORMAP.put("25","当前遥控器的防脱逃分组解散");
		ERRORMAP.put("26","启动防脱逃电击");
		ERRORMAP.put("27","停止防脱逃电击");
		ERRORMAP.put("28","启动防脱逃模式失败");
		ERRORMAP.put("29","停止防脱逃模式失败");
		ERRORMAP.put("2A","防脱逃设置失败");
		ERRORMAP.put("2B","防脱逃删除失败");
		ERRORMAP.put("2C","防脱逃设置成功");
		ERRORMAP.put("2D","防脱逃删除成功");
		ERRORMAP.put("2E","防脱逃越界");
		ERRORMAP.put("2F","防脱逃可控");
		ERRORMAP.put("30","设备充电到4V");
		ERRORMAP.put("31","主锁未关好");
		ERRORMAP.put("32","任何电击强行停止");
		ERRORMAP.put("33","定位失败");
		ERRORMAP.put("34","定位成功");
		ERRORMAP.put("35","4G连接失败");
		ERRORMAP.put("36","4G连接正常");
		ERRORMAP.put("37","打开电击功能");
		ERRORMAP.put("38","关闭电击功能");
	}


}
