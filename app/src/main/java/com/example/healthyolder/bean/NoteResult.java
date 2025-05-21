package com.example.healthyolder.bean;

import java.util.List;
import android.text.TextUtils;

public class NoteResult {

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
        private String n_title;
        private String n_content;
        private String n_time;
        private String n_uid;
        private String n_id;
        private String n_type;
        private String u_id;
        private String username;
        private String nickname;
        private String u_pwd;
        private String u_email;
        private String u_icon;
        private String u_mobile;
        private String u_sex;
        private String u_time;
        
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
        private String a_id;
        private String a_type;
        private String a_path;
        private String a_frame;
        private String an_id;

        public String getN_title() {
            return n_title;
        }

        public void setN_title(String n_title) {
            this.n_title = n_title;
        }

        public String getN_content() {
            return n_content;
        }

        public void setN_content(String n_content) {
            this.n_content = n_content;
        }

        public String getN_time() {
            return n_time;
        }

        public void setN_time(String n_time) {
            this.n_time = n_time;
        }

        public String getN_uid() {
            return n_uid;
        }

        public void setN_uid(String n_uid) {
            this.n_uid = n_uid;
        }

        public String getN_id() {
            return n_id;
        }

        public void setN_id(String n_id) {
            this.n_id = n_id;
        }

        public String getN_type() {
            return n_type;
        }

        public void setN_type(String n_type) {
            this.n_type = n_type;
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

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
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

        public String getU_sex() {
            return u_sex;
        }

        public void setU_sex(String u_sex) {
            this.u_sex = u_sex;
        }

        public String getU_time() {
            return u_time;
        }

        public void setU_time(String u_time) {
            this.u_time = u_time;
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

        public String getA_id() {
            return a_id;
        }

        public void setA_id(String a_id) {
            this.a_id = a_id;
        }

        public String getA_type() {
            return a_type;
        }

        public void setA_type(String a_type) {
            this.a_type = a_type;
        }

        public String getA_path() {
            return a_path;
        }

        public void setA_path(String a_path) {
            this.a_path = a_path;
        }

        public String getA_frame() {
            return a_frame;
        }

        public void setA_frame(String a_frame) {
            this.a_frame = a_frame;
        }

        public String getAn_id() {
            return an_id;
        }

        public void setAn_id(String an_id) {
            this.an_id = an_id;
        }

        // For backward compatibility
        public String getU_account() {
            return username;
        }

        public void setU_account(String username) {
            this.username = username;
        }
        
        // For backward compatibility
        public String getU_name() {
            return username;
        }

        public void setU_name(String name) {
            this.username = name;
        }
    }
}
