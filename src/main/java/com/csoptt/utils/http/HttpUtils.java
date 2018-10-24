package com.csoptt.utils.http;

import com.csoptt.utils.common.CollectionUtils;
import com.csoptt.utils.common.GsonUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 发送Http请求的工具类
 *
 * @author qishao
 * @date 2018-09-28
 */
public class HttpUtils {

    /**
     * Log4j
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * TCP三次握手超时时间
     */
    private static int socketTimeout = 60000;

    /**
     * 建立连接超时时间
     */
    private static int connectTimeout = 60000;

    /**
     * 从连接池获取连接超时时间
     */
    private static int connectionRequestTimeout = 60000;

    /**
     * Multipart时所使用的contentType
     */
    private static final ContentType TEXT_PLAIN = ContentType.create("text/plain", Consts.UTF_8);

    /**
     * Sets the socketTimeout.
     * <p>
     * <p>You can use getSocketTimeout() to get the value of socketTimeout</p>
     *
     * @param socketTimeout socketTimeout
     */
    public static void setSocketTimeout(int socketTimeout) {
        HttpUtils.socketTimeout = socketTimeout;
    }

    /**
     * Sets the connectTimeout.
     * <p>
     * <p>You can use getConnectTimeout() to get the value of connectTimeout</p>
     *
     * @param connectTimeout connectTimeout
     */
    public static void setConnectTimeout(int connectTimeout) {
        HttpUtils.connectTimeout = connectTimeout;
    }

    /**
     * Sets the connectionRequestTimeout.
     * <p>
     * <p>You can use getConnectionRequestTimeout() to get the value of connectionRequestTimeout</p>
     *
     * @param connectionRequestTimeout connectionRequestTimeout
     */
    public static void setConnectionRequestTimeout(int connectionRequestTimeout) {
        HttpUtils.connectionRequestTimeout = connectionRequestTimeout;
    }

    /**
     * 关闭
     * @param closeables
     * @author qishao
     * date 2018-09-28
     */
    private static void close(Closeable... closeables) {
        if (CollectionUtils.isNotEmpty(closeables)) {
            for (Closeable closeable : closeables) {
                if (null != closeable) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        LOGGER.error("Close resource failed", e);
                    }
                }
            }
        }
    }

    /**
     * 获取httpClient
     * @param doBasicAuth
     * @param username
     * @param password
     * @return
     * @author qishao
     * date 2018-10-09
     */
    private static CloseableHttpClient getHttpClient(boolean doBasicAuth,
                                                     String username, String password) {
        if (doBasicAuth) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            return HttpClients.custom().setDefaultCredentialsProvider(provider).build();
        }
        return HttpClients.custom().build();
    }

    /**
     * 获取请求属性
     * @return
     * @author qishao
     * date 2018-09-28
     */
    private static RequestConfig getConfig() {
        return RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout).build();
    }

    /**
     * 发送get请求，不需要密码
     * @param url
     * @param paramMap
     * @return 
     * @author qishao
     * date 2018-10-24
     */
    public static ResponseMessage get(String url, Map<String, String> paramMap) {
        return get(url, paramMap, null, null);
    }

    /**
     * 发送get请求，需要密码
     * @param url
     * @param paramMap
     * @param username
     * @param password
     * @return
     * @author qishao
     * date 2018-09-28
     */
    public static ResponseMessage get(String url, Map<String, String> paramMap,
                                      String username, String password) {
        CloseableHttpClient client = getHttpClient(null != username, username, password);
        if (CollectionUtils.isNotEmpty(paramMap)) {
            StringBuilder stringBuilder = new StringBuilder();
            paramMap.forEach((key, value) -> {
                stringBuilder.append("&").append(key).append("=").append(value);
            });
            String paramStr = stringBuilder.toString().substring(1);
            url = url + "?" + paramStr;
        }
        HttpGet get = new HttpGet(url);
        get.setConfig(getConfig());

        CloseableHttpResponse response = null;
        try {
            response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity, Consts.UTF_8);
            return Result.success("0", "Http Get Success.", data);
        } catch (IOException e) {
            return Result.error("-1", "Http Get failed.", e.getMessage());
        } finally {
            close(client, response);
        }
    }

    /**
     * 发送post请求，不需要密码
     * @param url
     * @param jsonBody
     * @return 
     * @author qishao
     * date 2018-10-24
     */
    public static ResponseMessage post(String url, String jsonBody) {
        return post(url, jsonBody, null, null);
    }

    /**
     * 发送post请求，需要密码
     * @param url
     * @param jsonBody
     * @param username
     * @param password
     * @return
     * @author qishao
     * date 2018-09-28
     */
    public static ResponseMessage post(String url, String jsonBody,
                                       String username, String password) {
        CloseableHttpClient client = getHttpClient(null != username, username, password);
        HttpPost post = new HttpPost(url);

        // json格式的requestbody
        EntityBuilder entityBuilder = EntityBuilder.create();
        entityBuilder.setContentType(ContentType.APPLICATION_JSON);
        entityBuilder.setText(jsonBody);
        
        post.setEntity(entityBuilder.build());
        post.setConfig(getConfig());
        
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity, Consts.UTF_8);
            return Result.success("0", "Http Post Success.", data);
        } catch (IOException e) {
            return Result.error("-1", "Http Post failed.", e.getMessage());
        } finally {
            close(client, response);
        }
    }

    /**
     * 发送post请求，传输文件，需要密码
     * @param url
     * @param multipartMap
     * @param otherParamMap
     * @return 
     * @author qishao
     * date 2018-10-24
     */
    public static ResponseMessage postFile(String url, Map<String, File> multipartMap,
                                           Map<String, Object> otherParamMap) {
        return postFile(url, multipartMap, otherParamMap, null, null);
    }

    /**
     * 发送post请求，传输文件
     * 可批量传输
     * 可附带传输其他参数
     * @param url
     * @param multipartMap
     * @param otherParamMap
     * @param username
     * @param password
     * @return 
     * @author qishao
     * date 2018-09-28
     */
    public static ResponseMessage postFile(String url, Map<String, File> multipartMap,
                                           Map<String, Object> otherParamMap,
                                           String username, String password) {
        CloseableHttpClient client = getHttpClient(null != username, username, password);
        HttpPost post = new HttpPost(url);
        
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setCharset(Consts.UTF_8);
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        // 将文件体批量加入entityBuilder
        multipartMap.forEach((paramName, file) -> {
            FileBody fileBody = new FileBody(file);
            entityBuilder.addPart(paramName, fileBody);
        });
        // 将其他参数批量加入entityBuilder
        otherParamMap.forEach((key, obj) -> {
            String value = obj instanceof String ? (String) obj : GsonUtils.toJson(obj);
            entityBuilder.addPart(key, new StringBody(value, TEXT_PLAIN));
        });
        
        post.setConfig(getConfig());
        post.setEntity(entityBuilder.build());
        
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity, Consts.UTF_8);
            return Result.success("0", "Multipart Post Success.", data);
        } catch (IOException e) {
            return Result.error("-1", "Multipart Post failed.", e.getMessage());
        } finally {
            close(client, response);
        }
    }
}
