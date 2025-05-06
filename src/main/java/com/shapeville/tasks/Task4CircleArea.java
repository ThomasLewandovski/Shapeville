package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;

public class Task4CircleArea {
    public JPanel task4;
    private JLabel questionLabel;
    private JTextField input;
    private JButton submitButton;
    private JButton homeButton;
    private JLabel feedbackLabel;
    private JLabel formulaLabel;
    private DrawCirclePanel drawPanel;
    private JLabel score;

    private ScoreManager scoreManager;
    public Runnable onReturnHome;

    private int radius;
    private int attempts;

    public Task4CircleArea(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        task4 = new JPanel(null);

        questionLabel = new JLabel();
        questionLabel.setBounds(50, 30, 400, 30);
        task4.add(questionLabel);

        input = new JTextField();
        input.setBounds(50, 70, 200, 30);
        task4.add(input);

        submitButton = new JButton("Submit");
        submitButton.setBounds(270, 70, 100, 30);
        task4.add(submitButton);

        feedbackLabel = new JLabel();
        feedbackLabel.setBounds(50, 110, 500, 30);
        task4.add(feedbackLabel);

        formulaLabel = new JLabel();
        formulaLabel.setBounds(50, 140, 600, 30);
        task4.add(formulaLabel);

        drawPanel = new DrawCirclePanel();
        drawPanel.setBounds(400, 20, 300, 300);
        task4.add(drawPanel);

        score = new JLabel();
        score.setText("Score: 0");
        score.setBounds(10, 0, 200, 40);
        task4.add(score);

        homeButton = new JButton("Home");
        homeButton.setBounds(650, 500, 100, 30);
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) {
                onReturnHome.run();
            }
        });
        task4.add(homeButton);

        submitButton.addActionListener(e -> checkAnswer());
    }

    public void start() {
        input.setText("");
        feedbackLabel.setText("");
        formulaLabel.setText("");
        drawPanel.setVisible(false);
        radius = new Random().nextInt(20) + 1;
        attempts = 1;
        questionLabel.setText("🟢 Given a circle with radius = " + radius + ", calculate its area (π≈3.14)");
    }

    private void checkAnswer() {
        String userInput = input.getText().trim();
        double correctArea = 3.14 * radius * radius;

        try {
            double userAnswer = Double.parseDouble(userInput);
            double diff = Math.abs(userAnswer - correctArea);
            if (diff <= 0.01) {
                int points = switch (attempts) {
                    case 1 -> 3;
                    case 2 -> 2;
                    case 3 -> 1;
                    default -> 0;
                };
                scoreManager.addScore(points);
                score.setText("Score: " + scoreManager.getScore());
                feedbackLabel.setText("✅ Correct! You earned " + points + " points.");
                start();
            } else {
                if (attempts == 3) {
                    feedbackLabel.setText("❌ You've used all attempts.");
                    formulaLabel.setText("公式：π×r² = 3.14×" + radius + "×" + radius + " = " + String.format("%.2f", correctArea));
                    drawPanel.setRadius(radius);
                    drawPanel.setVisible(true);
                } else {
                    feedbackLabel.setText("❌ Incorrect. Try again.");
                }
                attempts++;
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("❌ Please enter a valid number.");
        }
    }

    // 自定义面板绘制圆和半径
    static class DrawCirclePanel extends JPanel {
        private int radius = 0;

        public void setRadius(int radius) {
            this.radius = radius;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int drawRadius = 80;

            // 画圆
            g2.drawOval(centerX - drawRadius, centerY - drawRadius, drawRadius * 2, drawRadius * 2);

            // 画半径
            g2.drawLine(centerX, centerY, centerX + drawRadius, centerY);

            // 标注半径值
            g2.drawString("r = " + radius, centerX + drawRadius + 10, centerY);
        }
    }
}