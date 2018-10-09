package com.csoptt.utils.sonar.vo;

import java.util.List;

/**
 * 调用sonar请求时，返回的json字符串对应的measure对象Java实体类
 *
 * @author qishao
 * @date 2018-10-09
 */
public class MeasureVO {

    /**
     * metric
     * 指标字段（可在sonar数据库中的metric表中找到对应值）
     */
    private String metric;

    /**
     * value
     * 指标对应的值
     */
    private String value;

    /**
     * periods
     */
    private List<PeriodVO> periods;

    /**
     * Gets the value of metric.
     *
     * @return the value of metric
     */
    public String getMetric() {
        return metric;
    }

    /**
     * Sets the metric.
     * <p>
     * <p>You can use getMetric() to get the value of metric</p>
     *
     * @param metric metric
     */
    public void setMetric(String metric) {
        this.metric = metric;
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
     * Gets the value of periods.
     *
     * @return the value of periods
     */
    public List<PeriodVO> getPeriods() {
        return periods;
    }

    /**
     * Sets the periods.
     * <p>
     * <p>You can use getPeriods() to get the value of periods</p>
     *
     * @param periods periods
     */
    public void setPeriods(List<PeriodVO> periods) {
        this.periods = periods;
    }
}
