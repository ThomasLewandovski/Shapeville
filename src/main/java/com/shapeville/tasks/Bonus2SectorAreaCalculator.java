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
        selectPanel = new JPanel(null);

        JLabel title = new JLabel("Select a Sector Shape:");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(300, 20, 300, 30);
        selectPanel.add(title);

        for (int i = 1; i <= 8; i++) {
            int id = i;
            try {
                ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/circle" + i + ".png"));
                Image scaledImg = rawIcon.getImage().getScaledInstance(160, 120, Image.SCALE_SMOOTH);
                JButton btn = new JButton(new ImageIcon(scaledImg));
                btn.setBounds(50 + ((i - 1) % 4) * 180, 70 + ((i - 1) / 4) * 160, 160, 120);
                btn.addActionListener(e -> showQuestion(id));
                selectPanel.add(btn);
            } catch (Exception ex) {
                JButton btn = new JButton("circle" + i);
                btn.setBounds(50 + ((i - 1) % 4) * 180, 70 + ((i - 1) / 4) * 160, 160, 120);
                btn.addActionListener(e -> showQuestion(id));
                selectPanel.add(btn);
            }
        }

        JButton homeButton = new JButton("Home");
        homeButton.setBounds(600, 470, 100, 30);
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });
        selectPanel.add(homeButton);
    }

    private void initQuestionPanel() {
        questionPanel = new JPanel(null);

        imageLabel = new JLabel();
        imageLabel.setBounds(100, 40, 300, 250);
        questionPanel.add(imageLabel);

        JLabel prompt = new JLabel("Enter the calculated area (2 decimals):");
        prompt.setBounds(450, 80, 250, 30);
        questionPanel.add(prompt);

        answerField = new JTextField();
        answerField.setBounds(450, 120, 100, 30);
        questionPanel.add(answerField);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(560, 120, 80, 30);
        submitButton.addActionListener(this::handleSubmit);
        questionPanel.add(submitButton);

        feedbackLabel = new JLabel("");
        feedbackLabel.setBounds(450, 180, 400, 30);
        questionPanel.add(feedbackLabel);

        scoreLabel = new JLabel("Score: " + scoreManager.getScore());
        scoreLabel.setBounds(10, 10, 200, 30);
        questionPanel.add(scoreLabel);

        JButton homeButton = new JButton("Home");
        homeButton.setBounds(600, 470, 100, 30);
        homeButton.addActionListener(e -> {
            ((CardLayout) taskPanel.getLayout()).show(taskPanel, "select");
            if (onReturnHome != null) onReturnHome.run();
        });
        questionPanel.add(homeButton);
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