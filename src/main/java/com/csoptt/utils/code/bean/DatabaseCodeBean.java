package com.csoptt.utils.code.bean;

import com.csoptt.utils.code.enums.DataBaseCodeEnums;
import com.csoptt.utils.common.CollectionUtils;
import com.csoptt.utils.exception.BaseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取数据库表的各项信息
 * 暂仅支持MySQL、oracle
 * 暂不支持主外键查询
 *
 * @author qishao
 * @date 2018-09-29
 */
@Component
public class DatabaseCodeBean {

    /**
     * Log4j
     */
    private final Logger logger = LoggerFactory.getLogger(DatabaseCodeBean.class);

    /**
     * 数据库驱动类
     */
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    /**
     * 数据库链接
     */
    @Value("${spring.datasource.url}")
    private String url;

    /**
     * 数据库用户名
     */
    @Value("${spring.datasource.username}")
    private String username;

    /**
     * 数据库密码
     */
    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 是否为oracle
     * 否 则为MySQL
     */
    private boolean isOracle = false;

    /**
     * 数据库类型
     * 默认MySQL
     */
    private DataBaseCodeEnums dbCodeEnum = DataBaseCodeEnums.MYSQL;

    /**
     * 数据库
     * 此字段仅供MySQL用
     */
    private String tableSchema;

    /**
     * 构造方法
     * 此处会据库类型判断
     */
    public DatabaseCodeBean() {
        // 如果是oracle，改枚举
        if (StringUtils.equals("oracle.jdbc.driver.OracleDriver", driverClassName)) {
            dbCodeEnum = DataBaseCodeEnums.ORACLE;
            isOracle = true;
        } else {
            // 是MySQL，截取数据库
            // 去掉前13个字符，即去掉 'jdbc:mysql://' 部分
            String subUrl = url.substring(13);
            /*
             * 其中，第一个/之后紧接着数据库名称
             * 如果有参数，截取第一个问号前
             * 如果无参数，截取到末尾
             */
            int paramIndex = subUrl.indexOf("?");
            if (paramIndex > 0) {
                tableSchema = subUrl.substring(subUrl.indexOf("/"), paramIndex);
            } else {
                tableSchema = subUrl.substring(subUrl.indexOf("/"));
            }
        }
    }

    /**
     * 获取连接
     * 连接到目标数据库
     * @return
     * @author qishao
     * date 2018-09-29
     */
    private Connection getConnection() {
        // 1. 加载驱动类
        try {
            Class.forName(driverClassName);
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new BaseException("-1", "加载驱动类" + driverClassName + "失败");
        } catch (SQLException e) {
            throw new BaseException("-1", "连接数据库失败");
        }
    }

