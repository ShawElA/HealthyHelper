package com.example.healthyolder.bean;

import java.util.Date;

public class DepressionTestHistory {
    private Integer hId;
    private Integer hUid;
    private Integer hScore;
    private String hLevel;
    private Date hDate;
    private String hAnswers;

    public Integer getHId() {
        return hId;
    }

    public void setHId(Integer hId) {
        this.hId = hId;
    }

    public Integer getHUid() {
        return hUid;
    }

    public void setHUid(Integer hUid) {
        this.hUid = hUid;
    }

    public Integer getHScore() {
        return hScore;
    }

    public void setHScore(Integer hScore) {
        this.hScore = hScore;
    }

    public String getHLevel() {
        return hLevel;
    }

    public void setHLevel(String hLevel) {
        this.hLevel = hLevel;
    }

    public Date getHDate() {
        return hDate;
    }

    public void setHDate(Date hDate) {
        this.hDate = hDate;
    }

    public String getHAnswers() {
        return hAnswers;
    }

    public void setHAnswers(String hAnswers) {
        this.hAnswers = hAnswers;
    }
} 