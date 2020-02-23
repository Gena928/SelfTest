package com.example.controllers;

import com.example.models.history.HistoryModel;
import com.example.models.history.HistoryRow;
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

    HistoryModel myHistoryModel = new HistoryModel();

    /*
    * Clear test results before start testing
    * */
    @GetMapping("clearresults")
    public String clearResults(
            @RequestParam(required = true, defaultValue = "0", value="id") int id,
            HttpSession session, Model model){

        // Зачищаем результаты тестирования
        if (!myHistoryModel.ClearTestReslts(id) ){
            session.setAttribute("errorMessage" , myHistoryModel.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:/test?historyHeaderId=" + id;
    }


    /*
    * Получаем следующий вопрос и продолжаем тестирование
    * */
    @GetMapping("test")
    public String test(@RequestParam(required = true, defaultValue = "0", value="historyHeaderId") int historyHeaderId,
                       HttpSession session, Model model){

        // Ищем следующий вопрос
        HistoryRow row = myHistoryModel.GetNextQuestionForTesting(historyHeaderId);
        if (row == null){
            session.setAttribute("errorMessage" , myHistoryModel.getErrorMessage());
            return "redirect:error";
        }

        // Если все вопросы закончились, надо прекратить тестирование
        if (row.getQuestionID() == -100){
            return "redirect:/index";
        }

        // Добавляем строку (текущий вопрос и его параметры) в модель
        model.addAttribute("historyRow", row);


        return "";
    }



}
