package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;
import com.shapeville.data.AngleData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class Task2AngleIdentification {
    private ScoreManager scoreManager;
    public JPanel task2;
    private JLabel q;
    private JTextField input;
    private JButton submitButton;
    public JButton goHomeButton;
    public Runnable onReturnHome;
    private JLabel score;
    private int result = 0;

    private int currentAngle = -1;
    private int attempt = 1;
    private final Set<String> identifiedTypes = new HashSet<>();
    private boolean waitingForAngleInput = true;

    private AngleCanvas angleCanvas;

    public Task2AngleIdentification(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.task2 = new JPanel(null);

        this.score = new JLabel();
        score.setBounds(10, 0, 200, 40);
        score.setText("points: 0");
        task2.add(score);

        q = new JLabel("Enter an angle (0-360, multiple of 10):");
        q.setBounds(100, 20, 600, 30);
        task2.add(q);

        input = new JTextField();
        input.setBounds(100, 60, 400, 25);
        task2.add(input);

        submitButton = new JButton("Submit");
        submitButton.setBounds(500, 60, 100, 25);
        task2.add(submitButton);

        angleCanvas = new AngleCanvas();
        angleCanvas.setBounds(200, 100, 300, 300);
        task2.add(angleCanvas);

        goHomeButton = new JButton("🏠 Return to Home");
        goHomeButton.setBounds(200, 420, 200, 30);
        goHomeButton.setVisible(false);
        task2.add(goHomeButton);
        goHomeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });

        submitButton.addActionListener(e -> handleInput());
    }

    public void start() {
        q.setText("Enter an angle (0-360, multiple of 10):");
        input.setText("");
        input.setVisible(true);
        submitButton.setVisible(true);
        goHomeButton.setVisible(false);
        angleCanvas.setAngle(-1);
        angleCanvas.repaint();
        waitingForAngleInput = true;
        attempt = 1;
        identifiedTypes.clear();
    }

    private void handleInput() {
        String userInput = input.getText().trim();
        if (waitingForAngleInput) {
            try {
                int angle = Integer.parseInt(userInput);
                if (angle < 0 || angle > 360 || angle % 10 != 0) {
                    q.setText("❌ Invalid angle. Must be 0-360 and a multiple of 10.");
                    return;
                }
                currentAngle = angle;
                angleCanvas.setAngle(currentAngle);
                angleCanvas.repaint();
                q.setText("What type of angle is this? (Acute / Right / Obtuse / Reflex / Straight / Full)");
                input.setText("");
                waitingForAngleInput = false;
                attempt = 1;
            } catch (NumberFormatException e) {
                q.setText("❌ Please enter a number between 0 and 360.");
            }
        } else {
            String userAnswer = userInput;
            String correct = AngleData.classifyAngle(currentAngle);

            if (userAnswer.equalsIgnoreCase(correct)) {
                q.setText("✅ Correct! It was a " + correct + " angle.");
                int points = switch (attempt) {
                    case 1 -> 3;
                    case 2 -> 2;
                    case 3 -> 1;
                    default -> 0;
                };
                scoreManager.addScore(points);
                result += points;
                score.setText("points: " + result);
                identifiedTypes.add(correct.toLowerCase());
                checkCompletion();
            } else {
                attempt++;
                if (attempt > 3) {
                    q.setText("⚠️ The correct answer was: " + correct);
                    identifiedTypes.add(correct.toLowerCase());
                    checkCompletion();
                } else {
                    q.setText("❌ Incorrect. Try again. What type of angle?");
                }
            }
            input.setText("");
        }
    }

    private void checkCompletion() {
        if (identifiedTypes.size() >= 3) {
            q.setText("🎉 You have identified all 4 types of angles! Task Complete!");
            input.setVisible(false);
            submitButton.setVisible(false);
            goHomeButton.setVisible(true);
        } else {
            q.setText("Enter an angle (0-360, multiple of 10):");
            waitingForAngleInput = true;
        }
    }

    static class AngleCanvas extends JPanel {
        private int angle = -1;

        public void setAngle(int angle) {
            this.angle = angle;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(3));

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int radius = 100;

            g2.setColor(Color.BLACK);
            g2.drawLine(centerX, centerY, centerX + radius, centerY); // base line

            if (angle >= 0) {
                double rad = Math.toRadians(angle);
                int x = centerX + (int) (radius * Math.cos(rad));
                int y = centerY - (int) (radius * Math.sin(rad));
                g2.setColor(Color.RED);
                g2.drawLine(centerX, centerY, x, y);
            }
        }
    }
}