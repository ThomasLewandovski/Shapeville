package com.shapeville.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;

import com.shapeville.data.ShapeData;
import com.shapeville.data.ShapeData.ShapeItem;
import com.shapeville.manager.ScoreManager;

public class Task1ShapeIdentification {
    private ScoreManager scoreManager;
    public JPanel task1;
    public JLabel img;
    public JLabel q;
    public JTextField input;

    public Task1ShapeIdentification(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.task1 = new JPanel(null);
        this.img = new JLabel();
        this.q = new JLabel();
        this.input = new JTextField();
        img.setBounds(100, 10, 400, 400);
        input.setBounds(100, 450, 400, 20);
        q.setBounds(100, 420, 400, 20);
        task1.add(img);
        task1.add(q);
        task1.add(input);
    }

    public void start() {
        q.setText("\n📐 Task 1: Identify 2D / 3D Shapes" + "1. 2D Shapes (Basic Level)" + "2. 3D Shapes (Advanced Level)" + "Choose an option: ");
        final String[] choice = new String[1];
        input.addActionListener(e -> {
            choice[0] = input.getText();
            switch (choice[0]) {
                case "1":
                    runSubtask("2D");
                    break;
                case "2":
                    runSubtask("3D");
                    break;
                default:
                    System.out.println("Invalid choice. Returning to home.");
            }
        });
    }

    private void runSubtask(String type) {
        List<ShapeItem> shapes = new ArrayList<>((type.equals("2D") ? ShapeData.getAll2DShapes() : ShapeData.getAll3DShapes()));
        boolean isAdvanced = type.equals("3D");

        // 打乱顺序
        Collections.shuffle(shapes);

        for (ShapeItem shape : shapes) {
            askShape(shape, isAdvanced);
        }

        q.setText("\n🎉 You've completed the " + type + " shape task!");
    }

    private void askShape(ShapeItem shape, boolean advancedLevel) {
        int maxAttempts = 3;
        final int[] points = {0}; // 使用数组
        String imgPath = "main/resources/images" + shape + ".png";
        ImageIcon imageIcon = new ImageIcon(imgPath);
        img.setIcon(imageIcon);
        img.repaint();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            q.setText("\nWhat is the name of this shape?" + "Your answer: ");
            input.setText("");
            CountDownLatch latch = new CountDownLatch(1);
            int finalAttempt = attempt;
            input.addActionListener(e -> {
                String answer = input.getText();
                if (checkAnswer(answer, shape.getName())) {
                    // 计算分数（按任务书规则）
                    if (finalAttempt == 1) {
                        points[0] = advancedLevel? 6 : 3; // 修改数组元素值
                    } else if (finalAttempt == 2) {
                        points[0] = advancedLevel? 4 : 2;
                    } else if (finalAttempt == 3) {
                        points[0] = advancedLevel? 2 : 1;
                    } else {
                        points[0] = 0;
                    }
                    scoreManager.addScore(points[0]);
                    latch.countDown();
                } else {
                    q.setText("❌ Incorrect. Try again.");
                    if (finalAttempt == maxAttempts) {
                        latch.countDown();
                    }
                }
            });
            try {
                latch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (points[0] > 0) {
                break;
            }
        }

        // 3次都错了
        if (points[0] == 0) {
            q.setText("⚠️ The correct answer was: " + shape.getName());
        }
    }

    private boolean checkAnswer(String input, String correct) {
        return input.equalsIgnoreCase(correct);
    }
}