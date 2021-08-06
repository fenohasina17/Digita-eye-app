package com.webexert.digitaleye.Model;

public class QuestionModel {

    private String question;
    private Boolean answer;

    public QuestionModel(String question, Boolean answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
}
