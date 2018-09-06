package com.csoptt.utils.base.eo;

import java.util.Date;

/**
 * EO基类
 *
 * @author qishao
 * @date 2018-09-05
 */
public class BaseEO {

    /**
     * 所有表都有主键id
     */
    protected Integer id;

    /**
     * 数据创建人
     * 一般是userId
     */
    protected String createUser;

    /**
     * 数据创建时间
     */
    protected Date createTime;

    /**
     * 数据最后编辑人
     * 一般是userId
     */
    protected String updateUser;

    /**
     * 数据最后编辑时间
     */
    protected Date updateTime;

    /**
     * Gets the value of id.
     *
     * @return the value of id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * <p>
     * <p>You can use getId() to get the value of id</p>
     *
     * @param id id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the value of createUser.
     *
     * @return the value of createUser
     */
    public String getCreateUser() {
        return createUser;
    }

    /**
     * Sets the createUser.
     * <p>
     * <p>You can use getCreateUser() to get the value of createUser</p>
     *
     * @param createUser createUser
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * Gets the value of createTime.
     *
     * @return the value of createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * Sets the createTime.
     * <p>
     * <p>You can use getCreateTime() to get the value of createTime</p>
     *
     * @param createTime createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Gets the value of updateUser.
     *
     * @return the value of updateUser
     */
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * Sets the updateUser.
     * <p>
     * <p>You can use getUpdateUser() to get the value of updateUser</p>
     *
     * @param updateUser updateUser
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    /**
     * Gets the value of updateTime.
     *
     * @return the value of updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets the updateTime.
     * <p>
     * <p>You can use getUpdateTime() to get the value of updateTime</p>
     *
     * @param updateTime updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
