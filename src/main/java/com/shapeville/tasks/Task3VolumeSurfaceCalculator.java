package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Task3VolumeSurfaceCalculator {
    public JPanel task3;
    public Runnable onReturnHome;
    private ScoreManager scoreManager;
    public JLabel score;

    private JLabel questionLabel;
    private JTextField inputField;
    private JButton submitButton, homeButton;
    private JComboBox<String> shapeSelector;
    private Timer countdownTimer;
    private JLabel timerLabel;
    //三次失败后把图形绘制出来的面板
    private JPanel drawingPanel;

    private String currentShape;
    private int param1, param2, param3;
    private int correctAnswer;
    private int attemptsLeft;
    private int timeRemaining;

    private Set<String> completedShapes;

    public Task3VolumeSurfaceCalculator(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.task3 = new JPanel(null);
        this.completedShapes = new HashSet<>();

        questionLabel = new JLabel("Choose a shape:");
        questionLabel.setBounds(100, 30, 500, 30);
        task3.add(questionLabel);

        shapeSelector = new JComboBox<>(new String[]{"Rectangle", "Parallelogram", "Triangle", "Trapezium"});
        shapeSelector.setBounds(100, 70, 200, 30);
        task3.add(shapeSelector);

        JButton generateButton = new JButton("Generate Problem");
        generateButton.setBounds(320, 70, 200, 30);
        task3.add(generateButton);

        inputField = new JTextField();
        inputField.setBounds(100, 150, 200, 30);
        task3.add(inputField);

        submitButton = new JButton("Submit");
        submitButton.setBounds(320, 150, 100, 30);
        task3.add(submitButton);

        timerLabel = new JLabel("Time left: 180s");
        timerLabel.setBounds(100, 110, 200, 30);
        task3.add(timerLabel);

        homeButton = new JButton("Home");
        homeButton.setBounds(600, 470, 100, 30);
        task3.add(homeButton);

        score = new JLabel();
        score.setText("Score: 0");
        score.setBounds(10, 0, 200, 40);
        task3.add(score);

        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentShape != null && attemptsLeft <= 0) {
                    drawShapeWithLabel(g);
                }
            }
        };
        drawingPanel.setBounds(100, 200, 400, 200);
        drawingPanel.setBackground(Color.WHITE);
        task3.add(drawingPanel);

        generateButton.addActionListener(e -> start());

        submitButton.addActionListener(e -> checkAnswer());

        homeButton.addActionListener(e -> {
            if (countdownTimer != null) countdownTimer.stop();
            if (onReturnHome != null) onReturnHome.run();
        });
    }

    public void start() {
        currentShape = (String) shapeSelector.getSelectedItem();
        if (completedShapes.contains(currentShape)) {
            questionLabel.setText("You already completed " + currentShape);
            return;
        }

        // 参数生成（1~20）
        Random rand = new Random();
        param1 = rand.nextInt(20) + 1;
        param2 = rand.nextInt(20) + 1;
        param3 = rand.nextInt(20) + 1;
        attemptsLeft = 3;
        timeRemaining = 180;

        // 设置题目内容
        switch (currentShape) {
            case "Rectangle" -> {
                correctAnswer = param1 * param2;
                questionLabel.setText("📐 Rectangle: length = " + param1 + ", width = " + param2 + ". Calculate area:");
            }
            case "Parallelogram" -> {
                correctAnswer = param1 * param2;
                questionLabel.setText("📐 Parallelogram: base = " + param1 + ", height = " + param2 + ". Calculate area:");
            }
            case "Triangle" -> {
                correctAnswer = (param1 * param2) / 2;
                questionLabel.setText("📐 Triangle: base = " + param1 + ", height = " + param2 + ". Calculate area:");
            }
            case "Trapezium" -> {
                correctAnswer = ((param1 + param2) * param3) / 2;
                questionLabel.setText("📐 Trapezium: a = " + param1 + ", b = " + param2 + ", height = " + param3 + ". Calculate area:");
            }
        }

        inputField.setText("");
        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("Time left: " + timeRemaining + "s");
            if (timeRemaining <= 0) {
                ((Timer) e.getSource()).stop();
                showExplanation();
            }
        });
        countdownTimer.start();
        drawingPanel.repaint(); // 清除旧图形
    }

    private void checkAnswer() {
        try {
            int userAns = Integer.parseInt(inputField.getText().trim());
            if (userAns == correctAnswer) {
                countdownTimer.stop();
                int score = switch (attemptsLeft) {
                    case 3 -> 3;
                    case 2 -> 2;
                    case 1 -> 1;
                    default -> 0;
                };
                scoreManager.addScore(score);
                completedShapes.add(currentShape);
                questionLabel.setText("✅ Great job! +" + score + " points");
            } else {
                attemptsLeft--;
                if (attemptsLeft <= 0) {
                    countdownTimer.stop();
                    showExplanation();
                } else {
                    questionLabel.setText("❌ Incorrect. Attempts left: " + attemptsLeft);
                }
            }
        } catch (Exception e) {
            questionLabel.setText("Please enter a valid number");
        }
        score.setText("Score: " + scoreManager.getScore());
    }

    private void showExplanation() {
        String formula = switch (currentShape) {
            case "Rectangle" -> "Area = length × width = " + param1 + " × " + param2 + " = " + correctAnswer;
            case "Parallelogram" -> "Area = base × height = " + param1 + " × " + param2 + " = " + correctAnswer;
            case "Triangle" -> "Area = base × height / 2 = " + param1 + " × " + param2 + " / 2 = " + correctAnswer;
            case "Trapezium" -> "Area = (a + b) × height / 2 = (" + param1 + " + " + param2 + ") × " + param3 + " / 2 = " + correctAnswer;
            default -> "Unknown shape.";
        };
        completedShapes.add(currentShape);
        //questionLabel.setText("❗ Correct Answer: " + formula);
        drawingPanel.repaint();
    }

    private void drawShapeWithLabel(Graphics g) {
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        switch (currentShape) {
            case "Rectangle" -> {
                g.drawRect(50, 50, 100, 60);
                g.drawString("length: " + param1, 100, 45);
                g.drawString("width: " + param2, 160, 85);
            }
            case "Parallelogram" -> {
                int[] xPoints = {50, 90, 140, 100};
                int[] yPoints = {100, 50, 50, 100};
                g.drawPolygon(xPoints, yPoints, 4);
                g.drawString("base: " + param1, 90, 45);
                g.drawString("height: " + param2, 30, 75);
            }
            case "Triangle" -> {
                int[] xPoints = {50, 150, 100};
                int[] yPoints = {150, 150, 70};
                g.drawPolygon(xPoints, yPoints, 3);
                g.drawString("base: " + param1, 80, 160);
                g.drawString("height: " + param2, 105, 110);
            }
            case "Trapezium" -> {
                int topLeftX = 100;
                int topRightX = 200;
                int bottomRightX = 230;
                int bottomLeftX = 70;
                int topY = 60;
                int bottomY = 110;

                // 梯形四个点
                int[] xPoints = {topLeftX, topRightX, bottomRightX, bottomLeftX};
                int[] yPoints = {topY, topY, bottomY, bottomY};

                g.setColor(Color.BLUE);
                g.drawPolygon(xPoints, yPoints, 4);

                // 标注上底 a 和下底 b
                g.drawString("a: " + param1, (topLeftX + topRightX) / 2 - 15, topY - 10);
                g.drawString("b: " + param2, (bottomLeftX + bottomRightX) / 2 - 15, bottomY + 20);

                // 绘制高度线 + 标注
                int midX = (topLeftX + bottomLeftX) / 2;
                g.setColor(Color.GRAY);
                g.drawLine(midX, topY, midX, bottomY); // 高度线
                g.setColor(Color.BLUE);
                g.drawString("height: " + param3, midX - 45, (topY + bottomY) / 2);
            }
        }
        g.setColor(Color.RED);
        g.drawString("Formula + Answer: " + getFormulaExplanation(), 10, 190);
    }

    private String getFormulaExplanation() {
        return switch (currentShape) {
            case "Rectangle" -> param1 + " * " + param2 + " = " + correctAnswer;
            case "Parallelogram" -> param1 + " * " + param2 + " = " + correctAnswer;
            case "Triangle" -> param1 + " * " + param2 + " / 2 = " + correctAnswer;
            case "Trapezium" -> "(" + param1 + " + " + param2 + ") * " + param3 + " / 2 = " + correctAnswer;
            default -> "Unknown";
        };
    }
}
