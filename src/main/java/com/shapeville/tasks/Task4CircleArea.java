package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class   Task4CircleArea {
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
    public Runnable onComplete;

    private int radius;
    private int attempts;

    public Task4CircleArea(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;

        // 使用BorderLayout作为主面板布局
        task4 = new JPanel(new BorderLayout(10, 10));
        task4.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部面板 - 包含分数和问题描述
        JPanel topPanel = new JPanel(new BorderLayout());
        score = new JLabel("Score: 0");
        score.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(score, BorderLayout.NORTH);

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionLabel.setVerticalAlignment(JLabel.TOP);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(questionLabel, BorderLayout.CENTER);

        task4.add(topPanel, BorderLayout.NORTH);

        // 中间面板 - 包含输入区域和反馈
        JPanel middlePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        input = new JTextField();
        input.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        middlePanel.add(input, gbc);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        middlePanel.add(submitButton, gbc);

        feedbackLabel = new JLabel();
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        middlePanel.add(feedbackLabel, gbc);

        formulaLabel = new JLabel();
        formulaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        middlePanel.add(formulaLabel, gbc);

        task4.add(middlePanel, BorderLayout.CENTER);

        // 底部面板 - 包含绘图区域和返回按钮
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        drawPanel = new DrawCirclePanel();
        drawPanel.setPreferredSize(new Dimension(300, 300));
        drawPanel.setMinimumSize(new Dimension(200, 200));
        drawPanel.setBackground(Color.WHITE);
        bottomPanel.add(drawPanel, BorderLayout.CENTER);

        homeButton = new JButton("Home");
        homeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        homeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JPanel homeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        homeButtonPanel.add(homeButton);
        bottomPanel.add(homeButtonPanel, BorderLayout.SOUTH);

        task4.add(bottomPanel, BorderLayout.SOUTH);

        // 按钮事件处理
        submitButton.addActionListener(e->checkAnswer());
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) {
                onReturnHome.run();
            }
        });

        start();
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

    public void checkAnswer() {
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

                // 触发完成回调
                if (onComplete != null) {
                    onComplete.run();
                }

                Timer timer = new Timer(1500, e -> start());
                timer.setRepeats(false);
                timer.start();
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

    // 自定义面板绘制圆和半径，支持自适应大小
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            // 使用面板尺寸的一半作为最大圆半径，留出边距
            int maxRadius = Math.min(getWidth(), getHeight()) / 2 - 20;

            // 画圆
            g2.setColor(Color.BLUE);
            g2.drawOval(centerX - maxRadius, centerY - maxRadius, maxRadius * 2, maxRadius * 2);

            // 画半径
            g2.setColor(Color.RED);
            g2.drawLine(centerX, centerY, centerX + maxRadius, centerY);

            // 标注半径值
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString("r = " + radius, centerX + maxRadius + 10, centerY + 5);

            // 标注面积公式和结果
            g2.setColor(Color.BLACK);
            g2.drawString("Area = π×r² = " + String.format("%.2f", 3.14 * radius * radius),
                    centerX - maxRadius, centerY + maxRadius + 20);
        }
    }
}