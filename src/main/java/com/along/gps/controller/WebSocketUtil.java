/*
package com.along.gps.controller;


import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;

import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;

@ServerEndpoint(prefix = "netty-websocket")
@Component
public class WebSocketUtil {

	@OnOpen
	public void onOpen(Session session, HttpHeaders headers) throws IOException {
		System.out.println("new connection");
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		System.out.println("one connection closed");
		session.flush();
		session.close();
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		throwable.printStackTrace();
	}

	@OnMessage
	public void OnMessage(Session session, String message) {
		session.sendText("111");
	}
	@OnBinary
	public void OnBinary(Session session, byte[] bytes) {
		for (byte b : bytes) {
			System.out.println(b);
		}
		session.sendBinary(bytes);
	}

	@OnEvent
	public void onEvent(Session session, Object evt) {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			switch (idleStateEvent.state()) {
				case READER_IDLE:
					System.out.println("read idle");
					break;
				case WRITER_IDLE:
					System.out.println("write idle");
					break;
				case ALL_IDLE:
					System.out.println("all idle");
					break;
				default:
					break;
			}
		}
	}

	public static  String createDate(){
		StringBuffer str=new StringBuffer();
		for (int i = 0; i < 100; i++) {
			new Thread(()->{
				str.append(Thread.currentThread().getName()).append(";");
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return str.toString();

	}

}

*/
