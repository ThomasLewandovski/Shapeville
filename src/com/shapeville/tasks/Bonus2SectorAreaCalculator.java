// 包名保持不变
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
    public ScoreManager scoreManager;
    public int scores;
    public int completedTasks = 0;
    private JButton submitButton;

    private JPanel selectPanel;
    private JPanel questionPanel;
    private JPanel bottomPanel;

    private JLabel imageLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;
    public JLabel scoreLabel;
    public JLabel scorelable;

    private JLabel mascotSpeechBubble;
    private JLabel mascotImageLabel;

    private Timer countdownTimer;
    private int timeRemaining; // 单位：秒
    private final int TOTAL_TIME = 300; // 5分钟 = 300秒
    private JLabel timerLabel;

    public int currentShapeId;
    public int attemptCount;
    private Map<Integer, Double> correctAnswers;
    private Map<Integer, String> explanations;
    public Map<Integer, JButton> shapeButtons = new HashMap<>();
    public boolean[] completed = new boolean[9]; // 1-based indexing for 8 questions

    public Bonus2SectorAreaCalculator(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.taskPanel = new JPanel(new CardLayout());
        taskPanel.setBackground(new Color(255, 250, 205));

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
        explanations.put(1, """
            Area = (central angle / 360) × π × r²
                = (90 / 360) × 3.14 × 8 × 8
                = 50.24
            """);
        correctAnswers.put(2, 659.22);
        explanations.put(2, """
            Area = (central angle / 360) × π × r²
                = (130 / 360) × 3.14 × 18 × 18
                = 659.22
            """);

        correctAnswers.put(3, 189.65);
        explanations.put(3, """
            Area = (central angle / 360) × π × r²
                = (240 / 360) × 3.14 × 19 × 19
                = 189.65
            """);
        // correctAnswers.put(3, 377.85);
        // explanations.put(3, "Area = (120 / 360) × 3.14 × 19² = 377.85");//？？

        correctAnswers.put(4, 467.06);
        explanations.put(4, """
            Area = (central angle / 360) × π × r²
                = (110 / 360) × 3.14 × 22 × 22
                = 467.06
            """);

        correctAnswers.put(5, 107.24);
        explanations.put(5, """
            Area = (central angle / 360) × π × r²
                = (100 / 360) × 3.14 × 3.5 × 3.5
                = 107.24
            """);

        correctAnswers.put(6, 150.72);
        explanations.put(6, """
            Area = (central angle / 360) × π × r²
                = (270 / 360) × 3.14 × 8 × 8
                = 150.72
            """);
        correctAnswers.put(7, 377.0);
        explanations.put(7, """
            Area = (central angle / 360) × π × r²
                = (280 / 360) × 3.14 × 12 × 12
                = 377.00
            """);
        correctAnswers.put(8, 491.85);
        explanations.put(8, """
            Area = (central angle / 360) × π × r²
                = (250 / 360) × 3.14 × 15 × 15
                = 491.85
            """);
    }

    private void initSelectPanel() {
        selectPanel = new JPanel(new BorderLayout());
        selectPanel.setBackground(new Color(255, 250, 205));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS)); // 设置垂直布局
        titlePanel.setBackground(new Color(255, 250, 205));
        JLabel title = new JLabel("Select a Sector Shape:");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 18));

        scorelable = new JLabel("points: " + scoreManager.getScore());
        scorelable.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        scorelable.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        titlePanel.add(scorelable);
        titlePanel.add(title);

        selectPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 20, 20));
        buttonPanel.setBackground(new Color(255, 250, 205));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        for (int i = 1; i <= 8; i++) {
            int id = i;
            JButton btn;
            try {
                ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/circle" + i + ".png"));
                Image scaledImg = rawIcon.getImage().getScaledInstance(160, 120, Image.SCALE_SMOOTH);
                btn = new JButton(new ImageIcon(scaledImg));
            } catch (Exception ex) {
                btn = new JButton("circle" + i);
            }
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            shapeButtons.put(i, btn);

            btn.addActionListener(e -> {
                if (!completed[id]) {
                    submitButton.setEnabled(true);
                    showQuestion(id);
                }
            });
            buttonPanel.add(btn);
        }

        selectPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(255, 250, 205));
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });
        bottomPanel.add(homeButton);
        selectPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initQuestionPanel() {
        questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(new Color(255, 250, 205));

        // ✅ 修复：使用嵌套面板和强制最小尺寸
        JPanel scoreContainer = new JPanel(new BorderLayout());
        scoreContainer.setBackground(new Color(255, 250, 205));
        scoreContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 分数面板 - 使用FlowLayout并设置最小尺寸
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scorePanel.setBackground(new Color(255, 250, 205));
        scorePanel.setMinimumSize(new Dimension(0, 40)); // 强制最小高度

        scoreLabel = new JLabel("Score: " + scoreManager.getScore());
        scoreLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        scorePanel.add(scoreLabel);
        // 添加时钟标签
        timerLabel = new JLabel("Time Left: 05:00");
        timerLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        scorePanel.add(timerLabel);

        // 添加分隔线
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);

        scoreContainer.add(scorePanel, BorderLayout.NORTH);
        scoreContainer.add(separator, BorderLayout.SOUTH);

        questionPanel.add(scoreContainer, BorderLayout.NORTH);
        // 主内容区域
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(255, 250, 205));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.BOTH;

        imageLabel = new JLabel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        contentPanel.add(imageLabel, gbc);

        JLabel prompt = new JLabel("Enter the calculated area (2 decimals, Use π = 3.14):");
        prompt.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0.1;
        contentPanel.add(prompt, gbc);

        answerField = new JTextField(10);
        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        submitButton.addActionListener(this::handleSubmit);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(new Color(255, 250, 205));
        inputPanel.add(answerField);
        inputPanel.add(submitButton);
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(inputPanel, gbc);

        feedbackLabel = new JLabel("");
        feedbackLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        gbc.gridx = 1;
        gbc.gridy = 2;
        contentPanel.add(feedbackLabel, gbc);

        questionPanel.add(contentPanel, BorderLayout.CENTER);

        // 底部面板
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(255, 250, 205));
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        backButton.addActionListener(e -> {
            resetQuestion();
            ((CardLayout) taskPanel.getLayout()).show(taskPanel, "select");
        });
        backButton.setVisible(true);
        bottomPanel.add(backButton);

        // 吉祥物面板
        JPanel mascotWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mascotWrapper.setBackground(new Color(255, 250, 205));

        JPanel mascotPanel = new JPanel();
        mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
        mascotPanel.setOpaque(false);

        mascotSpeechBubble = new JLabel("<html><div style='padding:10px; background:#fff8dc; border-radius:10px; border:1px solid #ccc;'>Let's solve this problem together!</div></html>");
        mascotSpeechBubble.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        mascotSpeechBubble.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            ImageIcon bunnyIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Bunny.png"));
            Image scaled = bunnyIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            mascotImageLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception ex) {
            mascotImageLabel = new JLabel("🐰");
        }
        mascotImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mascotPanel.add(mascotSpeechBubble);
        mascotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mascotPanel.add(mascotImageLabel);
        mascotWrapper.add(mascotPanel);

        // 合并底部面板
        JPanel unifiedBottomPanel = new JPanel(new BorderLayout());
        unifiedBottomPanel.setBackground(new Color(255, 250, 205));
        unifiedBottomPanel.add(bottomPanel, BorderLayout.WEST);
        unifiedBottomPanel.add(mascotWrapper, BorderLayout.EAST);

        questionPanel.add(unifiedBottomPanel, BorderLayout.SOUTH);
    }

    private void showQuestion(int shapeId) {
        startCountdownTimer();
        currentShapeId = shapeId;
        attemptCount = 0;
        submitButton.setEnabled(true); // ✅ 这里确保按钮重新启用
        answerField.setText("");
        feedbackLabel.setText("");
        mascotSpeechBubble.setText("<html><div style='padding:10px; background:#fff8dc; border-radius:10px; border:1px solid #ccc;'>Let's take a look at this question!🐰</div></html>");
        scoreLabel.setText("Score: " + scores); // 确保每次显示问题时更新分数
        scorelable.setText("Score: " + scores);
        answerField.setEnabled(true);

        try {
            ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/circle" + shapeId + ".png"));
            Image scaledImage = rawIcon.getImage().getScaledInstance(300, 250, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception ex) {
            imageLabel.setText("Image not found");
        }

        //for (Component comp : bottomPanel.getComponents()) {
            // if (comp instanceof JButton && ((JButton) comp).getText().equals("Back")) {
            //     comp.setVisible(false);
            //     break;
            // }
        //}

        ((CardLayout) taskPanel.getLayout()).show(taskPanel, "question");

        timerLabel.setText("Time Left: 05:00");
    }

    private void startCountdownTimer() {
        timeRemaining = TOTAL_TIME;

        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;

            // ✅ 更新倒计时标签
            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;
            String timeFormatted = String.format("Time Left: %02d:%02d", minutes, seconds);
            timerLabel.setText(timeFormatted);

            // ✅ 超时处理
            if (timeRemaining <= 0) {
                countdownTimer.stop();

                if (submitButton.isEnabled()) {
                    submitButton.setEnabled(false);
                    answerField.setEnabled(false);
                    feedbackLabel.setText("⏰ Time is up! Let's see the solution:");

                    String explanation = explanations.get(currentShapeId).replace("\n", "<br>");
                    mascotSpeechBubble.setText("<html><div style='padding:10px; background:#ffe0e0;'>Time's up!<br>" + explanation + "</div></html>");

                    completeCurrentShape();
                }
            }
        });
        countdownTimer.start();
    }

    private void handleSubmit(ActionEvent e) {
        // if (countdownTimer != null && countdownTimer.isRunning()) {
        //     countdownTimer.stop();
        // }
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
                // submitButton.setVisible(false);
                if (countdownTimer != null && countdownTimer.isRunning()) {
                    countdownTimer.stop();// 停止计时器
                }
                submitButton.setEnabled(false);  // ✅ 禁用 submitButton
                scoreManager.addScore(score);
                scores+=score;
                feedbackLabel.setText("✅ Correct! +" + score + " points");
                mascotSpeechBubble.setText("<html><div style='padding:10px; background:#e0ffe0; border-radius:10px; border:1px solid #8bc34a;'>Great! You got it right!🎉</div></html>");
                completeCurrentShape();
            } else {
                attemptCount++;
                if (attemptCount >= 3) {
                    if (countdownTimer != null && countdownTimer.isRunning()) {
                        countdownTimer.stop();// 停止计时器
                    }
                    submitButton.setEnabled(false);
                    //feedbackLabel.setText("❌ Incorrect. " + explanations.get(currentShapeId));
                    //mascotSpeechBubble.setText("<html><div style='padding:10px; background:#ffe0e0; border-radius:10px; border:1px solid #e57373;'>Never mind, the correct answer is: " + explanations.get(currentShapeId) + " 🐰</div></html>");
                    feedbackLabel.setText("Oops! Not quite right, but don't worry—we'll figure it out!");
                     // ✅ 显示换行格式的分步解析
                    String explanation = explanations.get(currentShapeId).replace("\n", "<br>");
                    mascotSpeechBubble.setText("<html><div style='padding:10px; background:#ffe0e0;'>Here's the solution:<br>" + explanation + "</div></html>");
                    
                    completeCurrentShape();
                } else {
                    feedbackLabel.setText("❌ Try again. Attempts left: " + (3 - attemptCount));
                    mascotSpeechBubble.setText("<html><div style='padding:10px; background:#fff3cd; border-radius:10px; border:1px solid #ffeb3b;'>Think again! You can do it!🌟</div></html>");
                }
            }
            scoreLabel.setText("Score: " + scores); // 更新分数显示
            scorelable.setText("Score: " + scores);
        } catch (Exception ex) {
            feedbackLabel.setText("❌ Please enter a valid number.");
            mascotSpeechBubble.setText("<html><div style='padding:10px; background:#ffe0e0; border-radius:10px; border:1px solid #e57373;'>Please enter a valid number. 🐰</div></html>");
        }
    }

    private void completeCurrentShape() {
        completed[currentShapeId] = true;
        completedTasks++;

        if (shapeButtons.containsKey(currentShapeId)) {
            JButton btn = shapeButtons.get(currentShapeId);
            btn.setEnabled(false);
            btn.setBackground(Color.LIGHT_GRAY);
        }

        for (Component comp : bottomPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Back")) {
                comp.setVisible(true);
                break;
            }
        }

        answerField.setEnabled(false);
    }

    private void resetQuestion() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }//防止计时器残留
        timerLabel.setText("Time Left: 05:00");

        answerField.setText("");
        feedbackLabel.setText("");
        answerField.setEnabled(true);

        mascotSpeechBubble.setText("<html><div style='padding:10px; background:#fff8dc; border-radius:10px; border:1px solid #ccc;'>Let's solve this problem!🐰</div></html>");

        // for (Component comp : bottomPanel.getComponents()) {
        //     if (comp instanceof JButton && ((JButton) comp).getText().equals("Back")) {
        //         comp.setVisible(false);
        //         break;
        //     }
        // }
    }
}