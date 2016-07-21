package com.theironyard.controllers;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jeffryporter on 7/21/16.
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketConfigurer
{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {

        registry.addHandler(new MessageHandler(), "/messages");
    }

    class MessageHandler extends TextWebSocketHandler
    {

        private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception
        {
            sessions.add(session);
        }

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage msg)
        {
            //goes to all sessions
            for(WebSocketSession s : sessions)
            {
                try
                {
                    s.sendMessage(msg);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
