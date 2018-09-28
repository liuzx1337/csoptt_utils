package com.csoptt.utils.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map与Java对象之间的转换工具类
 *
 * @author qishao
 * @date 2018-09-28
 */
public class MapUtils {

    /**
     * Log4j
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MapUtils.class);

    /**
     * 将javaBean转换为Map
     * @param javaBean
     * @return
     * @author qishao
     * date 2018-09-28
     */
    public static Map<String, Object> javaToMap(Object javaBean) {
        Map<String, Object> map = new HashMap<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(javaBean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (CollectionUtils.isNotEmpty(propertyDescriptors)) {
                String propertyName;
                Object propertyValue;

                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    propertyName = propertyDescriptor.getName();
                    if (!StringUtils.equals("class", propertyName)) {
                        Method readMethod = propertyDescriptor.getReadMethod();
                        propertyValue = readMethod.invoke(javaBean);

                        map.put(propertyName, propertyValue);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Transform JavaBean to Map failed.", e);
        }

        return map;
    }

    /**
     * 将javaBean转换为Map
     * 批量转换
     * @param javaList
     * @return 
     * @author qishao
     * date 2018-09-28
     */
    public static List<Map<String, Object>> javaListToMapList(List javaList) {
        if (CollectionUtils.isEmpty(javaList)) {
            return null;
        }

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Object javaBean : javaList) {
            mapList.add(javaToMap(javaBean));
        }
        return mapList;
    }

    /**
     * 将Map转换为javaBean
     * @param cls
     * @param map
     * @return
     * @author qishao
     * date 2018-09-28
     */
    public static <T> T mapToJava(Class<T> cls, Map<String, Object> map) {
        T javaBean = null;

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(cls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (CollectionUtils.isNotEmpty(propertyDescriptors)) {
                javaBean = cls.newInstance();

                String propertyName;
                Object propertyValue;
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    propertyName = propertyDescriptor.getName();
                    if (map.containsKey(propertyName)) {
                        propertyValue = map.get(propertyName);
                        propertyDescriptor.getWriteMethod().invoke(javaBean, propertyValue);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Transform Map to JavaBean failed.", e);
        }

        return javaBean;
    }

    /**
     * 将Map转换为javaBean
     * 批量转换
     * @param cls
     * @param mapList
     * @return
     * @author qishao
     * date 2018-09-28
     */
    public static <T>List<T> mapListToJavaList(Class<T> cls, List<Map<String, Object>> mapList) {
        if (CollectionUtils.isEmpty(mapList)) {
            return null;
        }

        List<T> javaList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            javaList.add(mapToJava(cls, map));
        }

        return javaList;
    }
}
