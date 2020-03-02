package com.example.controllers.test;

import com.example.models.questions.QuestionGroup;
import com.example.models.questions.QuestionStorageProxy;
import com.example.models.test.TestGroup;
import com.example.models.test.TestStorageProxy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;


/*
* All things about creating, deleting and updating test
* testing process is not here, it's in "testing" controller
* */
@Controller
@RequestMapping("/test")
public class TestController {

    QuestionStorageProxy questionStorageProxy = new QuestionStorageProxy();
    TestStorageProxy testStorageProxy = new TestStorageProxy();

    /*
     * Main page
     * */
    @GetMapping(value={"/", "index"})
    public String Welcome(HttpSession session, Model model){

        // List of test from storage
        if (!testStorageProxy.GetGroupsFromStorage()){
            session.setAttribute("errorMessage" , testStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        ArrayList<TestGroup> check = testStorageProxy.getAllTests();
        model.addAttribute("testGroups", testStorageProxy.getAllTests());
        return "/test/index";
    }


    /*
     * Create a new test - enter test header and choose questions (get mapping)
     * */
    @GetMapping(value = "create")
    public String CreateTest(HttpSession session, Model model){

        // All tests from database
        if (!questionStorageProxy.getGroupsFromStorage()){
            session.setAttribute("errorMessage" , questionStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        model.addAttribute("modelTests", questionStorageProxy.getAllTests());
        return "/test/create";
    }


    /*
     * Create a new test - enter header and choose questions (post mapping)
     * finishes creating a new test
     * */
    @PostMapping(value = "create")
    public String CreateTestPost(HttpSession session, Model model,
                                 String txtHeader,
                                 String txtQuestionsList){

        // Saving a new test in database
        int newGroupID = testStorageProxy.CreateGroup(txtHeader, txtQuestionsList);
        if (newGroupID== -1){
            session.setAttribute("errorMessage" , testStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:/testing/preview?groupID=" + newGroupID;
    }


    /*
     * Removes group from database
     * */
    @PostMapping("delete")
    public String DeleteHeader(String delGroupID, HttpSession session, Model model){

        if (testStorageProxy.DeleteTestGroup(Integer.valueOf(delGroupID)) == false){
            session.setAttribute("errorMessage" , testStorageProxy.getErrorMessage());
            return "redirect:error";
        }
        return "redirect:/test/index";
    }


    /*
    * Edit test group header (get)
    * */
    @GetMapping(value = {"editheader"})
    public String editheader(@RequestParam(required = true, defaultValue = "0", value="groupID") int groupID,
                       HttpSession session, Model model){

        // Getting group by it's ID
        TestGroup testGroup = testStorageProxy.GetGroupByID(groupID);
        if (testGroup == null){
            session.setAttribute("errorMessage" , testStorageProxy.getErrorMessage());
            return "redirect:error";
        }

        model.addAttribute("testGroup", testGroup);
        return "/test/editheader";
    }


    /*
    * Edit test group header (post)
    * */
    @PostMapping(value = {"editheader"})
    public String editheader(String groupHeader, int  groupID, HttpSession session){

        // updating group in database
        if (!testStorageProxy.UpdateGroup (groupID, groupHeader)){
            session.setAttribute("errorMessage" , testStorageProxy.getErrorMessage());
            return "redirect:error";
        }
        return "redirect:/test/index";
    }

}
