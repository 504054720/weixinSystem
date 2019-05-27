package com.luying.weixinSystem;

import com.luying.weixinSystem.service.FuwuhaoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeixinSystemApplicationTests {
	@Autowired
	private FuwuhaoService fuwuhaoService;

	@Test
	public void contextLoads() {
	}
	@Test
	public void subscribeWeixin(){
		Date date = new Date();
		System.out.println(date.getTime());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,String> map = new HashMap<>();
		map.put("FromUserName","okmFV1il0kT6OT9DRjV0-7K55uh0");
		map.put("MsgType","event");
		map.put("Event","subscribe");
		map.put("CreateTime",simpleDateFormat.format(date));

		fuwuhaoService.handleEventMessage(map);
	}
	@Test
	public void unSubscribeWeixin(){
		Date date = new Date();
		System.out.println(date.getTime());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,String> map = new HashMap<>();
		map.put("FromUserName","okmFV1il0kT6OT9DRjV0-7K55uh0");
		map.put("MsgType","event");
		map.put("Event","unsubscribe");
		map.put("CreateTime",simpleDateFormat.format(date));

		fuwuhaoService.handleEventMessage(map);
	}

}
