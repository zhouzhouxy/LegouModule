package com.pinyougou.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult implements Serializable {
    /**
     * 总记录数
     * */
    private Integer total;
    /**
    当前页结果
    */
    private List rows;

    public PageResult(Integer total, List rows) {
        this.total = total;
        this.rows = rows;
    }
}
