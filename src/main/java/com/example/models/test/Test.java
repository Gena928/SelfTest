package com.example.models.test;

import com.example.models.questions.Question;

/*
* Одна строка с ответом в базе данных
* */
public class Test {
    private int testGroupID;                // ID of the current test group
    // private int testID;                     // Unique ID of this class
    private int questionID;                 // ID of the question
    private boolean questionAnswered;       // True / False (answered / not answered)
    private boolean answerResult;           // True / False (correct / incorrect)
    private Question question;              // Required when testing is in process


    /*
    * Constructor with default values
    * */
    public Test(){
        this.answerResult = false;
        this.questionAnswered = false;
    }

    public int getTestGroupID() {
        return testGroupID;
    }

    public void setTestGroupID(int testGroupID) {
        this.testGroupID = testGroupID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public boolean isQuestionAnswered() {
        return questionAnswered;
    }

    public void setQuestionAnswered(boolean questionAnswered) {
        this.questionAnswered = questionAnswered;
    }

    public boolean isAnswerResult() {
        return answerResult;
    }

    public void setAnswerResult(boolean answerResult) {
        this.answerResult = answerResult;
    }

    public Question getQuestion() {return question;}

    public void setQuestion(Question question) {this.question = question; }
}
