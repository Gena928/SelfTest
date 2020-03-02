package com.example.controllers.testing;

import com.example.models.test.TestGroup;
import com.example.models.test.TestStorageProxy;
import com.example.models.test.Test;
import com.example.models.questions.QuestionAnswer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/*
* Controller is responsible for testing process
* I.e. when user created questions, then created a test and added some questins to test.
* */
@Controller
@RequestMapping("/testing")
public class TestingController {

    TestStorageProxy myTestStorageProxy = new TestStorageProxy();


    /*
    * Clear test results before test start
    * */
    @GetMapping("clearresults")
    public String clearResults(
            @RequestParam(required = true, defaultValue = "0", value="groupID") int groupID,
            HttpSession session, Model model){

        if (!myTestStorageProxy.ClearGroupResults(groupID) ){
            session.setAttribute("errorMessage" , myTestStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:process?groupID=" + groupID;
    }


    /*
     * Preview of questions before start of the testing
     * */
    @GetMapping("preview")
    public String preview(@RequestParam(required = true, defaultValue = "0", value="groupID") int groupID,
                          HttpSession session, Model model){

        // Search parameters of current test
        TestGroup testGroup = myTestStorageProxy.GetGroupWithQuestionText(groupID);
        if (testGroup == null){
            session.setAttribute("errorMessage" , myTestStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        model.addAttribute("testGroup", testGroup);
        return "testing/preview";
    }



    /*
    * Gets next question and continue testing
    * */
    @GetMapping("process")
    public String test(@RequestParam(required = true, defaultValue = "0", value="groupID") int groupID,
                       HttpSession session, Model model){

        // Searching group of questions
        TestGroup testGroup = myTestStorageProxy.GetGroupWithQuestionText(groupID);
        if (testGroup == null){
            session.setAttribute("errorMessage" , myTestStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        // How many answers are left?
        if (testGroup.GetQuantityUnansweredQuestions() == 0){
            return "redirect:/index";
        }

        // Group of questions (test)
        model.addAttribute("testGroup", testGroup);

        // Unanswered question
        Test currentTest = testGroup.GetRandomUnansweredQuestion();
        model.addAttribute("currentTest", currentTest);

        // Setting answers ID.
        // required for working in HTML form
        for (int i = 0; i<currentTest.getQuestion().getQuestionAnswers().size(); i++){
            QuestionAnswer q = currentTest.getQuestion().getQuestionAnswers().get(i);
            q.fakeID = i;
        }

        model.addAttribute("question", currentTest.getQuestion());

        return "testing/process";
    }



    /*
    * Save answer result before showing next question
    * */
    @PostMapping("save")
    public String save(@RequestParam(required = true, defaultValue = "0", value="groupID") int groupID,
                       @RequestParam(required = true, defaultValue = "0", value="questionID") int questionID,
                       int ErrorsQty,
                             HttpSession session, Model model){

        // Result (correct / incorrect)
        boolean answerResult = false;
        if (ErrorsQty == 0)
            answerResult = true;


        // Saving result
        if (!myTestStorageProxy.MakeQuestionAnswered(answerResult, groupID, questionID) ){
            session.setAttribute("errorMessage" , myTestStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:process?groupID=" + groupID;
    }

}

