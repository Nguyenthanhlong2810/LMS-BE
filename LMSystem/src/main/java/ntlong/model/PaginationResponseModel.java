package ntlong.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class PaginationResponseModel<T> {
    private List<T> items;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long totalRecords;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageNo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageSize;

    public PaginationResponseModel() {
        items = new ArrayList<>();
    }

    public PaginationResponseModel(List<T> items, long totalRecords, Integer pageNo, Integer pageSize) {
        this.items = items;
        this.totalRecords = totalRecords;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}