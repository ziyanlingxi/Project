package com.cn.client.handler;


import com.cn.client.frame.ChatFrame;
import com.cn.common.MessageUtils;
import java.io.IOException;
import java.net.Socket;

/**
 * 客户端和服务器的交互处理类
 *
 * @author Loong
 * @email 327395128@qq.com
 * @date: 2024/12/7
 */
public class ClientHandler extends Thread {

    //服务器套接字(用户和服务器通讯)
    private static Socket socket;
    //聊天窗口(后续要操作聊天窗口)
    private static ChatFrame chatFrame;

    /**
     * 连接服务器
     *
     * @return 是否连接成功
     */
    public static String connectServer() {
        System.out.println("连接到服务器");
        try {
            socket=new Socket("127.0.0.1",8888);
        } catch (IOException e) {
            return "连接异常";
        }
        System.out.println("连接到服务器成功");
        return "ok";
    }

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 是否登录成功
     */
    public static boolean login(String username, String password) {
        System.out.println("请求登录");
        //向服务器发送登录请求
        String requestBody="login/"+username+"/"+password;
        MessageUtils.sendMessage(requestBody,socket);
        //接收服务器返回的登录结果
        String responseBody = MessageUtils.receiveMessage(socket);

        return responseBody.equals("success");

    }


    /**
     * 处理上线业务
     */
    public static void online(String username) {
        System.out.println("处理上线");
        //1.打开聊天窗口
        //2.向服务区发送上线消息
        //3.创建一个线程处理服务器返回的消息
        chatFrame = new ChatFrame(username);
        chatFrame.setVisible(true);
        String requestBody="online/"+username;
        MessageUtils.sendMessage(requestBody,socket);
        new ClientHandler().start();
        //创建心跳
    }

    /**
     * 处理下线业务
     */
    public static void offline(String username) {
        System.out.println("处理下线");
        String requestBody="offline/"+username;

        MessageUtils.sendMessage(requestBody,socket);
        System.out.println("处理下线结束");
    }

    public static void sendChatMessage(String username, String friend, String message) {
        System.out.println("发送聊天消息");
        String requestBody="chat/"+username+"/"+friend+"/"+message;
        MessageUtils.sendMessage(requestBody,socket);
        System.out.println("发送聊天消息结束");
    }


    @Override
    public void run() {
        while (true) {
            System.out.println("客户端：等待服务器消息...");
            String message = MessageUtils.receiveMessage(socket);
            System.out.println("客户端：收到服务器消息:" + message);
            //解析报文
            String[] data = message.split("/");
            String msgType = data[0].trim();
            if ("welcome".contains(msgType)) {
                handleWelcome(data);
            } else if ("update".contains(msgType)) {
                handleUpdateFriendList(data);
            }else if("chat".contains(msgType)){
                handleChatMessage(data);
            }else if("pong".contains(msgType)){
                System.out.println("服务器响应"+message);
            }
        }
    }



    private void handleOffline(String[] data) {
        System.out.println("处理下线消息");
        chatFrame.receiveOfflineMessage(data);

    }


    private void handleChatMessage(String[] data) {

        chatFrame.receiveChatMessage(data);
    }

    private void handleUpdateFriendList(String[] data) {
        System.out.println("处理更新好友列表消息");
        chatFrame.updateFriendList(data);
    }

    private void handleWelcome(String[] data) {
        System.out.println("处理欢迎消息");
        chatFrame.showWelcomeMessage(data);
    }
}