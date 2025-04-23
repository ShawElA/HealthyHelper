package com.example.healthyolder.bean;

public class CommonResult {
    private boolean success;
    private String data;
    private String result;

    public boolean isSuccess() {
        return success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }
}
