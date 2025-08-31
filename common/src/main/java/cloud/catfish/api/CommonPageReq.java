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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public T getCondition() {
        return condition;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }
}
