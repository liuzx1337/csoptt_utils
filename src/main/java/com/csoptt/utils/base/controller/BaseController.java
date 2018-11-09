package com.csoptt.utils.base.controller;

import com.csoptt.utils.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller基类
 * 拥有少数基础功能
 *
 * @author qishao
 * @date 2018-11-09
 */
public abstract class BaseController {

    /**
     * Log4j
     */
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    
    /**
     * 获取错误信息
     * 如果不是基类异常，返回固定信息
     *
     * @param e 异常
     * @return 错误信息
     * @author qishao
     * date 2018-11-09
     */
    protected String getErrorMsg(Exception e) {
        String message;
        if (e instanceof BaseException) {
            message = e.getMessage();
        } else {
            message = "系统错误";
        }
        return message;
    }
}
