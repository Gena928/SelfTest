package com.example.models;

import com.example.models.test.Test;

import java.util.ArrayList;

public interface ITestStorage {

    /*
    * Возвращаем список тестов
    * */
    ArrayList<Test> getAllTests();

    /*
    * Возвращает тест по его ID
    * */
    Test getTestByID(int id);


    /*
    * Сообщение об ошибке (если такая была)
    * */
    String getErrorMessage();

    /*
    * Получает список тестов из хранинища данных
    * */
    boolean getTestsFromStorage();


    /*
    * Обновляем тест в базе по его ID
    * */
    boolean updateTest(int testID, String testHeader, int sortField);

    /*
    * Создаем текст в хранилище
    * */
    boolean createTest(String testText);

    /*
    * Удаляем тест из базы
    * */
    boolean deleteTest(int testID);

    /*
    * Обновляем вопрос в базе
    * */
    boolean updateQuestion(int questionID,
                           int testID,
                           String questionText,
                           boolean isMultiChoice,
                           String answerComment);

    /*
    * Создаем новый вопрос в базе
    * */
    int createQuestion(int testID,
                           String questionText,
                           boolean isMultiChoice,
                           String answerComment);

    /*
    * Удаляем вопрос из базы
    * */
    boolean deleteQuestion(int questionID);

    /*
    * Обновляем текст ответа в базе
    * */
    boolean updateAnswer(int answerID, int questionID, String answerText, boolean isCorrect);

    /*
    * Создаем ответ на указанный вопрос в базе
    * */
    boolean createAnswer(int questionID, String answerText, boolean isCorrect);

    /*
    * Удаляем ответ в базе
    * */
    boolean deleteAnswer(int answerID);


    /*
    * Меняем порядок следования для одного из тестов
    * */
    boolean moveTest(String upDown, int testID);

}
