package com.csoptt.utils.base.dao;

import com.csoptt.utils.base.page.PageInfo;

import java.util.List;

/**
 * 单表操作DAO
 *
 * @author qishao
 * @date 2018-09-05
 */
public interface BaseDao<T> {

    /**
     * 向数据库中插入一条数据，可插入所有不为null的
     * @param eo
     * @return
     * @author qishao
     * date 2018-09-05
     */
    int insertSelective(T eo);

    /**
     * 以此EO为基准，修改这个id对应的数据，只修改此eo对象中不为null的列
     * @param eo
     * @return
     * @author qishao
     * date 2018-09-05
     */
    int updateByPrimaryKeySelectvie(T eo);

    /**
     * 根据主键，将数据从库中删除
     * @param primaryKey
     * @return
     * @author qishao
     * date 2018-09-05
     */
    int deleteByPrimaryKey(Object primaryKey);

    /**
     * 根据主键，查询单条数据
     * @param primaryKey
     * @return
     * @author qishao
     * date 2018-09-05
     */
    T selectByPrimaryKey(Object primaryKey);

    /**
     * 根据入参中不为null的项，查询满足条件的list
     *
     * <p>1.0.0版本只满足全等查询</p>
     *
     * @param param
     * @return
     * @author qishao
     * date 2018-09-05
     */
    List<T> queryList(T param);

    /**
     * 根据入参中requestParam不为null的项，查询满足条件的分页list
     *
     * <p>1.0.0版本只满足全等查询</p>
     *
     * @param param
     * @return
     * @author qishao
     * date 2018-09-05
     */
    List<T> queryPage(PageInfo<T> param);

    /**
     * 根据入参中不为null的项，查询满足条件的数据总数
     *
     * <p>1.0.0版本只满足全等查询</p>
     *
     * @param param
     * @return
     * @author qishao
     * date 2018-09-05
     */
    Integer queryCount(T param);
}
