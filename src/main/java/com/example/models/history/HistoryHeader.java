package com.example.models.history;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
}
