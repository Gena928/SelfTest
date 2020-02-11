package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {

    /*
    * Главная страничка
    * */
    @GetMapping(value={"/", "index", "main", "hello"})
    public String Welcome(){
        return "index";
    }

}
