package com.example.controllers;

import com.example.models.history.HistoryModel;
import com.example.models.test.TestModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;


@Controller
public class IndexController {

    TestModel myTestModel = new TestModel();
    HistoryModel myHistoryModel = new HistoryModel();


    /*
    * Главная страничка
    * */
    @GetMapping(value={"/", "index", "main", "hello"})
    public String Welcome(HttpSession session, Model model){

        // Получаем историю из базы
        if (!myHistoryModel.GetHistoryFromStorage()){
            session.setAttribute("errorMessage" , myHistoryModel.getErrorMessage());
            return "redirect:error";
        }

        // Добавляем историю работы в модель
        model.addAttribute("historyHeaders", myHistoryModel.getAllTests());

        return "index";
    }


    /*
    * Начало тестирования - создаем список вопросов, по которым будем пробегаться
    * */
    @GetMapping(value = "createTest")
    public String CreateTest(HttpSession session, Model model){

        // Получаем список тестов из базы
        if (!myTestModel.getTestsFromStorage()){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        // Добавляем все тесты в модель
        model.addAttribute("modelTests", myTestModel.getAllTests());

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
        int newHeader = myHistoryModel.SaveTestToHistory(txtHeader, txtQuestionsList);
        if (newHeader== -1){
            session.setAttribute("errorMessage" , myHistoryModel.getErrorMessage());
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
        if (myHistoryModel.DeleteHistoryHeader(Integer.valueOf(headerID)) == false){
            session.setAttribute("errorMessage" , myHistoryModel.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:index";
    }



}
