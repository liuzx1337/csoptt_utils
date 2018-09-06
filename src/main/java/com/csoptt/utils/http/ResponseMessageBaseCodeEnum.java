package com.csoptt.utils.http;

/**
 * 最基本的响应码枚举
 *
 * @author qishao
 * @date 2018-09-05
 */
public enum ResponseMessageBaseCodeEnum {
    /**
     * 成功
     */
    SUCCESS("0"),
    /**
     * 错误
     */
    ERROR("-1");

    /**
     * 响应码
     */
    private String code;

    ResponseMessageBaseCodeEnum(String code) {
        this.code = code;
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
}
