package com.csoptt.utils.sonar.vo;

import java.util.List;

/**
 * 调用请求返回的JSON本身对应的对象
 *
 * @author liuzixi
 * @date 2018-10-09
 */
public class ComponentResponseVO {

    /**
     * component
     */
    private ComponentVO component;

    /**
     * periods
     */
    private List<PeriodVO> periods;

    /**
     * Gets the value of component.
     *
     * @return the value of component
     */
    public ComponentVO getComponent() {
        return component;
    }

    /**
     * Sets the component.
     * <p>
     * <p>You can use getComponent() to get the value of component</p>
     *
     * @param component component
     */
    public void setComponent(ComponentVO component) {
        this.component = component;
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
