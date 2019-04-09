package com.luying.weixinSystem.util.http;

import org.apache.http.conn.HttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdleConnectionEvictor extends Thread{
	
	@Autowired
	private HttpClientConnectionManager conManager;
	
	private volatile boolean shutdown;
	
	public IdleConnectionEvictor(){
		super();
		super.start();
	}

	@Override
	public void run() {

		try {
			while(!shutdown){
				synchronized (this) {
					wait(5000);
					//关闭失效的连接
					conManager.closeExpiredConnections();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//清理无效连接线程
	public void shutdown(){
		shutdown = true;
		synchronized (this) {
			notifyAll();
		}
	}
	

}
