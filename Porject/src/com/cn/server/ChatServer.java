package com.cn.server;

import com.cn.server.handler.ServerHandler;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static final int PORT = 8888;
    public static void main(String[] args) {
        try{
            ServerSocket socketServer = new ServerSocket(PORT);
            String serverIp=socketServer.getInetAddress().getHostAddress();
            System.out.println("服务器启动,监听端口为"+PORT);
            while(true){
                Socket socket=socketServer.accept();
                System.out.println("客户端:"+socket.getInetAddress().getHostAddress()+"已连接");
                new ServerHandler(socket,serverIp).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