    /**
     * 获取表和列的信息
     * @param tableName
     * @param packageName
     * @param tablePrefix
     * @return 
     * @author qishao
     * date 2018-09-29
     */
    public TableInfo getTableInfo(String tableName, String packageName, String tablePrefix) {
        TableInfo tableInfo = null;
        List<ColumnInfo> columnInfos = new ArrayList<>();
        // 查看表结构SQL
        String checkColumnSql = dbCodeEnum.getCheckColumnSql().replace("@tableName", tableName);
        if (!isOracle) {
            checkColumnSql = checkColumnSql.replace("@tableSchema", tableSchema);
        }
        // 查看表注释SQL
        String checkTableCommentsSql = dbCodeEnum.getCheckTableCommentsSql().replace("@tableName", tableName);
        if (!isOracle) {
            checkTableCommentsSql = checkTableCommentsSql.replace("@tableSchema", tableSchema);
        }

        try (Connection connection = getConnection();
             PreparedStatement tableStm = connection.prepareStatement(checkTableCommentsSql);
             ResultSet tableResultSet = tableStm.executeQuery();
             PreparedStatement columnStm = connection.prepareStatement(checkColumnSql);
             ResultSet columnResultSet = columnStm.executeQuery()) {
            tableInfo = new TableInfo();
            
            /*
             * 1. 获取表信息
             * 包括表注释（sql获取），类名称
             */
            if (tableResultSet.next()) {
                tableInfo.setTableComment(tableResultSet.getString(1));
            } else {
                throw new BaseException("-1", "query table failed.");
            }
            tableInfo.setTableName(tableName);
            String shortClassName = formatFromDB(tableName.replace(tablePrefix, "")) + "EO"; // 实体类对象要加上EO
            String className = packageName + "." + shortClassName;
            tableInfo.setClassName(className);
            tableInfo.setShortClassName(shortClassName);

            /*
             * 2. 查看列信息
             * 包括对应的Java变量信息
             */
            ColumnInfo columnInfo;
            while (columnResultSet.next()) {
                columnInfo = new ColumnInfo();

                String columnName = columnResultSet.getString(1);
                String columnType = columnResultSet.getString(2);
                Integer columnLength = StringUtils.isBlank(columnResultSet.getString(3)) ? null
                        : Integer.valueOf(columnResultSet.getString(3));
                String columnComment = columnResultSet.getString(4);
                Integer columnScale = StringUtils.isBlank(columnResultSet.getString(5)) ? null
                        : Integer.valueOf(columnResultSet.getString(5));

                // 将数据库字段名称转换为java字段名称
                String fieldName = getFieldNameByColumnName(columnName);
                // 将数据库类型转换为java类型
                String fieldType = getFieldTypeByColumnType(columnType, columnLength, columnScale);
                String shortFieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);

                columnInfo.setColumnName(columnName);
                columnInfo.setColumnType(columnType);
                columnInfo.setColumnLength(columnLength);
                columnInfo.setColumnComment(columnComment);
                columnInfo.setFieldName(fieldName);
                columnInfo.setFieldType(fieldType);
                columnInfo.setShortFieldType(shortFieldType);

                columnInfos.add(columnInfo);
            }

            if (CollectionUtils.isNotEmpty(columnInfos)) {
                tableInfo.setColumnInfoList(columnInfos);
            } else {
                throw new BaseException("-1", "query table's column failed.");
            }
        } catch (Exception e) {
            logger.error("check column failed.", e);
        }

        return tableInfo;
    }

    /**
     * 将数据库命名格式转化为Java命名格式
     * @param str
     * @return
     * @author qishao
     * date 2018-09-29
     */
    private String formatFromDB(String str) {
        // 单个单词，首字母大写返回
        if (!str.contains("_")) {
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }

        // 多个单词，用_隔开，采用小写驼峰命名法返回
        String[] words = str.split("_");
        StringBuilder builder = new StringBuilder();

        for (String word : words) {
            builder.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }

        return builder.toString();
    }
    
    /**
     * 获取数据库列名对应的字段名
     * @param columnName
     * @return 
     * @author qishao
     * date 2018-09-29
     */
    private String getFieldNameByColumnName(String columnName) {
        // 单个单词，原样小写返回
        if (!columnName.contains("_")) {
            return columnName.toLowerCase();
        }

        String fieldNameUpper = formatFromDB(columnName);
        return fieldNameUpper.replace(fieldNameUpper.substring(0, 1),
                fieldNameUpper.substring(0, 1).toLowerCase());
    }
    
    /**
     * 根据数据库字段类型，获取java对象类型
     * @param columnType
     * @param columnLength
     * @param columnScale
     * @return 
     * @author qishao
     * date 2018-09-29
     */
    private String getFieldTypeByColumnType(String columnType, Integer columnLength, Integer columnScale) {
        columnType = columnType.toLowerCase();

        if (columnType.contains("char") || columnType.contains("text")) { // 文本型
            return "java.lang.String";
        } else if (columnType.contains("bit")) { // 布尔型
            return "java.lang.Boolean";
        } else if (columnType.contains("bigint")) { // long
            return "java.lang.Long";
        } else if (columnType.contains("int")) { // int
            return "java.lang.Integer";
        } else if (columnType.contains("float")) { // float
            return "java.lang.Float";
        } else if (columnType.contains("double")) { // double
            return "java.lang.Double";
        } else if (columnType.contains("decimal")) { // decimal
            return "java.math.BigDecimal";
        } else if (columnType.contains("number")) {
            // number型，分别处理
            if (null != columnScale && columnScale > 0) {
                return "java.lang.Double";
            } else if (columnLength > 10) {
                return "java.lang.Long";
            } else {
                return "java.lang.Integer";
            }
        } else if (columnType.contains("date") || columnType.contains("time")) {
            return "java.util.Date";
        } else if (columnType.contains("clob")) {
            return "java.sql.Clob";
        } else {
            return "java.lang.Object";
        }
    }
}
