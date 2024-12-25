package com.cn.server.service;


import com.cn.server.handler.ServerHandler;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 在线状态服务
 * @author Loong
 * @email 327395128@qq.com
 * @date: 2024/12/7
 */
public class OnlineStatusService {
    //用户在线状态 key:周杰伦 [在线] value: socket
    private static final int TIME_OUT=100000;
    private static final ConcurrentHashMap<String, Socket> onlineMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,Long>lastHeartbeatMap = new ConcurrentHashMap<>();
    static {
        new Thread(()->{
            while(true){
                lastHeartbeatMap.entrySet().forEach(e->{
                    Long lastUpdateTime=e.getValue();
                    String username=e.getKey();
                    if(System.currentTimeMillis()-lastUpdateTime>TIME_OUT){
                        System.out.println("用户"+username+"心跳超时，已下线");
                        onlineMap.remove(username);
                        lastHeartbeatMap.remove(username);
                        ServerHandler.sendUpdateFriendListMessage();
                    }
                });
            }
        }).start();
    }
    public static void updateOnlineStatus(String username, Socket socket) {
        onlineMap.put(username, socket);
        lastHeartbeatMap.put(username,System.currentTimeMillis());
    }

    public static void updateLastHeartbeatTime(String username) {
        lastHeartbeatMap.put(username,System.currentTimeMillis());
    }
    public static String getFriendNameList() {
        return onlineMap.keySet().stream().collect(Collectors.joining(","));
    }

    public static Collection<Socket> getOnlineSocketMap() {
        return onlineMap.values();
    }

    public static Socket getSocketByUsername(String to) {
        return onlineMap.get(to);
    }

    public static void offline(String username) {
        onlineMap.remove(username);
    }
}
