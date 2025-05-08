package com.example.healthyolder.bean;

import java.util.List;

public class FavResult {


    private boolean success;
    private List<DataBean> data;
    private String result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public static class DataBean {

        private String f_id;
        private String fu_id;
        private String fn_id;

        public String getF_id() {
            return f_id;
        }

        public void setF_id(String f_id) {
            this.f_id = f_id;
        }

        public String getFu_id() {
            return fu_id;
        }

        public void setFu_id(String fu_id) {
            this.fu_id = fu_id;
        }

        public String getFn_id() {
            return fn_id;
        }

        public void setFn_id(String fn_id) {
            this.fn_id = fn_id;
        }
    }
}
