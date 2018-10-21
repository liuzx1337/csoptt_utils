package com.csoptt.utils.sonar.query;

import com.csoptt.utils.sonar.bean.MeasureComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.services.Query;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * /api/measures/component
 *
 * @author qishao
 * @date 2018-10-10
 */
public class MeasureComponentQuery extends Query<MeasureComponent> {

    /**
     * Log4j
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureComponentQuery.class);

    /**
     * 基础URL
     */
    private static final String BASE_URL = "/api/measures/component?";

    /**
     * 查询参数中的componentKey
     * 对应sonar配置时的sonar.projectKey一项
     */
    private String componentKey;

    /**
     * 查询参数中的metricKeys
     * 对应想要查询的参数配置项名称
     */
    private String metricKeys;

    public MeasureComponentQuery(String componentKey, String metricKeys) {
        this.componentKey = componentKey;
        this.metricKeys = metricKeys;
    }
    
    /**
     * 创建查询
     * @param componentKey
     * @param metricKeys
     * @return
     * @author liuzixi
     * date 2018-10-10
     */
    public static MeasureComponentQuery create(String componentKey, String metricKeys) {
        return new MeasureComponentQuery(componentKey, metricKeys);
    }

    @Override
    public Class<MeasureComponent> getModelClass() {
        return MeasureComponent.class;
    }

    /**
     * url
     * @return
     * @author liuzixi
     * date 2018-10-10
     */
    @Override
    public String getUrl() {
        StringBuilder stringBuilder = new StringBuilder(BASE_URL);
        appendUrlParameter(stringBuilder, "componentKey", componentKey);
        appendUrlParameter(stringBuilder, "metricKeys", metricKeys);
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("&"));
        try {
            String url = URLEncoder.encode(stringBuilder.toString(), "UTF-8");
            return url;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Url encode failed.", e);
            return null;
        }
    }
}
