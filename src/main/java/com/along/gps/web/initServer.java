package com.along.gps.web;


import com.along.gps.util.GpsServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;



@Component
public class initServer implements CommandLineRunner {
	@Override
	public void run(String... args) throws Exception {
		new Thread(){
			public void run() {
				System.out.println("开始启动netty...");
				new GpsServer().openNettyServer(8899);

			}
		}.start();

	}
}
