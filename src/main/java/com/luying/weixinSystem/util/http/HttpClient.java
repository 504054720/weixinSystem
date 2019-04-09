package com.luying.weixinSystem.util.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Configuration
public class HttpClient {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private Integer maxTotal=100;
	
	private Integer defaultMaxPerRoute=20;
	
	private Integer connectionTimeout=1000;
	
	private Integer connectionRequestTimeout=500;
	
	private Integer socketTimeout=10000;

	private boolean staleConnectionCheckEnabled=true;
	
	/**
	 * 首先实例化一个httpClient连接池管理器，设置最大连接数和并发连接数
	 * @return
	 */
	@Bean(name = "httpClientConnectionManager")
	public PoolingHttpClientConnectionManager getHttpClientConnectionManager(){
		PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
		httpClientConnectionManager.setMaxTotal(maxTotal);//最大连接数
		httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);//并发数
		return httpClientConnectionManager;
	}
	
	/**
	 * 实例化连接池，设置连接池管理器。这里需要以参数形式注入上边实例化的连接池管理器
	 * @param httpClientConnectionManager
	 * @return
	 */
	@Bean(name = "httpClientBuilder")
	public HttpClientBuilder getHttpClientBuilder(@Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager){
		//由于HttpClientBuilder中的构造方法被protected修饰，因此无法直接new出对象，可以使用HttpClientBuilder的静态create方法获取HttpClientBuilder对象
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		httpClientBuilder.setConnectionManager(httpClientConnectionManager);
		return httpClientBuilder;
	}
	/**
	 * 注入连接池，用户获取httpclient
	 * @param httpClientBuilder
	 * @return
	 */
	
	@Bean
	public CloseableHttpClient getCloseableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder){
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[]{new X509TrustManager() {
				
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return new X509Certificate[0];
				}
				
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
				}
			}}, new SecureRandom());
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
			CloseableHttpClient closeableHttpClient = httpClientBuilder.create().setSSLSocketFactory(socketFactory).build();
			return closeableHttpClient;
		} catch (Exception e) {
			logger.error("getCloseableHttpClient_exception_ssl"+e);
			return httpClientBuilder.build();
		}
	}
	/**
	 * Builder是RequestConfig的一个内部类，通过requestConfig的custom方法来获取到一个Builder对象
	 * 并设置builder的连接信息，这里还可以设置proxy，cookieSpec等属性，如有需要可在这里设置
	 * @return
	 */
	@Bean("builder")
	public RequestConfig.Builder getBuilder(){
		RequestConfig.Builder builder = RequestConfig.custom();
		return builder.setConnectTimeout(connectionTimeout)
				.setConnectionRequestTimeout(connectionRequestTimeout)
				.setSocketTimeout(socketTimeout)
				.setStaleConnectionCheckEnabled(staleConnectionCheckEnabled);
	}
	/**
	 * 使用builder构建一个requestConfig对象
	 * @param builder
	 * @return
	 */
	@Bean
	public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder){
		return builder.build();
	}
	
}
