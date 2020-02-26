package com.example.models.history;

import com.example.models.IHistoryStorage;

import java.util.ArrayList;

public class HistoryModel {

    private IHistoryStorage iHistoryStorage;


    /*
    * Конструктор
    * */
    public HistoryModel(){
        // Инициализация с помощью одного из хранилищ
        this.iHistoryStorage = new XMLHistoryStorage();
    }


    /*
    * Возвращаем сообщение об ошибке
    * */
    public String getErrorMessage(){
        return iHistoryStorage.getErrorMessage();
    }


    /*
    * Выдаем список истории
    * */
    public ArrayList<HistoryHeader> getAllTests() {
        return iHistoryStorage.getAllTests();
    }


    /*
    * Создаем новый список для тестирования из уже существующих тестов
    * @testHeader - заголовок
    * @listOfTests - список тестов в строковом формате (как оно приходит из HTML формы)
    * */
    public int SaveTestToHistory(String testHeader, String listOfTests){
        return iHistoryStorage.SaveTestToHistory(testHeader, listOfTests);
    }


    /*
    * Получаем данные из базы. Метод НЕ возвращает результат, а только получает
    * */
    public boolean GetHistoryFromStorage(){return iHistoryStorage.GetHistoryFromStorage();}


    /*
    * Чистим результаты тестирования в XML файле
    * */
    public boolean ClearTestReslts(int historyHeaderID){
        return iHistoryStorage.ClearTestReslts(historyHeaderID);
    }


    /*
    * Получаем следующий вопрос для тестирования
    * */
    public HistoryHeader GetHeaderWithQuestionsText(int historyHeaderiD){
        return iHistoryStorage.GetHeaderWithQuestionsText(historyHeaderiD);
    }


    /*
     * Ставим ответ в базе
     * */
    public boolean MakeRowAnswered(boolean answerResult, int headerID, int testID, int questionID){
        return iHistoryStorage.MakeRowAnswered(answerResult, headerID, testID, questionID);
    }

    /*
     * Удаляем заголовок теста из базы
     * */
    public boolean DeleteHistoryHeader(int headerID){
        return iHistoryStorage.DeleteHistoryHeader(headerID);
    }

}
