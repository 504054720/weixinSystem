package com.luying.weixinSystem;

import javafx.application.Application;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan("com.luying.weixinSystem.mapper")
public class WeixinSystemApplication{


	public static void main(String[] args) {
		SpringApplication.run(WeixinSystemApplication.class, args);
	}
	//打war包时 此类继承extends SpringBootServletInitializer 并打开以下代码
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		return builder.sources(WeixinSystemApplication.class);
//	}


}
