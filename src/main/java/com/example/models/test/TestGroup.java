package com.example.models.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/*
* Один тест из таблицы с историей (содержит в себе множество строк)
* */
public class TestGroup {
    private int groupID;
    private Date createdDate;
    private boolean isFinished;
    private ArrayList<Test> tests;
    private String headerText;


    /*
    * Конструктор (параметры по умолчанию)
    * */
    public TestGroup(){
        this.tests = new ArrayList<>();
        this.createdDate = Calendar.getInstance().getTime();
        this.isFinished = false;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public ArrayList<Test> getTests() {
        return tests;
    }

    public void setTests(ArrayList<Test> tests) {
        this.tests = tests;
    }

    public String getHeaderText() {return headerText;}

    public void setHeaderText(String headerText) {this.headerText = headerText;}


    /*
    * Количество НЕ отвеченных вопросов
    * */
    public int GetQuantityUnansweredQuestions(){
        int qty = 0;
        for (int i = 0; i< tests.size(); i++){
            if (tests.get(i).isQuestionAnswered() == false)
                qty++;
        }
        return qty;
    }

    /*
    * Количество отвеченных вопросов
    * */
    public int GetQuantityAnswerredQuestions(){
        return tests.size() - GetQuantityUnansweredQuestions();
    }


    /*
    * Количество правильно отвеченных вопросов
    * */
    public int GetQuantityCorrectAnswers(){
        int result = 0;

        for (int i = 0; i< tests.size(); i++){
            if (tests.get(i).isAnswerResult() == true)
                result++;
        }

        return result;
    }


    /*
    * Процент правильно отвеченных вопросов
    * */
    public int GetCorrectAnswersPercent(){
        double ttl = tests.size();
        double correct = GetQuantityCorrectAnswers();

        return (int)((correct / ttl)*100);
    }


    /*
    * Выдаем случайный неотвеченный вопрос из базы
    * (нужно для тестирования)
    * */
    public Test GetRandomUnansweredQuestion(){

        if (tests.size() == 0)
            return null;

        ArrayList<Test> rows = new ArrayList<>();
        for (int i = 0; i< tests.size(); i++){
            Test currentRow = tests.get(i);
            if (currentRow.isQuestionAnswered() == false)
                rows.add(currentRow);
        }

        if (rows.size() == 0)
            return null;

        // Случайный вопрос
        Random random = new Random();
        Test result = rows.get(random.nextInt(rows.size()));

        return result;
    }

}
