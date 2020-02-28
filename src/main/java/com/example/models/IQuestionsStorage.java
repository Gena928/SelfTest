package com.example.models;

import com.example.models.questions.QuestionGroup;

import java.util.ArrayList;


/*
* Interface for questions storage. Used as variable in StorageProxy (see constructor).
* this way you can create you own storage (Oracle, MS SQL and other) and pass it as parameter
* to StorageProxy
* */
public interface IQuestionsStorage {

    /*
     * Returns an error message
     * */
    String getErrorMessage();

    /*
    * List of all QuestionGroups (each group contains several questions)
    * you can use getGroupsFromStorage() method to fill the list
    * */
    ArrayList<QuestionGroup> getAllQuestionGroups();

    /*
    * Returns a group by it's ID
    *
    * @groupID - id of the group to be returned
    * */
    QuestionGroup getGroupByID(int groupID);

    /*
     * Returns a group by question ID
     *
     * @questionID id of the question in group
     * */
    QuestionGroup getGroupByQuestionID(int questionID);

    /*
    * Fills local array with QuestionGroups
    * */
    boolean getGroupsFromStorage();

    /*
    * Updates a group by it's ID
    * */
    boolean updateGroup(int groupID, String groupHeader, int sortField);

    /*
    * Creates a new group of questions in storage
    * */
    boolean createGroup(String groupHeader);

    /*
    * Removes QuestionGroup from database
    * */
    boolean deleteGroup(int groupID);

    /*
    * Updates a question in storage
    *
    * @questionID - id of a question to be updated
    * @questionText - question
    * @isMultiChoice - is this question a multi choice question?
    * @answerComment - comment for answer
    *
    * */
    boolean updateQuestion(int questionID,
                           String questionText,
                           boolean isMultiChoice,
                           String answerComment);

    /*
    * Creates a new question in storage
    *
    * @groupID - id of the QuestionGroup, where you need to create this question
    * @questionText - text of the question
    * @isMultiChoice - is this question multi choice? (radio buttons or checkboxes on HTML form)
    * @answerComment - comment for the question answer
    * */
    int createQuestion(int groupID,
                           String questionText,
                           boolean isMultiChoice,
                           String answerComment);

    /*
    * Removes a question from storage
    *
    * @questionID - id of the question to be removed
    * */
    boolean deleteQuestion(int questionID);

    /*
    * Updates a answer text in storage
    *
    * @answerID - id of the answer in database
    * @questionID - id of the question in database
    * @answerText - new text of the answer
    * @isCorrect - is this answer correct?
    * */
    boolean updateAnswer(int answerID, int questionID, String answerText, boolean isCorrect);

    /*
    * Creates a new answer for question
    *
    * @questionID - id of the question where you need to create the answer
    * @answerText - text of the answer
    * @isCorrect - is this answer correct or not
    * */
    boolean createAnswer(int questionID, String answerText, boolean isCorrect);

    /*
    * Deletes answer from question
    *
    * @answerID - id of the answer to be removed
    * */
    boolean deleteAnswer(int answerID);


    /*
    * Moves the test UP or DOWN (changes it's sorting order)
    *
    * @upDown - string (up/down) which shows direction for movement
    * @groupID - id of the group to be moved
    * */
    boolean moveTest(String upDown, int groupID);

}
