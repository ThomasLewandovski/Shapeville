package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;
import com.shapeville.data.AngleData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class Task2AngleIdentification {
    private ScoreManager scoreManager;
    public JPanel task2;
    private JLabel questionLabel;
    private JTextField inputField;
    private JButton submitButton;
    public JButton goHomeButton;
    public Runnable onReturnHome;
    private JLabel scoreLabel;
    private int result = 0;

    private int currentAngle = -1;
    private int attempt = 1;
    private final Set<String> identifiedTypes = new HashSet<>();
    private boolean waitingForAngleInput = true;

    private AngleCanvas angleCanvas;

    public Task2AngleIdentification(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;

        // 使用BorderLayout作为主面板布局
        task2 = new JPanel(new BorderLayout(10, 10));
        task2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部面板 - 包含分数和问题描述
        JPanel topPanel = new JPanel(new BorderLayout());
        scoreLabel = new JLabel("points: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(scoreLabel, BorderLayout.NORTH);

        questionLabel = new JLabel("Enter an angle (0-360, multiple of 10):");
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionLabel.setVerticalAlignment(JLabel.TOP);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(questionLabel, BorderLayout.CENTER);

        task2.add(topPanel, BorderLayout.NORTH);

        // 中间面板 - 包含角度画布
        JPanel canvasPanel = new JPanel(new GridBagLayout());
        angleCanvas = new AngleCanvas();
        angleCanvas.setPreferredSize(new Dimension(300, 300));
        angleCanvas.setMinimumSize(new Dimension(200, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        canvasPanel.add(angleCanvas, gbc);

        task2.add(canvasPanel, BorderLayout.CENTER);

        // 底部面板 - 包含输入框、按钮
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.insets = new Insets(5, 5, 5, 5);
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.weightx = 0.8;
        bottomPanel.add(inputField, gbcBottom);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbcBottom.gridx = 1;
        gbcBottom.gridy = 0;
        gbcBottom.weightx = 0.2;
        bottomPanel.add(submitButton, gbcBottom);

        goHomeButton = new JButton("🏠 Return to Home");
        goHomeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 1;
        gbcBottom.gridwidth = 2;
        gbcBottom.anchor = GridBagConstraints.CENTER;
        gbcBottom.fill = GridBagConstraints.NONE;
        goHomeButton.setVisible(false);
        bottomPanel.add(goHomeButton, gbcBottom);

        task2.add(bottomPanel, BorderLayout.SOUTH);

        // 按钮事件处理
        goHomeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });

        submitButton.addActionListener(this::handleInput);
    }

    public void start() {
        questionLabel.setText("Enter an angle (0-360, multiple of 10):");
        inputField.setText("");
        inputField.setVisible(true);
        submitButton.setVisible(true);
        goHomeButton.setVisible(false);
        angleCanvas.setAngle(-1);
        angleCanvas.repaint();
        waitingForAngleInput = true;
        attempt = 1;
        identifiedTypes.clear();
    }

    private void handleInput(ActionEvent e) {
        String userInput = inputField.getText().trim();
        if (waitingForAngleInput) {
            try {
                int angle = Integer.parseInt(userInput);
                if (angle < 0 || angle > 360 || angle % 10 != 0) {
                    questionLabel.setText("❌ Invalid angle. Must be 0-360 and a multiple of 10.");
                    return;
                }
                currentAngle = angle;
                angleCanvas.setAngle(currentAngle);
                angleCanvas.repaint();
                questionLabel.setText("What type of angle is this? (Acute / Right / Obtuse / Reflex / Straight / Full)");
                inputField.setText("");
                waitingForAngleInput = false;
                attempt = 1;
            } catch (NumberFormatException ex) {
                questionLabel.setText("❌ Please enter a number between 0 and 360.");
            }
        } else {
            String userAnswer = userInput;
            String correct = AngleData.classifyAngle(currentAngle);

            if (userAnswer.equalsIgnoreCase(correct)) {
                questionLabel.setText("✅ Correct! It was a " + correct + " angle.");
                int points = switch (attempt) {
                    case 1 -> 3;
                    case 2 -> 2;
                    case 3 -> 1;
                    default -> 0;
                };
                scoreManager.addScore(points);
                result += points;
                scoreLabel.setText("points: " + result);
                identifiedTypes.add(correct.toLowerCase());
                checkCompletion();
            } else {
                attempt++;
                if (attempt > 3) {
                    questionLabel.setText("⚠️ The correct answer was: " + correct);
                    identifiedTypes.add(correct.toLowerCase());
                    checkCompletion();
                } else {
                    questionLabel.setText("❌ Incorrect. Try again. What type of angle?");
                }
            }
            inputField.setText("");
        }
    }

    private void checkCompletion() {
        if (identifiedTypes.size() >= 3) {
            questionLabel.setText("🎉 You have identified all 4 types of angles! Task Complete!");
            inputField.setVisible(false);
            submitButton.setVisible(false);
            goHomeButton.setVisible(true);
        } else {
            questionLabel.setText("Enter an angle (0-360, multiple of 10):");
            waitingForAngleInput = true;
        }
    }

    static class AngleCanvas extends JPanel {
        private int angle = -1;

        public void setAngle(int angle) {
            this.angle = angle;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(3));

            // 计算中心和半径，适应面板大小
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int radius = Math.min(getWidth(), getHeight()) / 3;

            // 绘制基线
            g2.setColor(Color.BLACK);
            g2.drawLine(centerX, centerY, centerX + radius, centerY);

            // 如果有角度值，绘制角度线
            if (angle >= 0) {
                double rad = Math.toRadians(angle);
                int x = centerX + (int) (radius * Math.cos(rad));
                int y = centerY - (int) (radius * Math.sin(rad));
                g2.setColor(Color.RED);
                g2.drawLine(centerX, centerY, x, y);

                // 绘制角度弧线
                g2.setColor(Color.BLUE);
                if (angle <= 180) {
                    g2.drawArc(centerX - radius/4, centerY - radius/4, radius/2, radius/2, 0, -angle);
                } else {
                    g2.drawArc(centerX - radius/4, centerY - radius/4, radius/2, radius/2, 180, -(angle - 180));
                }
            }
        }
    }
}