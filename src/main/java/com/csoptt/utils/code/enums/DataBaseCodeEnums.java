package com.csoptt.utils.code.enums;

/**
 * 主要有ORACLE、MYSQL的一些指令
 *
 * @author liuzixi
 * @date 2018-09-29
 */
public enum DataBaseCodeEnums {

    /**
     * ORACLE数据库
     */
    ORACLE("oracle.jdbc.driver.OracleDriver",
            "SELECT UTC.COLUMN_NAME, UTC.DATA_TYPE, UTC.DATA_PRECISION, UCC.COMMENTS, UTC.DATA_SCALE " +
                    "FROM user_tab_cols UTC LEFT JOIN user_col_comments UCC " +
                    "ON UTC.TABLE_NAME = UCC.TABLE_NAME AND UTC.COLUMN_NAME = UCC.COLUMN_NAME " +
                    "WHERE UTC.table_name = '@tableName'",
            "SELECT COMMENTS FROM user_tab_comments WHERE table_name = '@tableName'"),
    /**
     * MySQL数据库
     */
    MYSQL("com.mysql.jdbc.Driver",
            "SELECT COLUMN_NAME, DATA_TYPE, NUMERIC_PRECISION, COLUMN_COMMENT, NUMERIC_SCALE " +
                    "FROM information_schema.COLUMNS " +
                    "WHERE table_name = '@tableName' AND TABLE_SCHEMA = '@tableSchema'",
            "SELECT TABLE_COMMENT FROM information_schema.TABLES " +
                    "WHERE table_name = '@tableName' AND TABLE_SCHEMA = '@tableSchema'");

    /**
     * 驱动类名
     */
    private String driverClassName;

    /**
     * 查看表内字段信息的sql语句
     */
    private String checkColumnSql;

    /**
     * 查看表注释的sql语句
     */
    private String checkTableSql;

    DataBaseCodeEnums(String driverClassName, String checkColumnSql, String checkTableSql) {
        this.driverClassName = driverClassName;
        this.checkColumnSql = checkColumnSql;
        this.checkTableSql = checkTableSql;
    }

    /**
     * Gets the value of driverClassName.
     *
     * @return the value of driverClassName
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * Gets the value of checkColumnSql.
     *
     * @return the value of checkColumnSql
     */
    public String getCheckColumnSql() {
        return checkColumnSql;
    }

    /**
     * Gets the value of checkTableSql.
     *
     * @return the value of checkTableSql
     */
    public String getCheckTableSql() {
        return checkTableSql;
    }
}
