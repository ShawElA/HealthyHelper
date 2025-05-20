package com.example.healthyolder.bean;

import java.util.List;

public class ChatResult {


    /**
     * success : true
     * data : [{"c_id":"1","c_gid":"1","c_remark":"我签到啦","c_uid":"453452","c_date":"2023-03-03 10:00:00","c_type":"0","u_id":"453452","nickname":"weven","u_pwd":"123456","u_email":"594771590@qq,com","u_icon":"","u_mobile":"13750435172","u_time":"2022-11-13"},{"c_id":"2","c_gid":"1","c_remark":"我签到啦","c_uid":"453453","c_date":"2023-03-03 11:00:00","c_type":"0","u_id":"453453","nickname":"weven1","u_pwd":"123456","u_email":"594771590@qq,com","u_icon":"","u_mobile":"13750435172","u_time":"2022-11-13"}]
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

        private String c_id;
        private String c_gid;
        private String c_remark;
        private String c_uid;
        private String c_date;
        private String c_type;
        private String u_id;
        private String nickname;
        private String u_pwd;
        private String u_email;
        private String u_icon;
        private String u_mobile;
        private String u_time;
        private String temp_id; // 用于临时消息标识，如"正在输入"提示

        public String getTemp_id() {
            return temp_id;
        }

        public void setTemp_id(String temp_id) {
            this.temp_id = temp_id;
        }

        public String getC_id() {
            return c_id;
        }

        public void setC_id(String c_id) {
            this.c_id = c_id;
        }

        public String getC_gid() {
            return c_gid;
        }

        public void setC_gid(String c_gid) {
            this.c_gid = c_gid;
        }

        public String getC_remark() {
            return c_remark;
        }

        public void setC_remark(String c_remark) {
            this.c_remark = c_remark;
        }

        public String getC_uid() {
            return c_uid;
        }

        public void setC_uid(String c_uid) {
            this.c_uid = c_uid;
        }

        public String getC_date() {
            return c_date;
        }

        public void setC_date(String c_date) {
            this.c_date = c_date;
        }

        public String getC_type() {
            return c_type;
        }

        public void setC_type(String c_type) {
            this.c_type = c_type;
        }

        public String getU_id() {
            return u_id;
        }

        public void setU_id(String u_id) {
            this.u_id = u_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
        
        // For backward compatibility
        public String getU_name() {
            return nickname;
        }

        public void setU_name(String nickname) {
            this.nickname = nickname;
        }

        public String getU_pwd() {
            return u_pwd;
        }

        public void setU_pwd(String u_pwd) {
            this.u_pwd = u_pwd;
        }

        public String getU_email() {
            return u_email;
        }

        public void setU_email(String u_email) {
            this.u_email = u_email;
        }

        public String getU_icon() {
            return u_icon;
        }

        public void setU_icon(String u_icon) {
            this.u_icon = u_icon;
        }

        public String getU_mobile() {
            return u_mobile;
        }

        public void setU_mobile(String u_mobile) {
            this.u_mobile = u_mobile;
        }

        public String getU_time() {
            return u_time;
        }

        public void setU_time(String u_time) {
            this.u_time = u_time;
        }
    }
}
