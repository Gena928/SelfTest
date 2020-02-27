package com.example.models.questions;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
* This is a "header" of questions.
* I.e. Test is not something, wich test you, but it just a header for a questions.
* Testing process is in the History classes
* */
public class QuestionGroup {
    private int testID;
    private String testHeader;
    private int sortField;
    private ArrayList<Question> questions;
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



    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    // Qantity of questions in this test
    public int getQuestionsQty(){
        return (this.questions == null) ? 0 : this.questions.size();
    }

    // Gets a question by it's id
    public Question GetQuestionByID(int questionID){
        for (int i = 0; i<questions.size(); i++){
            Question q = questions.get(i);
            if (q.getQuestionID() == questionID)
                return q;
        }
        return null;
    }



    public Date getCreatedDate() {
        return createdDate;
    }

    /*
    * Created date in String dd.MM.yyyy
    * */
    public String getCreatedDate_ddMMyyyy(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return (createdDate==null) ? "" : formatter.format(createdDate);
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
