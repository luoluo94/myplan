package com.guima.controller;

/**
 * Created by Ran on 2018/7/9.
 */
import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class WebSocketController {


    @OnOpen
    public void onOpen(Session session) {
        System.out.println("isopen");
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("isclose");
    }

    @OnMessage
    public void onMessage(String requestJson, Session session) {
        try {
            System.out.println("ismessage");
            session.getBasicRemote().sendText(requestJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
