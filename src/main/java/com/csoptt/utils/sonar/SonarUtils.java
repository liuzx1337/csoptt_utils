package com.csoptt.utils.sonar;

import com.alibaba.fastjson.JSONObject;
import com.csoptt.utils.http.HttpUtils;
import com.csoptt.utils.http.ResponseMessage;
import com.csoptt.utils.sonar.vo.ComponentResponseVO;
import com.csoptt.utils.sonar.vo.ComponentVO;
import com.csoptt.utils.sonar.vo.MeasureVO;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利用sonar查看代码质量的工具类
 *
 * @author qishao
 * @date 2018-09-25
 */
public class SonarUtils {

    public static void main(String[] args) {
        // HttpGet
        String url = "http://10.10.10.37:9000/api/measures/component";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("componentKey", "competitive");
        paramMap.put("metricKeys", "ncloc");

        ResponseMessage response = HttpUtils.get(url, paramMap, "admin", "admin");
        String data = response.getData().toString();
        ComponentResponseVO responseVO = JSONObject.parseObject(data, ComponentResponseVO.class);
        ComponentVO componentVO = responseVO.getComponent();
        List<MeasureVO> measures = componentVO.getMeasures();
        MeasureVO nclosMeasure = measures.stream().filter(m -> StringUtils.equals("nclos", m.getMetric()))
                .findFirst().orElse(null);
        if (null != nclosMeasure) {
            System.out.println(nclosMeasure.getValue());
        }
    }
}
