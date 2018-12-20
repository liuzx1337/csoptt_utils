package com.csoptt.utils.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Gson工具类, 使用Gson进行Java Bean与Json字符串之间的转换
 *
 * <p>1.0.0版本只支持简单的转换</p>
 *
 * @author qishao
 * @date 2018-09-06
 */
public final class GsonUtils {
    /**
     * 无法创建对象
     */
    private GsonUtils() {
    }

    /**
     * gson：
     * 去掉null字段，日期格式设置为yyyy-MM-dd HH:mm:ss
     */
    private static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    /**
     * 将Java Bean转换成json字符串
     *
     * @param object
     * @return
     * @author qishao
     * date 2018-09-06
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * 将Java Bean转换成json字符串，指定class
     *
     * @param object
     * @param cls
     * @return
     * @author qishao
     * date 2018-09-06
     */
    public static String toJson(Object object, Class<?> cls) {
        return gson.toJson(object, cls);
    }

    /**
     * 将json字符串转换成Java Bean，指定class
     *
     * @param jsonStr
     * @param cls
     * @param <T>
     * @return
     * @author qishao
     * date 2018-09-06
     */
    public static <T> T fromJson(String jsonStr, Class<T> cls) {
        return gson.fromJson(jsonStr, cls);
    }

    /**
     * 将json字符串转换成Java List
     *
     * @param jsonStr
     * @param type
     * @param <T>
     * @return
     * @author qishao
     * date 2018-09-06
     */
    public static <T> List<T> jsonToList(String jsonStr, Type type) {
        List<T> list;
        try {
            Object obj = gson.fromJson(jsonStr, type);
            list = (List) obj;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("json字符串无法转换为目标集合");
        }
        return list;
    }
}
