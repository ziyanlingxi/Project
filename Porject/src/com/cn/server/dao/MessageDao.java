package com.cn.server.dao;

import com.cn.server.domain.Message;
import com.mysql.cj.xdevapi.Collection;
import com.mysql.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageDao {
    static{
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch(ClassNotFoundException e){
            throw new RuntimeException(e);
        }

    }
    public void saveMessage(Message msg) {

        // TODO: 保存消息到数据库
        String url="jdbc:mysql://localhost:3306/实训";
        String user="root";
        String password="tan511322";
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            String insertSQL="insert into message(from_user,to_user,message) values(?,?,?)";
            PreparedStatement preparedStatement=connection.prepareStatement(insertSQL);
            preparedStatement.setString(1,msg.getFrom());
            preparedStatement.setString(2,msg.getTo());
            preparedStatement.setString(3,msg.getMessage());
            boolean execute = preparedStatement.execute();
            if(execute){
                System.out.println("保存成功");
            }else{
                System.out.println("保存失败");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
