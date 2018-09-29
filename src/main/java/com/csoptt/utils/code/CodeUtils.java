package com.csoptt.utils.code;

import com.csoptt.utils.code.bean.DatabaseCodeBean;
import com.csoptt.utils.code.bean.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 根据数据库，自动生成实体类等
 *
 * 生成以EO结尾的实体类，EODao结尾的持久层、以及与之对应的，写好基础sql的xml文件。
 * 生成的实体类（EO）与数据库中的表一一对应。
 *
 * @author qishao
 * @date 2018-09-29
 */
public class CodeUtils {

    /**
     * Log4j
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeUtils.class);

    /**
     * 获取数据库表的各项信息
     */
    @Autowired
    private DatabaseCodeBean databaseCodeBean;

    /**
     * 自身实例，用于调用依赖注入的bean
     */
    private static CodeUtils codeUtils;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        codeUtils = this;
    }

    /**
     * 根据数据表生成一套文件
     * @param tableName 表名
     * @param packageName 目标包名
     * @param tablePrefix 数据表中无意义的前缀
     * @author qishao
     * date 2018-09-29
     */
    public static void generateJavaCode(String tableName, String packageName, String tablePrefix) {
        // 1. 获取表和列的所有属性
        TableInfo tableInfo = codeUtils.databaseCodeBean.getTableInfo(tableName, packageName, tablePrefix);

        // 2. 根据模版，生成文件
    }
}
