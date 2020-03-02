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
    private IQuestionsStorage yourQuestionsStorage;

    /*
    * Initializing Proxy
    * */
    public QuestionStorageProxy(){
        this.yourQuestionsStorage = new QuestionStorageXML();
    }

    /*
    * Error message
    * */
    public String getErrorMessage() {
        return yourQuestionsStorage.getErrorMessage();
    }

    /*
     * List of all QuestionGroups (each group contains several questions)
     * you can use getGroupsFromStorage() method to fill the list
     * */
    public ArrayList<QuestionGroup> getAllTests(){
        return yourQuestionsStorage.getAllQuestionGroups();
    }

    /*
     * Returns a group by it's ID
     *
     * @groupID - id of the group to be returned
     * */
    public QuestionGroup getGroupByID(int groupID){ return yourQuestionsStorage.getGroupByID(groupID); }

    /*
     * Returns a group by question ID
     *
     * @questionID id of the question to be returned
     * */
    public QuestionGroup getGroupByQuestionID(int questionID){
        return yourQuestionsStorage.getGroupByQuestionID(questionID);
    }

    /*
     * Fills local array with QuestionGroups
     * */
    public boolean getGroupsFromStorage(){
        return yourQuestionsStorage.getGroupsFromStorage();
    }

    /*
     * Updates a group by it's ID
     * */
    public boolean updateGroup(int groupID, String groupHeader, int sortField) {
        return yourQuestionsStorage.updateGroup(groupID, groupHeader, sortField);
    }

    /*
     * Creates a new group of questions in storage
     * */
    public boolean CreateGroup(String groupHeader){return yourQuestionsStorage.createGroup(groupHeader);}

    /*
     * Removes QuestionGroup from database
     * */
    public boolean DeleteGroup(int groupID) {
        return yourQuestionsStorage.deleteGroup(groupID);
    }

    /*
     * Updates a question in storage
     *
     * @questionID - id of a question to be updated
     * @questionText - question
     * @isMultiChoice - is this question a multi choice question?
     * @answerComment - comment for answer
     *
     * */
    public boolean updateQuestion(int questionID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        return yourQuestionsStorage.updateQuestion(questionID, questionText, isMultiChoice, answerComment);
    }

    /*
     * Creates a new question in storage
     *
     * @groupID - id of the QuestionGroup, where you need to create this question
     * @questionText - text of the question
     * @isMultiChoice - is this question multi choice? (radio buttons or checkboxes on HTML form)
     * @answerComment - comment for the question answer
     * */
    public int createQuestion(int groupID,
                              String questionText,
                              boolean isMultiChoice,
                              String answerComment){
        return yourQuestionsStorage.createQuestion(groupID, questionText, isMultiChoice, answerComment);
    }

    /*
     * Removes a question from storage
     *
     * @questionID - id of the question to be removed
     * */
    public boolean deleteQuestion(int questionID){
        return yourQuestionsStorage.deleteQuestion(questionID);
    }

    /*
     * Updates a answer text in storage
     *
     * @answerID - id of the answer in database
     * @questionID - id of the question in database
     * @answerText - new text of the answer
     * @isCorrect - is this answer correct?
     * */
    public boolean updateAnswer(int answerID, int questionID, String answerText, boolean isCorrect){
        return yourQuestionsStorage.updateAnswer(answerID, questionID, answerText, isCorrect);
    }

    /*
     * Creates a new answer for question
     *
     * @questionID - id of the question where you need to create the answer
     * @answerText - text of the answer
     * @isCorrect - is this answer correct or not
     * */
    public boolean createAnswer(int questionID, String answerText, boolean isCorrect){
        return yourQuestionsStorage.createAnswer(questionID, answerText, isCorrect);
    }

    /*
     * Deletes answer from question
     *
     * @answerID - id of the answer to be removed
     * */
    public boolean deleteAnswer(int answerID){
        return yourQuestionsStorage.deleteAnswer(answerID);
    }


    /*
     * Moves the test UP or DOWN (changes it's sorting order)
     *
     * @upDown - string (up/down) which shows direction for movement
     * @groupID - id of the group to be moved
     * */
    public boolean moveTest(String upDown, int testID){ return yourQuestionsStorage.moveTest(upDown, testID); }
}
