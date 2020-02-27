package com.example.models;

import com.example.models.test.TestGroup;

import java.util.ArrayList;

/*
 * Interface for test storage. Used as variable in StorageProxy (see constructor).
 * this way you can create you own storage (Oracle, MS SQL and other) and pass it as parameter
 * to StorageProxy
 * */
public interface ITestStorage {

    /*
    * Error message
    * */
    String getErrorMessage();


    /*
    * List of all tests in history
    * */
    ArrayList<TestGroup> getAllGroups();


    /*
    * Sets a list of test (records in history)
    * */
    void setAllGroups(ArrayList<TestGroup> allGroups);


    /*
    * Creates a group with test in database
    *
    * @groupHeader - header of this group
    * @listOfTestID - list of test ID's from HTML form (1,3;1,5;1,8;2,10;2,14...)
    * */
    int CreateGroup(String groupHeader, String listOfTestID);


    /*
    * Gets list of test groups and their content from storate.
    * Fills local variable allTests
    * */
    boolean GetGroupsFromStorage();


    /*
    * Clears results of TestGroup (correct/incorrect & Answered/non answered
    * required before you start testing
    *
    * @groupID - id of the header to be cleared
    * */
    boolean ClearGroupResults(int groupID);


    /*
    * Returns group of tests by groupID
    *
    * @groupID id of the header, to be returned
    * */
    public TestGroup GetGroupByID(int groupID);


    /*
    * Gets header and all it's questions, including "Question" class for every test
    * this required for testing - when you need to show next "Unanswered" question with text
    * and answer options
    *
    * @groupID - id of the group to be returned
    * */
    TestGroup GetGroupWithQuestionText(int groupID);


    /*
     * Marks a test "answered"
     *
     * @answerResult - True/False (correct/incorrect)
     * @headerID - id of the test
     * @testID - testID
     * @questionID - questionID
     * */
    boolean MakeTestAnswered(boolean answerResult, int testID, int questionGroupID, int questionID);


    /*
    * Removes TestGroup from storage
    *
    * @groupID to be removed
    * */
    public boolean DeleteTestGroup(int groupID);

}
