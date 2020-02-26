package com.example.models.history;

import com.example.models.test.TestQuestion;

/*
* Одна строка с ответом в базе данных
* */
public class HistoryRow {
    private int headerID;
    private int questionID;
    private int testID;
    private boolean questionAnswered;
    private boolean answerResult;
    private TestQuestion question;      // Реальный экземпляр вопроса (с текстом) для этой строки истории


    /*
    * Конструктор (значения по умолчанию)
    * */
    public HistoryRow(){
        this.answerResult = false;
        this.questionAnswered = false;
    }

    public int getHeaderID() {
        return headerID;
    }

    public void setHeaderID(int headerID) {
        this.headerID = headerID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getTestID() {return testID;}

    public void setTestID(int testID) {this.testID = testID;}

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

    public TestQuestion getQuestion() {return question;}

    public void setQuestion(TestQuestion question) {this.question = question; }
}
