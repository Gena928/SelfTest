package com.example.models.test;


import com.example.models.ITestStorage;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
* Класс для работы для работы с тестами:
*   - получение списка из базы
*   - обновление;
*   - добавление;
* */
public class XMLTestStorage implements ITestStorage {

    private ArrayList<Test> allTests;
    private String errorMessage;
    private String testFileName = "src/main/resources/XMLfiles/Tests.xml";


    /*
    * Конструктор
    * */
    public XMLTestStorage(){

    }


    /*
    * Возвращаем список тестов
    * */
    @Override
    public ArrayList<Test> getAllTests() {
        return allTests;
    }


    /*
    * Получаем тест по его ID (сначала надо выполнить getTestsFromStorage())
    * */
    @Override
    public Test getTestByID(int id){

        for (int i = 0; i<allTests.size(); i++){
            if (allTests.get(i).getTestID() == id)
                return allTests.get(i);
        }

        return null;
    }



    /*
    * Сообщение об ошибке (если такая была)
    * */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }


    /*
    * Получает список тестов из хранинища данных
    * и пишет в переменную allTests
    * */
    @Override
    public boolean getTestsFromStorage(){

        // Зачистка переменных
        errorMessage = "";
        allTests = new ArrayList<>();

        try {
            Document doc = getXMLDocument(testFileName);
            NodeList nodeList = doc.getElementsByTagName("Test");
            for (int i = 0; i<nodeList.getLength(); i++){
                Node currentNode = nodeList.item(i);
                Test currentTest = new Test();
                int testID = Integer.parseInt(currentNode
                        .getAttributes()
                        .getNamedItem("TestID")
                        .getNodeValue());

                String dateInString = currentNode.getAttributes().getNamedItem("CreatedDate").getNodeValue();
                currentTest.setTestID(testID);
                currentTest.setTestHeader(currentNode.getAttributes().getNamedItem("TestHeader").getNodeValue());
                currentTest.setCreatedDate(DateFromString_ddMMyyyy(dateInString));

                int sortField = Integer.valueOf(currentNode.getAttributes().getNamedItem("SortField").getNodeValue());
                currentTest.setSortField(sortField);

                // Вопросы (сделан отдельный метод)
                currentTest.setQuestions(getQuestionsFromXML(currentNode));

                allTests.add(currentTest);
            }

        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
    * Обновляем тест в базе по его ID
    * */
    @Override
    public boolean updateTest(int testID, String testHeader, int sortField){

        Document doc;

        // Убираем все что не похоже на русские/латинские буквы или числа
        testHeader = testHeader.replaceAll("[^A-Za-zА-Яа-я0-9 ]", "");

        // Читаем файл с диска
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Обновляем XML узел
        try {

            NodeList nodeList = doc.getElementsByTagName("Test");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int currentID = Integer.parseInt(currentNode.getAttributes().getNamedItem("TestID").getNodeValue());

                if (currentID == testID){
                    Node attrHeader = currentNode.getAttributes().getNamedItem("TestHeader");
                    attrHeader.setTextContent(testHeader);

                    Node attrSort = currentNode.getAttributes().getNamedItem("SortField");
                    attrSort.setTextContent(String.valueOf(sortField));
                }
            }

            // Сохраняем документ
            saveXMLDocument(testFileName, doc);


        }catch (Exception e){
            errorMessage = "Can't update XML document: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
    * Создаем текст в хранилище
    * */
    @Override
    public boolean createTest(String testHeader){
        Document doc;

        // Убираем все что не похоже на русские/латинские буквы или числа
        testHeader = testHeader.replaceAll("[^A-Za-zА-Яа-я0-9 ]", "");

        // Читаем файл с диска
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Создаем новый элемент
        try {

            Element rootElement = doc.getDocumentElement();

            Element element = doc.createElement("Test");
            int nextID = getMaxID(testFileName, "Test", "TestID") + 1;
            int nextSort = getMaxID(testFileName, "Test", "SortField") + 100;

            element.setAttribute("TestID", String.valueOf(nextID));
            element.setAttribute("TestHeader", testHeader);
            element.setAttribute("SortField", String.valueOf(nextSort));
            element.setAttribute("CreatedDate", TodayAs_ddMMyyyy());
            rootElement.appendChild(element);


        }catch (Exception e){
            errorMessage = "Can't create new XML node with test name: " + e.getMessage();
            return false;
        }

        // Пишем XML файл обратно на диск
        try {
                saveXMLDocument(testFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
    * Удаляем тест из базы
    * */
    @Override
    public boolean deleteTest(int testID){

        Document doc;

        // Читаем файл с диска
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Удаляем тест
        try {

            NodeList nodeList = doc.getElementsByTagName("Test");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int currentID = Integer.parseInt(currentNode.getAttributes().getNamedItem("TestID").getNodeValue());

                if (currentID == testID)
                    currentNode.getParentNode().removeChild(currentNode);
            }

            // Сохраняем документ
            saveXMLDocument(testFileName, doc);




        }catch (Exception e){
            errorMessage = "Can't remove node from XML document: " + e.getMessage();
            return false;
        }

        // Пишем XML файл обратно на диск
        try {
            saveXMLDocument(testFileName, doc);

            // Удаляем пустые строки
            XMLTestStorage.RemoveNewLines(testFileName);

        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }




        return true;
    }


    /*
    * Обновляем вопрос в базе
    * */
    @Override
    public boolean updateQuestion(int questionID,
                                  int testID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        Document doc;

        // Проверки данных
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


        // Читаем файл с диска
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Обновляем элемент в XML узле
        try {

            NodeList nodeList = doc.getElementsByTagName("Question");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int qID = Integer.parseInt(currentNode.getAttributes().getNamedItem("id").getNodeValue());
                int tID = Integer.parseInt(currentNode.getAttributes().getNamedItem("testID").getNodeValue());

                if (qID == questionID && tID == testID){

                    Node qHeader = ((Element)currentNode).getElementsByTagName("Qtext").item(0);
                    qHeader.setTextContent(stringToBytes(questionText));

                    Node qAnswer = ((Element)currentNode).getElementsByTagName("AText").item(0);
                    qAnswer.setTextContent(stringToBytes(answerComment));

                    Node attrIsMultiChoice = currentNode.getAttributes().getNamedItem("IsMultiChoice");
                    attrIsMultiChoice.setTextContent(String.valueOf(isMultiChoice));

                }
            }

            // Сохраняем документ
            saveXMLDocument(testFileName, doc);

        }catch (Exception e){
            errorMessage = "Can't update XML node: " + e.getMessage();
            return false;
        }

        // Пишем XML файл обратно на диск
        try {
            saveXMLDocument(testFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
    * Создаем новый вопрос в базе
    * Возвращаем id нового вопроса
    * */
    @Override
    public int createQuestion(int testID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        Document doc;
        int questionID = -1;


        // Читаем файл с диска
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return -1;
        }

        // Создаем новый элемент
        try {

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            String xpathString = "//Test[@TestID=repl" + String.valueOf(testID) +  "repl]";
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
                    newQuestion.setAttribute("testID", String.valueOf(testID));
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

        // Пишем XML файл обратно на диск
        try {
            saveXMLDocument(testFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return -1;
        }

        return questionID;
    }


    /*
    * Удаляем вопрос из базы
    * */
    @Override
    public boolean deleteQuestion(int questionID){
        Document doc;

        // Читаем файл с диска
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Удаляем вопрос из базы
        try {

            NodeList nodeList = doc.getElementsByTagName("Question");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int qID = Integer.parseInt(currentNode.getAttributes().getNamedItem("id").getNodeValue());
                int tID = Integer.parseInt(currentNode.getAttributes().getNamedItem("testID").getNodeValue());

                if (qID == questionID){
                    currentNode.getParentNode().removeChild(currentNode);
                    break;
                }
            }


        }catch (Exception e){
            errorMessage = "Can't update XML node: " + e.getMessage();
            return false;
        }

        // Пишем XML файл обратно на диск
        try {
            saveXMLDocument(testFileName, doc);

            // Удаляем пустые строки
            XMLTestStorage.RemoveNewLines(testFileName);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }
        return true;
    }



    /*
    * Обновляем текст ответа в базе
    * */
    @Override
    public boolean updateAnswer(int answerID, int questionID, String answerText, boolean isCorrect){
        return false;
    }


    /*
    * Создаем ответ на указанный вопрос в базе
    * */
    @Override
    public boolean createAnswer(int questionID, String answerText, boolean isCorrect){
        Document doc;


        // Читаем файл с диска
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Создаем новый элемент
        try {

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            String xpathString = "//Question[@id=repl" + String.valueOf(questionID) +  "repl]";
            xpathString = xpathString.replaceAll("repl","\"");
            XPathExpression expr = xpath.compile(xpathString);
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);


            if (nl != null && nl.getLength() > 0){
                if (nl.item(0).getNodeType() == Element.ELEMENT_NODE){
                    Element parent = (Element) nl.item(0);

                    Element newAnswer = doc.createElement("Answer");
                    int nextID = getMaxID(testFileName, "Answer", "id") + 1;

                    newAnswer.setAttribute("id", String.valueOf(nextID));
                    newAnswer.setAttribute("QuestionID", String.valueOf(questionID));
                    newAnswer.setAttribute("IsCorrect", String.valueOf(isCorrect));
                    newAnswer.setTextContent(stringToBytes(answerText));

                    parent.appendChild(newAnswer);
                }
            }

        }catch (Exception e){
            errorMessage = "Can't create new XML node with answer: " + e.getMessage();
            return false;
        }

        // Пишем XML файл обратно на диск
        try {
            saveXMLDocument(testFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
    * Удаляем ответ в базе
    * */
    @Override
    public boolean deleteAnswer(int answerID){
        Document doc;

        // Читаем файл с диска
        try {
            doc = getXMLDocument(testFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }

        // Удаляем ответ из базы
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

        // Пишем XML файл обратно на диск
        try {
            saveXMLDocument(testFileName, doc);

            // Удаляем пустые строки
            XMLTestStorage.RemoveNewLines(testFileName);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
    * Меняем порядок следования теста (переносим его вверх или вниз)
    * */
    @Override
    public boolean moveTest(String upDown, int testID){

        // Получаем список тестов из базы
        if (!getTestsFromStorage())
            return false;

        // Сортируем
        allTests.sort(Comparator.comparing(Test::getSortField));

        // меняем sortField у нужного теста
        for (int i = 0; i<allTests.size(); i++){
            Test currentTest = allTests.get(i);

            if (currentTest.getTestID() == testID){
                if (upDown.equals("up"))
                    currentTest.setSortField(currentTest.getSortField() - 150);
                else
                    currentTest.setSortField(currentTest.getSortField() + 150);
            }
        }

        // Выравниваем sortField - чтобы было 100, 200, 300, 400....
        allTests.sort(Comparator.comparing(Test::getSortField));
        int sort = 100;
        for (int i = 0; i<allTests.size(); i++){
            allTests.get(i).setSortField(sort);
            sort+= 100;
        }

        // Пишем значения sortField в документ
        Document doc;

        // Обновляем XML узел
        try {
            doc = getXMLDocument(testFileName);

            NodeList nodeList = doc.getElementsByTagName("Test");
            for (int i = 0; i<nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);

                int currentID = Integer.parseInt(currentNode.getAttributes().getNamedItem("TestID").getNodeValue());
                int currentSort = 0;

                for (int j = 0; j<allTests.size(); j++){
                    if (allTests.get(j).getTestID() == currentID)
                        currentSort = allTests.get(j).getSortField();
                }

                Node attrSort = currentNode.getAttributes().getNamedItem("SortField");
                attrSort.setTextContent(String.valueOf(currentSort));
            }

            // Сохраняем документ
            saveXMLDocument(testFileName, doc);


        }catch (Exception e){
            errorMessage = "Can't update XML document: " + e.getMessage();
            return false;
        }

        return true;
    }


    /*
     * Метод нужен для удаления пустых строк из XML файла
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


    //<editor-fold desc="Локальные методы">
    /*
    * Создаем документ и возвращаем обратно
    * Метод используется много раз, поэтому вынесен в отдельный кусок кода
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
    * Сохраняем документ обратно на диск
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
    * Метод позволяет получить максимальный ID из элементов
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
    * Получаем текущую дату в формате dd.MM.yyyy (строка)
    * */
    private String TodayAs_ddMMyyyy(){

        Date date= Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return formatter.format(date);
    }


    /*
    * Преобразуем строку в дату
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
    * Получаем вопросы из XML узла
    * */
    private ArrayList<TestQuestion> getQuestionsFromXML(Node currentNode) throws Exception{
        ArrayList<TestQuestion> allQuestions = new ArrayList<>();
        int testID = Integer.parseInt(currentNode
                .getAttributes()
                .getNamedItem("TestID")
                .getNodeValue());

        // Вопросы теста
        NodeList xmlQuestions = ((Element)currentNode).getElementsByTagName("Question");
        for (int q=0; q<xmlQuestions.getLength(); q++){
            TestQuestion currentQuestion = new TestQuestion();
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

            currentQuestion.setTestID(testID);
            currentQuestion.setQuestionID(questionID);
            currentQuestion.setQuestionText(qText);
            currentQuestion.setAnswerComment(aComment);
            currentQuestion.setMultiChoice(Boolean.valueOf(isMultiChoice));


            // Варианты ответов
            currentQuestion.setTestQuestionAnswers(getAnswersFromXML(xmlCurrentQuestion));

            allQuestions.add(currentQuestion);
        }

        return allQuestions;
    }


    /*
    * Получаем ответы на вопрос из XML узла
    * */
    private ArrayList<TestQuestionAnswer> getAnswersFromXML(Node questionNode) throws Exception {
        ArrayList<TestQuestionAnswer> result = new ArrayList<>();

        NodeList xmlQuestions = ((Element)questionNode).getElementsByTagName("Answer");
        for (int i = 0; i<xmlQuestions.getLength(); i++){
            Node currentAnswer = xmlQuestions.item(i);

            String strIsCorrect = currentAnswer
                    .getAttributes()
                    .getNamedItem("IsCorrect")
                    .getNodeValue();

            String strQuestionID = currentAnswer
                    .getAttributes()
                    .getNamedItem("QuestionID")
                    .getNodeValue();

            String strAnswerID = currentAnswer
                    .getAttributes()
                    .getNamedItem("id")
                    .getNodeValue();

            String answerText = BytesToString(currentAnswer.getTextContent());

            // Добавляем новый элемент в коллекцию
            TestQuestionAnswer a = new TestQuestionAnswer();
            a.setAnswerID(Integer.valueOf(strAnswerID));
            a.setQuestionID(Integer.valueOf(strQuestionID));
            a.setCorrect(Boolean.valueOf(strIsCorrect));
            a.setAnswerText(answerText);
            result.add(a);
        }
        return result;
    }


    /*
    * Преобразуем строку в массив символов
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
    * Преобразуем байты (100 117 109 109 121) в нормальную строку
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
