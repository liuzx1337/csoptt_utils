package com.csoptt.utils.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.safety.Whitelist;

import java.util.Set;

/**
 * xss非法标签过滤工具类
 * 过滤html中的xss字符
 *
 * @author qishao
 * @date 2018-10-08
 */
public class JsoupUtils {

    /**
     * basicWithImages白名单
     * 允许的标签有a,b,blockquote,br,cite,code,dd,dl,dt,em,i,li,ol,p,pre,q,small,span,strike,strong,sub,sup,u,ul,img
     * 以及a标签的href,img标签的src,align,alt,height,width,title属性
     */
    private static final Whitelist WHITELIST = Whitelist.basicWithImages();

    static {
        /*
         * 富文本编辑时一些样式是使用style来进行实现的
         * 故需要给标签加上style属性
         */
        WHITELIST.addAttributes(":all", "style");
    }

    /**
     * 特殊字符处理（去空）
     * @param content
     * @return
     * @author qishao
     * date 2018-10-11
     */
    public static String clean(String content) {
        if (StringUtils.isNotBlank(content)) {
            content = content.trim();
        }

        // 替换字符
        return cleanXSS(content);
    }

    /**
     * 参数特殊字符处理（去空）
     * @param param
     * @return
     * @author qishao
     * date 2018-10-11
     */
    public static String cleanParam(String param) {
        if (StringUtils.isNotBlank(param)) {
            param = param.trim();
        }

        // 替换字符
        return cleanXSSParam(param);
    }

    /**
     * 替换特殊字符
     * @param str
     * @return
     * @author qishao
     * date 2018-10-11
     */
    public static String cleanXSS(String str) {
        return str.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
                .replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;")
                .replaceAll("'", "&#39;").replaceAll("\"", "\\\"");
    }

    /**
     * 替换入参中的特殊字符
     * @param str
     * @return
     * @author qishao
     * date 2018-10-11
     */
    public static String cleanXSSParam(String str) {
        return convertStr(cleanXSS(str));
    }

    /**
     * 将JSON字符串中的"转换为&quot;
     * @param str
     * @return
     * @author qishao
     * date 2018-10-11
     */
    public static String convertStr(String str) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        JSONObject json = new JSONObject();
        Set<String> keySet = jsonObject.keySet();
        if (CollectionUtils.isNotEmpty(keySet)) {
            for (String key : keySet) {
                String value = jsonObject.getString(key);
                if (value.startsWith("[")) {
                    // 数组类型
                    value = listConvertQuot(value);
                    json.put(key, value);
                } else if (value.startsWith("{")) {
                    // 对象类型
                    value = objConvertQuot(value);
                    json.put(key, value);
                } else {
                    // 普通字符串
                    value = strConvertQuot(value);
                    json.put(key, value);
                }
            }
        }

        return json.toString();
    }

    /**
     * 引号转化
     * 将字符串中的"变成&quot;
     * @param str
     * @return
     * @author qishao
     * date 2018-10-09
     */
    public static String strConvertQuot(String str) {
        return str.replace("\"", "&quot;");
    }

    /**
     * 将JSON字符串中的"object"进行引号转化
     * @param str
     * @return
     * @author qishao
     * date 2018-10-09
     */
    public static String objConvertQuot(String str) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        JSONObject objJson = new JSONObject();
        Set<String> keySet = jsonObject.keySet();
        if (CollectionUtils.isNotEmpty(keySet)) {
            for (String key : keySet) {
                String valueStr = strConvertQuot(jsonObject.getString(key));
                objJson.put(key, valueStr);
            }
        }
        return objJson.toString();
    }

    /**
     * 将JSON字符串（数组）进行引号转化
     * @param str
     * @return
     * @author qishao
     * date 2018-10-09
     */
    public static String listConvertQuot(String str) {
        JSONArray jsonArray = JSONArray.parseArray(str);
        JSONArray arrJson = new JSONArray();
        if (CollectionUtils.isNotEmpty(jsonArray)) {
            for (Object object : jsonArray) {
                String jsonStr = objConvertQuot(object.toString());
                arrJson.add(jsonStr);
            }
        }
        return arrJson.toString();
    }
}
