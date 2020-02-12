package com.example.controllers;

import com.example.models.test.TestModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;


@Controller
public class IndexController {

    TestModel myTestModel = new TestModel();


    /*
    * Главная страничка
    * */
    @GetMapping(value={"/", "index", "main", "hello"})
    public String Welcome(){
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





        return "index";
    }






}
