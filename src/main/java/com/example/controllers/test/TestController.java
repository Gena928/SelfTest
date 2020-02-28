package com.example.controllers.test;

import com.example.models.questions.QuestionStorageProxy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;


/*
* All things about creating, deleting and updating test
* testing process is not here, it's in "testing" controller
* */
@Controller
@RequestMapping("/test")
public class TestController {

    QuestionStorageProxy questionsProxy = new QuestionStorageProxy();



    /*
    * Edit - get mapping
    * */
    @GetMapping(value = {"edit"})
    public String edit(@RequestParam(required = true, defaultValue = "0", value="groupID") int groupID,
                       HttpSession session, Model model){

        // Получаем список тестов из базы
        if (!questionsProxy.getGroupsFromStorage()){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        // Если там ничего нет по этому ID
        if (questionsProxy.getGroupByID(groupID) == null) {
            return "redirect:index";
        }

        // Добавляем тест в модель
        model.addAttribute("test", questionsProxy.getGroupByID(groupID));

        return "test/edit";
    }


    /*
    * Edit - post mapping
    * */
    @PostMapping(value = {"edit"})
    public String edit(String inputHeader, int  testID, int sortID, HttpSession session){

        // Получаем список тестов из базы
        if (!questionsProxy.updateGroup(testID, inputHeader, sortID)){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:index";
    }









}
