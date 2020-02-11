package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/*
* Необходим чтобы отслеживать ошибки и выводить сообщения о них в браузер
* */
@Controller
public class ErrorController {

    @GetMapping(value = {"error"})
    public String errorMessage(ModelMap modelMap, HttpSession session){

        // Получаем сообщение об ошибке
        modelMap.addAttribute("errorMessage", session.getAttribute("errorMessage"));
        return "error";
    }

}
