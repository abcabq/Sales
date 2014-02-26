package com.renyu.sales.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class HttpClientUpload {
	
	/**
	 * httpClient上传
	 * @param path
	 * @param map
	 * @param type 1：发帖  2：个人信息
	 * @return
	 * @throws Exception
	 */
	public static String post(String path, HashMap<String, String> map, File file, String type) throws Exception {
		String result="";
	    HttpClient httpclient = new DefaultHttpClient();
	    try {
	        HttpPost httppost = new HttpPost(path);
	        MultipartEntity reqEntity = new MultipartEntity();
	        Iterator<Entry<String, String>> it=map.entrySet().iterator();
	        while(it.hasNext()) {
	        	Entry<String, String> entry=it.next();
	        	reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("utf-8")));
	        }
	        reqEntity.addPart(type, new FileBody(file));
	        httppost.setEntity(reqEntity);
	        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
	        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity resEntity = response.getEntity();
	        if (resEntity != null) {	        	
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(resEntity.getContent()));
	        	String line = "";
	        	while((line = reader.readLine()) != null) {
	        		result+=line;
	        	}	 
	        	resEntity.consumeContent();
	        }
	        //EntityUtils.consume(resEntity);
	        result=new String(result.getBytes("utf-8"), "iso-8859-1");
	    } catch(Exception e) {
	    	result=null;
	    } finally {
	    	httpclient.getConnectionManager().shutdown();
	    }
	    return result;
	}
}
