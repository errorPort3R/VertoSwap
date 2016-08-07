package com.theironyard.controllers;

import com.theironyard.entities.*;
import com.theironyard.services.*;
import com.theironyard.utilities.PasswordStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by jeffryporter on 8/6/16.
 */
@Controller
public class VSMessageController
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
    //                  MESSAGE ROUTES
    //
    //***************************************************************************************

    //reply's to a message.  no longer implemented
    @RequestMapping(path = "/message-reply", method = RequestMethod.POST)
    public String messageFromBuyer(HttpSession session, Model model, String body, int itemid, int receiverid, String conversation)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Item item = items.findOne(itemid);
        User receiver = users.findOne(receiverid);
        Messagea m = new Messagea(user, receiver, item, body, LocalDateTime.now(), conversation);
        messages.save(m);
        session.setAttribute("username", user.getUsername());
        return "redirect:/thread-read-all";
    }

    //initial message creation by customer.  creates a specific conversation key then loads the home page.
    @RequestMapping(path = "/message-to-seller", method = RequestMethod.POST)
    public String messagetoseller(HttpSession session, @RequestParam String itemId, @RequestParam String body)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Item item = items.findOne(Integer.valueOf(itemId));
        //create conversation key
        String conversation = String.format("%s_%s", itemId, user.getId());
        Messagea m = new Messagea(user, item.getUser(), item, body, LocalDateTime.now(), conversation);
        messages.save(m);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    //gets all the messages in a conversation that were saved to the database
    //loads it and displays the message-display page
    @RequestMapping(path = "/message-get-by-conversation", method = RequestMethod.GET)
    public String getMessages(HttpSession session, Model model, String conversation)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        VertoSwapController.state.setConversation(conversation);
        List<Messagea> messageList = messages.findByConversation(conversation);
        Collections.sort(messageList);
        //get variables for page

        User receiver = new User();
        int itemId = messageList.get(messageList.size()-1).getItem().getId();
        String conKey = messageList.get(messageList.size()-1).getConversation();
        if (user.getId() == messageList.get(messageList.size()-1).getItem().getUser().getId())
        {
            receiver = messageList.get(messageList.size()-1).getAuthor();
        }
        else
        {
            receiver = messageList.get(messageList.size()-1).getItem().getUser();
        }
        Collections.sort(messageList);
        for(Messagea m: messageList)
        {
            if (user.getUsername().equals(m.getAuthor().getUsername()))
            {
                m.setPossession("mine");
            }
            else
            {
                m.setPossession("theirs");
            }
        }
        session.setAttribute("username", user.getUsername());
        model.addAttribute("name", user.getUsername());
        model.addAttribute("conkey", conKey);
        model.addAttribute("itemid", itemId);
        model.addAttribute("receiverid", receiver.getId());
        model.addAttribute("messages", messageList);
        return "message-display";
    }

    //finds all conversations that the user is a part of, loads them into the message-list
    @RequestMapping(path = "/thread-read-all", method = RequestMethod.GET)
    public String getConversation(HttpSession session, Model model, ArrayList<String> conversationId)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        ArrayList<Messagea> messageList = new ArrayList();
        HashMap<String ,Messagea> mapList= new HashMap<>();
        User user = users.findByUsername(username);
        List<Messagea> messageLista = messages.findByRecipient(user);
        List<Messagea> messageListb = messages.findByAuthor(user);
        for (Messagea b: messageListb)
        {
            messageLista.add(b);
        }
        Collections.sort(messageLista);

        for(Messagea m : messageLista)
        {
            mapList.put(m.getConversation(), m);
        }
        Iterator iter = mapList.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pair = (Map.Entry)iter.next();
            messageList.add((Messagea)pair.getValue());
            iter.remove();
        }
        for (Messagea n : messageList)
        {
            if (user.getUsername().equals(n.getRecipient().getUsername()))
            {
                n.setRecipient(n.getAuthor());
            }
        }
        session.setAttribute("username", user.getUsername());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("conversations", messageList);
        return "message-list";
    }

    //message update route from original build.  not implemented or necessary
    @RequestMapping(path = "/message-update", method = RequestMethod.POST)
    public String updateMessage(HttpSession session,int id, Messagea message)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        message.setId(id);
        messages.save(message);
        session.setAttribute("username", user.getUsername());
        return "chatpage";
    }

    //message delete route from original build.  not implemented or necessary
    @RequestMapping(path = "/message-delete", method = RequestMethod.POST)
    public String deleteMessage(HttpSession session, int id)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        messages.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    //deletes a conversation and all associated messages, then reloads the page.
    @RequestMapping(path = "/conversation-delete", method = RequestMethod.POST)
    public String deleteConversation(HttpSession session, String conversation, HttpServletRequest request)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Iterable<Messagea> messageList = messages.findByConversation(conversation);
        for(Messagea m : messageList)
        {
            messages.delete(m.getId());
        }
        session.setAttribute("username", user.getUsername());

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

}
