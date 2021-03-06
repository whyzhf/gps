package com.along.gps.web;


//import com.along.gps.util.GpsServer;
import com.along.gps.util.Gps.ColorUtil;
import com.along.gps.util.Gps.GpsHandleServer;
import com.along.gps.util.Order.ErrorMsg;
import com.along.gps.util.SystemUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class initServer implements CommandLineRunner {
	@Override
	public void run(String... args) throws Exception {
		new Thread(){
			public void run() {
				System.out.println("开始启动netty...");
		//		new GpsServer().openNettyServer(8899);
				new GpsHandleServer().openNettyServer(8899);
		//		SystemUtil.sessionmap=new ConcurrentHashMap<>();

			}
		}.start();

	}
}
