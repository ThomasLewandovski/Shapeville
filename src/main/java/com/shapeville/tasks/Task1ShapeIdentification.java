package com.shapeville.tasks;

import com.shapeville.data.ShapeData;
import com.shapeville.data.ShapeData.ShapeItem;
import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task1ShapeIdentification {
    private final int[] is_played_task1;
    private final String[] encouragements = {
            "🎉 Well done!",
            "👏 Excellent!",
            "🌟 You're a shape master!",
            "👍 Great job!",
            "💡 Smart thinking!",
            "🔥 Keep it up!"
    };
    private ScoreManager scoreManager;
    public Runnable onReturnHome;
    public JButton nextButton;
    public JButton goHomeButton;
    public JButton goBackButton;
    public JPanel task1;
    public JLabel img;
    public JLabel output;
    public JTextField input;
    public JLabel score;
    private KeyAdapter keyAdapter;

    private List<ShapeItem> currentShapes;
    private ShapeItem currentShape;
    private int currentIndex = 0;
    private int attempt = 1;
    private boolean isAdvanced = false;
    private boolean isSubtaskStarted = false;
    private boolean isSubtaskCompleted = false; // 新增：子任务完成标记

    private String getRandomEncouragement() {
        int idx = (int) (Math.random() * encouragements.length);
        return encouragements[idx];
    }

    public Task1ShapeIdentification(ScoreManager scoreManager, int[] is_played_task1) {
        this.scoreManager = scoreManager;
        this.is_played_task1 = is_played_task1;

        task1 = new JPanel(new BorderLayout(10, 10));
        task1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        score = new JLabel("points: 0");
        score.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(score, BorderLayout.NORTH);

        output = new JLabel();
        output.setFont(new Font("Arial", Font.PLAIN, 16));
        output.setVerticalAlignment(JLabel.TOP);
        output.setHorizontalAlignment(JLabel.CENTER);
        output.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(output, BorderLayout.CENTER);

        task1.add(topPanel, BorderLayout.NORTH);

        // 中间图像面板
        JPanel imagePanel = new JPanel(new GridBagLayout());
        img = new JLabel();
        img.setHorizontalAlignment(JLabel.CENTER);
        img.setVerticalAlignment(JLabel.CENTER);
        img.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        imagePanel.add(img, gbc);

        task1.add(imagePanel, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.insets = new Insets(5, 5, 5, 5);
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;

        input = new JTextField();
        input.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.gridwidth = 3;
        gbcBottom.weightx = 1.0;
        bottomPanel.add(input, gbcBottom);

        goHomeButton = new JButton("🏠 Return to Home");
        goHomeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 1;
        gbcBottom.gridwidth = 1;
        gbcBottom.weightx = 0.5;
        bottomPanel.add(goHomeButton, gbcBottom);

        goBackButton = new JButton("🔙 Back to Selection");
        goBackButton.setFont(new Font("Arial", Font.PLAIN, 14));
        goBackButton.setVisible(false); // 初始隐藏
        gbcBottom.gridx = 1;
        gbcBottom.gridy = 1;
        gbcBottom.gridwidth = 1;
        gbcBottom.weightx = 0.5;
        bottomPanel.add(goBackButton, gbcBottom);

        nextButton = new JButton("Next Question ▶");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 14));
        nextButton.setVisible(false);
        gbcBottom.gridx = 2;
        gbcBottom.gridy = 1;
        gbcBottom.weightx = 0.5;
        bottomPanel.add(nextButton, gbcBottom);

        task1.add(bottomPanel, BorderLayout.SOUTH);

        // 按钮事件
        goHomeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });

        goBackButton.addActionListener(e -> {
            this.start(); // 重新启动任务
            isSubtaskStarted = false;
            isSubtaskCompleted = false;
            img.setIcon(null);
            input.setEnabled(true);
            input.requestFocus();
        });

        nextButton.addActionListener(e -> {
            currentIndex++;
            attempt = 1;
            if (currentIndex < currentShapes.size()) {
                currentShape = currentShapes.get(currentIndex);
                showShape();
                input.setEnabled(true);
                input.requestFocus();
                nextButton.setVisible(false);
            } else {
                finishTask();
            }
        });

        // 键盘事件
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!isSubtaskStarted) {
                        handleInput();
                    } else if (!isSubtaskCompleted) { // 未完成时处理答案
                        handleShapeAnswer();
                    }
                }
            }
        };
        input.addKeyListener(keyAdapter);
    }

    public void start() {
        output.setText("<html>📐 Task 1: Identify 2D / 3D Shapes<br>" +
                "1. 2D Shapes (Basic Level)<br>" +
                "2. 3D Shapes (Advanced Level)<br><br>" +
                "Type '2D' or '3D' to start:</html>");
        isSubtaskStarted = false;
        isSubtaskCompleted = false;
        goBackButton.setVisible(false);
        img.setIcon(null);
        input.setEnabled(true);
        input.requestFocus();
        score.setText("points: " + scoreManager.getScore());
    }

    private void handleInput() {
        String choice = input.getText().trim();
        switch (choice) {
            case "2D":
                if (is_played_task1[0] == 0) {
                    is_played_task1[0] = 1;
                    startSubtask("2D");
                } else {
                    JOptionPane.showMessageDialog(null, "<html>You have played this module,<br>" +
                            "please try other modules</html>", "Prompt", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "3D":
                if (is_played_task1[1] == 0) {
                    is_played_task1[1] = 1;
                    startSubtask("3D");
                } else {
                    JOptionPane.showMessageDialog(null, "<html>You have played this module,<br>" +
                            "please try other modules</html>", "Prompt", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            default:
                output.setText("Invalid input. Please type '2D' or '3D'.");
        }
        input.setText("");
    }

    private void startSubtask(String type) {
        currentShapes = new ArrayList<>((type.equals("2D") ? ShapeData.getAll2DShapes() : ShapeData.getAll3DShapes()));
        isAdvanced = type.equals("3D");
        Collections.shuffle(currentShapes);
        currentIndex = 0;
        attempt = 1;
        isSubtaskStarted = true;
        isSubtaskCompleted = false;
        goBackButton.setVisible(false); // 答题中隐藏返回按钮

        int maxQuestions = 4;
        if (currentShapes.size() > maxQuestions) {
            currentShapes = currentShapes.subList(0, maxQuestions);
        }
        if (currentShapes.size() > 0) {
            currentShape = currentShapes.get(currentIndex);
            showShape();
        } else {
            finishTask(); // 无题目时直接完成
        }
    }

    private void showShape() {
        String imgPath = "images/" + currentShape.getImageFilename();
        URL imageUrl = getClass().getClassLoader().getResource(imgPath);

        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image originalImage = originalIcon.getImage();
            Dimension panelSize = img.getSize();
            if (panelSize.width <= 0 || panelSize.height <= 0) {
                panelSize = new Dimension(400, 400);
            }

            double ratio = Math.min((double) panelSize.width / originalImage.getWidth(null),
                    (double) panelSize.height / originalImage.getHeight(null));
            int newWidth = (int) (originalImage.getWidth(null) * ratio);
            int newHeight = (int) (originalImage.getHeight(null) * ratio);
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            img.setIcon(new ImageIcon(scaledImage));
        } else {
            img.setIcon(null);
            img.setText("Image Not Found");
        }

        output.setText("<html>What is the name of this shape?<br>" +
                "Your answer: ");
        input.setEnabled(true);
        input.requestFocus();
    }

    private void handleShapeAnswer() {
        String answer = input.getText().trim();
        input.setText("");
        if (checkAnswer(answer, currentShape.getName())) {
            int points = calculatePoints();
            scoreManager.addScore(points);
            score.setText("points: " + scoreManager.getScore());
            output.setText("<html>✅ Correct! +" + points + " points.<br>" +
                    getRandomEncouragement() + "</html>");
            input.setEnabled(false);
            nextButton.setVisible(true);
        } else {
            attempt++;
            if (attempt <= 3) {
                output.setText("❌ Incorrect. Try again.");
            } else {
                output.setText("⚠️ The correct answer was: " + currentShape.getName());
                input.setEnabled(false);
                nextButton.setVisible(true);
            }
        }
    }

    private int calculatePoints() {
        if (attempt == 1) return isAdvanced ? 6 : 3;
        else if (attempt == 2) return isAdvanced ? 4 : 2;
        else if (attempt == 3) return isAdvanced ? 2 : 1;
        else return 0;
    }

    private boolean checkAnswer(String input, String correct) {
        return input.equalsIgnoreCase(correct);
    }

    public int[] getIs_played_task1() {
        return is_played_task1;
    }

    private void finishTask() {
        int finalScore = scoreManager.getScore();
        output.setText("<html>🎉 You've completed the " + (isAdvanced ? "3D" : "2D") + " shape task!<br>" +
                "🏆 Your total score: <b>" + finalScore + "</b> points.<br>" +
                "Click 'Back to Selection' to choose another module or 'Return to Home' to exit.</html>");

        input.setEnabled(false);
//        input.removeKeyListener(keyAdapter);
        nextButton.setVisible(false);
        isSubtaskCompleted = true; // 标记任务完成
        goBackButton.setVisible(true); // 仅完成后显示返回按钮
    }
}