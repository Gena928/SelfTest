package com.example.controllers.test;

import com.example.models.test.TestGroup;
import com.example.models.test.TestStorageProxy;
import com.example.models.test.Test;
import com.example.models.questions.QuestionAnswer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/*
* Testing process
* */
@Controller
@RequestMapping("/testing")
public class TestingController {

    TestStorageProxy myTestStorageProxy = new TestStorageProxy();

    /*
    * Clear test results before start testing
    * */
    @GetMapping("clearresults")
    public String clearResults(
            @RequestParam(required = true, defaultValue = "0", value="historyHeaderId") int historyHeaderId,
            HttpSession session, Model model){

        // Зачищаем результаты тестирования
        if (!myTestStorageProxy.ClearGroupResults(historyHeaderId) ){
            session.setAttribute("errorMessage" , myTestStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:process?historyHeaderId=" + historyHeaderId;
    }


    /*
     * Предварительный просмотр перед началом тестирования
     * */
    @GetMapping("preview")
    public String preview(@RequestParam(required = true, defaultValue = "0", value="historyHeaderId") int historyHeaderId,
                          HttpSession session, Model model){


        // Ищем заголовок для текущего тестирования
        TestGroup header = myTestStorageProxy.GetGroupWithQuestionText(historyHeaderId);
        if (header == null){
            session.setAttribute("errorMessage" , myTestStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        // Добавляем заголовок в аттрибуты сессии
        model.addAttribute("historyHeader", header);

        return "testing/preview";
    }



    /*
    * Получаем следующий вопрос и продолжаем тестирование
    * */
    @GetMapping("process")
    public String test(@RequestParam(required = true, defaultValue = "0", value="historyHeaderId") int historyHeaderId,
                       HttpSession session, Model model){

        // Ищем заголовок для текущего тестирования
        TestGroup header = myTestStorageProxy.GetGroupWithQuestionText(historyHeaderId);
        if (header == null){
            session.setAttribute("errorMessage" , myTestStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        // Если все вопросы закончились, надо прекратить тестирование
        if (header.GetQuantityUnansweredQuestions() == 0){
            return "redirect:/index";
        }

        // Добавляем заголовок истории
        model.addAttribute("historyHeader", header);

        // Добавляем НЕ отвеченный вопрос
        Test currentRow = header.GetRandomUnansweredQuestion();
        model.addAttribute("historyRow", currentRow);

        // Ставим ID в варианты ответа (тупо фейковые номера, чтобы можно было опознать их в HTML форме)
        for (int i = 0; i<currentRow.getQuestion().getQuestionAnswers().size(); i++){
            QuestionAnswer q = currentRow.getQuestion().getQuestionAnswers().get(i);
            q.fakeID = i;
        }

        model.addAttribute("question", currentRow.getQuestion());

        return "testing/process";
    }


    /*
    * Сохраняем результаты тестирования перед показом следующего вопроса
    * */
    @PostMapping("save")
    public String save(@RequestParam(required = true, defaultValue = "0", value="HeaderID") int HeaderID,
                       @RequestParam(required = true, defaultValue = "0", value="TestID") int TestID,
                       @RequestParam(required = true, defaultValue = "0", value="QuestionID") int QuestionID,
                       int ErrorsQty,
                             HttpSession session, Model model){

        // Результат (правильно / неправильно)
        boolean answerResult = false;
        if (ErrorsQty == 0)
            answerResult = true;


        // Ставим результат ответа на данный вопрос
        if (!myTestStorageProxy.MakeTestAnswered(answerResult, HeaderID, TestID, QuestionID) ){
            session.setAttribute("errorMessage" , myTestStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:process?historyHeaderId=" + HeaderID;
    }
}
