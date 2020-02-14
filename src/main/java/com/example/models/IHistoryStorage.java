package com.example.models;

import com.example.models.history.HistoryHeader;

import java.util.ArrayList;

public interface IHistoryStorage {
    // Выдаем сообщение об ошибке
    String getErrorMessage();

    // Получаем сообщение об ошибке
    void setErrorMessage(String errorMessage);

    // Выдаем список истории
    ArrayList<HistoryHeader> getAllTests();

    // Получаем список истории
    void setAllTests(ArrayList<HistoryHeader> allTests);

    /*
    * Сохраняем новый тест в XML таблицу
    *
    * @testHeader - заголовок
    * @listOfTests - список вопросов теста, как они получены из HTML формы (1,3;1,5;1,8;2,10;2,14...)
    * */
    int SaveTestToHistory(String testHeader, String listOfTests);


    /*
    * Заполняем список данными из таблицы истории
    * */
    boolean GetHistoryFromStorage();
}
