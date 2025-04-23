package com.example.healthyolder.bean;

import java.util.List;

public class DicTypeResult {

    private boolean success;
    private String result;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private String d_id;
        private String d_name;

        public String getD_id() {
            return d_id;
        }

        public void setD_id(String d_id) {
            this.d_id = d_id;
        }

        public String getD_name() {
            return d_name;
        }

        public void setD_name(String d_name) {
            this.d_name = d_name;
        }

    }
}
