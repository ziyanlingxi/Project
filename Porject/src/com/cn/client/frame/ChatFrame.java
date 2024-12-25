package com.cn.client.frame;

import com.cn.client.handler.ClientHandler;
import com.cn.server.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.text.*;



/**
 * 聊天窗口
 */
public class ChatFrame extends JFrame {
    private JTextPane chatArea;      // 聊天内容显示区域
    private JPanel friendPanel;        // 好友列表面板
    private JTextField inputField;    // 消息输入框
    private JButton sendButton;       // 发送消息按钮
    private JList<String> friendList; // 好友列表
    private final String username;          // 当前用户名
    /**
     * 聊天窗口构造函数
     * 初始化窗口界面，设置布局和各个组件
     */
    public ChatFrame(String username) {
        this.username = username;
        setTitle(username+"聊天窗口");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建好友列表面板
        friendPanel = createFriendListPanel();

        // 创建聊天区域面板
        JPanel chatPanel = createChatPanel();

        // 创建分隔面板
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                friendPanel,
                chatPanel
        );
        splitPane.setDividerLocation(200);

        // 添加分隔面板到主面板
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // 设置边距
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 自定义关闭窗口事件
        addWindowListener(windowsListener());
        // 设置主面板
        add(mainPanel);
        // 显示窗口在正中央
        setLocationRelativeTo(null);
    }



    /**
     * 窗口事件监听器
     * 处理窗口关闭事件，当窗口关闭时退出程序
     * @return WindowAdapter 窗口适配器对象
     */
    private  WindowAdapter windowsListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("关闭窗口");
                // 关闭窗口时退出程序
                ClientHandler.offline(username);
                System.exit(0);
            }
        };
    }

    /**
     * 创建好友列表面板
     * 包含好友列表标题和可滚动的好友列表
     * @return JPanel 好友列表面板
     */
    private JPanel createFriendListPanel() {
        JPanel friendPanel = new JPanel(new BorderLayout());

        // 创建好友列表标题
        JLabel titleLabel = new JLabel("好友列表", SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        titleLabel.setBackground(new Color(240, 240, 240));
        titleLabel.setOpaque(true);

        // 创建好友列表数据
        DefaultListModel<String> listModel = new DefaultListModel<>();

        listModel.addElement("张三");
        listModel.addElement("李四");

        // 创建好友列表
        friendList = new JList<>(listModel);
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setCellRenderer(new FriendListRenderer());

        // 添加好友列表选择事件
        friendList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = friendList.getSelectedValue();
                if (selected != null) {
                    appendToChat("已选择与 " + selected + " 聊天\n", Color.BLACK);
                }
            }
        });

        // 将组件添加到面板
        friendPanel.add(titleLabel, BorderLayout.NORTH);
        friendPanel.add(new JScrollPane(friendList), BorderLayout.CENTER);

        return friendPanel;
    }

    /**
     * 创建聊天面板
     * 包含聊天显示区域和底部的消息输入区域
     * @return JPanel 聊天面板
     */
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());

        // 创建聊天显示区域，使用JTextPane替代JTextArea
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // 创建底部输入面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("发送");
        sendButton.setPreferredSize(new Dimension(80, 30));

        // 添加发送按钮事件监听
        sendButton.addActionListener(e -> sendMessage());

        // 添加输入框回车事件监听
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(bottomPanel, BorderLayout.SOUTH);

        return chatPanel;
    }

    /**
     * 刷新好友列表数据，显示在好友列表中
     * @param data
     */
    public void updateFriendList(String[] data) {
        DefaultListModel<String> model = (DefaultListModel<String>) friendList.getModel();
        //清空好友列表
        model.clear();
        //刷新好友列表
        String friendName=data[1];
        String[] friendArray = friendName.split(",");
        for (String friend : friendArray) {
            //排除自己:
            if (friend.equals(username)) {
                continue;
            }
            model.addElement(friend);
        }
//        model.addElement("周杰伦 [在线]");
//        model.addElement("刘德华 [在线]");

    }


    public void showWelcomeMessage(String[] data) {
        appendToChat(data[1], Color.RED);
    }

    public void receiveChatMessage(String[] data) {
        appendToChat(data[1] + "说: " + data[2] + "\n", new Color(0, 0, 255)); // 蓝色显示接收到的消息
    }

    public void receiveOfflineMessage(String[] data) {
        appendToChat(data[1] + "已下线" + "\n", new Color(128, 128, 128)); // 灰色显示离线消息
    }


    /**
     * 自定义好友列表渲染器类
     * 用于自定义好友列表中每个项目的显示样式
     * 根据好友在线状态显示不同的颜色
     */
    private class FriendListRenderer extends DefaultListCellRenderer {
        /**
         * 重写渲染组件方法
         * 自定义列表项的显示效果
         */
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            // 设置图标（这里可以根据在线状态设置不同的图标）
            String text = value.toString();
            if (text.contains("[在线]")) {
                label.setForeground(new Color(0, 128, 0)); // 在线用户显示绿色
            } else {
                label.setForeground(Color.GRAY); // 离线用户显示灰色
            }

            // 设置边距
            label.setBorder(new EmptyBorder(5, 10, 5, 10));

            return label;
        }
    }


    /**
     * 发送消息方法
     * 处理消息发送逻辑，将消息显示在聊天区域
     * 并清空输入框
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            String friend = friendList.getSelectedValue();
            if (friend != null) {
                ClientHandler.sendChatMessage(username, friend, message);
                appendToChat("我对 " + friend + " 说: " + message + "\n", new Color(255, 0, 0)); // 红色显示发送的消息
            } else {
                // 没有选择好友时，提示选择好友
                JOptionPane.showMessageDialog(this, "请选择好友", "提示", JOptionPane.WARNING_MESSAGE);
            }
            inputField.setText("");
            inputField.requestFocus();
        }
    }

    // 添加新的辅助方法来设置带颜色的文本
    private void appendToChat(String message, Color color) {
        StyledDocument doc = chatArea.getStyledDocument();
        Style style = chatArea.addStyle("Color Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), message, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}
