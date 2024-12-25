package com.cn.client.task;

import com.cn.common.MessageUtils;

import java.net.Socket;

public class HeartbeatTask extends Thread {
    private Socket socket;
    private String username;

    public HeartbeatTask(String username, Socket socket) {
        this.username = username;
        this.socket = socket;
    }

    public void run(){
        while(true){
            MessageUtils.sendMessage("ping/"+username,socket);
            try {
                Thread.sleep(10000);
            }catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }
    }
}
