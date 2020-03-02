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
    * Main page
    * */
    @GetMapping(value={"/", "index", "main", "hello"})
    public String Welcome(HttpSession session, Model model){

        // Getting groups of tests from database
        if (!testProxy.GetGroupsFromStorage()){
            session.setAttribute("errorMessage" , testProxy.getErrorMessage());
            return "redirect:error";
        }

        model.addAttribute("testGroups", testProxy.getAllTests());

        return "index";
    }




}
