package com.example.models.questions;

import java.util.ArrayList;
import java.util.Collections;

/*
* One question in test (contains multiple answer options)
* */
public class Question {
    private int testID;
    private int questionID;
    private String questionText;
    private boolean isMultiChoice;
    private String answerComment;       // Explanation of correct answer. I.e. why it is correct
    private ArrayList<QuestionAnswer> questionAnswers;

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

    /*
    * For HTML page (replaces new line with <br/>
    * */
    public String getQuestionTextAsTHML(){
        return textToHTML(questionText);
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /*
    * First 150 symbols of question header
    * */
    public String getQuestion150chars(){
        int len = questionText.length();
        if (len > 150)
            len  = 150;
        return questionText.substring(0, len);
    }

    /*
    * First 50 symbols of question header
    * */
    public String getQuestion50chars(){
        int len = questionText.length();
        if (len > 50)
            len  = 50;
        return questionText.substring(0, len);
    }


    public String getAnswerComment() {
        return answerComment;
    }

    /*
    * Comment for question as HTML
    * Comment is an explanation of correct answer. Why it is correct?
    * */
    public String getAnswerCommentAsHTML(){
        return textToHTML(answerComment);
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


    public ArrayList<QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public ArrayList<QuestionAnswer> getTestQuestionAnswerShuffled(){
        ArrayList<QuestionAnswer> shuffled = questionAnswers;
        Collections.shuffle(shuffled);
        return shuffled;
    }

    public void setQuestionAnswers(ArrayList<QuestionAnswer> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    // Quantity of answer options
    public int getAnswersQty(){
        return (this.questionAnswers == null) ? 0 : questionAnswers.size();
    }


    /*
    * Local method - replaces new line symbol with <br/>
    * */
    private String textToHTML(String inputStr){
        return inputStr
                .replace("\n", "<br/>")
                .replace("\r", "<br/>")
                .replace("<br/><br/>", "<br/>")
                .replace("<br/><br/>", "<br/>");
    }
}
