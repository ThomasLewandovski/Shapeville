package com.shapeville.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Introduction {
    private JPanel panel;
    private JLabel p1;
    private JLabel p2;
    private JButton home;
    private JDialog dialog; // 对话框引用

    public Introduction(JFrame parentFrame) {
        panel = new JPanel();
        panel.setLayout(null);

        // 初始化标签
        p1 = new JLabel("Introduction");
        p1.setFont(new Font("Arial", Font.BOLD, 18));
        p1.setBounds(10, 10, 200, 30);

        p2 = new JLabel("Welcome to Shapeville!");
        p2.setFont(new Font("Arial", Font.PLAIN, 14));
        p2.setBounds(10, 50, 400, 30);

        // 初始化按钮
        home = new JButton("Close"); // 改为"Close"更符合对话框语义
        home.setFont(new Font("Arial", Font.PLAIN, 14));
        home.setBounds(10, 100, 120, 35);

        // 添加组件到面板
        panel.add(p1);
        panel.add(p2);
        panel.add(home);

        // 设置按钮点击事件：关闭对话框
        home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dialog != null) {
                    dialog.dispose(); // 关闭对话框
                }
            }
        });

        // 初始化对话框
        dialog = new JDialog(parentFrame, "Introduction", false); // 非模态对话框
        dialog.setContentPane(panel);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(parentFrame); // 居中显示
    }

    // 显示对话框的方法
    public void show() {
        dialog.setVisible(true);
    }
}