package com.example.models.questions;


import com.example.models.IQuestionsStorage;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
* This class works with tests. A test is a list of questinons. I.e. test is like a header with a lot
* of questions. You can use "history" classes later to orginize questions in groups and start actual testing process
*   - it gets test/questions from database
*   - updates test/questions;
*   - removes test/questions from database;
* */
public class QuestionStorageXML implements IQuestionsStorage {

    private ArrayList<QuestionGroup> allQuestionGroups;
    private String errorMessage;
    private String testFileName = "src/main/resources/XMLfiles/Questions.xml";


    /*
    * Constructor
    * */
    public QuestionStorageXML(){

    }


    /*
     * Returns an error message
     * */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }


    /*
     * List of all QuestionGroups (each group contains several questions)
     * you can use getGroupsFromStorage() method to fill the list
     * */
    @Override
    public ArrayList<QuestionGroup> getAllQuestionGroups() {
        return allQuestionGroups;
    }


    /*
     * Returns a group by it's ID
     *
     * @groupID - id of the group to be returned
     * */
    @Override
    public QuestionGroup getGroupByID(int groupID){

        // Get all groups from file
        if (!getGroupsFromStorage())
            return null;

        for (int i = 0; i< allQuestionGroups.size(); i++){
            if (allQuestionGroups.get(i).getGroupID() == groupID)
                return allQuestionGroups.get(i);
        }
        errorMessage = "group was not found in database";
        return null;
    }


    /*
    * Returns a group by question ID
    *
    * @questionID id of the question to be returned
    * */
    @Override
    public QuestionGroup getGroupByQuestionID(int questionID){

        // Getting all groups from file
        if (!getGroupsFromStorage())
            return null;

        // Iterating all groups and searching required questionID
        for (int group = 0; group<allQuestionGroups.size(); group++){
            QuestionGroup currentGroup = allQuestionGroups.get(group);
            for (int q = 0; q<currentGroup.getQuestions().size();q++ ){
                if (questionID == currentGroup.getQuestions().get(q).getQuestionID())
                    return currentGroup;
            }
        }

        errorMessage = "Group was not found in database";
        return null;
    }



    /*
     * Fills local array with QuestionGroups
     * */
    @Override
    public boolean getGroupsFromStorage(){

        // Clear variables
        errorMessage = "";
        allQuestionGroups = new ArrayList<>();

        try {
            Document doc = getXMLDocument(testFileName);

            // Iterating all groups in XML file
            NodeList nodeList = doc.getElementsByTagName("QuestionGroup");
            for (int i = 0; i<nodeList.getLength(); i++){
                Node currentNode = nodeList.item(i);
                QuestionGroup currentQuestionGroup = new QuestionGroup();
                int groupID = Integer.parseInt(currentNode
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());

                String dateInString = currentNode.getAttributes().getNamedItem("CreatedDate").getNodeValue();
                currentQuestionGroup.setGroupID(groupID);
                currentQuestionGroup.setGroupHeader(currentNode.getAttributes().getNamedItem("Header").getNodeValue());
                currentQuestionGroup.setCreatedDate(DateFromString_ddMMyyyy(dateInString));

                int sortField = Integer.valueOf(currentNode.getAttributes().getNamedItem("SortField").getNodeValue());
                currentQuestionGroup.setSortField(sortField);

                // Getting questions from XML node
                currentQuestionGroup.setQuestions(getQuestionsFromXML(currentNode));

                allQuestionGroups.add(currentQuestionGroup);
            }

        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Updates a group by it's ID
     * */
    @Override
    public boolean updateGroup(int groupID, String groupHeader, int sortField){

        Document doc;

        // Test header stored as usual string, therefore need to remove all
        // non alphanumeric symbols (Russian and Latin)
        groupHeader = groupHeader.replaceAll("[^A-Za-zА-Яа-я0-9 ]", "");

        // Reading an XML from disk
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Updating XML node
        try {

            NodeList nodeList = doc.getElementsByTagName("QuestionGroup");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int currentID = Integer.parseInt(currentNode.getAttributes().getNamedItem("id").getNodeValue());

                if (currentID == groupID){
                    Node attrHeader = currentNode.getAttributes().getNamedItem("Header");
                    attrHeader.setTextContent(groupHeader);

                    Node attrSort = currentNode.getAttributes().getNamedItem("SortField");
                    attrSort.setTextContent(String.valueOf(sortField));
                }
            }

            // Saving document back to disk
            saveXMLDocument(testFileName, doc);


        }catch (Exception e){
            errorMessage = "Can't update XML document: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Creates a new group of questions in storage
     * */
    @Override
    public boolean createGroup(String groupHeader){
        Document doc;

        // Test header stored as usual string, therefore need to remove all
        // non alphanumeric symbols (Russian and Latin)
        groupHeader = groupHeader.replaceAll("[^A-Za-zА-Яа-я0-9 ]", "");

        // Reading file from disk
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Creating new XML element
        try {

            Element rootElement = doc.getDocumentElement();

            Element element = doc.createElement("QuestionGroup");
            int nextID = getMaxID(testFileName, "QuestionGroup", "id") + 1;
            int nextSort = getMaxID(testFileName, "QuestionGroup", "SortField") + 100;

            element.setAttribute("id", String.valueOf(nextID));
            element.setAttribute("Header", groupHeader);
            element.setAttribute("SortField", String.valueOf(nextSort));
            element.setAttribute("CreatedDate", TodayAs_ddMMyyyy());
            rootElement.appendChild(element);


        }catch (Exception e){
            errorMessage = "Can't create new XML node with test name: " + e.getMessage();
            return false;
        }

        // Writting XML back to disk
        try {
            saveXMLDocument(testFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Removes QuestionGroup from database
     * */
    @Override
    public boolean deleteGroup(int groupID){

        Document doc;

        // Reading file from disk
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Removing test
        try {

            NodeList nodeList = doc.getElementsByTagName("QuestionGroup");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);
                int currentID = Integer.parseInt(currentNode
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue()
                );
                if (currentID == groupID)
                    currentNode.getParentNode().removeChild(currentNode);
            }

            // Saving document
            saveXMLDocument(testFileName, doc);

        }catch (Exception e){
            errorMessage = "Can't remove node from XML document: " + e.getMessage();
            return false;
        }

        // Writting XML back to disk
        try {
            saveXMLDocument(testFileName, doc);

            // Removing empty lines from XML doc
            QuestionStorageXML.RemoveNewLines(testFileName);

        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
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
    @Override
    public boolean updateQuestion(int questionID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        Document doc;

        // Checking strings
        questionText = questionText.trim();
        answerComment = answerComment.trim();
        if (questionText.isEmpty()){
            this.errorMessage = "Question text can not be empty";
            return false;
        }
        if (answerComment.isEmpty()){
            this.errorMessage = "Answer can not be empty";
            return false;
        }


        // Reading file from disk
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Updating XML element
        try {

            NodeList nodeList = doc.getElementsByTagName("Question");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int qID = Integer.parseInt(currentNode.getAttributes().getNamedItem("id").getNodeValue());

                if (qID == questionID){

                    Node qHeader = ((Element)currentNode).getElementsByTagName("Qtext").item(0);
                    qHeader.setTextContent(stringToBytes(questionText));

                    Node qAnswer = ((Element)currentNode).getElementsByTagName("AText").item(0);
                    qAnswer.setTextContent(stringToBytes(answerComment));

                    Node attrIsMultiChoice = currentNode.getAttributes().getNamedItem("IsMultiChoice");
                    attrIsMultiChoice.setTextContent(String.valueOf(isMultiChoice));
                }
            }

        }catch (Exception e){
            errorMessage = "Can't update XML node: " + e.getMessage();
            return false;
        }

        // Saving XML document back to disk
        try {
            saveXMLDocument(testFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Creates a new question in storage
     *
     * @groupID - id of the QuestionGroup, where you need to create this question
     * @questionText - text of the question
     * @isMultiChoice - is this question multi choice? (radio buttons or checkboxes on HTML form)
     * @answerComment - comment for the question answer
     * */
    @Override
    public int createQuestion(int groupID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        Document doc;
        int questionID = -1;

        // Reading file from disk
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return -1;
        }

        // Creating a new question (XML node)
        try {

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            String xpathString = "//QuestionGroup[@id=repl" + groupID +  "repl]";
            xpathString = xpathString.replaceAll("repl","\"");


            XPathExpression expr = xpath.compile(xpathString);
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);


            if (nl != null && nl.getLength() > 0){
                if (nl.item(0).getNodeType() == Element.ELEMENT_NODE){
                    Element parent = (Element) nl.item(0);


                    Element newQuestion = doc.createElement("Question");
                    int nextID = getMaxID(testFileName, "Question", "id") + 1;
                    questionID = nextID;
                    newQuestion.setAttribute("id", String.valueOf(nextID));
                    newQuestion.setAttribute("IsMultiChoice", String.valueOf(isMultiChoice));

                    Element QText = doc.createElement("Qtext");
                    QText.setTextContent(stringToBytes(questionText));
                    newQuestion.appendChild(QText);

                    Element AText = doc.createElement("AText");
                    AText.setTextContent(stringToBytes(answerComment));
                    newQuestion.appendChild(AText);

                    parent.appendChild(newQuestion);

                }
            }

        }catch (Exception e){
            errorMessage = "Can't create new XML node with test name: " + e.getMessage();
            return -1;
        }

        // Writting XML document back to disk
        try {
            saveXMLDocument(testFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return -1;
        }

        return questionID;
    }


    /*
     * Removes a question from storage
     *
     * @questionID - id of the question to be removed
     * */
    @Override
    public boolean deleteQuestion(int questionID){
        Document doc;

        // Reading file from disk
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Removing a question from database (from XML document)
        try {

            NodeList nodeList = doc.getElementsByTagName("Question");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int qID = Integer.parseInt(currentNode.getAttributes()
                        .getNamedItem("id")
                        .getNodeValue()
                );

                if (qID == questionID){
                    currentNode.getParentNode().removeChild(currentNode);
                    break;
                }
            }


        }catch (Exception e){
            errorMessage = "Can't update XML node: " + e.getMessage();
            return false;
        }

        // Saving XML back to disk
        try {
            saveXMLDocument(testFileName, doc);

            // Removing empty lines in XML document
            QuestionStorageXML.RemoveNewLines(testFileName);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }
        return true;
    }



    /*
     * Updates a answer text in storage
     *
     * @answerID - id of the answer in database
     * @questionID - id of the question in database
     * @answerText - new text of the answer
     * @isCorrect - is this answer correct?
     * */
    @Override
    public boolean updateAnswer(int answerID, int questionID, String answerText, boolean isCorrect){

        Document doc;

        // Checking strings
        answerText = answerText.trim();
        if (answerText.isEmpty()){
            this.errorMessage = "Answer text can not be empty";
            return false;
        }


        // Reading file from disk
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Updating XML element
        try {

            NodeList nodeList = doc.getElementsByTagName("Answer");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int currElementID = Integer.parseInt(currentNode.getAttributes().getNamedItem("id").getNodeValue());
                if (currElementID == answerID){
                    currentNode.setTextContent(stringToBytes(answerText));
                    Node atrrIsCorrect = currentNode.getAttributes().getNamedItem("IsCorrect");
                    atrrIsCorrect.setTextContent(String.valueOf(isCorrect));
                }
            }

        }catch (Exception e){
            errorMessage = "Can't update XML node: " + e.getMessage();
            return false;
        }

        // Saving XML document back to disk
        try {
            saveXMLDocument(testFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Creates a new answer for question
     *
     * @questionID - id of the question where you need to create the answer
     * @answerText - text of the answer
     * @isCorrect - is this answer correct or not
     * */
    @Override
    public boolean createAnswer(int questionID, String answerText, boolean isCorrect){
        Document doc;


        // Reading xml from disk
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Creating a new answer (new XML node)
        try {

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            String xpathString = "//Question[@id=repl" + questionID +  "repl]";
            xpathString = xpathString.replaceAll("repl","\"");
            XPathExpression expr = xpath.compile(xpathString);
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);


            if (nl != null && nl.getLength() > 0){
                if (nl.item(0).getNodeType() == Element.ELEMENT_NODE){
                    Element parent = (Element) nl.item(0);

                    Element newAnswer = doc.createElement("Answer");
                    int nextID = getMaxID(testFileName, "Answer", "id") + 1;

                    newAnswer.setAttribute("id", String.valueOf(nextID));
                    newAnswer.setAttribute("IsCorrect", String.valueOf(isCorrect));
                    newAnswer.setTextContent(stringToBytes(answerText));

                    parent.appendChild(newAnswer);
                }
            }

        }catch (Exception e){
            errorMessage = "Can't create new XML node with answer: " + e.getMessage();
            return false;
        }

        // Writting file back to disk
        try {
            saveXMLDocument(testFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Deletes answer from question
     *
     * @answerID - id of the answer to be removed
     * */
    @Override
    public boolean deleteAnswer(int answerID){
        Document doc;

        // Reading file from disk
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Removing answer from XML document (removing xml node)
        try {

            NodeList nodeList = doc.getElementsByTagName("Answer");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int aID = Integer.parseInt(currentNode.getAttributes().getNamedItem("id").getNodeValue());

                if (aID == answerID){
                    currentNode.getParentNode().removeChild(currentNode);
                    break;
                }
            }


        }catch (Exception e){
            errorMessage = "Can't update XML node: " + e.getMessage();
            return false;
        }

        // Writting XMl document back to disk
        try {
            saveXMLDocument(testFileName, doc);

            // Removing all empty lines in XML document
            QuestionStorageXML.RemoveNewLines(testFileName);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Moves the test UP or DOWN (changes it's sorting order)
     *
     * @upDown - string (up/down) which shows direction for movement
     * @groupID - id of the group to be moved
     * */
    @Override
    public boolean moveTest(String upDown, int groupID){

        // Getting a list of test from XML document
        if (!getGroupsFromStorage())
            return false;

        // Sorting
        allQuestionGroups.sort(Comparator.comparing(QuestionGroup::getSortField));

        // Now we change "sort field" property of ONE test
        // i.e. pushing it up or down
        for (int i = 0; i< allQuestionGroups.size(); i++){
            QuestionGroup currentQuestionGroup = allQuestionGroups.get(i);

            if (currentQuestionGroup.getGroupID() == groupID){
                if (upDown.equals("up"))
                    currentQuestionGroup.setSortField(currentQuestionGroup.getSortField() - 150);
                else
                    currentQuestionGroup.setSortField(currentQuestionGroup.getSortField() + 150);
            }
        }

        // Sorting the list with tests. This will move desired test up or down
        allQuestionGroups.sort(Comparator.comparing(QuestionGroup::getSortField));

        // Now we make all "sort fields" smooth (100, 200, 300, 400...)
        int sort = 100;
        for (int i = 0; i< allQuestionGroups.size(); i++){
            allQuestionGroups.get(i).setSortField(sort);
            sort+= 100;
        }

        // Writting SortField values back to XML document
        Document doc;

        try {
            doc = getXMLDocument(testFileName);

            NodeList nodeList = doc.getElementsByTagName("QuestionGroup");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int currentID = Integer.parseInt(currentNode.getAttributes().getNamedItem("id").getNodeValue());
                int currentSort = 0;

                for (int j = 0; j< allQuestionGroups.size(); j++){
                    if (allQuestionGroups.get(j).getGroupID() == currentID)
                        currentSort = allQuestionGroups.get(j).getSortField();
                }

                Node attrSort = currentNode.getAttributes().getNamedItem("SortField");
                attrSort.setTextContent(String.valueOf(currentSort));
            }

            // Saving the document
            saveXMLDocument(testFileName, doc);


        }catch (Exception e){
            errorMessage = "Can't update XML document: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Removes empty lines from XML file
     *
     * @fileName - fileName where you need to remove lines
     * */
    public static void RemoveNewLines(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        while (bufferedReader.ready()){
            String currentLine = bufferedReader.readLine();
            currentLine = currentLine.replace("\n", "").replace("\r", "");

            if (currentLine.trim().length() > 3) {
                stringBuilder.append(currentLine);
                stringBuilder.append(System.getProperty("line.separator"));
            }
        }
        bufferedReader.close();

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
        bufferedWriter.write(stringBuilder.toString());
        bufferedWriter.close();
    }


    //<editor-fold desc="Local methods">

    /*
    * Creates XML document
    *
    * @fileName - name of the file, which should be read
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
    * Saves XML document back to disk
    *
    * @fileName - name of the file to read
    * @doc - XML document, which must be saved to this file
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
    * Gets maximuim ID number for selected tag.This way you can increment ID numbers
    *
    * @fileName - name of the file where to search
    * @tagName - XML tag name
    * @attributeName - XML attribute name
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
    * Gets current date as String dd.MM.yyyy
    * */
    private String TodayAs_ddMMyyyy(){

        Date date= Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return formatter.format(date);
    }


    /*
    * Converts string to date
    *
    * @input - input string, containing date DD.MM.YYYY format
    * */
    private Date DateFromString_ddMMyyyy(String input) throws ParseException {

        // https://www.tutorialspoint.com/how-to-parse-date-from-string-in-the-format-dd-mm-yyyy-to-dd-mm-yyyy-in-java

        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        //Parsing the given String to Date object
        // System.out.println("Date object value: " + date);
        return formatter.parse(input);
    }


    /*
    * Gets a list of Questions from XML node
    *
    * @currentNode - XML node for searching
    * */
    private ArrayList<Question> getQuestionsFromXML(Node currentNode) throws Exception{
        ArrayList<Question> allQuestions = new ArrayList<>();

        // Questions of the test
        NodeList xmlQuestions = ((Element)currentNode).getElementsByTagName("Question");
        for (int q=0; q<xmlQuestions.getLength(); q++){
            Question currentQuestion = new Question();
            Node xmlCurrentQuestion = xmlQuestions.item(q);

            int questionID = Integer.parseInt(
                    xmlCurrentQuestion
                    .getAttributes()
                    .getNamedItem("id")
                    .getNodeValue()
            );

            String isMultiChoice = xmlCurrentQuestion
                            .getAttributes()
                            .getNamedItem("IsMultiChoice")
                            .getNodeValue();

            String qText = ((Element)xmlCurrentQuestion).getElementsByTagName("Qtext").item(0)
                    .getTextContent();
            qText = BytesToString(qText);

            String aComment = ((Element)xmlCurrentQuestion).getElementsByTagName("AText").item(0)
                    .getTextContent();
            aComment = BytesToString(aComment);

            currentQuestion.setQuestionID(questionID);
            currentQuestion.setQuestionText(qText);
            currentQuestion.setAnswerComment(aComment);
            currentQuestion.setMultiChoice(Boolean.valueOf(isMultiChoice));


            // Answer options
            currentQuestion.setQuestionAnswers(getAnswersFromXML(xmlCurrentQuestion));

            allQuestions.add(currentQuestion);
        }

        return allQuestions;
    }


    /*
    * Gets an list of QuestionAnswers from XML node
    *
    * @questionNode - XML node to search answers
    * */
    private ArrayList<QuestionAnswer> getAnswersFromXML(Node questionNode) throws Exception {
        ArrayList<QuestionAnswer> result = new ArrayList<>();

        NodeList xmlQuestions = ((Element)questionNode).getElementsByTagName("Answer");
        for (int i = 0; i<xmlQuestions.getLength(); i++){
            Node currentAnswer = xmlQuestions.item(i);

            String strIsCorrect = currentAnswer
                    .getAttributes()
                    .getNamedItem("IsCorrect")
                    .getNodeValue();

            String strAnswerID = currentAnswer
                    .getAttributes()
                    .getNamedItem("id")
                    .getNodeValue();

            String answerText = BytesToString(currentAnswer.getTextContent());

            QuestionAnswer a = new QuestionAnswer();
            a.setAnswerID(Integer.valueOf(strAnswerID));
            a.setCorrect(Boolean.valueOf(strIsCorrect));
            a.setAnswerText(answerText);
            result.add(a);
        }
        return result;
    }


    /*
    * Converts a string into an array of symbols (like (100 117 109 109 121)
    *
    * @input - text
    * */
    private String stringToBytes(String input) throws UnsupportedEncodingException {

        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        String result = "";
        for (int i = 0; i<bytes.length; i++){
            result += bytes[i] + " ";
        }
        return result.trim();
    }


    /*
    * Converts an array of symbols (like (100 117 109 109 121) back to normal string
    *
    * @inputBytes - string, containing numbers
    * */
    private String BytesToString(String inputBytes) throws UnsupportedEncodingException{

        String[] strBytes = inputBytes.trim().split(" ");
        byte[] bytes = new byte[strBytes.length];

        for (int i = 0; i<strBytes.length; i++)
            bytes[i] = Byte.valueOf(strBytes[i]);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    //</editor-fold>

}
