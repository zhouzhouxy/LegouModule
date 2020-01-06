package com.pinyougou.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {
    private boolean success;
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
