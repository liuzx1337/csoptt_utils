package com.csoptt.utils.sonar;

import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.ResourceQuery;

/**
 * 利用sonar查看代码质量的工具类
 *
 * @author qishao
 * @date 2018-09-25
 */
public class SonarUtils {

    public static void main(String[] args) {
        Sonar sonar = Sonar.create("10.10.10.37:9000", "admin", "admin");

        sonar.find(ResourceQuery.createForMetrics("org.apache.struts:struts-parent",
                "coverage", "lines", "violations"));
    }
}
