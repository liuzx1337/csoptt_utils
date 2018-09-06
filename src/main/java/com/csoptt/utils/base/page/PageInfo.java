package com.csoptt.utils.base.page;

import java.util.List;

/**
 * 分页相关
 *
 * @author qishao
 * @date 2018-09-05
 */
public class PageInfo<T> {

    /**
     * 页数
     */
    private Integer page;

    /**
     * 每页最大条数
     */
    private Integer pageSize;

    /**
     * 数据总条数
     */
    private Integer count;

    /**
     * 总页数
     */
    private Integer pageCount;

    /**
     * 查询参数
     */
    private T requestParam;

    /**
     * 返回数据
     */
    private List<T> responseList;

    /**
     * 默认每页10条显示第一页
     */
    public PageInfo() {
        page = 1;
        pageSize = 10;
    }

    /**
     * Gets the value of page.
     *
     * @return the value of page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * Sets the page.
     * <p>
     * <p>You can use getPage() to get the value of page</p>
     *
     * @param page page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * Gets the value of pageSize.
     *
     * @return the value of pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Sets the pageSize.
     * <p>
     * <p>You can use getPageSize() to get the value of pageSize</p>
     *
     * @param pageSize pageSize
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the value of count.
     *
     * @return the value of count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Sets the count.
     * <p>
     * <p>You can use getCount() to get the value of count</p>
     *
     * @param count count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * Gets the value of pageCount.
     *
     * @return the value of pageCount
     */
    public Integer getPageCount() {
        // 计算页数
        if (count % pageSize != 0) {
            pageCount = count / pageSize + 1;
        } else {
            pageCount = count / pageSize;
        }
        // 如果少于1页，按1页算
        if (pageCount < 1) {
            pageCount = 1;
        }
        return pageCount;
    }

    /**
     * Sets the pageCount.
     * <p>
     * <p>You can use getPageCount() to get the value of pageCount</p>
     *
     * @param pageCount pageCount
     */
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Gets the value of requestParam.
     *
     * @return the value of requestParam
     */
    public T getRequestParam() {
        return requestParam;
    }

    /**
     * Sets the requestParam.
     * <p>
     * <p>You can use getRequestParam() to get the value of requestParam</p>
     *
     * @param requestParam requestParam
     */
    public void setRequestParam(T requestParam) {
        this.requestParam = requestParam;
    }

    /**
     * Gets the value of responseList.
     *
     * @return the value of responseList
     */
    public List<T> getResponseList() {
        return responseList;
    }

    /**
     * Sets the responseList.
     * <p>
     * <p>You can use getResponseList() to get the value of responseList</p>
     *
     * @param responseList responseList
     */
    public void setResponseList(List<T> responseList) {
        this.responseList = responseList;
    }
}
