package com.csoptt.utils.http;

import com.csoptt.utils.common.CollectionUtils;
import com.csoptt.utils.common.JsoupUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 自定义处理Http请求的Wrapper
 * 目前支持持久化requestBody、自定义Header等功能
 *
 * @author qishao
 * @date 2018-10-08
 */
public class ModifyHttpRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Log4j
     */
    private Logger logger = LoggerFactory.getLogger(ModifyHttpRequestWrapper.class);

    /**
     * 待持久化的请求数据
     */
    private byte[] requestBodyBytes;

    /**
     * 自定义的Header
     */
    private Map<String, String> customHeaders;

    /**
     * 是否进行XSS检测
     */
    private boolean doXss;

    /**
     * 构造方法
     *
     * @param request
     */
    public ModifyHttpRequestWrapper(HttpServletRequest request) {
        super(request);
        // 从request对象中取出输出流，将其中的数据转化为字节
        try {
            requestBodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
        } catch (IOException e) {
            logger.error("Get inputStream from request failed.", e);
        }
        customHeaders = new HashMap<>();
    }

    /**
     * 构造方法
     * 是否进行XSS检测
     *
     * @param request
     * @param doXss
     */
    public ModifyHttpRequestWrapper(HttpServletRequest request, boolean doXss) {
        this(request);
        this.doXss = doXss;
    }

    /**
     * 重写getInputStream()方法
     * 将requestBody持久化
     * 根据是否进行XSS检测，对requestBody的处理也不同
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (null == requestBodyBytes) {
            requestBodyBytes = new byte[0]; // 防止NullPointerException
        }

        ByteArrayInputStream in;
        // XSS字符替换
        if (doXss) {
            String str = new String(requestBodyBytes, "UTF-8");
            String newJson = JsoupUtils.cleanParam(str);
            in = new ByteArrayInputStream(newJson.getBytes("UTF-8"));
        } else {
            in = new ByteArrayInputStream(requestBodyBytes);
        }

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                int result = -1;
                try {
                    result = in.read();
                } catch (Exception e) {
                    logger.error("Read servletInputStream failed.", e);
                } finally {
                    in.close();
                }
                return result;
            }
        };
    }

    /**
     * 重写 getReader()方法
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * 获取对应名称Header的值
     * 可获取自定义内容
     *
     * @param name
     */
    @Override
    public String getHeader(String name) {
        if (doXss) {
            name = JsoupUtils.clean(name);
        }
        // 如果自定义存在
        String headerValue = customHeaders.get(name);

        if (headerValue == null) {
            headerValue = super.getHeader(name);
        }

        // XSS
        if (doXss) {
            headerValue = JsoupUtils.clean(headerValue);
        }

        return headerValue;
    }

    /**
     * The default behavior of this method is to return getHeaderNames()
     * on the wrapped request object.
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        // 自定义的HeaderNames
        Set<String> headerNameSet = customHeaders.keySet();
        // 请求头中的HeaderNames
        Enumeration<String> headerNamesInHeader = super.getHeaderNames();
        // 合并
        while (headerNamesInHeader.hasMoreElements()) {
            headerNameSet.add(headerNamesInHeader.nextElement());
        }
        // XSS
        if (doXss) {
            Set<String> headerNameSetCleared = new HashSet<>();
            headerNameSet.forEach(headerName -> headerNameSetCleared.add(JsoupUtils.clean(headerName)));
            headerNameSet.clear();
            headerNameSet.addAll(headerNameSetCleared);
        }
        return Collections.enumeration(headerNameSet);
    }

    /**
     * 自定义Header内容
     * @param name
     * @param value
     * @author qishao
     * date 2018-10-08
     */
    public void putHeader(String name, String value) {
        // 1. 试图从header中获取
        String valueInHeader = getHeader(name);
        if (StringUtils.equals(value, valueInHeader)) {
            // 存在，则不做任何事情
            return;
        }

        // 2. 放入自定义的Map中
        customHeaders.put(name, value);
    }

    /**
     * 获取参数用XSS过滤
     *
     * @param name
     */
    @Override
    public String getParameter(String name) {
        boolean flag = !("content".equals(name) || name.endsWith("WithHtml"));
        if (flag || doXss) {
            name = JsoupUtils.clean(name);

            String value = super.getParameter(name);
            if (StringUtils.isNotBlank(value)) {
                value = JsoupUtils.clean(value);
            }

            return value;
        } else {
            return super.getParameter(name);
        }
    }

    /**
     * 获取参数用XSS过滤
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> paramMap = super.getParameterMap();
        if (CollectionUtils.isEmpty(paramMap)) {
            return paramMap;
        }
        
        if (doXss) {
            paramMap.forEach((name, values) -> {
                if (CollectionUtils.isNotEmpty(values)) {
                    for (int i = 0, len = values.length; i < len; i++) {
                        values[i] = JsoupUtils.clean(values[i]);
                    }
                }
            });
        }
        return paramMap;
    }

    /**
     * 获取参数用XSS过滤
     *
     * @param name
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (CollectionUtils.isNotEmpty(values)) {
            if (doXss) {
                for (int i = 0, len = values.length; i < len; i++) {
                    values[i] = JsoupUtils.clean(values[i]);
                }
            }
        }
        return values;
    }
}