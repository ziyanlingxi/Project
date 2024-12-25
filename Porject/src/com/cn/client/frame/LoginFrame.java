package com.cn.client.frame;

import com.cn.client.handler.ClientHandler;

import javax.swing.*;

/**
 * 登录界面
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    public LoginFrame() {
        // 设置窗口标题和基本属性
        setTitle("用户登录");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // 窗口居中显示

        // 创建面板
        JPanel panel = new JPanel();
        panel.setLayout(null);  // 使用绝对布局

        // 添加用户名标签和输入框
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setBounds(50, 30, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(120, 30, 150, 25);
        panel.add(usernameField);

        // 添加密码标签和输入框
        JLabel passLabel = new JLabel("密码:");
        passLabel.setBounds(50, 65, 80, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 65, 150, 25);
        panel.add(passwordField);

        // 添加登录和取消按钮
        loginButton = new JButton("登录");
        loginButton.setBounds(80, 110, 80, 25);
        panel.add(loginButton);

        cancelButton = new JButton("取消");
        cancelButton.setBounds(180, 110, 80, 25);
        panel.add(cancelButton);

        // 添加面板到窗口
        add(panel);

        // 添加按钮事件监听器
        loginButton.addActionListener(e -> handleLogin());
        cancelButton.addActionListener(e -> handleCancel());

        setVisible(true);
    }

    private void handleLogin() {
        // 判断用户名和密码是否为空
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        System.out.println("用户名：" + username + ", 密码：" + password);
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名或密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 连接服务器
        String result = ClientHandler.connectServer();
        if (!result.equals("ok")) {
            JOptionPane.showMessageDialog(this, result, "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //从服务器校验账号密码
        boolean flag = ClientHandler.login(username, password);
        if (!flag) {
            JOptionPane.showMessageDialog(this, "用户不存在！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //上线
        ClientHandler.online(username);
        // 关闭登录界面
        dispose();
    }

    private void handleCancel() {
        usernameField.setText("");
        passwordField.setText("");
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}