package com.shapeville.ui;

import com.shapeville.manager.ScoreManager;
import com.shapeville.tasks.Task1ShapeIdentification;
import com.shapeville.tasks.Task2AngleIdentification;
import com.shapeville.tasks.Task3VolumeSurfaceCalculator;
import com.shapeville.tasks.Task4CircleArea;
import com.shapeville.tasks.Bonus1CompoundShapeTask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        // 禁用输入法特殊处理
        System.setProperty("java.awt.im.useInputMethodKeys", "false");
        System.setProperty("apple.awt.im.disable", "true"); // 新增 macOS 专用属性
        int result = 0;
        int[] is_played = new int[7];
        for (int i = 0; i < 7; i++) {
            is_played[i] = 0;
        }
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

        final JLabel counter1 = new JLabel("积分："+result);
        counter1.setBounds(10, 10, 60, 30);
        mainpanel.add(counter1);
        //更新积分方法：setText，在每次结束tasks时使用
        counter1.setText("积分："+result);

        //开始按钮
        JButton startButton = new JButton("Start!");
        startButton.setBounds(300, 10, 100, 30);
        mainpanel.add(startButton);

        // 创建第二个界面：关卡选择，管理进入各个task的通道及积分器
        JPanel startpanel = new JPanel(null);
        final JLabel counter2 = new JLabel("积分："+result);
        counter2.setBounds(10, 10, 60, 30);
        startpanel.add(counter2);
        counter2.setText("积分："+result);
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

        // 添加按钮点击事件监听器
        //切换至开始界面
        startButton.addActionListener(e -> cardLayout.show(cardPanel, "startPanel"));
        //回到主界面
        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "mainPanel"));

        task1Button.addActionListener(e ->{
            Task1ShapeIdentification task1 = new Task1ShapeIdentification(scoremanager);
            cardPanel.add(task1.task1,"task1");
            task1.onReturnHome = () -> {
                cardLayout.show(cardPanel, "startPanel");
                counter1.setText("积分：" + scoremanager.getScore());
                counter2.setText("积分：" + scoremanager.getScore());
            };
            cardLayout.show(cardPanel,"task1");
            task1.start();
        });

        task2Button.addActionListener(e -> {
            if(is_played[2]==0){
                is_played[2] = 1;
                Task2AngleIdentification task2 = new Task2AngleIdentification(scoremanager);
                cardPanel.add(task2.task2, "task2");
                task2.onReturnHome = () -> {
                    cardLayout.show(cardPanel, "startPanel");
                    counter1.setText("积分：" + scoremanager.getScore());
                    counter2.setText("积分：" + scoremanager.getScore());
                };
                cardLayout.show(cardPanel, "task2");
                task2.start();
            }else{
                JOptionPane.showMessageDialog(null, "you have played this module,please try other modules", "提示", JOptionPane.INFORMATION_MESSAGE);
            }

        });

        task3Button.addActionListener(e -> {
            if (is_played[3] == 0) {
                is_played[3] = 1;
                Task3VolumeSurfaceCalculator task3 = new Task3VolumeSurfaceCalculator(scoremanager);
                cardPanel.add(task3.task3, "task3");
                task3.onReturnHome = () -> {
                    cardLayout.show(cardPanel, "startPanel");
                    counter1.setText("积分：" + scoremanager.getScore());
                    counter2.setText("积分：" + scoremanager.getScore());
                };
                cardLayout.show(cardPanel, "task3");
                task3.start();
            } else {
                JOptionPane.showMessageDialog(null, "you have played this module, please try other modules", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        task4Button.addActionListener(e -> {
            if (is_played[4] == 0) {
                is_played[4] = 1;
                Task4CircleArea task4 = new Task4CircleArea(scoremanager);
                cardPanel.add(task4.task4, "task4");
                task4.onReturnHome = () -> {
                    cardLayout.show(cardPanel, "startPanel");
                    counter1.setText("积分：" + scoremanager.getScore());
                    counter2.setText("积分：" + scoremanager.getScore());
                };
                cardLayout.show(cardPanel, "task4");
                task4.start();  // 启动任务流程
            } else {
                JOptionPane.showMessageDialog(null, "You have already played this module. Please try another one.", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        Bonus1CompoundShapeTask bonus1 = new Bonus1CompoundShapeTask(scoremanager);
        cardPanel.add(bonus1.mainPanel, "bonus1");
        bonus1.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter1.setText("积分：" + scoremanager.getScore());
            counter2.setText("积分：" + scoremanager.getScore());
        };
        bonus1Button.addActionListener(e -> {
            cardLayout.show(cardPanel, "bonus1");
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