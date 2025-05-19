package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;
import com.shapeville.data.AngleData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

public class Task2AngleIdentification {
    public ScoreManager scoreManager;
    private final String[] encouragements = {
    "Well done!",
    "Great job!",
    "You're getting better!",
    "Excellent thinking!",
    "Smart answer!"
    };

    private JButton nextButton;  // ⏭️ 下一题按钮
    public JPanel task2;
    private JLabel questionLabel;
    private JTextField inputField;
    private JButton submitButton;
    public JButton goHomeButton;
    public Runnable onReturnHome;
    public JLabel scoreLabel;
    public int scores = 0;
    public Runnable onComplete;
    private JLabel mascotImageLabel;
    private JLabel mascotSpeechBubble;

    public int currentAngle = -1;
    public int attempt = 1;
    public Set<String> identifiedTypes = new HashSet<>();
    public boolean waitingForAngleInput = true;

    private AngleCanvas angleCanvas;

    public Task2AngleIdentification(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;

        // 设置主任务面板，米黄色背景
        task2 = new JPanel(new BorderLayout(10, 10));
        task2.setBackground(new Color(255, 250, 205)); // 米黄色
        task2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(255, 250, 205));

        scoreLabel = new JLabel("points: " + scoreManager.getScore());
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        topPanel.add(scoreLabel, BorderLayout.NORTH);

        questionLabel = new JLabel("Enter an angle (0-360, multiple of 10):");
        questionLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        questionLabel.setVerticalAlignment(JLabel.TOP);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(questionLabel, BorderLayout.CENTER);

        task2.add(topPanel, BorderLayout.NORTH);

        // 中间画布区域
        JPanel canvasPanel = new JPanel(new GridBagLayout());
        canvasPanel.setBackground(new Color(255, 250, 205));
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

        // 底部区域（输入 + 按钮）
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(new Color(255, 250, 205));
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.insets = new Insets(5, 5, 5, 5);

        inputField = new JTextField();
        inputField.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.gridwidth = 2;
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;
        gbcBottom.weightx = 1.0;
        bottomPanel.add(inputField, gbcBottom);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbcBottom.gridy = 1;
        gbcBottom.gridx = 1;
        gbcBottom.gridwidth = 1;
        gbcBottom.weightx = 0.5;
        bottomPanel.add(submitButton, gbcBottom);

        goHomeButton = new JButton("Return to Home");
        goHomeButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbcBottom.gridx = 0;
        bottomPanel.add(goHomeButton, gbcBottom);

        nextButton = new JButton("Next ▶");
        nextButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        nextButton.setVisible(false);
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 2;
        gbcBottom.gridwidth = 2;
        gbcBottom.weightx = 1.0;
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;
        bottomPanel.add(nextButton, gbcBottom);

        task2.add(bottomPanel, BorderLayout.SOUTH);

        // ✅ 右侧狐狸吉祥物区域
        JPanel mascotWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mascotWrapper.setBackground(new Color(255, 250, 205));

        JPanel mascotPanel = new JPanel();
        mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
        mascotPanel.setOpaque(false);

