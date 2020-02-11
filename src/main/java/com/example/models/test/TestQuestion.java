package com.example.models.test;

import java.util.ArrayList;

/*
* Вариант ответа на тест.
* */
public class TestQuestion {
    private int testID;
    private int questionID;
    private String questionText;
    private boolean isMultiChoice;
    private String answerComment;
    private ArrayList<TestQuestionAnswer> testQuestionAnswers;



    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }



    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }



    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }


    /*
    * 150 символов из текста вопроса (надо для html формы)
    * */
    public String getQuestionTextSmall(){
        int len = questionText.length();
        if (len > 150)
            len  = 150;

        return questionText.substring(0, len);
    }

    public String getAnswerComment() {
        return answerComment;
    }

    public void setAnswerComment(String answerComment) {
        this.answerComment = answerComment;
    }




    public boolean isMultiChoice() {
        return isMultiChoice;
    }

    public void setMultiChoice(boolean multiChoice) {
        isMultiChoice = multiChoice;
    }



    public ArrayList<TestQuestionAnswer> getTestQuestionAnswers() {
        return testQuestionAnswers;
    }

    public void setTestQuestionAnswers(ArrayList<TestQuestionAnswer> testQuestionAnswers) {
        this.testQuestionAnswers = testQuestionAnswers;
    }

    // Количество ответов на вопрос
    public int getAnswersQty(){
        return (this.testQuestionAnswers == null) ? 0 : testQuestionAnswers.size();
    }



}
