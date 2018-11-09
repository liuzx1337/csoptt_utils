package com.csoptt.utils.sonar.vo;

import java.util.List;

/**
 * 调用sonar请求时，返回的json字符串对应的component对象Java实体类
 *
 * @author qishao
 * @date 2018-10-09
 */
public class ComponentVO {

    /**
     * id
     */
    private String id;

    /**
     * key
     * 对应sonarScanner给项目配置的projectKey
     */
    private String key;

    /**
     * name
     * 对应sonarScanner给项目配置的projectName
     */
    private String name;

    /**
     * qualifer
     * 质量检测器
     */
    private String qualifer;

    /**
     * measures
     * 返回的指标
     */
    private List<MeasureVO> measures;

    /**
     * Gets the value of id.
     *
     * @return the value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     * <p>
     * <p>You can use getId() to get the value of id</p>
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the value of key.
     *
     * @return the value of key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key.
     * <p>
     * <p>You can use getKey() to get the value of key</p>
     *
     * @param key key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value of name.
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * <p>
     * <p>You can use getName() to get the value of name</p>
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of qualifer.
     *
     * @return the value of qualifer
     */
    public String getQualifer() {
        return qualifer;
    }

    /**
     * Sets the qualifer.
     * <p>
     * <p>You can use getQualifer() to get the value of qualifer</p>
     *
     * @param qualifer qualifer
     */
    public void setQualifer(String qualifer) {
        this.qualifer = qualifer;
    }

    /**
     * Gets the value of measures.
     *
     * @return the value of measures
     */
    public List<MeasureVO> getMeasures() {
        return measures;
    }

    /**
     * Sets the measures.
     * <p>
     * <p>You can use getMeasures() to get the value of measures</p>
     *
     * @param measures measures
     */
    public void setMeasures(List<MeasureVO> measures) {
        this.measures = measures;
    }
}