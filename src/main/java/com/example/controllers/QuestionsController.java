package com.example.controllers;

import com.example.models.test.Test;
import com.example.models.test.TestModel;
import com.example.models.test.TestQuestion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.PublicKey;

@Controller
@RequestMapping("/questions")
public class QuestionsController {

    TestModel myTestModel = new TestModel();


    /*
    * Index - list of all questions for this test
    * */
    @GetMapping(value = {"index", "main", "all"})
    public String Index(@RequestParam(required = true, defaultValue = "0", value="testID") int testID,
                        HttpSession session, Model model){


        // Получаем список тестов из базы
        if (!myTestModel.getTestsFromStorage()){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        // Если там ничего нет по этому ID
        if (myTestModel.getTestByID(testID) == null) {
            session.setAttribute("errorMessage" , "test id was not found in database");
            return "redirect:error";
        }

        // Добавляем тест в модель
        model.addAttribute("test", myTestModel.getTestByID(testID));


        return "questions/Index";
    }


    /*
     * Add - adding question to test
     * */
    @GetMapping(value = {"add"})
    public String add(@RequestParam(required = true, defaultValue = "0", value="testID") int testID,
                       HttpSession session,
                      RedirectAttributes redirectAttributes){

        // Создаем новый вопрос для этого теста (пустой!!)
        int questionID = myTestModel.createQuestion(testID, "dummy question",
                false, "dummy answer comment");
        if (questionID == -1){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        // 4 варианта ответа
        for (int i = 0; i<4; i++){
            String answerText = "dummy answer No." + String.valueOf(i);
            Boolean isCorrect = true;
            if (i > 0)
                isCorrect = false;
            if (!myTestModel.createAnswer(questionID, answerText, isCorrect)){
                session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
                return "redirect:error";
            }
        }

        // Атрибут для переадресации
        redirectAttributes.addFlashAttribute("testID", testID);
        return "redirect:edit?testID=" + testID + "&QuestionID=" + questionID;
    }


    /*
    * Edit - edit selected question
    * */
    @GetMapping(value = {"edit"})
    public String edit(@RequestParam(required = true, defaultValue = "0", value="testID") int testID,
                      @RequestParam(required = true, defaultValue = "0", value = "QuestionID") int QuestionID,
                      HttpSession session, Model model){

        // Получаем список тестов из базы
        if (!myTestModel.getTestsFromStorage()){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        // Если там ничего нет по этому ID
        if (myTestModel.getTestByID(testID) == null) {
            session.setAttribute("errorMessage" , "test id was not found in database");
            return "redirect:error";
        }

        // Добавляем тест в модель
        Test currentTest = myTestModel.getTestByID(testID);
        model.addAttribute("test", currentTest);

        // Добавляем вопрос в модель
        TestQuestion currentQuestion = new TestQuestion();
        if (currentTest.GetQuestionByID(QuestionID) != null)
            currentQuestion = currentTest.GetQuestionByID(QuestionID);

        model.addAttribute("Question", currentQuestion);

        return "questions/edit";
    }


    /*
     * Updateheader - update question header & answer
     * */
    @PostMapping(value = {"updateheader"})
    @ResponseBody
    public ResponseEntity<String> updateheader(@RequestParam(required = true, defaultValue = "0", value="testID") int testID,
                               @RequestParam(required = true, defaultValue = "0", value = "QuestionID") int QuestionID,
                               String questionHeader,
                                String questionAnswer,
                                String inputIsMultichoice,
                                HttpSession session, Model model){
        Boolean isMultiChoice = true;
        if (inputIsMultichoice.equals("0"))
            isMultiChoice = false;

        // Получаем список тестов из базы
        if (!myTestModel.getTestsFromStorage())
            return new ResponseEntity<>(myTestModel.getErrorMessage(), HttpStatus.BAD_REQUEST);


        // Если там ничего нет по этому ID
        if (myTestModel.getTestByID(testID) == null)
            return new ResponseEntity<>("test id was not found in database", HttpStatus.BAD_REQUEST);


        // Ищем вопрос в текущем тесте
        Test currentTest = myTestModel.getTestByID(testID);
        TestQuestion currentQuestion = currentTest.GetQuestionByID(QuestionID);

        if (currentQuestion == null)
            return new ResponseEntity<>("question id was not found in database", HttpStatus.BAD_REQUEST);


        // Обновляем заголовок вопроса в базе
        if (!myTestModel.updateQuestion(QuestionID, testID,
                questionHeader, isMultiChoice,questionAnswer))
            return new ResponseEntity<>(myTestModel.getErrorMessage(), HttpStatus.BAD_REQUEST);


        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    /*
     * remove - delete question from database
     * */
    @GetMapping(value = {"delete"})
    public String delete(@RequestParam(required = true, defaultValue = "0", value="testID") int testID,
                               @RequestParam(required = true, defaultValue = "0", value = "QuestionID") int QuestionID,
                               HttpSession session, Model model){

        // Удаляем вопрос из базы
        if (!myTestModel.deleteQuestion(QuestionID)) {
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:index?testID=" + testID;
    }


    /*
     * deleteanswer - delete answer
     * */
    @PostMapping(value = {"deleteanswer"})
    public String deleteanswer(@RequestParam(required = true, defaultValue = "0", value="testID") int testID,
                               @RequestParam(required = true, defaultValue = "0", value = "QuestionID") int QuestionID,
                               @RequestParam(required = true, defaultValue = "0", value = "answerID") int answerID,
                               HttpSession session, Model model){

        // Удаляем ответ из базы
        if (!myTestModel.deleteAnswer(answerID)){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }


        return "redirect:edit?testID=" + testID + "&QuestionID=" + QuestionID;
    }

















}
