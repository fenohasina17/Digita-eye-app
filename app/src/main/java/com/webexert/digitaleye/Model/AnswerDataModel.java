package com.webexert.digitaleye.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AnswerDataModel {


    @SerializedName("q_id")
    @Expose
    private Integer qId;
    @SerializedName("and")
    @Expose
    private boolean and;

    public AnswerDataModel(Integer qId, boolean and) {
        this.qId = qId;
        this.and = and;
    }

    public Integer getqId() {
        return qId;
    }

    public void setqId(Integer qId) {
        this.qId = qId;
    }


    public boolean isAnd() {
        return and;
    }

    public void setAnd(boolean and) {
        this.and = and;
    }
}
