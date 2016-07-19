package com.theironyard.controllers;

import com.theironyard.entities.Item;
import com.theironyard.entities.User;
import com.theironyard.entities.Work;
import com.theironyard.entities.Thread;
import com.theironyard.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * Created by Dan on 7/19/16.
 */
@RestController
public class VertoSwapController
{

    @Autowired
    UserRepository users;

    @Autowired
    WorkRepository works;

    @Autowired
    ItemRepository items;

    @Autowired
    MessageRepository messages;

    @Autowired
    ThreadRepository threads;

    @Autowired
    PhotoRepository photos;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public void login(HttpSession session, @RequestBody User user)
    {
        //User userFromDB = users.findByName


    }























































































    @RequestMapping(path = "/work-create", method = RequestMethod.POST)
    public String createWork(HttpSession session,String job_title, String description)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Work w = new Work(job_title, description, user);
        works.save(w);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/work-read", method = RequestMethod.GET)
    public String getWork(HttpSession session)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Iterable<Work> workList = works.findByUser(user);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/work-update", method = RequestMethod.POST)
    public String updateWork(HttpSession session, int id, String job_title, String description)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Work w = new Work(job_title, description, user);
        w.setId(id);
        works.save(w);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/work-delete", method = RequestMethod.POST)
    public String deleteWork(HttpSession session, int id)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        works.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";

    }


























































//String title, String location, String description, String acceptableExchange, Status status, LocalDateTime time, boolean service, User user
    @RequestMapping(path = "/item-create", method = RequestMethod.POST)
    public String createItem(HttpSession session, String title, String location, String description, String acceptableExchange, String stat, boolean service)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        LocalDateTime time = LocalDateTime.now();
        Item.Status status = Item.Status.valueOf(stat);
        Item i = new Item(title, location, description, acceptableExchange, status, time, service, user);
        items.save(i);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/item-read", method = RequestMethod.GET)
    public String getItem(HttpSession session)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Iterable<Item> itemsList = items.findByUser(user);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/item-update", method = RequestMethod.POST)
    public String updateItem(HttpSession session, int id, String title, String location, String description, String acceptableExchange, String stat, boolean service)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        LocalDateTime time = LocalDateTime.now();
        Item.Status status = Item.Status.valueOf(stat);
        Item i = new Item(title, location, description, acceptableExchange, status, time, service, user);
        i.setId(id);
        items.save(i);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/item-delete", method = RequestMethod.POST)
    public String deleteitem (HttpSession session, int id)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        works.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";

    }























































































































































    @RequestMapping(path = "/thread-create", method = RequestMethod.POST)
    public String createThread(HttpSession session, User receiver, Item item)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Thread t = new Thread(user,receiver, item);
        threads.save(t);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/thread-read", method = RequestMethod.GET)
    public String getThread(HttpSession session)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Iterable<Item> itemsList = items.findByUser(user);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/thread-update", method = RequestMethod.POST)
    public String updateThread(HttpSession session,int id, User receiver, Item item)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Thread t = new Thread(user,receiver, item);
        t.setId(id);
        threads.save(t);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/thread-delete", method = RequestMethod.POST)
    public String deleteThread(HttpSession session, int id)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        works.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";

    }











    @RequestMapping(path = "/photo-create", method = RequestMethod.POST)
    public String createThread(HttpSession session, User receiver, Item item)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Thread t = new Thread(user,receiver, item);
        threads.save(t);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/thread-read", method = RequestMethod.GET)
    public String getThread(HttpSession session)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Iterable<Item> itemsList = items.findByUser(user);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/thread-update", method = RequestMethod.POST)
    public String updateThread(HttpSession session,int id, User receiver, Item item)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        Thread t = new Thread(user,receiver, item);
        t.setId(id);
        threads.save(t);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/thread-delete", method = RequestMethod.POST)
    public String deleteThread(HttpSession session, int id)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByName(username);
        works.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";

    }

}
