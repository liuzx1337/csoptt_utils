package com.csoptt.utils.common;

import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类
 * 1.0.0版本只提供判断是否为空的方法
 *
 * @author qishao
 * @date 2018-09-05
 */
public class CollectionUtils {
    /**
     * 无法生成对象
     */
    private CollectionUtils() {
    }

    /**
     * 判断集合不是空
     * @param collection
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static boolean isNotEmpty(Collection collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 判断集合是空
     * @param collection
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static boolean isEmpty(Collection collection) {
        return !isNotEmpty(collection);
    }

    /**
     * 判断map不为空
     * @param map
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static boolean isNotEmpty(Map map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 判断map为空
     * @param map
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static boolean isEmpty(Map map) {
        return !isNotEmpty(map);
    }

    /**
     * 判断数组不为空
     * @param array
     * @param <T>
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 判断数组为空
     * @param array
     * @param <T>
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static <T> boolean isEmpty(T[] array) {
        return !isNotEmpty(array);
    }
}
