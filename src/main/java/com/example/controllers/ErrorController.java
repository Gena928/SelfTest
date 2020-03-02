package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/*
* Errors in browser
* */
@Controller
public class ErrorController {

    @GetMapping(value = {"error"})
    public String errorMessage(ModelMap modelMap, HttpSession session){

        // Getting error message
        modelMap.addAttribute("errorMessage", session.getAttribute("errorMessage"));
        return "error";
    }

}
