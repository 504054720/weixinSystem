package com.luying.weixinSystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	 
		/**
		 * 不需要登录拦截的url
		 */
		final String[] notLoginInterceptPaths = {"/static/**","/templates/**"};
	 
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			// 这里添加多个拦截器
		}
	 
		@Override
		public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
			configurer.enable();
		}

		@Bean
		public InternalResourceViewResolver viewResolver() {
			InternalResourceViewResolver resolver = new InternalResourceViewResolver();
			resolver.setPrefix("/templates/");
			resolver.setSuffix(".html");
			return resolver;
		}
	 
		/**
		 * 配置不需要经过controller就跳转到登录页面
		 */
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("/index").setViewName("index");
	 
		}

		/***
		 * addResourceLocations指的是文件放置的目录，addResoureHandler指的是对外暴露的访问路径
		 */
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			//排除静态资源拦截
			registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/");
			registry.addResourceHandler("/templates/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/templates/");
			WebMvcConfigurer.super.addResourceHandlers(registry);
		}
		
}
