package com.csoptt.utils.code.bean;

/**
 * 列
 * 对应Java成员变量
 *
 * @author qishao
 * @date 2018-09-29
 */
public class ColumnInfo {

    /**
     * 列名
     */
    private String columnName;

    /**
     * 列名对应的Java属性名
     */
    private String fieldName;

    /**
     * 数据表列的数据类型
     */
    private String columnType;

    /**
     * 数据最大长度
     * 仅用于数字类型字段
     */
    private Integer columnLength;

    /**
     * 数据精度
     * 大于0时，为小数
     */
    private Integer columnScale;

    /**
     * Java对象对应属性的数据类型
     * 完整，包名+类名
     */
    private String fieldType;

    /**
     * Java对象对应属性的数据类型
     * 只有类名
     */
    private String shortFieldType;

    /**
     * 列的注释
     */
    private String columnComment = "";

    /**
     * Gets the value of columnName.
     *
     * @return the value of columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets the columnName.
     * <p>
     * <p>You can use getColumnName() to get the value of columnName</p>
     *
     * @param columnName columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Gets the value of fieldName.
     *
     * @return the value of fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the fieldName.
     * <p>
     * <p>You can use getFieldName() to get the value of fieldName</p>
     *
     * @param fieldName fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the value of columnType.
     *
     * @return the value of columnType
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * Sets the columnType.
     * <p>
     * <p>You can use getColumnType() to get the value of columnType</p>
     *
     * @param columnType columnType
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    /**
     * Gets the value of columnLength.
     *
     * @return the value of columnLength
     */
    public Integer getColumnLength() {
        return columnLength;
    }

    /**
     * Sets the columnLength.
     * <p>
     * <p>You can use getColumnLength() to get the value of columnLength</p>
     *
     * @param columnLength columnLength
     */
    public void setColumnLength(Integer columnLength) {
        this.columnLength = columnLength;
    }

    /**
     * Gets the value of columnScale.
     *
     * @return the value of columnScale
     */
    public Integer getColumnScale() {
        return columnScale;
    }

    /**
     * Sets the columnScale.
     * <p>
     * <p>You can use getColumnScale() to get the value of columnScale</p>
     *
     * @param columnScale columnScale
     */
    public void setColumnScale(Integer columnScale) {
        this.columnScale = columnScale;
    }

    /**
     * Gets the value of fieldType.
     *
     * @return the value of fieldType
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * Sets the fieldType.
     * <p>
     * <p>You can use getFieldType() to get the value of fieldType</p>
     *
     * @param fieldType fieldType
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Gets the value of shortFieldType.
     *
     * @return the value of shortFieldType
     */
    public String getShortFieldType() {
        return shortFieldType;
    }

    /**
     * Sets the shortFieldType.
     * <p>
     * <p>You can use getShortFieldType() to get the value of shortFieldType</p>
     *
     * @param shortFieldType shortFieldType
     */
    public void setShortFieldType(String shortFieldType) {
        this.shortFieldType = shortFieldType;
    }

    /**
     * Gets the value of columnComment.
     *
     * @return the value of columnComment
     */
    public String getColumnComment() {
        return columnComment;
    }

    /**
     * Sets the columnComment.
     * <p>
     * <p>You can use getColumnComment() to get the value of columnComment</p>
     *
     * @param columnComment columnComment
     */
    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}
