package com.csoptt.utils.code.file;

import com.csoptt.utils.exception.BaseException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 生成文件
 *
 * @author qishao
 * @date 2018-09-29
 */
public class CodeFileGenerateFactory {

    /**
     * Log4j
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeFileGenerateFactory.class);

    /**
     * 存储模版与目录的对应关系
     */
    private static Map<String, String> templateMap = new HashMap<>();

    /**
     * 模版引擎
     */
    private static VelocityEngine engine;

    static {
        // 初始化模版与目录的对应关系
        templateMap.put("EOTemplate", "src" + File.separator + "main" + File.separator + "java"
                + File.separator + "@middleDir" + File.separator + "eo" + File.separator + "@className.java");
        templateMap.put("DaoTemplate", "src" + File.separator + "main" + File.separator + "java"
                + File.separator + "@middleDir" + File.separator + "dao" + File.separator + "@classNameDao.java");
        templateMap.put("MapperTemplate", "src" + File.separator + "main" + File.separator + "resources"
                + File.separator + "mapper" + File.separator + "mybatis" + File.separator + "@classNameMapper.xml");

        // 初始化模版引擎
        engine = new VelocityEngine();
        Properties properties = new Properties();
        properties.setProperty("resource.loader", "file");
        properties.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
        properties.setProperty("file.resource.loader.path", System.getProperty("user.dir").replace("\\", "/")
            + "src/main/resources/templates");
        properties.setProperty("file.resource.loader.cache", "true");
        properties.setProperty("file.resource.loader.modificationCheckInterval", "30");
        properties.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
        properties.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity");
        properties.setProperty("directive.set.null.allowed", "true");

        engine.init(properties);
    }

    /**
     * 根据对应的模版，生成文件
     * @param templateName
     * @param className
     * @param packagePath
     * @return
     * @author qishao
     * date 2018-09-30
     */
    public static void generateFile(String templateName, String className, String packagePath, VelocityContext context) {
        // 1. 根据模版名称确定文件目标路径
        String pathInMap = templateMap.get(templateName);
        if (null == pathInMap) {
            throw new BaseException("-1", "Template is not prepared.");
        }
        if (pathInMap.contains("@middleDir")) {
            pathInMap = pathInMap.replace("@middleDir", packagePath);
        }
        String filePath = pathInMap.replace("@className", className);

        // 2. 创建临时文件
        String dirPath = pathInMap.substring(0, pathInMap.indexOf("@") - 1);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            throw new BaseException("-1", "Cannot get template path" + dirPath + ".");
        }
        File file = new File(filePath);
        if (file.exists()) {
            LOGGER.info("Replace file: " + filePath);
        }

        // 3. 使用模版引擎，生成文件
        Template template = engine.getTemplate(templateName + ".ftl", "UTF-8");
        try (FileOutputStream out = new FileOutputStream(file);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"))) {
            template.merge(context, writer);
            LOGGER.info("Generate file: " + filePath);
        } catch (Exception e) {
            LOGGER.error("Generate file failed.", e);
        }
    }
}
