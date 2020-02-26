package com.example.models.history;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/*
* Один тест из таблицы с историей (содержит в себе множество строк)
* */
public class HistoryHeader {
    private int headerID;
    private Date createdDate;
    private boolean isFinished;
    private ArrayList<HistoryRow> historyRows;
    private String headerText;


    /*
    * Конструктор (параметры по умолчанию)
    * */
    public HistoryHeader(){
        this.historyRows = new ArrayList<>();
        this.createdDate = Calendar.getInstance().getTime();
        this.isFinished = false;
    }

    public int getHeaderID() {
        return headerID;
    }

    public void setHeaderID(int headerID) {
        this.headerID = headerID;
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

    public ArrayList<HistoryRow> getHistoryRows() {
        return historyRows;
    }

    public void setHistoryRows(ArrayList<HistoryRow> historyRows) {
        this.historyRows = historyRows;
    }

    public String getHeaderText() {return headerText;}

    public void setHeaderText(String headerText) {this.headerText = headerText;}


    /*
    * Количество НЕ отвеченных вопросов
    * */
    public int GetQuantityUnansweredQuestions(){
        int qty = 0;
        for (int i = 0; i<historyRows.size(); i++){
            if (historyRows.get(i).isQuestionAnswered() == false)
                qty++;
        }
        return qty;
    }

    /*
    * Количество отвеченных вопросов
    * */
    public int GetQuantityAnswerredQuestions(){
        return historyRows.size() - GetQuantityUnansweredQuestions();
    }


    /*
    * Количество правильно отвеченных вопросов
    * */
    public int GetQuantityCorrectAnswers(){
        int result = 0;

        for (int i = 0; i<historyRows.size(); i++){
            if (historyRows.get(i).isAnswerResult() == true)
                result++;
        }

        return result;
    }


    /*
    * Процент правильно отвеченных вопросов
    * */
    public int GetCorrectAnswersPercent(){
        double ttl = historyRows.size();
        double correct = GetQuantityCorrectAnswers();

        return (int)((correct / ttl)*100);
    }


    /*
    * Выдаем случайный неотвеченный вопрос из базы
    * (нужно для тестирования)
    * */
    public HistoryRow GetRandomUnansweredQuestion(){

        if (historyRows.size() == 0)
            return null;

        ArrayList<HistoryRow> rows = new ArrayList<>();
        for (int i = 0; i<historyRows.size(); i++){
            HistoryRow currentRow = historyRows.get(i);
            if (currentRow.isQuestionAnswered() == false)
                rows.add(currentRow);
        }

        if (rows.size() == 0)
            return null;

        // Случайный вопрос
        Random random = new Random();
        HistoryRow result = rows.get(random.nextInt(rows.size()));

        return result;
    }

}
