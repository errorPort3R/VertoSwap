package com.theironyard.controllers;

import com.theironyard.entities.*;
import com.theironyard.entities.Thread;
import com.theironyard.services.*;
import com.theironyard.utilities.PasswordStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;


/**
 * Created by Dan on 7/19/16.
 */
@Controller
public class VertoSwapController
{

    public static String PHOTOS_DIR = "photos/";

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


    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String main(HttpSession session, Model model)
    {
        return "home";
    }

    @RequestMapping(path = "/account-create", method = RequestMethod.GET)
    public String createAccountPage(HttpSession session, Model model)
    {
        return "redirect:/";
    }





    //***************************************************************************************
    //
    //USER ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/account-create", method = RequestMethod.POST)
    public String createAccount(HttpSession session, String username, String password) throws Exception {
        User user = users.findByUsername(username);
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

    @RequestMapping(path = "/account-update", method = RequestMethod.POST)
    public String editAccount(HttpSession session, String password, String newUsername, String newPassword) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findByUsername(username);
        if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password.");
        }
        user.setUsername(newUsername);
        user.setPassword(PasswordStorage.createHash(newPassword));
        users.save(user);
        return "redirect:/";
    }

    @RequestMapping(path = "/account-delete", method = RequestMethod.POST)
    public String deleteAccount(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findByUsername(username);

        // delete all user-connected DBs
        users.delete(user.getId());
        return "redirect:/";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, String username, String password) throws Exception {
        User userFromDB = users.findByUsername(username);
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


    //***************************************************************************************
    //
    //WORK ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/work-create", method = RequestMethod.POST)
    public String createWork(HttpSession session,String job_title, String description)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Work w = new Work(job_title, description, user);
        works.save(w);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/work-read", method = RequestMethod.GET)
    public String getWork(HttpSession session)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Work> workList = works.findByUser(user);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/work-update", method = RequestMethod.POST)
    public String updateWork(HttpSession session, int id, String job_title, String description)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
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
        User user = users.findByUsername(username);
        works.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    //***************************************************************************************
    //
    //ITEM ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/item-create", method = RequestMethod.POST)
    public String createItem(HttpSession session, String title, String location, String description, String acceptableExchange, String stat, boolean service)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        LocalDateTime time = LocalDateTime.now();
        Item.Status status = Item.Status.valueOf(stat);
        Item i = new Item(title, location, description, acceptableExchange, status, time, service, user);
        items.save(i);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/item-read-specific", method = RequestMethod.GET)
    public String getSpecificItem(HttpSession session, int id)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Item item = items.findOne(id);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/item-read", method = RequestMethod.GET)
    public Iterable<Item> getItem(HttpSession session)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Item> itemsList = items.findByUser(user);
        session.setAttribute("username", user.getUsername());
        return itemsList;
    }

    @RequestMapping(path = "/item-update", method = RequestMethod.POST)
    public String updateItem(HttpSession session, int id, String title, String location, String description, String acceptableExchange, String stat, boolean service)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
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
        User user = users.findByUsername(username);
        items.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }
















































































































































































    //***************************************************************************************
    //
    //THREAD ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/thread-create", method = RequestMethod.POST)
    public String createThread(HttpSession session, User receiver, Item item)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Thread t = new Thread(user,receiver, item);
        threads.save(t);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/thread-read", method = RequestMethod.GET)
    public Iterable<Thread> getThread(HttpSession session)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Thread> threadList = threads.findByReceiver(user);
        session.setAttribute("username", user.getUsername());
        return threadList;
    }

    @RequestMapping(path = "/thread-update", method = RequestMethod.POST)
    public String updateThread(HttpSession session,int id, User receiver, Item item)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
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
        User user = users.findByUsername(username);
        threads.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";

    }





    //***************************************************************************************
    //
    //           PHOTO ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/photo-create", method = RequestMethod.POST)
    public void addPhoto(HttpSession session, MultipartFile photo, String filename, String caption, Item item, HttpServletResponse response) throws Exception
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        File dir = new File("public/" + PHOTOS_DIR);
        dir.mkdirs();

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo newPhoto = new Photo(photoFile.getName(), caption, user, item);
        photos.save(newPhoto);
        session.setAttribute("username", user.getUsername());
    }

    @RequestMapping(path = "/photo-read", method = RequestMethod.GET)
    public String getPhoto(HttpSession session, Item item)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Photo> photoList = photos.findByItem(item);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/photo-update", method = RequestMethod.POST)
    public String updatePhoto(HttpSession session, MultipartFile photo, int id, String filename, String caption, Item item, HttpServletResponse response) throws Exception
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        File dir = new File("public/" + PHOTOS_DIR);
        dir.mkdirs();

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo newPhoto = new Photo(photoFile.getName(), caption, user, item);
        newPhoto.setId(id);
        photos.save(newPhoto);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/photo-delete", method = RequestMethod.POST)
    public String deletePhoto(HttpSession session, int id)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        works.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";

    }

}
