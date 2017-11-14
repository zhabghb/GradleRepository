package com.cetc.hubble.metagrid.controller.support;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by dahey on 2017/4/18.
 */
public class HttpUtil {

    public static String doPost(String param, String url) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost(url);
            StringEntity entity = new StringEntity(param,"utf-8");
            httppost.setEntity(entity);
            String response = httpclient.execute(httppost, new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws  IOException {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }
            });
            if (response == null)
                throw new IOException("连接不上服务器:" + url);
            return response;
        }
    }

    public static String doPut(String param, String url) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPut httpput = new HttpPut(url);
            StringEntity entity = new StringEntity(param,"utf-8");
            httpput.setEntity(entity);
            String response = httpclient.execute(httpput, new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws  IOException {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }
            });
            if (response == null)
                throw new IOException("连接不上服务器:" + url);
            return response;
        }
    }

    public static String doPostJson(String param, String url) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost(url);
            StringEntity entity = new StringEntity(param,"utf-8");
            httppost.setHeader("Content-Type","application/json;charset=UTF-8");
            httppost.setEntity(entity);
            String response = httpclient.execute(httppost, new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws  IOException {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }
            });
            if (response == null)
                throw new IOException("连接不上服务器:" + url);
            return response;
        }
    }

    public static String doPostJsonHaveTimeout(String param, String url) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(3000).setConnectionRequestTimeout(3000)
                    .setSocketTimeout(3000).build();
            httppost.setConfig(requestConfig);
            StringEntity entity = new StringEntity(param,"utf-8");
            httppost.setHeader("Content-Type","application/json;charset=UTF-8");
            httppost.setEntity(entity);
            String response = httpclient.execute(httppost, new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws  IOException {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }
            });
            if (response == null)
                throw new IOException("连接不上服务器:" + url);
            return response;
        }
    }

    public static String doGet(String url) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(url);
            String res = httpclient.execute(httpget, new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws IOException {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }
            });
            return res;
        }
    }
    public static byte[] doGetBytes(String url) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(url);
            byte[] res = httpclient.execute(httpget, new ResponseHandler<byte[]>() {
                @Override
                public byte[] handleResponse(
                        final HttpResponse response) throws IOException {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toByteArray(entity) : null;
                }
            });
            return res;
        }
    }


    public static String doGet(String url, String charset)  throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(url);
            String res = httpclient.execute(httpget, new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws IOException {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity,charset) : null;
                }
            });
            return res;
        }
    }
}
