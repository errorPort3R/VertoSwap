package com.theironyard.controllers;

import com.theironyard.entities.Item;
import com.theironyard.entities.User;
import com.theironyard.entities.Work;
import com.theironyard.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Created by jeffryporter on 8/6/16.
 */

@Controller
public class VSWorkController
{
    @Autowired
    UserRepository users;

    @Autowired
    WorkRepository works;

    @Autowired
    ItemRepository items;

    @Autowired
    MessageaRepository messages;

    @Autowired
    PhotoRepository photos;

    //***************************************************************************************
    //
    //                   WORK ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/work-create", method = RequestMethod.POST)
    public String createWork(HttpSession session, String jobTitle, String description)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Work w = new Work(jobTitle, description, user);
        works.save(w);
        session.setAttribute("username", user.getUsername());
        return "redirect:/work-history";
    }

    @RequestMapping(path = "/work-read", method = RequestMethod.GET)
    public String getWork(HttpSession session)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Iterable<Work> workList = works.findByUser(user);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/work-update", method = RequestMethod.POST)
    public String updateWork(HttpSession session, int id, String jobTitle, String description)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Work w = new Work(jobTitle, description, user);
        w.setId(id);
        works.save(w);
        session.setAttribute("username", user.getUsername());
        return "redirect:/work-history";
    }

    @RequestMapping(path = "/work-delete", method = RequestMethod.POST)
    public String deleteWork(HttpSession session, int id)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Work work = works.findOne(id);
        Iterable<Item> removeWorksList = new ArrayList<>();
        removeWorksList = items.findByWork(work);
        for(Item i : removeWorksList)
        {
            i.setWork(null);
            items.save(i);
        }
        works.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/work-history";
    }

    @RequestMapping(path = "/update-work", method = RequestMethod.GET)
    public String updateWork(HttpSession session, Model model, int id) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        Work work = works.findOne(id);
        model.addAttribute("jobTitle", work.getJobTitle());
        model.addAttribute("description", work.getDescription());
        model.addAttribute("id", work.getId());
        return "update-work";
    }

    @RequestMapping(path = "/work-history", method = RequestMethod.GET)
    public String workHistory(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Iterable<Work> workList = works.findByUser(user);
        model.addAttribute("workList", workList);
        model.addAttribute("username", username);
        return "work-history";
    }

}
