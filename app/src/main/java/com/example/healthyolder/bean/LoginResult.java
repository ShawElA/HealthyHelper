package com.example.healthyolder.bean;

import java.io.Serializable;
import java.util.List;

public class LoginResult {

    /**
     * success : true
     * data : [{"u_id":"2","u_name":"杨天成","u_mobile":"18814118285","u_login_pwd":"123456","u_pay_pwd":"123456","u_power":"1","u_email":"","u_time":"0000-00-00 00:00:00","u_icon":"","u_department_id":"1","dd_id":"1","dd_name":"神经内科"},{"u_id":"4","u_name":"陈宇","u_mobile":"13750423724","u_login_pwd":"123456","u_pay_pwd":"123456","u_power":"1","u_email":"594771472@qq.com","u_time":"0000-00-00 00:00:00","u_icon":"","u_department_id":"1","dd_id":"1","dd_name":"神经内科"},{"u_id":"5","u_name":"郑永平","u_mobile":"13724274375","u_login_pwd":"123456","u_pay_pwd":"123456","u_power":"1","u_email":"45727367327@qq.com","u_time":"0000-00-00 00:00:00","u_icon":"","u_department_id":"3","dd_id":"3","dd_name":"心血管内科"},{"u_id":"6","u_name":"林浩","u_mobile":"13752373707","u_login_pwd":"123456","u_pay_pwd":"123456","u_power":"1","u_email":"44375273287@qq.com","u_time":"2022-03-23 23:37:19","u_icon":"","u_department_id":"5","dd_id":"5","dd_name":"血液内科"}]
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

    public static class DataBean implements Comparable<DataBean>, Serializable {
        /**
         * u_id : 2
         * u_name : 杨天成
         * u_mobile : 18814118285
         * u_login_pwd : 123456
         * u_pay_pwd : 123456
         * u_power : 1
         * u_email :
         * u_time : 0000-00-00 00:00:00
         * u_icon :
         * u_department_id : 1
         * dd_id : 1
         * dd_name : 神经内科
         */

        private String u_id;
        private String u_name;
        private String u_mobile;
        private String u_login_pwd;
        private String u_pay_pwd;
        private String u_power;
        private String u_email;
        private String u_time;
        private String u_icon;
        private String u_department_id;
        private String u_remark;
        private String dd_id;
        private String dd_name;

        public String getU_id() {
            return u_id;
        }

        public void setU_id(String u_id) {
            this.u_id = u_id;
        }

        public String getU_name() {
            return u_name;
        }

        public void setU_name(String u_name) {
            this.u_name = u_name;
        }

        public String getU_mobile() {
            return u_mobile;
        }

        public void setU_mobile(String u_mobile) {
            this.u_mobile = u_mobile;
        }

        public String getU_login_pwd() {
            return u_login_pwd;
        }

        public void setU_login_pwd(String u_login_pwd) {
            this.u_login_pwd = u_login_pwd;
        }

        public String getU_pay_pwd() {
            return u_pay_pwd;
        }

        public void setU_pay_pwd(String u_pay_pwd) {
            this.u_pay_pwd = u_pay_pwd;
        }

        public String getU_power() {
            return u_power;
        }

        public void setU_power(String u_power) {
            this.u_power = u_power;
        }

        public String getU_email() {
            return u_email;
        }

        public void setU_email(String u_email) {
            this.u_email = u_email;
        }

        public String getU_time() {
            return u_time;
        }

        public void setU_time(String u_time) {
            this.u_time = u_time;
        }

        public String getU_icon() {
            return u_icon;
        }

        public void setU_icon(String u_icon) {
            this.u_icon = u_icon;
        }

        public String getU_department_id() {
            return u_department_id;
        }

        public void setU_department_id(String u_department_id) {
            this.u_department_id = u_department_id;
        }

        public String getDd_id() {
            return dd_id;
        }

        public void setDd_id(String dd_id) {
            this.dd_id = dd_id;
        }

        public String getDd_name() {
            return dd_name;
        }

        public void setDd_name(String dd_name) {
            this.dd_name = dd_name;
        }

        public String getU_remark() {
            return u_remark;
        }

        public void setU_remark(String u_remark) {
            this.u_remark = u_remark;
        }

        @Override
        public int compareTo(DataBean dataBean) {
            return Integer.valueOf(this.getU_department_id()) - Integer.valueOf(dataBean.getU_department_id());
        }
    }
}
