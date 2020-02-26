package com.example.models.history;


import com.example.models.test.Test;
import com.example.models.test.TestQuestion;
import com.example.models.test.XMLTestStorage;
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
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class XMLHistoryStorage implements com.example.models.IHistoryStorage {

    private ArrayList<HistoryHeader> allTests;
    private String errorMessage;
    private String historyFileName = "src/main/resources/XMLfiles/History.xml";


    // Выдаем сообщение об ошибке
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }


    // Получаем сообщение об ошибке
    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    // Выдаем список истории
    @Override
    public ArrayList<HistoryHeader> getAllTests() {
        return allTests;
    }


    // Получаем список истории
    @Override
    public void setAllTests(ArrayList<HistoryHeader> allTests) {
        this.allTests = allTests;
    }


    /*
    * Сохраняем новый тест в XML таблицу
    * @testHeader - заголовок
    * @listOfTests - список вопросов теста, как они получены из HTML формы (1,3;1,5;1,8;2,10;2,14...)
    * */
    @Override
    public int SaveTestToHistory(String testHeader, String listOfTests){

        Document doc;
        int historyHeaderId = -1;
        HistoryHeader historyHeader = new HistoryHeader();
        ArrayList<HistoryRow> rows = new ArrayList<>();

        // Читаем XML с диска
        try {
            doc = getXMLDocument(historyFileName);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return -1;
        }


        // Создаем заголовок и строки тестирования (классы)
        try{
            // Ищем очередной ID заголовка
            historyHeaderId = getMaxID(historyFileName, "Header", "id") + 1;
            historyHeader.setHeaderID(historyHeaderId);

            // Разбивка на куски [TestID][QuestionID]
            String[] allRecords = listOfTests.split(";");

            // Читаем все ID вопросов
            for (int i = 0; i<allRecords.length; i++){

                String[] currentRec = allRecords[i].split(",");
                int testID = Integer.valueOf(currentRec[0]);
                int questionID = Integer.valueOf(currentRec[1]);

                HistoryRow currentHistRow = new HistoryRow();
                currentHistRow.setQuestionID(questionID);
                currentHistRow.setTestID(testID);
                rows.add(currentHistRow);
            }
            historyHeader.setHistoryRows(rows);

        }
        catch (Exception e){
            errorMessage = "Can not get list of questions from HTML form";
            return -1;
        }


        // Создаем заголовок и строки тестирования (XML запись)
        try {
            Element rootElement = doc.getDocumentElement();

            // Свойства заголовка
            Element element = doc.createElement("Header");
            element.setAttribute("id", String.valueOf(historyHeader.getHeaderID()));
            element.setAttribute("CreatedDate", TodayAs_ddMMyyyy());

            // Текст заголовка
            Element element_header = doc.createElement("text");
            element_header.setTextContent(stringToBytes(testHeader));
            element.appendChild(element_header);


            // Вопросы заголовка
            for (int i = 0; i<historyHeader.getHistoryRows().size(); i++){
                HistoryRow currentRow = historyHeader.getHistoryRows().get(i);

                Element xmlHistoryRow = doc.createElement("row");
                xmlHistoryRow.setAttribute("headerID", String.valueOf(historyHeader.getHeaderID()));
                xmlHistoryRow.setAttribute("questionID", String.valueOf(currentRow.getQuestionID()));
                xmlHistoryRow.setAttribute("testID", String.valueOf(currentRow.getTestID()));
                xmlHistoryRow.setAttribute("questionAnswered", String.valueOf(currentRow.isQuestionAnswered()));
                xmlHistoryRow.setAttribute("answerResult", String.valueOf(currentRow.isAnswerResult()));

                element.appendChild(xmlHistoryRow);
            }

            // Добавляем текущий заголовок в XML документ
            rootElement.appendChild(element);

        }catch (Exception e){
            errorMessage = "Can't create new xml node: " + e.getMessage();
            return -1;

        }


        // Пишем XML файл обратно на диск
        try {
            saveXMLDocument(historyFileName, doc);
        }catch (Exception e){
            errorMessage = "Can't write XML file with new test to disk: " + e.getMessage();
            return -1;
        }

        return historyHeaderId;
    }


    /*
     * Получаем список (история тестирования) из XML в классы
     * Метод заполняет переменную allTests
     * */
    @Override
    public boolean GetHistoryFromStorage(){

        // Зачистка переменных
        errorMessage = "";
        allTests = new ArrayList<>();

        try {
            Document doc = getXMLDocument(historyFileName);

            // Бежим по каждому тесту в истории
            NodeList nodeList = doc.getElementsByTagName("Header");
            for (int i = 0; i<nodeList.getLength(); i++){

                // Текущий XML узел и экземпляр заголовка
                Node xmlHeaderNode = nodeList.item(i);
                HistoryHeader historyHeader = new HistoryHeader();

                // Параметры заголовка
                int id = Integer.parseInt(xmlHeaderNode
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());
                historyHeader.setHeaderID(id);

                String dateInString = xmlHeaderNode.getAttributes().getNamedItem("CreatedDate").getNodeValue();
                historyHeader.setCreatedDate(DateFromString_ddMMyyyy(dateInString));

                String hText = ((Element)xmlHeaderNode).getElementsByTagName("text").item(0)
                        .getTextContent();
                hText = BytesToString(hText);
                historyHeader.setHeaderText(hText);

                // Вопросы заголовка
                ArrayList<HistoryRow> rows = new ArrayList<>();
                NodeList xmlQuestions = ((Element)xmlHeaderNode).getElementsByTagName("row");
                for (int q = 0; q<xmlQuestions.getLength(); q++) {

                    HistoryRow historyRow = new HistoryRow();
                    Node xmlCurrentQuestion = xmlQuestions.item(q);
                    historyRow.setHeaderID(historyHeader.getHeaderID());

                    int tID = Integer.parseInt(
                            xmlCurrentQuestion
                                    .getAttributes()
                                    .getNamedItem("testID")
                                    .getNodeValue());
                    historyRow.setTestID(tID);

                    int qID = Integer.parseInt(
                            xmlCurrentQuestion
                                    .getAttributes()
                                    .getNamedItem("questionID")
                                    .getNodeValue());
                    historyRow.setQuestionID(qID);

                    boolean questionAnswered = Boolean.parseBoolean(
                            xmlCurrentQuestion
                            .getAttributes()
                            .getNamedItem("questionAnswered")
                            .getNodeValue());
                    historyRow.setQuestionAnswered(questionAnswered);

                    boolean answerResult = Boolean.parseBoolean(
                            xmlCurrentQuestion
                                    .getAttributes()
                                    .getNamedItem("answerResult")
                                    .getNodeValue());
                    historyRow.setAnswerResult(answerResult);
                    rows.add(historyRow);

                }
                historyHeader.setHistoryRows(rows);

                allTests.add(historyHeader);
            }

        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }
        return true;
    }


    /*
    * Чистим результаты теста в истории (подготовка перед самым началом тестирования)
    * */
    @Override
    public boolean ClearTestReslts(int historyHeaderID){

        // Зачистка сообщения об ошибке
        errorMessage = "";

        try {
            Document doc = getXMLDocument(historyFileName);

            // Бежим по каждому тесту в истории
            NodeList nodeList = doc.getElementsByTagName("Header");
            for (int i = 0; i<nodeList.getLength(); i++){

                // Текущий XML узел
                Node xmlHeaderNode = nodeList.item(i);

                // ID заголовка
                int id = Integer.parseInt(xmlHeaderNode
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());

                // Если это НЕ нужный элемент, то ищем дальше
                if (id != historyHeaderID)
                    continue;

                // В противном случае чистим результаты
                NodeList xmlQuestions = ((Element)xmlHeaderNode).getElementsByTagName("row");
                for (int q = 0; q<xmlQuestions.getLength(); q++) {

                    Node xmlCurrentQuestion = xmlQuestions.item(q);

                    // Макер что уже отвечен
                    Node questionAnswered = xmlCurrentQuestion.getAttributes().getNamedItem("questionAnswered");
                    questionAnswered.setTextContent("false");

                    // Маркер что отвечен правильно
                    Node answerResult = xmlCurrentQuestion.getAttributes().getNamedItem("answerResult");
                    answerResult.setTextContent("false");
                }
            }

            // Сохраняем документ
            saveXMLDocument(historyFileName, doc);

        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }
        return true;
    }


    /*
    * Получаем вопрос для тестирования
    * */
    @Override
    public HistoryHeader GetHeaderByID(int historyHeaderID){
        errorMessage = "";

        // Экземпляр истории из базы
        HistoryHeader historyHeader = null;

        // Получаем всю историю
        if (GetHistoryFromStorage() == false)
            return null;

        // Ищем именно нужный заголовок
        for (int i = 0; i<allTests.size(); i++){
            if (allTests.get(i).getHeaderID() == historyHeaderID)
                historyHeader = allTests.get(i);
        }
        if (historyHeader == null) {
            errorMessage = "History records were not found in database";
            return null;
        }

        return historyHeader;
    }


    /*
     * Получаем следующий вопрос для тестирования из базы
     * к каждой строке истории будет "приделан" реальный вопрос, чтобы можно было взять текст
     * */
    @Override
    public HistoryHeader GetHeaderWithQuestionsText(int historyHeaderID){

        // Переменные
        errorMessage = "";

        // Экземпляр истории из базы
        HistoryHeader historyHeader = GetHeaderByID(historyHeaderID);
        if (historyHeader == null)
            return null;

        // Получаем список вопросов из базы
        XMLTestStorage testStorage = new XMLTestStorage();
        if (testStorage.getTestsFromStorage() == false) {
            errorMessage = testStorage.getErrorMessage();
            return null;
        }

        // Бежим по каждой строке в истории
        for (int histRowID = 0; histRowID < historyHeader.getHistoryRows().size(); histRowID ++){
            HistoryRow currentRow = historyHeader.getHistoryRows().get(histRowID);

            // Бежим по каждому тесту в базе
            for (int testID = 0; testID < testStorage.getAllTests().size(); testID ++){
                Test test = testStorage.getAllTests().get(testID);

                // И внутри теста по каждому вопросу
                for (int questionID = 0; questionID<test.getQuestions().size(); questionID ++){
                    TestQuestion question = test.getQuestions().get(questionID);
                    if (question.getQuestionID() == currentRow.getQuestionID()){
                        currentRow.setQuestion(question);
                        break;
                    }
                }
            }
        }

        return historyHeader;
    }


    /*
    * Ставим ответ в базе
    * */
    @Override
    public boolean MakeRowAnswered(boolean answerResult, int headerID, int testID, int questionID){

        // Зачистка сообщения об ошибке
        errorMessage = "";

        try {
            Document doc = getXMLDocument(historyFileName);

            // Бежим по каждому тесту в истории
            NodeList nodeList = doc.getElementsByTagName("Header");
            for (int i = 0; i<nodeList.getLength(); i++){

                // Текущий XML узел
                Node xmlHeaderNode = nodeList.item(i);

                // ID заголовка
                int id = Integer.parseInt(xmlHeaderNode
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());

                // Если это НЕ нужный элемент, то ищем дальше
                if (id != headerID)
                    continue;

                // В противном случае бежим по каждой строке
                NodeList xmlQuestions = ((Element)xmlHeaderNode).getElementsByTagName("row");
                for (int q = 0; q<xmlQuestions.getLength(); q++) {

                    Node xmlCurrentRow = xmlQuestions.item(q);

                    int tID = Integer.parseInt(
                            xmlCurrentRow
                                    .getAttributes()
                                    .getNamedItem("testID")
                                    .getNodeValue()
                    );

                    int qID = Integer.parseInt(
                            xmlCurrentRow
                            .getAttributes()
                            .getNamedItem("questionID")
                            .getNodeValue()
                    );

                    if (qID == questionID && tID == testID){

                        // Макер что уже отвечен
                        Node questionAnswered = xmlCurrentRow.getAttributes().getNamedItem("questionAnswered");
                        questionAnswered.setTextContent("true");

                        // Маркер КАК отвечен
                        Node aResult = xmlCurrentRow.getAttributes().getNamedItem("answerResult");
                        aResult.setTextContent(String.valueOf(answerResult));
                    }
                }
            }

            // Сохраняем документ
            saveXMLDocument(historyFileName, doc);

        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }
        return true;
    }


    /*
    * Удаляем заголовок теста из базы
    * */
    @Override
    public boolean DeleteHistoryHeader(int headerID){

        // Зачистка переменных
        errorMessage = "";
        allTests = new ArrayList<>();

        try {
            Document doc = getXMLDocument(historyFileName);

            // Бежим по каждому тесту в истории
            NodeList nodeList = doc.getElementsByTagName("Header");
            for (int i = 0; i<nodeList.getLength(); i++){

                // Текущий XML узел и экземпляр заголовка
                Node xmlHeaderNode = nodeList.item(i);

                // Параметры заголовка
                int id = Integer.parseInt(xmlHeaderNode
                        .getAttributes()
                        .getNamedItem("id")
                        .getNodeValue());

                if (id == headerID)
                    xmlHeaderNode.getParentNode().removeChild(xmlHeaderNode);

            }

            // Сохраняем документ
            saveXMLDocument(historyFileName, doc);
        }
        catch (Exception e){
            errorMessage = "Can't read XML file from disk: " + e.getMessage();
            return false;
        }
        return true;
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
     * Преобразуем массив символов (100 117 109 109 121) в нормальную строку
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
