package com.example.controllers.questions;

import com.example.models.questions.QuestionGroup;
import com.example.models.questions.QuestionStorageProxy;
import com.example.models.questions.Question;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions")
public class QuestionsController {

    QuestionStorageProxy questionsProxy = new QuestionStorageProxy();


    /*
    * Index - list of all questions in group
    * */
    @GetMapping(value = {"all", "index"})
    public String Index(@RequestParam(required = true, defaultValue = "0", value="groupID") int groupID,
                        HttpSession session, Model model){


        // List of all groups from database
        if (!questionsProxy.getGroupsFromStorage()){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        // Check if required ID exists
        if (questionsProxy.getGroupByID(groupID) == null) {
            session.setAttribute("errorMessage" , "group id was not found in database");
            return "redirect:error";
        }

        // Adding group to current model
        QuestionGroup tst = questionsProxy.getGroupByID(groupID);
        model.addAttribute("QuestionGroup", questionsProxy.getGroupByID(groupID));


        return "questions/all";
    }


    /*
     * Add - adding question to test
     * */
    @GetMapping(value = {"add"})
    public String add(@RequestParam(required = true, defaultValue = "0", value="groupID") int groupID,
                       HttpSession session,
                      RedirectAttributes redirectAttributes){

        // Creating a new question for this group (just a dummy empty question)
        int questionID = questionsProxy.createQuestion(groupID, "dummy question",
                false, "dummy answer comment");
        if (questionID == -1){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        // 4 answer options
        for (int i = 0; i<4; i++){
            String answerText = "dummy answer No." + String.valueOf(i);
            Boolean isCorrect = true;
            if (i > 0)
                isCorrect = false;
            if (!questionsProxy.createAnswer(questionID, answerText, isCorrect)){
                session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
                return "redirect:error";
            }
        }

        return "redirect:edit/?QuestionID=" + questionID;
    }


    /*
     * Edit - edit selected question
     * */
    @GetMapping(value = {"edit"})
    public String edit(@RequestParam(required = true, defaultValue = "0", value = "QuestionID") int QuestionID,
                       HttpSession session, Model model){

        // Если там ничего нет по этому ID
        QuestionGroup currentQuestionGroup = questionsProxy.getGroupByQuestionID(QuestionID);
        if (currentQuestionGroup == null) {
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        model.addAttribute("questionGroup", currentQuestionGroup);

        // Добавляем вопрос в модель
        Question currentQuestion = new Question();
        if (currentQuestionGroup.GetQuestionByID(QuestionID) != null)
            currentQuestion = currentQuestionGroup.GetQuestionByID(QuestionID);

        model.addAttribute("Question", currentQuestion);

        return "questions/edit";
    }


    /*
     * remove - delete question from database
     * */
    @GetMapping(value = {"delete"})
    public String delete(@RequestParam(required = true, defaultValue = "0", value = "QuestionID") int QuestionID,
                         HttpSession session, Model model){

        // Getting question group (required for url)
        QuestionGroup group = questionsProxy.getGroupByQuestionID(QuestionID);
        if (group == null){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        // Deleting question from database
        if (!questionsProxy.deleteQuestion(QuestionID)) {
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:all?groupID=" + group.getGroupID();
    }


    /*
     * AddAnswer - adding answer to test
     * */
    @PostMapping(value = {"addanswer"})
    public String addanswer(@RequestParam(required = true, defaultValue = "0", value="testID") int testID,
                      @RequestParam(required = true, defaultValue = "-5", value = "questionID") int questionID,
                      String inputAnswerText, String inputIsCorrect,
                      HttpSession session,
                      RedirectAttributes redirectAttributes){

        // Верный или нет ответ на вопрос
        boolean isCorrect = (inputIsCorrect.equals("1") ? true : false);

        // Создаем
        if (!questionsProxy.createAnswer(questionID, inputAnswerText, isCorrect)){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:edit?testID=" + testID + "&QuestionID=" + questionID;
    }


    /*
     * Updateheader - update question header & answer
     * */
    @PostMapping(value = {"updateheader"})
    @ResponseBody
    public ResponseEntity<String> updateheader(@RequestParam(required = true, defaultValue = "0", value = "QuestionID") int questionID,
                               String questionHeader,
                                String questionAnswer,
                                String inputIsMultichoice,
                                HttpSession session, Model model){
        Boolean isMultiChoice = true;
        if (inputIsMultichoice.equals("0"))
            isMultiChoice = false;


        // Если там ничего нет по этому ID
        QuestionGroup currentQuestionGroup = questionsProxy.getGroupByQuestionID(questionID);
        if (currentQuestionGroup == null)
            return new ResponseEntity<>("test id was not found in database", HttpStatus.BAD_REQUEST);

        // Ищем вопрос в текущем тесте
        Question currentQuestion = currentQuestionGroup.GetQuestionByID(questionID);

        if (currentQuestion == null)
            return new ResponseEntity<>("question id was not found in database", HttpStatus.BAD_REQUEST);


        // Обновляем заголовок вопроса в базе
        if (!questionsProxy.updateQuestion(questionID, questionHeader, isMultiChoice,questionAnswer))
            return new ResponseEntity<>(questionsProxy.getErrorMessage(), HttpStatus.BAD_REQUEST);


        return new ResponseEntity<>("OK", HttpStatus.OK);
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
        if (!questionsProxy.deleteAnswer(answerID)){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }


        return "redirect:edit?testID=" + testID + "&QuestionID=" + QuestionID;
    }
}
