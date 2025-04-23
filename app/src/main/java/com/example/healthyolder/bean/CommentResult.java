package com.example.healthyolder.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentResult {

    /**
     * success : true
     * data : [{"c_id":"348137","c_content":"This is a test","cu_id":"1","cn_id":"475316","c_date":"2021-09-14 10:27:30","u_id":"1","username":"weven","password":"123456","number":"18814118285","e-mail":"594771580@qq.com","icon":"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3124289653,1178972108&fm=15&gp=0.jpg","u_sex":"1","u_date":"2020-01-20","u_level":"0"}]
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
        /**
         * c_id : 348137
         * c_content : This is a test
         * cu_id : 1
         * cn_id : 475316
         * c_date : 2021-09-14 10:27:30
         * u_id : 1
         * username : weven
         * password : 123456
         * number : 18814118285
         * e-mail : 594771580@qq.com
         * icon : https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3124289653,1178972108&fm=15&gp=0.jpg
         * u_sex : 1
         * u_date : 2020-01-20
         * u_level : 0
         */

        private String c_id;
        private String c_content;
        private String cu_id;
        private String cn_id;
        private String c_date;
        private String u_id;
        private String username;
        private String password;
        private String number;
        @SerializedName("e-mail")
        private String email;
        private String icon;
        private String u_sex;
        private String u_date;
        private String u_level;

        public String getC_id() {
            return c_id;
        }

        public void setC_id(String c_id) {
            this.c_id = c_id;
        }

        public String getC_content() {
            return c_content;
        }

        public void setC_content(String c_content) {
            this.c_content = c_content;
        }

        public String getCu_id() {
            return cu_id;
        }

        public void setCu_id(String cu_id) {
            this.cu_id = cu_id;
        }

        public String getCn_id() {
            return cn_id;
        }

        public void setCn_id(String cn_id) {
            this.cn_id = cn_id;
        }

        public String getC_date() {
            return c_date;
        }

        public void setC_date(String c_date) {
            this.c_date = c_date;
        }

        public String getU_id() {
            return u_id;
        }

        public void setU_id(String u_id) {
            this.u_id = u_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getU_sex() {
            return u_sex;
        }

        public void setU_sex(String u_sex) {
            this.u_sex = u_sex;
        }

        public String getU_date() {
            return u_date;
        }

        public void setU_date(String u_date) {
            this.u_date = u_date;
        }

        public String getU_level() {
            return u_level;
        }

        public void setU_level(String u_level) {
            this.u_level = u_level;
        }
    }
}
