package com.webexert.digitaleye.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class QuestionResponseModel {

    @SerializedName("data")
    @Expose
    private ArrayList<QuestionDataModel> data = null;
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;

    public ArrayList<QuestionDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<QuestionDataModel> data) {
        this.data = data;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
