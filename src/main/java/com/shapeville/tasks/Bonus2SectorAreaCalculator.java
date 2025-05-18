// File: Bonus2SectorAreaCalculator.java
package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class Bonus2SectorAreaCalculator {
    public JPanel taskPanel;
    public Runnable onReturnHome;
    private ScoreManager scoreManager;

    private JPanel selectPanel;
    private JPanel questionPanel;

    private JLabel imageLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;
    private JLabel scoreLabel;

    private int currentShapeId;
    private int attemptCount;
    private Map<Integer, Double> correctAnswers;
    private Map<Integer, String> explanations;

    public Bonus2SectorAreaCalculator(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.taskPanel = new JPanel(new CardLayout());

        initAnswers();
        initSelectPanel();
        initQuestionPanel();

        taskPanel.add(selectPanel, "select");
        taskPanel.add(questionPanel, "question");
    }

    private void initAnswers() {
        correctAnswers = new HashMap<>();
        explanations = new HashMap<>();

        correctAnswers.put(1, 50.24);
        explanations.put(1, "Area = (90 / 360) × 3.14 × 8² = 50.24");

        correctAnswers.put(2, 659.22);
        explanations.put(2, "Area = (130 / 360) × 3.14 × 18² = 659.22");

        correctAnswers.put(3, 189.65);
        explanations.put(3, "Area = (240 / 360) × 3.14 × 19² = 189.65");

        correctAnswers.put(4, 467.06);
        explanations.put(4, "Area = (110 / 360) × 3.14 × 22² = 467.06");

        correctAnswers.put(5, 107.24);
        explanations.put(5, "Area = (100 / 360) × 3.14 × 3.5² = 107.24");

        correctAnswers.put(6, 150.72);
        explanations.put(6, "Area = (270 / 360) × 3.14 × 8² = 150.72");

        correctAnswers.put(7, 377.0);
        explanations.put(7, "Area = (280 / 360) × 3.14 × 12² = 377.0");

        correctAnswers.put(8, 491.85);
        explanations.put(8, "Area = (250 / 360) × 3.14 × 15² = 491.85");
    }

    private void initSelectPanel() {
        selectPanel = new JPanel(new BorderLayout());
        selectPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Select a Sector Shape:");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(title);
        selectPanel.add(titlePanel, BorderLayout.NORTH);

        // 图像按钮面板 - 使用 GridBagLayout 实现响应式布局
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.25; // 四列平均分布
        gbc.weighty = 0.5;  // 两行平均分布

        for (int i = 1; i <= 8; i++) {
            int row = (i - 1) / 4;
            int col = (i - 1) % 4;
            gbc.gridx = col;
            gbc.gridy = row;

            int id = i;
            JButton btn;
            try {
                ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/circle" + i + ".png"));
                Image scaledImg = rawIcon.getImage().getScaledInstance(160, 120, Image.SCALE_SMOOTH);
                btn = new JButton(new ImageIcon(scaledImg));
            } catch (Exception ex) {
                btn = new JButton("circle" + i);
            }
            btn.addActionListener(e -> showQuestion(id));
            buttonPanel.add(btn, gbc);
        }

        selectPanel.add(buttonPanel, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });
        bottomPanel.add(homeButton);
        selectPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initQuestionPanel() {
        questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));

        // 顶部计分板
        scoreLabel = new JLabel("Score: " + scoreManager.getScore());
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        questionPanel.add(scoreLabel, BorderLayout.NORTH);

        // 主内容面板 - 使用 GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // 图像区域
        imageLabel = new JLabel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        contentPanel.add(imageLabel, gbc);

        // 问题区域
        JLabel prompt = new JLabel("Enter the calculated area (2 decimals):");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0.1;
        contentPanel.add(prompt, gbc);

        // 输入区域
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        answerField = new JTextField(10);
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(this::handleSubmit);
        inputPanel.add(answerField);
        inputPanel.add(submitButton);

        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(inputPanel, gbc);

        // 反馈区域
        feedbackLabel = new JLabel("");
        gbc.gridx = 1;
        gbc.gridy = 2;
        contentPanel.add(feedbackLabel, gbc);

        questionPanel.add(contentPanel, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton homeButton = new JButton("Back");
        homeButton.addActionListener(e -> {
            ((CardLayout)taskPanel.getLayout()).show(taskPanel, "select");
//            if (onReturnHome != null) onReturnHome.run();
        });
        bottomPanel.add(homeButton);
        questionPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showQuestion(int shapeId) {
        currentShapeId = shapeId;
        attemptCount = 0;
        answerField.setText("");
        feedbackLabel.setText("");
        scoreLabel.setText("Score: " + scoreManager.getScore());

        try {
            ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/circle" + shapeId + ".png"));
            Image scaledImage = rawIcon.getImage().getScaledInstance(300, 250, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception ex) {
            imageLabel.setText("Image not found");
        }

        ((CardLayout) taskPanel.getLayout()).show(taskPanel, "question");
    }

    private void handleSubmit(ActionEvent e) {
        try {
            double userAnswer = Double.parseDouble(answerField.getText());
            double correct = correctAnswers.get(currentShapeId);
            if (Math.abs(userAnswer - correct) < 0.1) {
                int score = switch (attemptCount) {
                    case 0 -> 6;
                    case 1 -> 4;
                    case 2 -> 2;
                    default -> 0;
                };
                scoreManager.addScore(score);
                feedbackLabel.setText("✅ Correct! +" + score + " points");
                scoreLabel.setText("Score: " + scoreManager.getScore());
            } else {
                attemptCount++;
                if (attemptCount >= 3) {
                    feedbackLabel.setText("❌ Incorrect. " + explanations.get(currentShapeId));
                } else {
                    feedbackLabel.setText("❌ Try again. Attempts left: " + (3 - attemptCount));
                }
            }
        } catch (Exception ex) {
            feedbackLabel.setText("❌ Please enter a valid number.");
        }
    }
}