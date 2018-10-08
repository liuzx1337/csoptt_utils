package com.csoptt.utils.common;

import org.jsoup.safety.Whitelist;

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
}
