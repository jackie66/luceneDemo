package library.jackie;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ApiRequest {
	
	
	public static String post(String url, Map<String, String> params) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		List<NameValuePair> nvps = new ArrayList <NameValuePair>();  
        Set<String> keySet = params.keySet();  
        for(String key : keySet) {  
            nvps.add(new BasicNameValuePair(key, params.get(key)));  
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8")); 
        logger.debug("请求url:{}", url);
        logger.debug("请求参数：{}", new Gson().toJson(params));
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		logger.info("接口请求完成,状态码：{}", httpResponse.getStatusLine());
		String response = URLDecoder.decode(EntityUtils.toString(httpResponse.getEntity()), "UTF-8");
		logger.debug("返回字符串：{}", response);
		httpClient.close();
		return response;
	}
	
	public static String post(String url, String params) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        
        StringEntity entity = new StringEntity(params, "UTF-8");//解决中文乱码问题    
        entity.setContentEncoding("UTF-8");    
        entity.setContentType("application/json");    
        httpPost.setEntity(entity); 
        
        logger.debug("请求url:{}", url);
        logger.debug("请求参数：{}", params);
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		logger.info("接口请求完成,状态码：{}", httpResponse.getStatusLine());
		String response = URLDecoder.decode(EntityUtils.toString(httpResponse.getEntity()), "UTF-8");
		logger.debug("返回字符串：{}", response);
		httpClient.close();
		return response;
	}
	
	public static String get(String url) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        logger.debug("请求url:{}", url);
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		logger.info("接口请求完成,状态码：{}", httpResponse.getStatusLine());
		String response = URLDecoder.decode(EntityUtils.toString(httpResponse.getEntity()), "UTF-8");
		logger.debug("返回字符串：{}", response);
		httpClient.close();
		return response;
	}
	
	public static InputStream getInputStream(String url) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        logger.debug("请求url:{}", url);
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		logger.info("接口请求完成,状态码：{}", httpResponse.getStatusLine());
		InputStream is = httpResponse.getEntity().getContent();
		InputStream in = IOUtils.toBufferedInputStream(is);
		logger.info("缓存输入流完成");
		httpClient.close();
		return in;
	}
	
	public static Map<String, String> upload(String url, File file) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		FileBody fileBody = new FileBody(file);
		HttpEntity reqEntity = MultipartEntityBuilder.create()
				.addPart("uploadFile", fileBody).build();
		httpPost.setEntity(reqEntity);
		CloseableHttpResponse response = httpClient.execute(httpPost);
		if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
			logger.info("上传成功");
			HttpEntity entity = response.getEntity();
			// 显示内容
			if (entity != null) {
				String json = EntityUtils.toString(entity);
				Gson gson = new Gson();
				Map<String, String> map = gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
				EntityUtils.consume(entity);
				httpClient.close();
				return map;
			}
		}
		httpClient.close();
		return null;
	}
	
}
