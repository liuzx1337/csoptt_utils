package com.csoptt.utils.http;

import com.csoptt.utils.common.CollectionUtils;
import com.csoptt.utils.common.GsonUtils;
import com.csoptt.utils.exception.BaseException;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * 请求属性
     */
    private static RequestConfig requestConfig;

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
     * 关闭客户端
     * @param client
     * @param response
     * @author qishao
     * date 2018-09-28
     */
    private static void close(CloseableHttpClient client, CloseableHttpResponse response) {
        if (null != client) {
            try {
                client.close();
            } catch (IOException e) {
                LOGGER.error("Close httpClient failed.", e);
            }
        }
        if (null != response) {
            try {
                response.close();
            } catch (IOException e) {
                LOGGER.error("Close httpResponse failed.", e);
            }
        }
    }

    /**
     * 获取请求属性
     * @return
     * @author qishao
     * date 2018-09-28
     */
    private static RequestConfig getConfig() {
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout).build();
        return requestConfig;
    }

    /**
     * 发送get请求
     * @param url
     * @param paramMap
     * @return
     * @author qishao
     * date 2018-09-28
     */
    public static String get(String url, Map<String, String> paramMap) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
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
            return EntityUtils.toString(entity, Consts.UTF_8);
        } catch (IOException e) {
            throw new BaseException("-1", "Send Get Request failed.");
        } finally {
            close(client, response);
        }
    }

    /**
     * 发送post请求
     * @param url
     * @param jsonBody
     * @return
     * @author qishao
     * date 2018-09-28
     */
    public static String post(String url, String jsonBody) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
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
            return EntityUtils.toString(entity, Consts.UTF_8);
        } catch (IOException e) {
            throw new BaseException("-1", "Send Post Request failed.");
        } finally {
            close(client, response);
        }
    }

    /**
     * 发送post请求，传输文件
     * 可批量传输
     * 可附带传输其他参数
     * @param url
     * @param multipartMap
     * @param otherParamMap
     * @return 
     * @author qishao
     * date 2018-09-28
     */
    public static String postFile(String url, Map<String, File> multipartMap, Map<String, Object> otherParamMap) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
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
            return EntityUtils.toString(entity, Consts.UTF_8);
        } catch (IOException e) {
            throw new BaseException("-1", "Send Multipart Post Request failed.");
        } finally {
            close(client, response);
        }
    }
}
