package com.example.healthyolder.bean;

import java.util.List;

public class UserInfo {

    /**
     * success : true
     * data : [{"u_id":"1","nickname":"weven","u_mobile":"13750435172","u_pwd":"123456","u_sex":"0","u_email":"597777714044@qq.com","u_time":"0000-00-00 00:00:00","u_icon":"","depression_score":"10","last_test_time":"2025-03-01 00:00:00"}]
     * result : 登录成功
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
         * u_id : 1
         * nickname : weven
         * u_mobile : 13750435172
         * u_pwd : 123456
         * u_sex : 0
         * u_email : 597777714044@qq.com
         * u_time : 0000-00-00 00:00:00
         * u_icon : 
         * depression_score : 10
         * last_test_time : 2025-03-01 00:00:00
         */

        private String u_id;
        private String nickname;
        private String u_mobile;
        private String u_pwd;
        private String u_sex;
        private String u_email;
        private String u_time;
        private String u_icon;
        
        /**
         * @deprecated 已弃用，使用depression_test_history表代替
         * 仅为兼容旧版API响应而保留
         */
        @Deprecated
        private String depression_score;
        
        /**
         * @deprecated 已弃用，使用depression_test_history表的h_date字段代替
         * 仅为兼容旧版API响应而保留
         */
        @Deprecated
        private String last_test_time;
        
        /**
         * @deprecated 已弃用，使用depression_test_history表代替
         * 仅为兼容旧版API响应而保留
         */
        @Deprecated
        private String test_history;

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

        public String getU_mobile() {
            return u_mobile;
        }

        public void setU_mobile(String u_mobile) {
            this.u_mobile = u_mobile;
        }

        public String getU_pwd() {
            return u_pwd;
        }

        public void setU_pwd(String u_pwd) {
            this.u_pwd = u_pwd;
        }

        public String getU_sex() {
            return u_sex;
        }

        public void setU_sex(String u_sex) {
            this.u_sex = u_sex;
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
        
        /**
         * @deprecated 已弃用，使用depression_test_history表代替
         * 仅为兼容旧版API响应而保留
         */
        @Deprecated
        public String getDepression_score() {
            return depression_score;
        }

        /**
         * @deprecated 已弃用，使用depression_test_history表代替
         * 仅为兼容旧版API响应而保留
         */
        @Deprecated
        public void setDepression_score(String depression_score) {
            this.depression_score = depression_score;
        }

        /**
         * @deprecated 已弃用，使用depression_test_history表的h_date字段代替
         * 仅为兼容旧版API响应而保留
         */
        @Deprecated
        public String getLast_test_time() {
            return last_test_time;
        }

        /**
         * @deprecated 已弃用，使用depression_test_history表的h_date字段代替
         * 仅为兼容旧版API响应而保留
         */
        @Deprecated
        public void setLast_test_time(String last_test_time) {
            this.last_test_time = last_test_time;
        }

        /**
         * @deprecated 已弃用，使用depression_test_history表代替
         * 仅为兼容旧版API响应而保留
         */
        @Deprecated
        public String getTest_history() {
            return test_history;
        }

        /**
         * @deprecated 已弃用，使用depression_test_history表代替
         * 仅为兼容旧版API响应而保留
         */
        @Deprecated
        public void setTest_history(String test_history) {
            this.test_history = test_history;
        }
    }
}
