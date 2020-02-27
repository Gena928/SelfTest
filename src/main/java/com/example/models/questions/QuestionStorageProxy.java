package com.example.models.questions;

import com.example.models.IQuestionsStorage;

import java.util.ArrayList;

/*
* A proxy class for working with tests
* It allows you to change storage (XMLTestStorage) without big damage for program logic
* I.e. you can create your own OracleTestStorage or MSSQLTestsStorage and put into this proxy
* contructor
* */
public class QuestionStorageProxy {
    private IQuestionsStorage iQuestionsStorage;


    /*
    * Initializing Proxy
    * */
    public QuestionStorageProxy(){
        this.iQuestionsStorage = new QuestionStorageXML();
    }


    /*
    * Error message
    * */
    public String getErrorMessage() {
        return iQuestionsStorage.getErrorMessage();
    }


    /*
     * Gets a list of test from storage and writes it into variable
     * */
    public boolean getTestsFromStorage(){
        return iQuestionsStorage.getTestsFromStorage();
    }


    /*
    * Gets a list of test from variable (use previous method to fill variable)
    * */
    public ArrayList<QuestionGroup> getAllTests(){
        return iQuestionsStorage.getAllQuestionGroups();
    }


    /*
    * Gets a test by it's ID
    * please use getTestsFromStorage() command first
    * */
    public QuestionGroup getTestByID(int id){ return iQuestionsStorage.getTestByID(id); }

    /*
    * Creates a new test in storage
    * */
    public boolean CreateTest(String testHeader){return iQuestionsStorage.createTest(testHeader);}


    /*
    * Updates test parameters
    * */
    public boolean updateTest(int testID, String testHeader, int sortField) {
        return iQuestionsStorage.updateTest(testID, testHeader, sortField);
    }


    /*
    * Removes test from storage
    * */
    public boolean deleteTest(int testID) {
        return iQuestionsStorage.deleteTest(testID);
    }


    /*
    * Changes sorting order of the test (moves it up or down)
    * */
    public boolean moveTest(String upDown, int testID){ return iQuestionsStorage.moveTest(upDown, testID); }


    /*
    * Creates a new question for test
    * */
    public int createQuestion(int testID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        return iQuestionsStorage.createQuestion(testID, questionText, isMultiChoice, answerComment);
    }


    /*
    * Updates question in database
    * */
    public boolean updateQuestion(int questionID,
                                  int testID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        return iQuestionsStorage.updateQuestion(questionID, testID, questionText, isMultiChoice, answerComment);
    }

    /*
    * Removes the question in database
    * */
    public boolean deleteQuestion(int questionID){
        return iQuestionsStorage.deleteQuestion(questionID);
    }


    /*
    * Creates answer for question
    * */
    public boolean createAnswer(int questionID, String answerText, boolean isCorrect){
        return iQuestionsStorage.createAnswer(questionID, answerText, isCorrect);
    }


    /*
    * Removes answer from database
    * */
    public boolean deleteAnswer(int answerID){
        return iQuestionsStorage.deleteAnswer(answerID);
    }

}