        mascotSpeechBubble = new JLabel("<html><div style='padding:10px; background:#fff8dc; border-radius:10px; border:1px solid #ccc;'>Let's start identifying angles! </div></html>");
        mascotSpeechBubble.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        mascotSpeechBubble.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            ImageIcon KuromiIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Kuromi.png"));
            Image scaledKuromi = KuromiIcon.getImage().getScaledInstance(160, 120, Image.SCALE_SMOOTH);
            mascotImageLabel = new JLabel(new ImageIcon(scaledKuromi));
        } catch (Exception ex) {
            //mascotImageLabel = new JLabel("🦊");
        }

        mascotImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mascotPanel.add(mascotSpeechBubble);
        mascotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mascotPanel.add(mascotImageLabel);

        mascotWrapper.add(mascotPanel);
        task2.add(mascotWrapper, BorderLayout.EAST);

        // 按钮交互逻辑
        goHomeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });

        nextButton.addActionListener(e -> {
            if (identifiedTypes.size() >= 5) {
                questionLabel.setText("You have identified all required angle types! Task Complete!");
                inputField.setVisible(false);
                submitButton.setVisible(false);
                nextButton.setVisible(false);
            } else {
                questionLabel.setText("Enter an angle (0-360, multiple of 10):");
                inputField.setText("");
                inputField.setVisible(true);
                submitButton.setVisible(true);
                nextButton.setVisible(false);
                waitingForAngleInput = true;
                angleCanvas.setAngle(-1);
                angleCanvas.repaint();
            }
        });

        submitButton.addActionListener(this::handleInput);

        // 启动初始化状态
        start();
    }

    public void start() {
        questionLabel.setText("Enter an angle (0-360, multiple of 10):");
        inputField.setText("");
        inputField.setVisible(true);
        submitButton.setVisible(true);
        //goHomeButton.setVisible(false);
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
                    questionLabel.setText("Invalid angle. Must be 0-360 and a multiple of 10.");
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
                questionLabel.setText("Please enter a number between 0 and 360.");
            }
        } else {
            String userAnswer = userInput;
            String correct = AngleData.classifyAngle(currentAngle);

            // if (userAnswer.equalsIgnoreCase(correct)) {
            //     questionLabel.setText("✅ Correct! It was a " + correct + " angle.");
            //     int points = switch (attempt) {
            //         case 1 -> 3;
            //         case 2 -> 2;
            //         case 3 -> 1;
            //         default -> 0;
            //     };
            //     scoreManager.addScore(points);
            //     result += points;
            //     scoreLabel.setText("points: " + result);
            //     identifiedTypes.add(correct.toLowerCase());
            //     checkCompletion();
            // }
            if (userAnswer.equalsIgnoreCase(correct)) {
                String encouragement = encouragements[(int) (Math.random() * encouragements.length)];
                questionLabel.setText("<html>Correct! It was a " + correct + " angle.<br>" + encouragement + "</html>");
                mascotSpeechBubble.setText("<html><div style='padding:10px; background:#e0ffe0; border-radius:10px; border:1px solid #8bc34a;'>Yay! It's a " + correct + " angle! </div></html>");

                int points = 0;
                if (!identifiedTypes.contains(correct)) {
                    points = switch (attempt) {
                        case 1 -> 3;
                        case 2 -> 2;
                        case 3 -> 1;
                        default -> 0;
                    };
                }
                waitingForAngleInput = true;
                scoreManager.addScore(points);
                scores += points;
                scoreLabel.setText("points: " + scores);
                identifiedTypes.add(correct.toLowerCase());

                inputField.setVisible(false);
                submitButton.setVisible(false);
                nextButton.setVisible(true);  // ⏭️ 等待点击“下一题”
            }
            else {
                attempt++;
                if (attempt > 3) {
                    // questionLabel.setText("The correct answer was: " + correct);
                    // identifiedTypes.add(correct.toLowerCase());
                    // checkCompletion();
                    waitingForAngleInput = true;
                    questionLabel.setText("<html>The correct answer was: <b>" + correct + "</b></html>");
                    mascotSpeechBubble.setText("<html><div style='padding:10px; background:#ffe0e0; border-radius:10px; border:1px solid #e57373;'>Oops! It was " + correct + "! Try harder next time! </div></html>");
                    identifiedTypes.add(correct.toLowerCase());

                    inputField.setVisible(false);
                    submitButton.setVisible(false);
                    nextButton.setVisible(true);  // ⏭️ 等待点击“下一题”
                } else {
                    questionLabel.setText("Incorrect. Try again. What type of angle? (Acute / Right / Obtuse / Reflex / Straight / Full)");
                    mascotSpeechBubble.setText("<html><div style='padding:10px; background:#fff3cd; border-radius:10px; border:1px solid #ffeb3b;'>Hmm... not quite! Guess again! </div></html>");

                }
            }
            inputField.setText("");
        }
    }

    private void checkCompletion() {
        if (identifiedTypes.size() == 4) {
            if (onComplete != null) {
                onComplete.run();
            }
            questionLabel.setText("You have identified all 4 types of angles! Task Complete!");
            inputField.setVisible(false);
            submitButton.setVisible(false);
            goHomeButton.setVisible(false);
        } else {
            questionLabel.setText("Enter an angle (0-360, multiple of 10):");
            waitingForAngleInput = true;
        }
    }

    static class AngleCanvas extends JPanel {
        private int angle = -1;

        public AngleCanvas() {
            setBackground(new Color(255, 250, 220)); // ✅ 设置画布背景为白色
        }

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
            g2.drawArc(centerX - radius/4, centerY - radius/4, radius/2, radius/2, 0, angle);
            }
        }
    }
}