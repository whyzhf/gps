package com.along.gps.util;

import com.alibaba.fastjson.JSON;
import com.along.gps.entity.GpsDescData;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.Map;

public class ServerEncoder implements Encoder.Text<Map<String,Object>> {


	@Override
	public void init(EndpointConfig endpointConfig) {

	}

	@Override
	public void destroy() {

	}

	@Override
	public String encode(Map<String, Object> map)  {
		System.out.println("JSON.toJSONString(map):"+JSON.toJSONString(map));
		return JSON.toJSONString(map);
	}
}
