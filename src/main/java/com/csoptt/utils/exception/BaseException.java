package com.csoptt.utils.exception;

/**
 * 基本异常类
 *
 * @author qishao
 * @date 2018-09-25
 */
public class BaseException extends RuntimeException {

    /**
     * 错误代码
     */
    private String errCode;

    /**
     * 普通无参构造
     */
    public BaseException() {
        errCode = "-1";
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * 带错误码的异常
     * @param errCode
     * @param message
     */
    public BaseException(String errCode, String message) {
        this(message);
        this.errCode = errCode;
    }

    /**
     * Gets the value of errCode.
     *
     * @return the value of errCode
     */
    public String getErrCode() {
        return errCode;
    }

    /**
     * Sets the errCode.
     * <p>
     * <p>You can use getErrCode() to get the value of errCode</p>
     *
     * @param errCode errCode
     */
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
}
