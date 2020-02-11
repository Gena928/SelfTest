package com.example.controllers;

import com.example.models.test.Test;
import com.example.models.test.TestModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;

@Controller
@RequestMapping("/test")
public class TestController {

    TestModel myTestModel = new TestModel();


    /*
    *  Тесты, список тестов
    * */
    @GetMapping(value = {"index", "main", "all"})
    public String index(HttpSession session, Model model){

        // Получаем список тестов из базы
        if (!myTestModel.getTestsFromStorage()){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        // Сортировка перед выдачей
        myTestModel.getAllTests().sort(Comparator.comparing(Test::getSortField));


        model.addAttribute("tests", myTestModel.getAllTests());
        Boolean listHasValues = (myTestModel.getAllTests().size() > 0) ? true : false;
        model.addAttribute("listHasValues", listHasValues);


        return "test/TestIndex";
    }


    /*
    * Создаем новый тест (метод Post)
    * */
    @PostMapping("create")
    public String create(String testName, HttpSession session){

        // Создаем новый тест
        if (!myTestModel.CreateTest(testName)){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }
        return "redirect:index";
    }


    /*
    * Edit - get mapping
    * */
    @GetMapping(value = {"edit"})
    public String edit(@RequestParam(required = true, defaultValue = "0", value="id") int id,
                       HttpSession session, Model model){

        // Получаем список тестов из базы
        if (!myTestModel.getTestsFromStorage()){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        // Если там ничего нет по этому ID
        if (myTestModel.getTestByID(id) == null) {
            return "redirect:index";
        }

        // Добавляем тест в модель
        model.addAttribute("test", myTestModel.getTestByID(id));

        return "test/edit";
    }


    /*
    * Edit - post mapping
    * */
    @PostMapping(value = {"edit"})
    public String edit(String inputHeader, int  testID, int sortID, HttpSession session){

        // Получаем список тестов из базы
        if (!myTestModel.updateTest(testID, inputHeader, sortID)){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:index";
    }



    /*
     * Delete - get mapping
     * */
    @GetMapping(value = {"delete"})
    public String delete(@RequestParam(required = true, defaultValue = "0", value="id") int id,
                       HttpSession session, Model model){

        // Получаем список тестов из базы
        if (!myTestModel.getTestsFromStorage()){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        // Если там ничего нет по этому ID
        if (myTestModel.getTestByID(id) == null)
            return "redirect:index";

        // Добавляем тест в модель
        model.addAttribute("test", myTestModel.getTestByID(id));

        return "test/delete";
    }


    /*
     * Delete - post mapping
     * */
    @PostMapping(value = {"delete"})
    public String delete(int testID, HttpSession session){

        // Получаем список тестов из базы
        if (!myTestModel.deleteTest(testID)){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:index";
    }



    /*
     * Sort - change the order of selected test
     * */
    @GetMapping(value = {"sort"})
    public String sort(@RequestParam(required = true) String updown,
                       @RequestParam(required = true) int testID,
                       HttpSession session){

        // Меняем порядок у теста
        if (!myTestModel.moveTest(updown, testID)){
            session.setAttribute("errorMessage" , myTestModel.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:index";
    }


}
