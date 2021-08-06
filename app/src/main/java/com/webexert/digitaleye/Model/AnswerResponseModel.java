package com.webexert.digitaleye.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AnswerResponseModel {

    @SerializedName("student_id")
    @Expose
    private Integer studentId;
    @SerializedName("questions")
    @Expose
    private ArrayList<AnswerDataModel> questions = null;

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public ArrayList<AnswerDataModel> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<AnswerDataModel> questions) {
        this.questions = questions;
    }
}
