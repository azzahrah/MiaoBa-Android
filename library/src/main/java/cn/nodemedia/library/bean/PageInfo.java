package cn.nodemedia.library.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 分页信息
 * Created by Bining.
 */
public class PageInfo<T> implements Serializable {

    public int pageNo;// 当前页数
    public int totalPageNo;// 总页数
    public int pageSize;// 页大小
    public int size;// 当前结果集数量
    public int totalCount;// 所有结果集的总数量
    public List<T> result;// 结果列表,其中的数据类型由具体的业务定义

}
