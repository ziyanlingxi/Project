package com.cn.common;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * 为了简化消息发送和接收的过程，定义了消息的格式，方便发送和接收
 * <p>
 *   +----------------+------------------+</br>
 *   | 消息长度(4字节)  |  消息内容(N字节)   |</br>
 *   +----------------+------------------+</br>
 * </p>
 * @author Loong
 * @email 327395128@qq.com
 * @date: 2024/12/8
 */
public class MessageUtils {
    /**
     * 接收消息的方法
     * @param socket 客户端Socket对象
     * @return 接收到的消息
     */
    public static String receiveMessage(Socket socket)  {
        try {
            // 获取输入流
            InputStream inputStream = socket.getInputStream();
            // 先读取4个字节的消息长度
            byte[] lengthBytes = new byte[4];
            int size = inputStream.read(lengthBytes);
            if (size == -1) {
                //如果没有读取到数据，多半是客户端已经断开连接
                throw new RuntimeException("客户端断开连接");
            }
            int length = bytesToInt(lengthBytes);
            // 再读取指定长度的消息内容
            byte[] messageBytes = new byte[length];
            int read = inputStream.read(messageBytes);

            // 将字节数组转换为字符串
            return new String(messageBytes,0, read, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("接收消息异常");
            throw new RuntimeException(e);
        }
    }


    /**
     * 发送消息的方法
     * @param message 要发送的消息
     * @param socket 客户端Socket对象
     */
    public static void sendMessage(String message, Socket socket)  {
        try {
            // 如果消息为空，直接返回
            if (message.isEmpty()) {
                return;
            }
            // 获取消息的字节数组
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            // 获取消息的长度
            int length = messageBytes.length;
            // 创建一个包含消息长度的字节数组
            byte[] lengthBytes = intToBytes(length);

            // 获取输出流
            OutputStream outputStream = socket.getOutputStream();
            // 先发送消息长度
            outputStream.write(lengthBytes);
            // 再发送消息内容
            outputStream.write(messageBytes);
            // 刷新输出流
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("发送消息异常");
            throw new RuntimeException(e);
        }
    }


    // 将4个字节的数组转换为int
    private static int bytesToInt(byte[] bytes) {
        return (bytes[0] << 24) |
                (bytes[1] << 16) |
                (bytes[2] << 8) |
                (bytes[3] & 0xFF);
    }

    // 将int转换为4个字节的数组
    private static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }


}