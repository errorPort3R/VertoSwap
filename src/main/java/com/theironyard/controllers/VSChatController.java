package com.theironyard.controllers;

import com.theironyard.entities.User;
import com.theironyard.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * Created by jeffryporter on 7/29/16.
 */
public class VSChatController
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
    PhotoRepository photos;


    static SimpMessagingTemplate messenger;

    @Autowired
    public VSChatController(SimpMessagingTemplate messenger, HttpSession session)
    {

        this.messenger = messenger;
    }

    @MessageMapping("/topic/chat")
    @SendTo("/chat")
    public Message sendMessage(Message msg, HttpSession session)
    {
//        String username = (String)session.getAttribute("username");
//        User user = users.findByUsername(username);
//        Message mess = new Message(new String((byte[]) msg.getPayload()));
//        messages.save(mess);
        return msg;
    }
}
