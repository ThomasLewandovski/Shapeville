// File: Bonus1CompoundShapeArea.java
package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class Bonus1CompoundShapeArea {
    public JPanel taskPanel;
    public Runnable onReturnHome;
    private ScoreManager scoreManager;

    private JPanel shapeSelectPanel;
    private JPanel questionPanel;

    private JLabel imageLabel;
    private JLabel feedbackLabel;
    private JTextField answerField;

    private int currentShapeId;
    private int attemptCount = 0;
    private Map<Integer, Double> correctAnswers;
    private Map<Integer, String> explanations;

    public Bonus1CompoundShapeArea(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.taskPanel = new JPanel(new CardLayout());
        initializeAnswerData();
        initShapeSelectPanel();
        initQuestionPanel();
        taskPanel.add(shapeSelectPanel, "select");
        taskPanel.add(questionPanel, "question");
    }

    private void initializeAnswerData() {
        correctAnswers = new HashMap<>();
        explanations = new HashMap<>();

        correctAnswers.put(1, 231.0);
        explanations.put(1, "Area = 14×14 = 196 + ½×5×14 = 35 → 231 cm²");

        correctAnswers.put(2, 320.0);
        explanations.put(2, "Area = 21×20 = 420 – 10×10 = 100 → 320 cm²");

        correctAnswers.put(3, 310.0);
        explanations.put(3, "Area = 18×3 = 54 + 16×16 = 256 → 310 cm²");

        correctAnswers.put(4, 216.0);
        explanations.put(4, "Area = 12×2 = 24 + 10×12 = 120 + 6×12 = 72 → 216 cm²");

        correctAnswers.put(5, 76.0);
        explanations.put(5, "Area = 16×4 = 64 + ½×(4+2)×4 = 12 → 76 cm²");

        correctAnswers.put(6, 187.0);
        explanations.put(6, "Area = ½×(20+14)×11 = 187 cm²");

        correctAnswers.put(7, 196.0);
        explanations.put(7, "Area = 14×12 = 168 + ½×14×4 = 28 → 196 cm²");

        correctAnswers.put(8, 3888.0);
        explanations.put(8, "Area = 36×36 × 3 = 3888 cm²");

        correctAnswers.put(9, 198.0);
        explanations.put(9, "Area = 10×11 = 110 + 8×3 = 24 + 8×8 = 64 → 198 cm²");
    }

    private void initShapeSelectPanel() {
        shapeSelectPanel = new JPanel(null);

        JLabel title = new JLabel("Select a Compound Shape:");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(300, 20, 300, 30);
        shapeSelectPanel.add(title);

        for (int i = 1; i <= 9; i++) {
            int shapeId = i;
            try {
                ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Shape" + i + ".png"));
                Image scaledImg = rawIcon.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
                JButton btn = new JButton(new ImageIcon(scaledImg));
                btn.setBounds(80 + ((i - 1) % 3) * 210, 70 + ((i - 1) / 3) * 150, 200, 130);
                btn.addActionListener(e -> showQuestion(shapeId));
                shapeSelectPanel.add(btn);
            } catch (Exception ex) {
                JButton btn = new JButton("Shape " + i);
                btn.setBounds(80 + ((i - 1) % 3) * 210, 70 + ((i - 1) / 3) * 150, 200, 130);
                btn.addActionListener(e -> showQuestion(shapeId));
                shapeSelectPanel.add(btn);
            }
        }

        JButton homeButton = new JButton("taskPanel");
        homeButton.setBounds(600, 470, 100, 30);
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });
        shapeSelectPanel.add(homeButton);
    }

    private void initQuestionPanel() {
        questionPanel = new JPanel(null);

        imageLabel = new JLabel();
        imageLabel.setBounds(100, 40, 300, 250);
        questionPanel.add(imageLabel);

        JLabel prompt = new JLabel("Enter the calculated area:");
        prompt.setBounds(450, 80, 200, 30);
        questionPanel.add(prompt);

        answerField = new JTextField();
        answerField.setBounds(450, 120, 100, 30);
        questionPanel.add(answerField);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(560, 120, 80, 30);
        submitButton.addActionListener(this::handleSubmit);
        questionPanel.add(submitButton);

        feedbackLabel = new JLabel("");
        feedbackLabel.setBounds(450, 180, 300, 30);
        questionPanel.add(feedbackLabel);

        JButton homeButton = new JButton("Back");
        homeButton.setBounds(600, 470, 100, 30);
        homeButton.addActionListener(e -> {
            answerField.setText("");
            feedbackLabel.setText("");
            imageLabel.setIcon(null);
            attemptCount = 0;
            ((CardLayout) taskPanel.getLayout()).show(taskPanel, "select");
        });
        questionPanel.add(homeButton);
    }

    private void showQuestion(int shapeId) {
        currentShapeId = shapeId;
        try {
            ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Shape" + shapeId + ".png"));
            Image scaledImage = rawIcon.getImage().getScaledInstance(300, 250, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception ex) {
            imageLabel.setText("❌ Image not found");
        }
        answerField.setText("");
        feedbackLabel.setText("");
        attemptCount = 0;
        ((CardLayout) taskPanel.getLayout()).show(taskPanel, "question");
    }

    private void handleSubmit(ActionEvent e) {
        try {
            double ans = Double.parseDouble(answerField.getText());
            double correct = correctAnswers.get(currentShapeId);
            if (Math.abs(ans - correct) < 0.01) {
                int score = switch (attemptCount) {
                    case 0 -> 3;
                    case 1 -> 2;
                    case 2 -> 1;
                    default -> 0;
                };
                feedbackLabel.setText("✅ Correct! +" + score + " points");
                scoreManager.addScore(score);
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