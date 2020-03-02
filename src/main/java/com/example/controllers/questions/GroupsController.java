package com.example.controllers.questions;

import com.example.models.questions.QuestionGroup;
import com.example.models.questions.QuestionStorageProxy;
import com.example.models.test.TestGroup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Comparator;

@Controller
@RequestMapping("/questions/groups")
public class GroupsController {

    QuestionStorageProxy questionsProxy = new QuestionStorageProxy();

    /*
     *  List of all test groups
     * */
    @GetMapping(value = {"all"})
    public String all(HttpSession session, Model model){

        // List of all groups from database
        if (!questionsProxy.getGroupsFromStorage()){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        // Sorting
        questionsProxy.getAllTests().sort(Comparator.comparing(QuestionGroup::getSortField));

        // Adding to model
        model.addAttribute("questionGroups", questionsProxy.getAllTests());
        Boolean listHasValues = (questionsProxy.getAllTests().size() > 0) ? true : false;
        model.addAttribute("listHasValues", listHasValues);

        return "questions/groups/all";
    }


    /*
     * Creating a new group
     * */
    @PostMapping("create")
    public String create(String groupName, HttpSession session){

        if (!questionsProxy.CreateGroup(groupName)){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }
        return "redirect:all";
    }


    /*
     * Delete group (post)
     * */
    @PostMapping(value = {"delete"})
    public String delete(int groupID, HttpSession session){

        if (!questionsProxy.DeleteGroup(groupID)){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:all";
    }



    /*
     * Sort - change the order of group
     * */
    @GetMapping(value = {"sort"})
    public String sort(@RequestParam(required = true) String updown,
                       @RequestParam(required = true) int groupID,
                       HttpSession session){

        if (!questionsProxy.moveTest(updown, groupID)){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:all";
    }



    /*
     * Edit test group header - get mapping
     * */
    @GetMapping(value = {"edit"})
    public String edit(@RequestParam(required = true, defaultValue = "0", value="groupID") int groupID,
                             HttpSession session, Model model){

        // Getting group by it's ID
        QuestionGroup questionGroup = questionsProxy.getGroupByID(groupID);
        if (questionGroup == null){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        model.addAttribute("questionGroup", questionGroup);

        return "/questions/groups/edit";
    }


    /*
     * Edit test group header - post mapping
     * */
    @PostMapping(value = {"edit"})
    public String edit(String groupHeader, int  groupID, HttpSession session){

        // Getting group by it's ID (need sort field)
        QuestionGroup questionGroup = questionsProxy.getGroupByID(groupID);
        if (questionGroup == null){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        // updating group in database
        if (!questionsProxy.updateGroup(groupID, groupHeader, questionGroup.getSortField())){
            session.setAttribute("errorMessage" , questionsProxy.getErrorMessage());
            return "redirect:error";
        }

        return "redirect:all";
    }



}
