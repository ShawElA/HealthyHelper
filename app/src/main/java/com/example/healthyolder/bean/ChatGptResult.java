package com.example.healthyolder.bean;

import java.util.List;

public class ChatGptResult {

    private String id;
    private String object;
    private int created;
    private String model;
    private UsageBean usage;
    private List<ChoicesBean> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public UsageBean getUsage() {
        return usage;
    }

    public void setUsage(UsageBean usage) {
        this.usage = usage;
    }

    public List<ChoicesBean> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoicesBean> choices) {
        this.choices = choices;
    }

    public static class UsageBean {
        /**
         * prompt_tokens : 12
         * completion_tokens : 50
         * total_tokens : 62
         */

        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

        public int getPrompt_tokens() {
            return prompt_tokens;
        }

        public void setPrompt_tokens(int prompt_tokens) {
            this.prompt_tokens = prompt_tokens;
        }

        public int getCompletion_tokens() {
            return completion_tokens;
        }

        public void setCompletion_tokens(int completion_tokens) {
            this.completion_tokens = completion_tokens;
        }

        public int getTotal_tokens() {
            return total_tokens;
        }

        public void setTotal_tokens(int total_tokens) {
            this.total_tokens = total_tokens;
        }
    }

    public static class ChoicesBean {
        /**
         * text : 佛冷的啊

         根据天气预报，今天汕头全天多云，气温较低，最高气温在20摄氏
         * index : 0
         * logprobs : null
         * finish_reason : length
         */

        private String text;
        private int index;
        private Object logprobs;
        private String finish_reason;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Object getLogprobs() {
            return logprobs;
        }

        public void setLogprobs(Object logprobs) {
            this.logprobs = logprobs;
        }

        public String getFinish_reason() {
            return finish_reason;
        }

        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }
    }
}
