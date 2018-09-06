package com.csoptt.utils.http;

/**
 * 返回消息正文
 *
 * @author qishao
 * @date 2018-09-05
 */
public class ResponseMessage<T> {

    /**
     * 响应码
     */
    private String code;

    /**
     * 业务
     */
    private boolean ok;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public ResponseMessage() {
    }

    public ResponseMessage(String code, boolean ok) {
        this.code = code;
        this.ok = ok;
    }

    public ResponseMessage(String code, boolean ok, String message) {
        this.code = code;
        this.ok = ok;
        this.message = message;
    }

    public ResponseMessage(String code, boolean ok, String message, T data) {
        this.code = code;
        this.ok = ok;
        this.message = message;
        this.data = data;
    }

    /**
     * Gets the value of code.
     *
     * @return the value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     * <p>
     * <p>You can use getCode() to get the value of code</p>
     *
     * @param code code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the value of ok.
     *
     * @return the value of ok
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * Sets the ok.
     * <p>
     * <p>You can use getOk() to get the value of ok</p>
     *
     * @param ok ok
     */
    public void setOk(boolean ok) {
        this.ok = ok;
    }

    /**
     * Gets the value of message.
     *
     * @return the value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     * <p>
     * <p>You can use getMessage() to get the value of message</p>
     *
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the value of data.
     *
     * @return the value of data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the data.
     * <p>
     * <p>You can use getData() to get the value of data</p>
     *
     * @param data data
     */
    public void setData(T data) {
        this.data = data;
    }
}
