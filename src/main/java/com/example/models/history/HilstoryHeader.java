package com.example.models.history;

import java.util.ArrayList;
import java.util.Date;

/*
* Один тест из таблицы с историей (содержит в себе множество строк)
* */
public class HilstoryHeader {
    private int headerID;
    private Date createdDate;
    private boolean isFinished;
    private ArrayList<HistoryRow> historyRows;

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
}
