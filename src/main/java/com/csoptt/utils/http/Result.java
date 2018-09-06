package com.csoptt.utils.http;

/**
 * 用于向客户端返回消息的工具类
 *
 * @author qishao
 * @date 2018-09-05
 */
public final class Result {
    /**
     * 无实例
     */
    private Result() {
    }

    /**
     * 单纯的请求成功，无提示消息和返回数据
     *
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static ResponseMessage success() {
        return new ResponseMessage(ResponseMessageBaseCodeEnum.SUCCESS.getCode(), true);
    }

    /**
     * 请求成功，并返回提示消息
     * @param message
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static ResponseMessage success(String message) {
        return new ResponseMessage(ResponseMessageBaseCodeEnum.SUCCESS.getCode(), true, message);
    }

    /**
     * 请求成功，并返回数据
     * 如果需要返回的data是String类型，可同时传3个入参
     * @param data
     * @param <T>
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage<>(ResponseMessageBaseCodeEnum.SUCCESS.getCode(), true, null, data);
    }

    /**
     * 请求成功，可自定义响应码、提示消息、返回数据
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static <T> ResponseMessage<T> success(String code, String message, T data) {
        return new ResponseMessage<>(code, true, message, data);
    }

    /**
     * 单纯的请求失败，无提示消息和返回数据
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static ResponseMessage error() {
        return new ResponseMessage(ResponseMessageBaseCodeEnum.ERROR.getCode(), false);
    }

    /**
     * 请求失败，并返回提示消息
     * @param message
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static ResponseMessage error(String message) {
        return new ResponseMessage(ResponseMessageBaseCodeEnum.ERROR.getCode(), false, message);
    }

    /**
     * 请求失败，并返回提示消息
     * 可自定义响应码
     * @param code
     * @param message
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static ResponseMessage error(String code, String message) {
        return new ResponseMessage(code, false, message);
    }

    /**
     * 请求失败
     * 当需要返回数据时，可调用此方法
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     * @author qishao
     * date 2018-09-05
     */
    public static <T> ResponseMessage<T> error(String code, String message, T data) {
        return new ResponseMessage<>(code, false, message, data);
    }
}
