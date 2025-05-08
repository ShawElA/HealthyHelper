package com.example.healthyolder.bean;

import java.util.List;

public class AvailableInfo {


    /**
     * success : true
     * data : [{"a_id":"1","a_uid":"2","a_date":"2022-04-09","a_num":"20"}]
     * result : 获取成功
     */

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

        private String a_id;
        private String a_uid;
        private String a_date;
        private String a_num;

        public String getA_id() {
            return a_id;
        }

        public void setA_id(String a_id) {
            this.a_id = a_id;
        }

        public String getA_uid() {
            return a_uid;
        }

        public void setA_uid(String a_uid) {
            this.a_uid = a_uid;
        }

        public String getA_date() {
            return a_date;
        }

        public void setA_date(String a_date) {
            this.a_date = a_date;
        }

        public String getA_num() {
            return a_num;
        }

        public void setA_num(String a_num) {
            this.a_num = a_num;
        }
    }
}
