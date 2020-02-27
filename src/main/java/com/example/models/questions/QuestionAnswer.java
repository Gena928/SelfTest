package com.example.models.questions;

/*
* Answer option for question
* I.e. one of the possible answers (can be correct and not correct)
* */
public class QuestionAnswer {
    private int questionID;
    private int answerID;
    private String answerText;
    private boolean isCorrect;
    public int fakeID;              // ID fake id. Need for HTML only

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getAnswerID() {
        return answerID;
    }

    public void setAnswerID(int answerID) {
        this.answerID = answerID;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
