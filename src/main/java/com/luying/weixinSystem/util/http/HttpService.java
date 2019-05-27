package com.luying.weixinSystem.util.http;

import com.alibaba.fastjson.JSON;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Component
public class HttpService {
	
	@Autowired
	private CloseableHttpClient httpClient;
	@Autowired
	private RequestConfig requestConfig;
	
	private HttpResult httpResult;

    /**
     * 无参数get方法
     * @param url
     * @return
     * @throws Exception
     */
    public HttpResult doGetlong(String url) throws Exception{
        HttpGet httpGet = new HttpGet(url);
        RequestConfig config =  RequestConfig.custom()
                .setConnectTimeout(50000)
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(80000).build();
        httpGet.setConfig(config);
        CloseableHttpResponse response = this.httpClient.execute(httpGet);
        return new HttpResult(String.valueOf(response.getStatusLine().getStatusCode()), EntityUtils.toString(response.getEntity(), "UTF-8"));
    }

	/**
	 * 无参数get方法
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public HttpResult doGet(String url) throws Exception{
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		CloseableHttpResponse response = this.httpClient.execute(httpGet);
		return new HttpResult(String.valueOf(response.getStatusLine().getStatusCode()), EntityUtils.toString(response.getEntity(), "UTF-8"));
	}
	/**
	 * 带参数get方法
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public HttpResult doGet(String url,Map<String, Object> map) throws Exception{
		URIBuilder uriBuilder = new URIBuilder(url);
		if(map != null){
			for(Map.Entry<String, Object> entry : map.entrySet()){
				uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
			}
		}
		return this.doGet(uriBuilder.build().toString());
	}
	/**
	 * 带参数post方法
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public HttpResult doPost(String url,Map<String, Object> map) throws Exception{
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
		if(map != null){
			String params = JSON.toJSONString(map);
			StringEntity entity = new StringEntity(params, "UTF-8");
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
		}
		CloseableHttpResponse response = this.httpClient.execute(httpPost);
		return new HttpResult(String.valueOf(response.getStatusLine().getStatusCode()), EntityUtils.toString(response.getEntity(), "UTF-8"));
	}

    /**
     * @desc ：微信上传素材的请求方法
     *
     * @param requestUrl  微信上传临时素材的接口url
     * @param file    要上传的文件
     * @return String  上传成功后，微信服务器返回的消息
     */
	public String httpRequest(String requestUrl, File file) throws IOException {
		StringBuffer buffer = new StringBuffer();

		//1.建立连接
		URL url = new URL(requestUrl);
		HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();  //打开链接

		//1.1输入输出设置
		httpUrlConn.setDoInput(true);
		httpUrlConn.setDoOutput(true);
		httpUrlConn.setUseCaches(false); // post方式不能使用缓存
		//1.2设置请求头信息
		httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
		httpUrlConn.setRequestProperty("Charset", "UTF-8");
		//1.3设置边界
		String BOUNDARY = "----------" + System.currentTimeMillis();
		httpUrlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		// 请求正文信息
		// 第一部分：
		//2.将文件头输出到微信服务器
		StringBuilder sb = new StringBuilder();
		sb.append("--"); // 必须多两道线
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"media\";filelength=\"" + file.length()
				+ "\";filename=\"" + file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		byte[] head = sb.toString().getBytes("utf-8");
		// 获得输出流
		OutputStream outputStream = new DataOutputStream(httpUrlConn.getOutputStream());
		// 将表头写入输出流中：输出表头
		outputStream.write(head);

		//3.将文件正文部分输出到微信服务器
		// 把文件以流文件的方式 写入到微信服务器中
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			outputStream.write(bufferOut, 0, bytes);
		}
		in.close();
		//4.将结尾部分输出到微信服务器
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
		outputStream.write(foot);
		outputStream.flush();
		outputStream.close();

		//5.将微信服务器返回的输入流转换成字符串
		InputStream inputStream = httpUrlConn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String str = null;
		while ((str = bufferedReader.readLine()) != null) {
			buffer.append(str);
		}

		bufferedReader.close();
		inputStreamReader.close();
		// 释放资源
		inputStream.close();
		inputStream = null;
		httpUrlConn.disconnect();

		return buffer.toString();
	}

}
