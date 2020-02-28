package com.example.controllers;

import com.example.models.questions.QuestionStorageProxy;
import com.example.models.test.TestStorageProxy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;


@Controller
public class IndexController {

    QuestionStorageProxy questionsProxy = new QuestionStorageProxy();
    TestStorageProxy testProxy = new TestStorageProxy();


    /*
    * Главная страничка
    * */
    @GetMapping(value={"/", "index", "main", "hello"})
    public String Welcome(HttpSession session, Model model){

        // Получаем историю из базы
        if (!testProxy.GetGroupsFromStorage()){
            session.setAttribute("errorMessage" , testProxy.getErrorMessage());
            return "redirect:error";
        }

        // Добавляем историю работы в модель
        model.addAttribute("testGroups", testProxy.getAllTests());

        return "index";
    }


    /*
    * Начало тестирования - создаем список вопросов, по которым будем пробегаться
    * */
    @GetMapping(value = "createTest")
    public String CreateTest(HttpSession session, Model model){

        // Получаем список тестов из базы
        if (!questionsProxy.getGroupsFromStorage()){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        // Добавляем все тесты в модель
        model.addAttribute("modelTests", questionsProxy.getAllTests());

        return "createTest";
    }


    /*
    * Заканчиваем создание списка для тестирования
    * */
    @PostMapping(value = "createTest")
    public String CreateTestPost(HttpSession session, Model model,
                                 String txtHeader,
                                 String txtQuestionsList){

        // Сохраняем список в базе
        int newHeader = testProxy.CreateGroup(txtHeader, txtQuestionsList);
        if (newHeader== -1){
            session.setAttribute("errorMessage" , testProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:/testing/preview?historyHeaderId=" + newHeader;
    }


    /*
    * Удаляем заголовок истории из базы
    * */
    @PostMapping("delete")
    public String DeleteHeader(String headerID, HttpSession session, Model model){

        // Удаляем вопрос из базы
        if (testProxy.DeleteTestGroup(Integer.valueOf(headerID)) == false){
            session.setAttribute("errorMessage" , testProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:index";
    }



}
