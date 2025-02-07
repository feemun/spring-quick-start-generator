package cloud.catfish.api;

import lombok.Data;

@Data
public class CommonPageReq<T> {

    /**
     * 当前页
     */
    int currentPage;

    /**
     * 分页大小
     */
    int pageSize;

    /**
     * 查询条件
     */
    T condition;
}
