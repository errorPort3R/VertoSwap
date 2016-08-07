package com.theironyard.controllers;

import com.theironyard.entities.*;
import com.theironyard.services.*;
import com.theironyard.utilities.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static com.theironyard.entities.Item.Status.*;



/**
 * Created by Dan on 7/19/16.
 */
@Controller
public class VertoSwapController
{

    public static String PHOTOS_DIR = "photos/";
    public static final String  USER_FILE = "demovs1users.txt";
    public static final String  ITEM_FILE = "demovs1items.txt";
    public static final String  WORK_FILE = "demovs1works.txt";
    public static final String  MESSAGE_FILE = "demovs1messages.txt";
    public static final String  PHOTO_FILE = "demovs1photo.txt";
    public static State state = new State();

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



    @PostConstruct
    public void init() throws SQLException, IOException, PasswordStorage.CannotPerformOperationException
    {
//        //for H2 builds ONLY!!!!!!*****************************//
//        Server.createWebServer("-webPort", "8082").start();    //
//        //for H2 builds ONLY!!!!!!*****************************//
        if (users.count() == 0)
        {
            migrateTextFiles();
        }
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String main(HttpSession session, Model model, String search) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        Iterable<Item> searchList;
        ArrayList<Item> searchServiceArray = new ArrayList<>();
        ArrayList<Item> searchGoodArray = new ArrayList<>();
        if (search != null) {
            searchList = items.searchText(search, search, search, search);
            //searchList = items.findByTitleLikeOrLocationLikeOrDescriptionLikeOrAcceptableExchangeLike(search, search, search, search);
            for (Item i : searchList) {
                if (!i.isService() && (i.getStatus() != DELETE)) {
                    searchGoodArray.add(i);
                }
                else if (i.isService() && (i.getStatus() != DELETE)) {
                    searchServiceArray.add(i);
                }
                model.addAttribute("goods", searchGoodArray);
                model.addAttribute("services", searchServiceArray);
            }
        }
        else {
            ArrayList<Item> s = new ArrayList<>();
            Iterable<Item> servicesList = items.findByServiceTrueOrderByTimeDesc();
            for (Item i : servicesList) {
                if (i.getStatus() != DELETE) {
                    s.add(i);
                    model.addAttribute("services", s);
                }
            }
            ArrayList<Item> g = new ArrayList<>();
            Iterable<Item> goodsList = items.findByServiceFalseOrderByTimeDesc();
            for (Item i : goodsList) {
                if (i.getStatus() != DELETE) {
                    g.add(i);
                    model.addAttribute("goods", g);
                }
            }
        }
        return "home";
    }

    @RequestMapping(path = "/account-create", method = RequestMethod.GET)
    public String createAccountPage(HttpSession session, Model model)
    {
        return "account-create";
    }

    @RequestMapping(path = "/user-profile", method = RequestMethod.GET )
    public String userProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);

        Iterable<Item> itemsList = items.findByUser(user);
        for(Item item : itemsList)
        {
            if(item.getTime().plusWeeks(2).compareTo(LocalDateTime.now()) < 0)
            {
                item.setStatus(INACTIVE);
                items.save(item);
            }
        }

        Iterable<Item> activeItems = items.findByUserAndStatusOrderByTimeDesc(user, ACTIVE);
        Iterable<Item> inactiveItems = items.findByUserAndStatusOrderByTimeDesc(user, INACTIVE);
        Iterable<Messagea> messagesList = messages.findByRecipient(user);
        model.addAttribute("username", username);
        model.addAttribute("activeBarters", activeItems);
        model.addAttribute("inactiveBarters", inactiveItems);

        //  to show photo/work upload only if good/service resp. :
        for (Item i : activeItems) {
            if (i.isService()) {
                model.addAttribute("service", i);
            }
            else {
                model.addAttribute("service", i);
            }
        }

        Iterable<Work> workHistory = works.findByUser(user);
        model.addAttribute("workHistory", workHistory);
        model.addAttribute("messages", messagesList);

        return "user-profile";
    }

    //***************************************************************************************
    //
    //             USER ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/account-create", method = RequestMethod.POST)
    public String createAccount(HttpSession session, String username, String password) throws PasswordStorage.CannotPerformOperationException
    {
        User user = users.findByUsername(username);
        if (user != null) {
            System.err.printf("User Already Exists!");
            return "redirect:/";
        }
        else {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
            session.setAttribute("username", username);
            return "redirect:/user-profile";
        }
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
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/account-delete", method = RequestMethod.POST)
    public String deleteAccount(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findByUsername(username);
        items.delete(items.findByUser(user));
        works.delete(works.findByUser(user));
        photos.delete(photos.findByUser(user));
        messages.delete(messages.findByAuthor(user));
        messages.delete(messages.findByRecipient(user));
        users.delete(user.getId());
        return "redirect:/";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, String username, String password, HttpServletRequest request) throws Exception {
        User userFromDB = users.findByUsername(username);
        if (userFromDB == null)
        {
            return "redirect:/account-create";
        }
        else if (!PasswordStorage.verifyPassword(password, userFromDB.getPassword()))
        {
            throw new Exception("Wrong password.");
        }
        session.setAttribute("username", username);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    public void migrateTextFiles() throws PasswordStorage.CannotPerformOperationException, IOException
    {
        //User: username|password 1234
        Scanner fileScanner =  new Scanner(new File(USER_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            users.save(new User(fields[0],PasswordStorage.createHash(fields[1])));
        }
        //Item: title|location|description|acceptableExchange|status|service|user
        fileScanner = new Scanner(new File(ITEM_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            items.save(new Item(fields[0],fields[1], fields[2], fields[3], Item.Status.valueOf(fields[4]), LocalDateTime.now(), Boolean.valueOf(fields[5]), users.findOne(Integer.valueOf(fields[6]))));
        }
        //Work:jobTitle|description|user
        fileScanner = new Scanner(new File(WORK_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            works.save(new Work(fields[0], fields[1], users.findOne(Integer.valueOf(fields[2]))));
        }
        //Message:author|recipient|item|body|conversation
        fileScanner = new Scanner(new File(MESSAGE_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            messages.save(new Messagea(users.findOne(Integer.valueOf(fields[1])), users.findOne(Integer.valueOf(fields[0])), items.findOne(Integer.valueOf(fields[2])), fields[3], LocalDateTime.now(), fields[4]));
        }
        //Photo:filename|caption|user|item
        fileScanner = new Scanner(new File(PHOTO_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            photos.save(new Photo(fields[0],fields[1],users.findOne(Integer.valueOf(fields[2])), items.findOne(Integer.valueOf(fields[3]))));
        }
        fileScanner.close();
    }
}
