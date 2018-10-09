package com.csoptt.utils.sonar.vo;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 调用sonar请求时，返回的json字符串对应的period对象Java实体类
 *
 * @author qishao
 * @date 2018-10-09
 */
public class PeriodVO {

    /**
     * index
     */
    private String index;

    /**
     * value
     */
    private String value;

    /**
     * date
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    /**
     * Gets the value of index.
     *
     * @return the value of index
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the index.
     * <p>
     * <p>You can use getIndex() to get the value of index</p>
     *
     * @param index index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * Gets the value of value.
     *
     * @return the value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     * <p>
     * <p>You can use getValue() to get the value of value</p>
     *
     * @param value value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of date.
     *
     * @return the value of date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date.
     * <p>
     * <p>You can use getDate() to get the value of date</p>
     *
     * @param date date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the value of mode.
     *
     * @return the value of mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Sets the mode.
     * <p>
     * <p>You can use getMode() to get the value of mode</p>
     *
     * @param mode mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * mode
     */

    private String mode;
}
