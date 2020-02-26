package com.example.models.test;

import com.example.models.ITestStorage;

import java.util.ArrayList;

/*
* Модель для работы с тестами
* нужна чтобы можно было менять хранилище (TestStorage) без больших правок в коде
* сейчас это XMLTestStorage, в будущем может быть SQLStorage или OracleStorage
* */
public class TestModel {
    private ITestStorage iTestStorage;


    /*
    * Инициализация с помощью одного из хранилищ
    * */
    public TestModel(){
        this.iTestStorage = new XMLTestStorage();
    }


    /*
    * Сообщение об ошибке
    * */
    public String getErrorMessage() {
        return iTestStorage.getErrorMessage();
    }


    /*
     * Получает список тестов из хранинища данных
     * и пишет в переменную
     * */
    public boolean getTestsFromStorage(){
        return iTestStorage.getTestsFromStorage();
    }


    /*
    * Возвращаем список тестов, которые до этого записали в переменную
    * */
    public ArrayList<Test> getAllTests(){
        return iTestStorage.getAllTests();
    }


    /*
    * Возвращаем толоко один (указанный) тест
    * Сначала надо выполнить команду getAllTest чтобы хранилище получило данные
    * */
    public Test getTestByID(int id){ return iTestStorage.getTestByID(id); }

    /*
    * Создаем новый тест в хранилище
    * */
    public boolean CreateTest(String testHeader){return iTestStorage.createTest(testHeader);}


    /*
    * Сохраняем изменения в тесте
    * */
    public boolean updateTest(int testID, String testHeader, int sortField) {
        return iTestStorage.updateTest(testID, testHeader, sortField);
    }


    /*
    * Удаляем выбранный тест из базы
    * */
    public boolean deleteTest(int testID) {
        return iTestStorage.deleteTest(testID);
    }


    /*
    * Меняем сортировку у нужного теста (двигаем вверх или вниз)
    * */
    public boolean moveTest(String upDown, int testID){ return iTestStorage.moveTest(upDown, testID); }


    /*
    * Создаем новый вопрос к указанному тесту в базе
    * */
    public int createQuestion(int testID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        return iTestStorage.createQuestion(testID, questionText, isMultiChoice, answerComment);
    }


    /*
    * Обновляем вопрос в базе
    * */
    public boolean updateQuestion(int questionID,
                                  int testID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        return iTestStorage.updateQuestion(questionID, testID, questionText, isMultiChoice, answerComment);
    }

    /*
    * Удаляем вопрос из базы
    * */
    public boolean deleteQuestion(int questionID){
        return iTestStorage.deleteQuestion(questionID);
    }


    /*
    * Создаем ответ на указанный вопрос
    * */
    public boolean createAnswer(int questionID, String answerText, boolean isCorrect){
        return iTestStorage.createAnswer(questionID, answerText, isCorrect);
    }


    /*
    * Удаляем ответ на вопрос из базы
    * */
    public boolean deleteAnswer(int answerID){
        return iTestStorage.deleteAnswer(answerID);
    }

}
