package com.example.healthyolder.bean;

public class PayResult {

    /**
     * code : 200
     * data : 4
     * result : 提交订单成功
     * success : true
     */

    private int code;
    private String data;
    private String result;
    private boolean success;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
