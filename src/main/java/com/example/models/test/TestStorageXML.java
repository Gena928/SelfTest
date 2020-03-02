package com.example.models.test;


import com.example.models.ITestStorage;
import com.example.models.questions.QuestionGroup;
import com.example.models.questions.Question;
import com.example.models.questions.QuestionStorageXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TestStorageXML implements ITestStorage {

    private ArrayList<TestGroup> allGroups;
    private String errorMessage;
    private String historyFileName = "src/main/resources/XMLfiles/Tests.xml";


    /*
     * Error message
     * */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }


    /*
     * List of all tests in history
     * */
    @Override
    public ArrayList<TestGroup> getAllGroups() {
        return allGroups;
    }


    /*
     * Sets a list of test (records in history)
     * */
    @Override
    public void setAllGroups(ArrayList<TestGroup> allGroups) {
        this.allGroups = allGroups;
    }


    /*
     * Creates a group with test in database
     *
     * @groupHeader - header of this group
     * @listOfTestID - list of Question ID's from HTML form (1,3,10,32,45...)
     * */
    @Override
    public int CreateGroup(String groupHeader, String listOfTestIDs){

        Document doc;
        TestGroup testGroup = new TestGroup();
        ArrayList<Test> tests = new ArrayList<>();

        // Reading XML file from disk
        try {
            doc = getXMLDocument(historyFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return -1;
        }


        // Creating group and list of tests inside group
        // test means ID numbers of questions (see Test class)
        try{
            // new group ID
            testGroup.setGroupID(
                    getMaxID(historyFileName, "TestGroup", "id") + 1
            );

            // List of questions from string into array
            // (each item contains a pair of "QuestionGroup, Question")
            String[] questionsArray = listOfTestIDs.split(",");

            // Generating test
            for (int i = 0; i<questionsArray.length; i++){
                Test currentTest = new Test();
                currentTest.setQuestionID(Integer.valueOf(questionsArray[i]));
                tests.add(currentTest);
            }
            testGroup.setTests(tests);

        }
        catch (Exception e){
            errorMessage = "Can not get list of questions from HTML form";
            return -1;
        }


        // Creating record in database (XML file)
        try {
            Element rootElement = doc.getDocumentElement();

            // TestGroup
            Element element = doc.createElement("TestGroup");
            element.setAttribute("id", String.valueOf(testGroup.getGroupID()));
            element.setAttribute("CreatedDate", TodayAs_ddMMyyyy());

            // TestGroup header
            Element element_header = doc.createElement("header");
            element_header.setTextContent(stringToBytes(groupHeader));
            element.appendChild(element_header);


            // Questions
            for (int i = 0; i< testGroup.getTests().size(); i++){
                Test test = testGroup.getTests().get(i);

                Element xmlHistoryRow = doc.createElement("Test");
                xmlHistoryRow.setAttribute("testGroupID", String.valueOf(testGroup.getGroupID()));
                // xmlHistoryRow.setAttribute("testID", String.valueOf(test.getTestID()));
                xmlHistoryRow.setAttribute("questionID", String.valueOf(test.getQuestionID()));
                xmlHistoryRow.setAttribute("questionAnswered", String.valueOf(test.isQuestionAnswered()));
                xmlHistoryRow.setAttribute("answerResult", String.valueOf(test.isAnswerResult()));

                element.appendChild(xmlHistoryRow);
            }

            // Adding TestGroup to XML document
            rootElement.appendChild(element);

        }catch (Exception e){
            errorMessage = "Can't create new xml node: " + e.getMessage();
            return -1;

        }


        // Writting XML back to disk
        try {
            saveXMLDocument(historyFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return -1;
        }

        return testGroup.getGroupID();
    }


    /*
     * Updates header of the group
     *
     * @groupID - id of the group
     * @groupHeader - new header of the group
     * */
    @Override
    public boolean UpdateGroup(int groupID, String groupHeader){

        try {
            Document doc = getXMLDocument(historyFileName);

            NodeList nodeList = doc.getElementsByTagName("TestGroup");
            for (int i = 0; i<nodeList.getLength(); i++){

                Node xmlTestGroup = nodeList.item(i);

                int id = Integer.parseInt(xmlTestGroup
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());

                // Continue to next record in case of wrong ID
                if (id != groupID)
                    continue;

                // Otherwise updating the group header
                Node gHeader = ((Element)xmlTestGroup).getElementsByTagName("header").item(0);
                gHeader.setTextContent(stringToBytes(groupHeader));
            }

            // Save document back to XML file
            saveXMLDocument(historyFileName, doc);

        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Gets list of test groups and their content from storate.
     * Fills local variable allTests
     * */
    @Override
    public boolean GetGroupsFromStorage(){

        allGroups = new ArrayList<>();

        try {
            // Reading XML into XMLDocument
            Document doc = getXMLDocument(historyFileName);

            NodeList nodeList = doc.getElementsByTagName("TestGroup");
            for (int i = 0; i<nodeList.getLength(); i++){

                // Current XML node with TestGroup
                Node xmlTestGroup = nodeList.item(i);
                TestGroup testGroup = new TestGroup();

                int id = Integer.parseInt(xmlTestGroup
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());
                testGroup.setGroupID(id);

                String dateInString = xmlTestGroup.getAttributes().getNamedItem("CreatedDate").getNodeValue();
                testGroup.setCreatedDate(DateFromString_ddMMyyyy(dateInString));

                String hText = ((Element)xmlTestGroup).getElementsByTagName("header").item(0)
                        .getTextContent();
                hText = BytesToString(hText);
                testGroup.setHeaderText(hText);

                // Tests inside TestGroup
                ArrayList<Test> listOfTests = new ArrayList<>();
                NodeList xmlQuestions = ((Element)xmlTestGroup).getElementsByTagName("Test");
                for (int q = 0; q<xmlQuestions.getLength(); q++) {

                    Test test = new Test();
                    Node xmlCurrentQuestion = xmlQuestions.item(q);
                    test.setTestGroupID(testGroup.getGroupID());

/*                    int tID = Integer.parseInt(
                            xmlCurrentQuestion
                                    .getAttributes()
                                    .getNamedItem("testID")
                                    .getNodeValue());
                    test.setTestID(tID);*/

                    int qID = Integer.parseInt(
                            xmlCurrentQuestion
                                    .getAttributes()
                                    .getNamedItem("questionID")
                                    .getNodeValue());
                    test.setQuestionID(qID);

                    boolean questionAnswered = Boolean.parseBoolean(
                            xmlCurrentQuestion
                            .getAttributes()
                            .getNamedItem("questionAnswered")
                            .getNodeValue());
                    test.setQuestionAnswered(questionAnswered);

                    boolean answerResult = Boolean.parseBoolean(
                            xmlCurrentQuestion
                                    .getAttributes()
                                    .getNamedItem("answerResult")
                                    .getNodeValue());
                    test.setAnswerResult(answerResult);
                    listOfTests.add(test);

                }
                testGroup.setTests(listOfTests);

                allGroups.add(testGroup);
            }

        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }
        return true;
    }


    /*
     * Clears results of tests in one TestGroup (correct/incorrect & Answered/non answered
     * required before you start testing
     *
     * @groupID - id of the header to be cleared
     * */
    @Override
    public boolean ClearGroupResults(int groupID){


        try {
            Document doc = getXMLDocument(historyFileName);

            NodeList nodeList = doc.getElementsByTagName("TestGroup");
            for (int i = 0; i<nodeList.getLength(); i++){

                Node xmlTestGroup = nodeList.item(i);

                int id = Integer.parseInt(xmlTestGroup
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());

                // Continue to next record in case of wrong ID
                if (id != groupID)
                    continue;

                // Otherwise clearing test results
                NodeList xmlQuestions = ((Element)xmlTestGroup).getElementsByTagName("Test");
                for (int q = 0; q<xmlQuestions.getLength(); q++) {

                    Node xmlCurrentQuestion = xmlQuestions.item(q);

                    Node questionAnswered = xmlCurrentQuestion.getAttributes().getNamedItem("questionAnswered");
                    questionAnswered.setTextContent("false");

                    Node answerResult = xmlCurrentQuestion.getAttributes().getNamedItem("answerResult");
                    answerResult.setTextContent("false");
                }
            }

            // Save document back to XML file
            saveXMLDocument(historyFileName, doc);

        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }
        return true;
    }


    /*
     * Returns group of tests by groupID
     *
     * @groupID id of the header, to be returned
     * */
    @Override
    public TestGroup GetGroupByID(int groupID){

        TestGroup testGroup = null;

        // All groups
        if (GetGroupsFromStorage() == false)
            return null;

        // Searching exact item
        for (int i = 0; i< allGroups.size(); i++){
            if (allGroups.get(i).getGroupID() == groupID)
                testGroup = allGroups.get(i);
        }
        if (testGroup == null) {
            errorMessage = "History records were not found in database";
            return null;
        }

        return testGroup;
    }


    /*
     * Gets header and all it's questions, including "Question" class for every test
     * this required for testing - when you need to show next "Unanswered" question with text
     * and answer options
     *
     * @groupID - id of the group to be returned
     * */
    @Override
    public TestGroup GetGroupWithQuestionText(int groupID){

        errorMessage = "";

        TestGroup testGroup = GetGroupByID(groupID);
        if (testGroup == null)
            return null;

        // Getting all groups from storage
        QuestionStorageXML testStorage = new QuestionStorageXML();
        if (testStorage.getGroupsFromStorage() == false) {
            errorMessage = testStorage.getErrorMessage();
            return null;
        }

        // Searching question for every test (need it's text and answer options for HTML form)
        // Loop each test in group
        for (int currTestID = 0; currTestID < testGroup.getTests().size(); currTestID ++){
            Test currentTest = testGroup.getTests().get(currTestID);

            // Loop each group with questons
            for (int questionGroupID = 0; questionGroupID < testStorage.getAllQuestionGroups().size(); questionGroupID ++){
                QuestionGroup questionGroup = testStorage.getAllQuestionGroups().get(questionGroupID);

                // Loop each question in QuestionGroup
                for (int questionID = 0; questionID< questionGroup.getQuestions().size(); questionID ++){
                    Question question = questionGroup.getQuestions().get(questionID);
                    if (question.getQuestionID() == currentTest.getQuestionID()){
                        currentTest.setQuestion(question);
                        break;
                    }
                }
            }
        }

        return testGroup;
    }


    /*
     * Marks a question "answered"
     *
     * @answerResult - True/False (correct/incorrect)
     * @headerID - id of the test
     * @testID - testID
     * @questionID - questionID
     * */
    @Override
    public boolean MakeQuestionAnswered(boolean answerResult, int groupID, int questionID){

        try {
            Document doc = getXMLDocument(historyFileName);

            NodeList nodeList = doc.getElementsByTagName("TestGroup");
            for (int i = 0; i<nodeList.getLength(); i++){

                Node xmlTestGroup = nodeList.item(i);

                int id = Integer.parseInt(xmlTestGroup
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());

                // Go to next item in case this is NOT what we are looking
                if (id != groupID)
                    continue;

                NodeList xmlQuestions = ((Element)xmlTestGroup).getElementsByTagName("Test");
                for (int q = 0; q<xmlQuestions.getLength(); q++) {

                    Node xmlCurrentTest = xmlQuestions.item(q);

                    int qID = Integer.parseInt(
                            xmlCurrentTest
                            .getAttributes()
                            .getNamedItem("questionID")
                            .getNodeValue()
                    );

                    if (qID == questionID){
                        Node questionAnswered = xmlCurrentTest.getAttributes().getNamedItem("questionAnswered");
                        questionAnswered.setTextContent("true");

                        Node aResult = xmlCurrentTest.getAttributes().getNamedItem("answerResult");
                        aResult.setTextContent(String.valueOf(answerResult));
                    }
                }
            }

            // Save document to database
            saveXMLDocument(historyFileName, doc);

        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }
        return true;
    }


    /*
     * Removes TestGroup from storage
     *
     * @groupID to be removed
     * */
    @Override
    public boolean DeleteTestGroup(int groupID){

        allGroups = new ArrayList<>();

        try {
            Document doc = getXMLDocument(historyFileName);

            // Бежим по каждому тесту в истории
            NodeList nodeList = doc.getElementsByTagName("TestGroup");
            for (int i = 0; i<nodeList.getLength(); i++){

                Node xmlCurrentHeader = nodeList.item(i);

                int id = Integer.parseInt(xmlCurrentHeader
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());

                if (id == groupID)
                    xmlCurrentHeader.getParentNode().removeChild(xmlCurrentHeader);
            }

            // Save document
            saveXMLDocument(historyFileName, doc);

            // Remove empty lines from XML doc
            QuestionStorageXML.RemoveNewLines(historyFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }
        return true;
    }


    //<editor-fold desc="Local methods">
    /*
     * Creates XML document using fileName
     *
     * @fileName name of the file with XML
     * */

    private Document getXMLDocument(String fileName) throws Exception{

        // InputStreamReader inputStreamReader = new InputStreamReader(System.in, "UTF-8");
        File xmlFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();


        return doc;
    }


    /*
     * Save XML document back to disk
     *
     * @fileName - name of the file
     * @doc - xml document
     * */
    private void saveXMLDocument(String fileName, Document doc) throws Exception{

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        // transformerFactory.setAttribute("indent-number", 2);

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // nice format

        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(fileName));
        transformer.transform(domSource, streamResult);
        // transformer.transform(xmlInput, streamResult);
    }


    /*
     * Gets maximum id in XML document
     *
     * @fileName - name of the file with XML nodes
     * @tagName - tag name inside XML document
     * @attributeName - attribute of the tag
     *
     * */
    private int getMaxID(String fileName, String tagName, String attributeName) throws Exception {
        int maxId = 0;
        Document doc = getXMLDocument(fileName);

        NodeList nodeList = doc.getElementsByTagName(tagName);
        for (int i = 0; i<nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);

            int currentID = Integer.parseInt(currentNode.getAttributes().getNamedItem(attributeName).getNodeValue());
            maxId = (currentID > maxId) ? currentID : maxId;
        }
        return maxId;
    }


    /*
     * Creates a string with DD.MM.YYYY from current date
     * */
    private String TodayAs_ddMMyyyy(){

        Date date= Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return formatter.format(date);
    }


    /*
     * Converts string into date (string format DD.MM.YYYY)
     *
     * @input - string with date
     * */
    private Date DateFromString_ddMMyyyy(String input) throws ParseException {

        // https://www.tutorialspoint.com/how-to-parse-date-from-string-in-the-format-dd-mm-yyyy-to-dd-mm-yyyy-in-java

        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        //Parsing the given String to Date object
        Date date = formatter.parse(input);
        // System.out.println("Date object value: " + date);
        return date;
    }


    /*
     * Converts a string into String of bytes (100 117 109 109 121)
     *
     * @input - string to be converted
     * */
    private String stringToBytes(String input) throws UnsupportedEncodingException {

        byte[] bytes = input.getBytes("UTF-8");
        String result = "";
        for (int i = 0; i<bytes.length; i++){
            result += bytes[i] + " ";
        }
        return result.trim();
    }


    /*
     * Converts string of bytes (100 117 109 109 121) back to normal text
     *
     * @inputBytes - string with bytes
     * */
    private String BytesToString(String inputBytes) throws UnsupportedEncodingException{

        String[] strBytes = inputBytes.trim().split(" ");
        byte[] bytes = new byte[strBytes.length];

        for (int i = 0; i<strBytes.length; i++)
            bytes[i] = Byte.valueOf(strBytes[i]);

        return new String(bytes, "UTF-8");
    }

    //</editor-fold>
}
