package com.theironyard.controllers;

import com.theironyard.entities.Item;
import com.theironyard.entities.User;
import com.theironyard.entities.Work;
import com.theironyard.entities.Thread;
import com.theironyard.services.*;
import com.theironyard.services.UserRepository;
import com.theironyard.utilities.PasswordStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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















    @RequestMapping(path = "/account-create", method = RequestMethod.POST)
    public String createAccount(HttpSession session, String username, String password) throws Exception {
        User user = users.findByName(username);
        if (user != null) {
            throw new Exception("Username unavailable.");
        }
        else {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
            session.setAttribute("username", username);
        }
        return "redirect:/";
    }

    @RequestMapping(path = "account-update", method = RequestMethod.POST)
    public String editAccount(HttpSession session, String password, String newUsername, String newPassword) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findByName(username);
        if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password.");
        }
        user.setUsername(newUsername);
        user.setPassword(PasswordStorage.createHash(newPassword));
        users.save(user);
        return "redirect:/";
    }

    @RequestMapping(path = "account-delete", method = RequestMethod.POST)
    public String deleteAccount(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findByName(username);

        // delete all user-connected DBs
        users.delete(user.getId());
        return "redirect:/";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, String username, String password) throws Exception {
        User userFromDB = users.findByName(username);
        if (userFromDB == null) {
            return "redirect:/create-account";
        }
        else if (!PasswordStorage.verifyPassword(password, userFromDB.getPassword())) {
            throw new Exception("Wrong password.");
        }
        session.setAttribute("username", username);
        return "redirect:/";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
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
