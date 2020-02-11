package com.example.models.test;

import com.example.models.ITestStorage;

import java.time.format.TextStyle;
import java.util.ArrayList;

/*
* Модель для работы с тестами
* нужна чтобы можно было менять хранилище (TestStorage) без больших правок в коде
* сейчас это XMLTestStorage, в будущем может быть SQLStorage или OracleStorage
* */
public class TestModel {
    private ITestStorage testStorage;


    /*
    * Инициализация с помощью одного из хранилищ
    * */
    public TestModel(){
        this.testStorage = new XMLTestStorage();
    }


    /*
    * Сообщение об ошибке
    * */
    public String getErrorMessage() {
        return testStorage.getErrorMessage();
    }


    /*
     * Получает список тестов из хранинища данных
     * и пишет в переменную
     * */
    public boolean getTestsFromStorage(){
        return testStorage.getTestsFromStorage();
    }


    /*
    * Возвращаем список тестов, которые до этого записали в переменную
    * */
    public ArrayList<Test> getAllTests(){
        return testStorage.getAllTests();
    }


    /*
    * Возвращаем толоко один (указанный) тест
    * Сначала надо выполнить команду getAllTest чтобы хранилище получило данные
    * */
    public Test getTestByID(int id){ return testStorage.getTestByID(id); }

    /*
    * Создаем новый тест в хранилище
    * */
    public boolean CreateTest(String testHeader){return testStorage.createTest(testHeader);}


    /*
    * Сохраняем изменения в тесте
    * */
    public boolean updateTest(int testID, String testHeader, int sortField) {
        return testStorage.updateTest(testID, testHeader, sortField);
    }


    /*
    * Удаляем выбранный тест из базы
    * */
    public boolean deleteTest(int testID) {
        return testStorage.deleteTest(testID);
    }


    /*
    * Меняем сортировку у нужного теста (двигаем вверх или вниз)
    * */
    public boolean moveTest(String upDown, int testID){ return testStorage.moveTest(upDown, testID); }


    /*
    * Создаем новый вопрос к указанному тесту в базе
    * */
    public int createQuestion(int testID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        return testStorage.createQuestion(testID, questionText, isMultiChoice, answerComment);
    }


    /*
    * Обновляем вопрос в базе
    * */
    public boolean updateQuestion(int questionID,
                                  int testID,
                                  String questionText,
                                  boolean isMultiChoice,
                                  String answerComment){
        return testStorage.updateQuestion(questionID, testID, questionText, isMultiChoice, answerComment);
    }

    /*
    * Удаляем вопрос из базы
    * */
    public boolean deleteQuestion(int questionID){
        return testStorage.deleteQuestion(questionID);
    }


    /*
    * Создаем ответ на указанный вопрос
    * */
    public boolean createAnswer(int questionID, String answerText, boolean isCorrect){
        return testStorage.createAnswer(questionID, answerText, isCorrect);
    }


    /*
    * Удаляем ответ на вопрос из базы
    * */
    public boolean deleteAnswer(int answerID){
        return testStorage.deleteAnswer(answerID);
    }

}
