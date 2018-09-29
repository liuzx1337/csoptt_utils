package com.csoptt.utils.code.bean;

import java.util.List;

/**
 * 表
 * 对应Java类
 *
 * @author qishao
 * @date 2018-09-29
 */
public class TableInfo {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 所属数据库（表空间）名
     */
    private String tableSchema;

    /**
     * 对应EO实体类名（只有类名）
     */
    private String shortClassName;

    /**
     * 对应EO实体类名（包名+类名）
     */
    private String className;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 表内的列信息
     */
    private List<ColumnInfo> columnInfoList;

    /**
     * Gets the value of tableName.
     *
     * @return the value of tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the tableName.
     * <p>
     * <p>You can use getTableName() to get the value of tableName</p>
     *
     * @param tableName tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Gets the value of tableSchema.
     *
     * @return the value of tableSchema
     */
    public String getTableSchema() {
        return tableSchema;
    }

    /**
     * Sets the tableSchema.
     * <p>
     * <p>You can use getTableSchema() to get the value of tableSchema</p>
     *
     * @param tableSchema tableSchema
     */
    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    /**
     * Gets the value of shortClassName.
     *
     * @return the value of shortClassName
     */
    public String getShortClassName() {
        return shortClassName;
    }

    /**
     * Sets the shortClassName.
     * <p>
     * <p>You can use getShortClassName() to get the value of shortClassName</p>
     *
     * @param shortClassName shortClassName
     */
    public void setShortClassName(String shortClassName) {
        this.shortClassName = shortClassName;
    }

    /**
     * Gets the value of className.
     *
     * @return the value of className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the className.
     * <p>
     * <p>You can use getClassName() to get the value of className</p>
     *
     * @param className className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the value of tableComment.
     *
     * @return the value of tableComment
     */
    public String getTableComment() {
        return tableComment;
    }

    /**
     * Sets the tableComment.
     * <p>
     * <p>You can use getTableComment() to get the value of tableComment</p>
     *
     * @param tableComment tableComment
     */
    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    /**
     * Gets the value of columnInfoList.
     *
     * @return the value of columnInfoList
     */
    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    /**
     * Sets the columnInfoList.
     * <p>
     * <p>You can use getColumnInfoList() to get the value of columnInfoList</p>
     *
     * @param columnInfoList columnInfoList
     */
    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }
}
