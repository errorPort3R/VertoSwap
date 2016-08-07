package com.theironyard.controllers;

import com.theironyard.entities.Item;
import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.entities.Work;
import com.theironyard.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.theironyard.entities.Item.Status.*;

/**
 * Created by jeffryporter on 8/6/16.
 */
@Controller
public class VSItemController
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
    //                  ITEM ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/item-create", method = RequestMethod.POST)
    public String createItem(HttpSession session, String title, String location, String description, String acceptableExchange, boolean service)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        LocalDateTime time = LocalDateTime.now();
        Item i = new Item(title, location, description, acceptableExchange, ACTIVE, time, service, user);
        items.save(i);
        session.setAttribute("username", user.getUsername());
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/view-item", method = RequestMethod.GET)
    public String getSpecificItem(HttpSession session, Model model, @RequestParam String id)
    {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        User user = users.findByUsername(username);
        Item item = items.findOne(Integer.valueOf(id));
        model.addAttribute("good", item);

        if(item.isService()) {
            model.addAttribute("service", true);
        }

        Iterable<Photo> photoIterable = photos.findByItem(item);
        ArrayList<Photo> photoArrayList = new ArrayList<>();
        for (Photo p : photoIterable) {
            photoArrayList.add(p);
            model.addAttribute("photoActive", photoArrayList.get(0));
            model.addAttribute("photos", photoArrayList.subList(1, photoArrayList.size()));
        }
        return "view-barter";
    }

    @RequestMapping(path = "/item-read", method = RequestMethod.GET)
    public Iterable<Item> getItem(HttpSession session)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Item> itemsList = items.findByUser(user);
        for(Item item : itemsList)
        {
            if(item.getTime().plusWeeks(2).compareTo(LocalDateTime.now()) > 0)
            {
                item.setStatus(INACTIVE);
                items.save(item);
            }
        }
        session.setAttribute("username", user.getUsername());
        return itemsList;
    }

    @RequestMapping(path = "/item-attach-work", method = RequestMethod.POST)
    public String attachWork(HttpSession session, Integer workId, Integer id) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        if (workId != null && id != null)
        {
            Work work = works.findOne(workId);
            Item i = items.findOne(id);
            i.setWork(work);
            items.save(i);
        }
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/item-update", method = RequestMethod.POST)
    public String updateItem(HttpSession session, int id, String title, String location, String description, String acceptableExchange, String stat, boolean service)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        LocalDateTime time = LocalDateTime.now();
        Item i = new Item(title, location, description, acceptableExchange, ACTIVE, time, service, user);
        i.setId(id);
        items.save(i);
        session.setAttribute("username", user.getUsername());
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/item-delete", method = RequestMethod.POST)
    public String deleteitem (HttpSession session, int id, HttpServletRequest request)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Item item = items.findOne(id);
        item.setStatus(DELETE);
        items.save(item);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @RequestMapping(path = "/item-archive", method = RequestMethod.POST)
    public String archiveItem(HttpSession session, int id)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        Item item = items.findOne(id);
        item.setStatus(ARCHIVE);
        items.save(item);
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/update-item", method = RequestMethod.GET)
    public String updateItem(HttpSession session, Model model, int id) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        Item item = items.findOne(id);
        model.addAttribute("title", item.getTitle());
        model.addAttribute("description", item.getDescription());
        model.addAttribute("location", item.getLocation());
        model.addAttribute("acceptableExchange", item.getAcceptableExchange());
        model.addAttribute("service", item.isService());
        model.addAttribute("id", item.getId());

        return "update-item";
    }

    @RequestMapping(path = "/archive", method = RequestMethod.GET)
    public String archive(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Iterable<Item> archivedItems = items.findByUserAndStatus(user, ARCHIVE);
        model.addAttribute("username", username);
        model.addAttribute("archived", archivedItems);
        return "archive";
    }
}
