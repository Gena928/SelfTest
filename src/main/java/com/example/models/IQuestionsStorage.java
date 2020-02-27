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
    * List of all tests (each test contains several questions)
    * you can use getTestsFromStorage() method to fill the list
    * */
    ArrayList<QuestionGroup> getAllQuestionGroups();

    /*
    * Returns a test by it's ID
    * */
    QuestionGroup getTestByID(int id);


    /*
    * Returns an error message
    * */
    String getErrorMessage();

    /*
    * Gets list of test from storage (XML, database or other).
    * it should put a list into a local variable (i.e. not return here)
    * */
    boolean getTestsFromStorage();


    /*
    * Updates a test by it's ID
    * */
    boolean updateTest(int testID, String testHeader, int sortField);

    /*
    * Creates a new test in data storage
    * */
    boolean createTest(String testText);

    /*
    * Removes test from database
    * */
    boolean deleteTest(int testID);

    /*
    * Updates a question in storage
    * */
    boolean updateQuestion(int questionID,
                           int testID,
                           String questionText,
                           boolean isMultiChoice,
                           String answerComment);

    /*
    * Creates a new question in storage
    * */
    int createQuestion(int testID,
                           String questionText,
                           boolean isMultiChoice,
                           String answerComment);

    /*
    * Removes a question from storage
    * */
    boolean deleteQuestion(int questionID);

    /*
    * Updates a answer text in storage
    * */
    boolean updateAnswer(int answerID, int questionID, String answerText, boolean isCorrect);

    /*
    * Creates a new answer for question
    * */
    boolean createAnswer(int questionID, String answerText, boolean isCorrect);

    /*
    * Deletes answer from question
    * */
    boolean deleteAnswer(int answerID);


    /*
    * Moves the test UP or DOWN (changes it's sorting order)
    * */
    boolean moveTest(String upDown, int testID);

}
