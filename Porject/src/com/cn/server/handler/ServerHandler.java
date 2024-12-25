package com.cn.server.handler;


import com.cn.common.MessageUtils;
import com.cn.server.ChatServer;
import com.cn.server.dao.MessageDao;
import com.cn.server.dao.UserDao;
import com.cn.server.domain.Message;
import com.cn.server.domain.User;
import com.cn.server.service.OnlineStatusService;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

/**
 * 服务器处理器，用于处理客户端消息
 * @author Loong
 * @email 327395128@qq.com
 * @date: 2024/12/6
 */
public class ServerHandler extends Thread {
    private final Socket socket;
    private String serverIp;
    private String clientIp;
    private int clientPort;
    private boolean isRunning=true;
    private MessageDao messageDao=new MessageDao();
    public ServerHandler(Socket socket, String serverIp) {

        this.socket = socket;
        this.serverIp = serverIp;
        //获取客户端IP
        this.clientIp  = socket.getInetAddress().getHostAddress();
        //获取客户端端口
        this.clientPort = socket.getPort();
        setName(clientIp + ":" + clientPort + "的服务线程");
        //获取客户端端口
        System.out.println("客户端IP: " + clientIp + ", 端口: " + clientPort);
    }

    @Override
    public void run() {
        //循环不断读取客户端发送的消息
        while(isRunning){
            try {
                String message = MessageUtils.receiveMessage(socket);
                //客户端有消息内容才处理
                System.out.println("客户端[" + clientIp + ":" + clientPort + "]报文：" + message);
                //解析报文
                String[] data = message.split("/");
                String msgType = data[0];
                //根据报文类型处理
                if ("login".contains(msgType)) {
                    handleLogin(data);
                } else if ("online".contains(msgType)) {
                    handleOnline(data);
                } else if ("offline".contains(msgType)) {
                    handleOffline(data);
                } else if ("chat".contains(msgType)) {
                    handleChat(data);
                }else if("ping".contains(msgType)){
                    handlePing(data);
                }
            } catch (Exception e) {
                System.out.println("客户端[" + clientIp + ":" + clientPort + "]处理异常："+e.getMessage());
                //发生异常后，服务器不再对该客户端进行处理
                break;
            }
        }
        System.out.println("停止对客户端[" + clientIp + ":" + clientPort + "]服务");
    }

    private void handlePing(String[] data) {
        MessageUtils.sendMessage("pong",socket);
        String username=data[1];
        OnlineStatusService.updateLastHeartbeatTime(username);
    }


    /**/
    private void handleChat(String[] data) {
        String from=data[1];
        String to=data[2];
        String message=data[3];
        //将消息发给对应的客户端
        Socket socket = OnlineStatusService.getSocketByUsername(to);
        String tomessage="chat/"+from+"/"+message;
        MessageUtils.sendMessage(tomessage, socket);
        Message msg=new Message();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setMessage(message);
        messageDao.saveMessage(msg);
    }

    /**
     * 处理客户端登录请求
     * @param data 报文数据
     * @throws IOException
     */
    private void handleLogin(String[] data) throws IOException {
        System.out.println("开始处理客户端登录------------");
        //1.验证用户名和密码
        String username = data[1];
        String password = data[2];
        UserDao userDao = new UserDao();
        User user =userDao.selectByUsername(username);
        if(user == null||!user.getPassword().equals(password)){
            MessageUtils.sendMessage("fail",socket);
            return;
        }
        //用户名或密码错误
        MessageUtils.sendMessage("success",socket);

        System.out.println("客户端登录结束----------------");
    }

    /**
     * 处理客户端上线请求
     * @param data 报文数据
     * @throws IOException
     */
    private void handleOnline(String[] data) throws IOException {
        System.out.println("开始处理客户端上线-------------");

        //1.更新在线状态
        String username = data[1];

        OnlineStatusService.updateOnlineStatus(username,socket);
        //2.发送欢迎消息
        String responseBody = "welcome/"+username+"欢迎上线\n"+(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))+"成功连接服务器\n"+"服务器IP:"+serverIp+",端口:"+ ChatServer.PORT +"\n"+"服务端IP:"
                +clientIp+",端口:"+clientPort+"\n";

        MessageUtils.sendMessage(responseBody,socket);
        //3.通知其他客户端刷新好友列表
        sendUpdateFriendListMessage();
        System.out.println("客户端上线结束----------------");
    }

    /**
     * 发送欢迎消息
     */


    /**
     * 通知其他客户端刷新好友列表
     */
    public static void sendUpdateFriendListMessage() {
        System.out.println("通知其他客户端刷新好友列表-----------");

        String friendNameList=OnlineStatusService.getFriendNameList();
        String responseBody="update/"+friendNameList;
        Collection<Socket> onlineSocketMap = OnlineStatusService.getOnlineSocketMap();
        for (Socket socket : onlineSocketMap) {
            MessageUtils.sendMessage(responseBody,socket);
        }
        System.out.println("通知其他客户端刷新好友列表结束--------");
    }


    /**
     * 处理客户端下线请求
     * @param data 报文数据
     */
    private void handleOffline(String[] data) {
        System.out.println("开始处理客户端下线------------------");

        //1.更新在线状态
        String username=data[1];
        OnlineStatusService.offline(username);
        //2.推送在线

        sendUpdateFriendListMessage();
        //3.停止对该服务端的服务
        isRunning=false;

        System.out.println("处理客户端下线结束------------------");
    }

}