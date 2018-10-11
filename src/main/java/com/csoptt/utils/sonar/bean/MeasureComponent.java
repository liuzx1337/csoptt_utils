package com.csoptt.utils.sonar.bean;

import org.sonar.wsclient.services.Model;

/**
 * //TODO 添加类/接口功能描述
 *
 * @author liuzixi
 * @date 2018-10-10
 */
public class MeasureComponent extends Model {

    /**
     * component
     */
    private Component component;

    /**
     * Gets the value of component.
     *
     * @return the value of component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Sets the component.
     * <p>
     * <p>You can use getComponent() to get the value of component</p>
     *
     * @param component component
     */
    public void setComponent(Component component) {
        this.component = component;
    }
}
