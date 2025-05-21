package com.example.healthyolder.util;

/**
 * 空数据结果类，用于处理那些不需要返回具体数据的接口响应
 * 例如删除账号，修改密码等只需要知道成功与否的操作
 */
public class EmptyResult {

    private boolean success;
    private String result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
} 