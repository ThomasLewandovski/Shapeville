package com.shapeville.ui;

import com.shapeville.manager.ScoreManager;
import com.shapeville.tasks.Task1ShapeIdentification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        // 创建主窗口
        JFrame frame = new JFrame("Shapeville");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // 创建卡片布局和面板
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        // 创建第一个界面：主界面
        JPanel mainpanel = new JPanel(null);
        //积分器

        JLabel counter = new JLabel("积分：");
        counter.setBounds(10, 10, 60, 30);
        mainpanel.add(counter);
        //更新积分方法：setText，在每次结束tasks时使用
        int result = 0;
        counter.setText("积分："+result);

        //开始按钮
        JButton startButton = new JButton("Start!");
        startButton.setBounds(300, 10, 100, 30);
        mainpanel.add(startButton);

        // 创建第二个界面：关卡选择，管理进入各个task的通道及积分器
        JPanel startpanel = new JPanel(null);
        //回到主界面按钮
        JButton homeButton = new JButton("Home");
        homeButton.setBounds(600, 470, 100, 30);
        startpanel.add(homeButton);
        //4个tasks按钮
        JButton task1Button = new JButton("task1:");
        JButton task2Button = new JButton("task2:");
        JButton task3Button = new JButton("task3:");
        JButton task4Button = new JButton("task4:");
        task1Button.setBounds(100, 50, 100, 30);
        task2Button.setBounds(250, 50, 100, 30);
        task3Button.setBounds(400, 50, 100, 30);
        task4Button.setBounds(550, 50, 100, 30);
        startpanel.add(task1Button);
        startpanel.add(task2Button);
        startpanel.add(task3Button);
        startpanel.add(task4Button);
        //bonus按钮
        JButton bonus1Button = new JButton("bonus1");
        JButton bonus2Button = new JButton("bonus2");
        bonus1Button.setBounds(400, 200, 100, 30);
        bonus2Button.setBounds(200, 200, 100, 30);
        startpanel.add(bonus1Button);
        startpanel.add(bonus2Button);

        // 将面板添加到卡片面板
        cardPanel.add(mainpanel, "mainPanel");
        cardPanel.add(startpanel, "startPanel");

        ScoreManager scoremanager = new ScoreManager();
        Task1ShapeIdentification n = new Task1ShapeIdentification(scoremanager);
        cardPanel.add(n.task1,"task1");

        // 添加按钮点击事件监听器
        //切换至开始界面
        startButton.addActionListener(e -> cardLayout.show(cardPanel, "startPanel"));
        //回到主界面
        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "mainPanel"));

        task1Button.addActionListener(e ->{
            cardLayout.show(cardPanel,"task1");
            n.start();
        });

//        task2Button.addActionListener(e -> );
//        task3Button.addActionListener(e -> );
//        task4Button.addActionListener(e -> );
//        bonus1Button.addActionListener(e -> );
//        bonus2Button.addActionListener(e -> );


        // 将卡片面板添加到主窗口
        frame.add(cardPanel);

        // 显示主窗口
        frame.setVisible(true);
    }
}