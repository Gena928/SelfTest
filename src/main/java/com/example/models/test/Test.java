package com.example.models.test;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
* Одна строка с тестами из базы. Любой базы.
* */
public class Test {
    private int testID;
    private String testHeader;
    private int sortField;
    private ArrayList<TestQuestion> questions;
    private Date createdDate;


    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }

    public String getTestHeader() {
        return testHeader;
    }

    public void setTestHeader(String testHeader) {
        this.testHeader = testHeader;
    }

    public int getSortField() {
        return sortField;
    }

    public void setSortField(int sortField) {
        this.sortField = sortField;
    }



    public ArrayList<TestQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<TestQuestion> questions) {
        this.questions = questions;
    }

    // Количество вопросов в данном тесте
    public int getQuestionsQty(){
        return (this.questions == null) ? 0 : this.questions.size();
    }

    // Выдаем вопрос по его ID
    public TestQuestion GetQuestionByID(int questionID){
        for (int i = 0; i<questions.size(); i++){
            TestQuestion q = questions.get(i);
            if (q.getQuestionID() == questionID)
                return q;
        }
        return null;
    }



    public Date getCreatedDate() {
        return createdDate;
    }

    /*
    * Дата создания в формате dd.MM.yyyy
    * */
    public String getCreatedDate_ddMMyyyy(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return (createdDate==null) ? "" : formatter.format(createdDate);
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
