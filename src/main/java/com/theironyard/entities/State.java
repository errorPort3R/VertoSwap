package com.theironyard.entities;

/**
 * Created by jeffryporter on 8/1/16.
 */
public class State
{
    private String conversation = null;

    public State()
    {
    }

    public State(String conversation)
    {
        this.conversation = conversation;
    }

    public String getConversation()
    {
        return conversation;
    }

    public void setConversation(String conversation)
    {
        this.conversation = conversation;
    }
}
