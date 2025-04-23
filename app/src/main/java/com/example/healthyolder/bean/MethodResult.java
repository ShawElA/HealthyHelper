package com.example.healthyolder.bean;

import java.util.List;

public class MethodResult {


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
        private String m_id;
        private String m_type;
        private String m_title;
        private String m_content;
        private String m_remark;

        public String getM_id() {
            return m_id;
        }

        public void setM_id(String m_id) {
            this.m_id = m_id;
        }

        public String getM_type() {
            return m_type;
        }

        public void setM_type(String m_type) {
            this.m_type = m_type;
        }

        public String getM_title() {
            return m_title;
        }

        public void setM_title(String m_title) {
            this.m_title = m_title;
        }

        public String getM_content() {
            return m_content;
        }

        public void setM_content(String m_content) {
            this.m_content = m_content;
        }

        public String getM_remark() {
            return m_remark;
        }

        public void setM_remark(String m_remark) {
            this.m_remark = m_remark;
        }
    }
}
