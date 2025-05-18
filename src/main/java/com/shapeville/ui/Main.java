package com.shapeville.ui;

import com.shapeville.manager.ScoreManager;
import com.shapeville.tasks.Task1ShapeIdentification;
import com.shapeville.tasks.Task2AngleIdentification;
import com.shapeville.tasks.Task3VolumeSurfaceCalculator;
import com.shapeville.tasks.Task4CircleArea;
import com.shapeville.tasks.Bonus1CompoundShapeArea;
import com.shapeville.tasks.Bonus2SectorAreaCalculator;

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
        var ref = new Object() {
            int[] is_played_task1 = new int[2];
        };
        ref.is_played_task1[0] = 0;
        ref.is_played_task1[1] = 0;

        // 在main方法开始处替换原有状态数组
        boolean[] taskCompletionStatus = new boolean[6]; // [0]task1 [1]task2 [2]task3 [3]task4 [4]bonus1 [5]bonus2
        for (int i = 0; i < 6; i++) {
            taskCompletionStatus[i] = false;
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
        counter1.setBounds(10, 0, 60, 30);
        mainpanel.add(counter1);
        //更新积分方法：setText，在每次结束tasks时使用
        counter1.setText("积分："+result);

        //开始按钮
        JButton startButton = new JButton("Start!");
        startButton.setBounds(300, 400, 100, 30);
        mainpanel.add(startButton);

        // 创建第二个界面：关卡选择，管理进入各个task的通道及积分器
        JPanel startpanel = new JPanel(null);
        final JLabel counter2 = new JLabel("积分："+result);
        counter2.setBounds(10, 0, 60, 30);
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
        task1Button.setBounds(100, 100, 150, 100);
        task2Button.setBounds(250, 100, 150, 100);
        task3Button.setBounds(400, 100, 150, 100);
        task4Button.setBounds(550, 100, 150, 100);
        startpanel.add(task1Button);
        startpanel.add(task2Button);
        startpanel.add(task3Button);
        startpanel.add(task4Button);
        //进度条
        JProgressBar task1ProgressBar = new JProgressBar(0, 8); // 4个小题
        task1ProgressBar.setBounds(100, 200, 150, 20); // 位于按钮下方
        task1ProgressBar.setStringPainted(true); // 显示百分比
        task1ProgressBar.setValue(0); // 初始值
        startpanel.add(task1ProgressBar);

        JProgressBar task2ProgressBar = new JProgressBar(0, 4); // 4个小题
        task2ProgressBar.setBounds(250, 200, 150, 20); // 位于按钮下方
        task2ProgressBar.setStringPainted(true); // 显示百分比
        task2ProgressBar.setValue(0); // 初始值
        startpanel.add(task2ProgressBar);

        JProgressBar task3ProgressBar = new JProgressBar(0, 4); // 4个小题
        task3ProgressBar.setBounds(400, 200, 150, 20); // 位于按钮下方
        task3ProgressBar.setStringPainted(true); // 显示百分比
        task3ProgressBar.setValue(0); // 初始值
        startpanel.add(task3ProgressBar);

        JProgressBar task4ProgressBar = new JProgressBar(0, 2); // 4个小题
        task4ProgressBar.setBounds(550, 200, 150, 20); // 位于按钮下方
        task4ProgressBar.setStringPainted(true); // 显示百分比
        task4ProgressBar.setValue(0); // 初始值
        startpanel.add(task4ProgressBar);

        //bonus按钮
        JButton bonus1Button = new JButton("bonus1");
        JButton bonus2Button = new JButton("bonus2");
        bonus1Button.setBounds(150, 300, 250, 100);
        bonus2Button.setBounds(400, 300, 250, 100);
        startpanel.add(bonus1Button);
        startpanel.add(bonus2Button);

        JProgressBar bonus1ProgressBar = new JProgressBar(0, 6); // 4个小题
        bonus1ProgressBar.setBounds(150, 400, 250, 20); // 位于按钮下方
        bonus1ProgressBar.setStringPainted(true); // 显示百分比
        bonus1ProgressBar.setValue(0); // 初始值
        startpanel.add(bonus1ProgressBar);

        JProgressBar bonus2ProgressBar = new JProgressBar(0, 8); // 4个小题
        bonus2ProgressBar.setBounds(400, 400, 250, 20); // 位于按钮下方
        bonus2ProgressBar.setStringPainted(true); // 显示百分比
        bonus2ProgressBar.setValue(0); // 初始值
        startpanel.add(bonus2ProgressBar);


        // 在创建按钮后添加样式设置
        task1Button.setOpaque(true);
        task2Button.setOpaque(true);
        task3Button.setOpaque(true);
        task4Button.setOpaque(true);
        bonus1Button.setOpaque(true);
        bonus2Button.setOpaque(true);

        task1Button.setContentAreaFilled(false);
        task2Button.setContentAreaFilled(false);
        task3Button.setContentAreaFilled(false);
        task4Button.setContentAreaFilled(false);
        bonus1Button.setContentAreaFilled(false);
        bonus2Button.setContentAreaFilled(false);

        // 将面板添加到卡片面板
        cardPanel.add(mainpanel, "mainPanel");
        cardPanel.add(startpanel, "startPanel");

        ScoreManager scoremanager = new ScoreManager();

        // 添加按钮点击事件监听器
        //切换至开始界面
        startButton.addActionListener(e -> cardLayout.show(cardPanel, "startPanel"));
        //回到主界面
        homeButton.addActionListener(e -> cardLayout.show(cardPanel, "mainPanel"));


        Task1ShapeIdentification task1 = new Task1ShapeIdentification(scoremanager, ref.is_played_task1);
        cardPanel.add(task1.task1, "task1");
        task1.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter1.setText("积分：" + scoremanager.getScore());
            counter2.setText("积分：" + scoremanager.getScore());
            ref.is_played_task1 = task1.getIs_played_task1(); // 更新游戏状态

            task1ProgressBar.setValue(task1.isIdentifiedShapes);
        };

        task1Button.addActionListener(e -> {
            if (ref.is_played_task1[0] == 0 || ref.is_played_task1[1] == 0) {
                cardLayout.show(cardPanel, "task1");
            }
        });

        // 修改task2Button监听逻辑
        Task2AngleIdentification task2 = new Task2AngleIdentification(scoremanager);
        cardPanel.add(task2.task2, "task2");

        task2.onComplete = () -> {
            taskCompletionStatus[1] = true;
            task2Button.setBackground(new Color(144, 238, 144));
            task2Button.setEnabled(false);

        };

        task2.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter1.setText("积分：" + scoremanager.getScore());
            counter2.setText("积分：" + scoremanager.getScore());

            task2ProgressBar.setValue(task2.identifiedTypes.size());
        };

        task2Button.addActionListener(e -> {
            if (!taskCompletionStatus[1]) {

                cardLayout.show(cardPanel, "task2");
            } else {
                JOptionPane.showMessageDialog(null, "该模块已完成，无法再次进入", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        Task3VolumeSurfaceCalculator task3 = new Task3VolumeSurfaceCalculator(scoremanager);
        cardPanel.add(task3.task3, "task3");

        task3.onComplete = () -> {
            taskCompletionStatus[2] = true;
            task3Button.setBackground(new Color(144, 238, 144)); // 浅绿色
            task3Button.setEnabled(false);

            task3ProgressBar.setValue(task3.CompletedShapes.size());
        };

        task3.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter1.setText("积分：" + scoremanager.getScore());
            counter2.setText("积分：" + scoremanager.getScore());
        };
        // 修改 task3 相关代码
        task3Button.addActionListener(e -> {
            if (!taskCompletionStatus[2]) {
                cardLayout.show(cardPanel, "task3");
            } else {
                JOptionPane.showMessageDialog(null,
                        "该模块已完成，无法再次进入",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        Task4CircleArea task4 = new Task4CircleArea(scoremanager);
        cardPanel.add(task4.task4, "task4");

        task4.onComplete = () -> {
            taskCompletionStatus[3] = true;
            task4Button.setBackground(new Color(144, 238, 144));
            task4Button.setEnabled(false);
        };

        task4.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter1.setText("积分：" + scoremanager.getScore());
            counter2.setText("积分：" + scoremanager.getScore());
        };
        task4Button.addActionListener(e -> {
            if (!taskCompletionStatus[3]) {

                cardLayout.show(cardPanel, "task4");
            } else {
                JOptionPane.showMessageDialog(null,
                        "该模块已完成，无法再次进入",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        Bonus1CompoundShapeArea bonus1 = new Bonus1CompoundShapeArea(scoremanager);
        cardPanel.add(bonus1.taskPanel, "bonus1");
        bonus1.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter1.setText("积分：" + scoremanager.getScore());
            counter2.setText("积分：" + scoremanager.getScore());

            bonus1ProgressBar.setValue(bonus1.completedTasks);
        };
        bonus1Button.addActionListener(e -> {
            cardLayout.show(cardPanel, "bonus1");
        });

        Bonus2SectorAreaCalculator bonus2 = new Bonus2SectorAreaCalculator(scoremanager);
        cardPanel.add(bonus2.taskPanel, "bonus2");
        bonus2.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter1.setText("积分：" + scoremanager.getScore());
            counter2.setText("积分：" + scoremanager.getScore());

            bonus2ProgressBar.setValue(bonus2.completedTasks);
        };
        bonus2Button.addActionListener(e -> {
            cardLayout.show(cardPanel, "bonus2");
        });

        // 将卡片面板添加到主窗口
        frame.add(cardPanel);

        // 显示主窗口
        frame.setVisible(true);
    }
}