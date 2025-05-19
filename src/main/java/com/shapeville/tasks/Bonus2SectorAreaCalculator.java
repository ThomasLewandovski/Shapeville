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
    public int completedTasks = 0;

    private JPanel selectPanel;
    private JPanel questionPanel;
    private JPanel bottomPanel;

    private JLabel imageLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;
    public JLabel scoreLabel;

    private JLabel mascotSpeechBubble;
    private JLabel mascotImageLabel;

    public int currentShapeId;
    public int attemptCount;
    private Map<Integer, Double> correctAnswers;
    private Map<Integer, String> explanations;
    private Map<Integer, JButton> shapeButtons = new HashMap<>();
    private boolean[] completed = new boolean[9]; // 1-based indexing for 8 questions

    public Bonus2SectorAreaCalculator(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.taskPanel = new JPanel(new CardLayout());
        taskPanel.setBackground(new Color(255, 250, 205)); // 温柔米黄背景

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
        selectPanel.setBackground(new Color(255, 250, 205));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(255, 250, 205));
        JLabel title = new JLabel("Select a Sector Shape:");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
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

        scoreLabel = new JLabel("Score: " + scoreManager.getScore());
        scoreLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        questionPanel.add(scoreLabel, BorderLayout.NORTH);

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

        JLabel prompt = new JLabel("Enter the calculated area (2 decimals):");
        prompt.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0.1;
        contentPanel.add(prompt, gbc);

        answerField = new JTextField(10);
        JButton submitButton = new JButton("Submit");
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

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(255, 250, 205));
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        backButton.addActionListener(e -> {
            resetQuestion();
            ((CardLayout) taskPanel.getLayout()).show(taskPanel, "select");
        });
        backButton.setVisible(false);
        bottomPanel.add(backButton);
        questionPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 🐰 添加吉祥物到右下角
        JPanel mascotWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mascotWrapper.setBackground(new Color(255, 250, 205));

        JPanel mascotPanel = new JPanel();
        mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
        mascotPanel.setOpaque(false);

        // 对话气泡
        mascotSpeechBubble = new JLabel("<html><div style='padding:10px; background:#fff8dc; border-radius:10px; border:1px solid #ccc;'>Let's solve this problem together!</div></html>");
        mascotSpeechBubble.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        mascotSpeechBubble.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 图像加载
        try {
            ImageIcon bunnyIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Bunny.png"));
            Image scaled = bunnyIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            mascotImageLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception ex) {
            mascotImageLabel = new JLabel("🐰");
        }
        mascotImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 组合吉祥物 panel
        mascotPanel.add(mascotSpeechBubble);
        mascotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mascotPanel.add(mascotImageLabel);

        // 加入底部右下
        mascotWrapper.add(mascotPanel);
        questionPanel.add(mascotWrapper, BorderLayout.SOUTH);
    }

    private void showQuestion(int shapeId) {
        currentShapeId = shapeId;
        attemptCount = 0;
        answerField.setText("");
        feedbackLabel.setText("");
        mascotSpeechBubble.setText("<html><div style='padding:10px; background:#fff8dc; border-radius:10px; border:1px solid #ccc;'>Let's take a look at this question!🐰</div></html>");
        scoreLabel.setText("Score: " + scoreManager.getScore());
        answerField.setEnabled(true);

        try {
            ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/circle" + shapeId + ".png"));
            Image scaledImage = rawIcon.getImage().getScaledInstance(300, 250, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception ex) {
            imageLabel.setText("Image not found");
        }

        for (Component comp : bottomPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Back")) {
                comp.setVisible(false);
                break;
            }
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
                mascotSpeechBubble.setText("<html><div style='padding:10px; background:#e0ffe0; border-radius:10px; border:1px solid #8bc34a;'>Great! You got it right!🎉</div></html>");
                completeCurrentShape();
            } else {
                attemptCount++;
                if (attemptCount >= 3) {
                    feedbackLabel.setText("❌ Incorrect. " + explanations.get(currentShapeId));
                    mascotSpeechBubble.setText("<html><div style='padding:10px; background:#ffe0e0; border-radius:10px; border:1px solid #e57373;'>Never mind, the correct answer is:" + explanations.get(currentShapeId) + " 🐰</div></html>");
                    completeCurrentShape();
                } else {
                    feedbackLabel.setText("❌ Try again. Attempts left: " + (3 - attemptCount));
                    mascotSpeechBubble.setText("<html><div style='padding:10px; background:#fff3cd; border-radius:10px; border:1px solid #ffeb3b;'>Think again! You can do it!🌟</div></html>");
                }
            }
            scoreLabel.setText("Score: " + scoreManager.getScore());
        } catch (Exception ex) {
            feedbackLabel.setText("❌ Please enter a valid number.");
            mascotSpeechBubble.setText("<html><div style='padding:10px; background:#ffe0e0; border-radius:10px; border:1px solid #e57373;'>Please enter a valid number.！🐰</div></html>");
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
        answerField.setText("");
        feedbackLabel.setText("");
        answerField.setEnabled(true);

        mascotSpeechBubble.setText("<html><div style='padding:10px; background:#fff8dc; border-radius:10px; border:1px solid #ccc;'>Let's solve this problem!🐰</div></html>");

        for (Component comp : bottomPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Back")) {
                comp.setVisible(false);
                break;
            }
        }
    }
}