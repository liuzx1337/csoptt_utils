package com.csoptt.utils.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.safety.Whitelist;

import java.util.Iterator;
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
     * 引号转化
     * 将字符串中的"变成&quot;
     * @param str
     * @return
     * @author liuzixi
     * date 2018-10-09
     */
    public static String strConvert(String str) {
        return str.replace("\"", "&quot;");
    }

    /**
     * 将JSON字符串中的"object"进行引号转化
     * @param str
     * @return
     * @author liuzixi
     * date 2018-10-09
     */
    public static String objConvert(String str) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        JSONObject objJson = new JSONObject();
        Set<String> keySet = jsonObject.keySet();
        if (CollectionUtils.isNotEmpty(keySet)) {
            for (String key : keySet) {
                String valueStr = strConvert(jsonObject.getString(key));
                objJson.put(key, valueStr);
            }
        }
        return objJson.toString();
    }

    /**
     * 将JSON字符串（数组）进行引号转化
     * @param str
     * @return
     * @author liuzixi
     * date 2018-10-09
     */
    public static String listConvert(String str) {
        JSONArray jsonArray = JSONArray.parseArray(str);
        JSONArray arrJson = new JSONArray();
        if (CollectionUtils.isNotEmpty(jsonArray)) {
            for (Object object : jsonArray) {
                String jsonStr = objConvert(object.toString());
                arrJson.add(jsonArray);
            }
        }
        return arrJson.toString();
    }
}
