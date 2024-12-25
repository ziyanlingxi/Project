package com.cn.server.dao;

import com.cn.server.domain.User;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户的数据访问对象
 */
public class UserDao {
    // 假设用户数据存储在内存中，这里使用ConcurrentHashMap来模拟
    private final static ConcurrentHashMap<String, User> USER_TABLE = new ConcurrentHashMap<>();
    static {
        USER_TABLE.put("a", new User("a", "a"));
        USER_TABLE.put("b", new User("b", "b"));
        USER_TABLE.put("c", new User("c", "c"));
    }
    public static User getUser(String username) {
        return USER_TABLE.get(username);
    }

    public static Set<String> getAllUser() {
        return USER_TABLE.keySet();
    }

    public User selectByUsername(String username) {

        return USER_TABLE.get(username);
    }
}