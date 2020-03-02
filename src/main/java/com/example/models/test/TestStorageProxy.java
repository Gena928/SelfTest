package com.example.models.test;

import com.example.models.ITestStorage;

import java.util.ArrayList;

public class TestStorageProxy {

    private ITestStorage YourTestStorage;


    /*
    * Constructor
    * */
    public TestStorageProxy(){
        // Initializing proxy unsing storage class.
        this.YourTestStorage = new TestStorageXML();
    }


    /*
     * Error message
     * */
    public String getErrorMessage(){
        return YourTestStorage.getErrorMessage();
    }


    /*
     * List of all tests in history
     * */
    public ArrayList<TestGroup> getAllTests() {
        return YourTestStorage.getAllGroups();
    }


    /*
     * Creates a group with test in database
     *
     * @groupHeader - header of this group
     * @listOfTestID - list of question ID's from HTML form (1,3,5,8...)
     * */
    public int CreateGroup(String groupHeader, String listOfTestID){
        return YourTestStorage.CreateGroup(groupHeader, listOfTestID);
    }


    /*
     * Updates header of the group
     *
     * @groupID - id of the group
     * @groupHeader - new header of the group
     * */
    public boolean UpdateGroup(int groupID, String groupHeader){
        return YourTestStorage.UpdateGroup(groupID, groupHeader);
    }


    /*
     * Gets list of test groups and their content from storate.
     * Fills local variable allTests
     * */
    public boolean GetGroupsFromStorage(){return YourTestStorage.GetGroupsFromStorage();}


    /*
     * Returns group of tests by groupID
     *
     * @groupID id of the header, to be returned
     * */
    public TestGroup GetGroupByID(int groupID){return YourTestStorage.GetGroupByID(groupID);}


    /*
     * Clears results of tests in one TestGroup (correct/incorrect & Answered/non answered
     * required before you start testing
     *
     * @groupID - id of the header to be cleared
     * */
    public boolean ClearGroupResults(int groupID){return YourTestStorage.ClearGroupResults(groupID);}


    /*
     * Gets header and all it's questions, including "Question" class for every test
     * this required for testing - when you need to show next "Unanswered" question with text
     * and answer options
     *
     * @groupID - id of the group to be returned
     * */
    public TestGroup GetGroupWithQuestionText(int groupID){
        return YourTestStorage.GetGroupWithQuestionText(groupID);
    }


    /*
     * Marks a test "answered"
     *
     * @answerResult - True/False (correct/incorrect)
     * @headerID - id of the test
     * @testID - testID
     * @questionID - questionID
     * */
    public boolean MakeQuestionAnswered(boolean answerResult, int groupID, int questionID){
        return YourTestStorage.MakeQuestionAnswered(answerResult, groupID, questionID);
    }


    /*
     * Removes TestGroup from storage
     *
     * @groupID to be removed
     * */
    public boolean DeleteTestGroup(int headerID){
        return YourTestStorage.DeleteTestGroup(headerID);
    }

}
