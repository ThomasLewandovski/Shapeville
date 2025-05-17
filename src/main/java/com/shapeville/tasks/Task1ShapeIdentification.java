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

    private String getRandomEncouragement() {
        int idx = (int) (Math.random() * encouragements.length);
        return encouragements[idx];
    }

    public Task1ShapeIdentification(ScoreManager scoreManager, int[] is_played_task1) {
        this.scoreManager = scoreManager;
        this.is_played_task1 = is_played_task1;

        // 使用BorderLayout作为主面板布局
        task1 = new JPanel(new BorderLayout(10, 10));
        task1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部面板 - 包含分数和说明
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

        // 中间面板 - 包含图像
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

        // 底部面板 - 包含输入框和按钮
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.insets = new Insets(5, 5, 5, 5);
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;

        input = new JTextField();
        input.setFont(new Font("Arial", Font.PLAIN, 16));
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.gridwidth = 2;
        gbcBottom.weightx = 1.0;
        bottomPanel.add(input, gbcBottom);

        goHomeButton = new JButton("🏠 Return to Home");
        goHomeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 1;
        gbcBottom.gridwidth = 1;
        gbcBottom.weightx = 0.5;
        bottomPanel.add(goHomeButton, gbcBottom);

        nextButton = new JButton("Next Question ▶");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbcBottom.gridx = 1;
        gbcBottom.gridy = 1;
        gbcBottom.weightx = 0.5;
        bottomPanel.add(nextButton, gbcBottom);

        task1.add(bottomPanel, BorderLayout.SOUTH);

        // 按钮事件处理
        goHomeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
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

        // 键盘事件处理
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (currentShapes == null) {
                        handleInput();
                    } else {
                        handleShapeAnswer();
                    }
                }
            }
        };
        input.addKeyListener(keyAdapter);

        // 初始隐藏下一题按钮
        nextButton.setVisible(false);
    }

    public void start() {
        output.setText("<html>📐 Task 1: Identify 2D / 3D Shapes<br>" +
                "1. 2D Shapes (Basic Level)<br>" +
                "2. 3D Shapes (Advanced Level)</html>");
    }

    private void handleInput() {
        String choice = input.getText().trim();
        switch (choice) {
            case "2D":
                if (is_played_task1[0] == 0) {
                    is_played_task1[0] = 1;
                    startSubtask("2D");
                } else {
                    JOptionPane.showMessageDialog(null, "<html>you have played this module,<br>" +
                            "please try other modules</html>", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "3D":
                if (is_played_task1[1] == 0) {
                    is_played_task1[1] = 1;
                    startSubtask("3D");
                } else {
                    JOptionPane.showMessageDialog(null, "<html>you have played this module,<br>" +
                            "please try other modules</html>", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            default:
                System.out.println("Invalid choice. Returning to home.");
        }
        input.setText("");
    }

    private void startSubtask(String type) {
        currentShapes = new ArrayList<>((type.equals("2D") ? ShapeData.getAll2DShapes() : ShapeData.getAll3DShapes()));
        isAdvanced = type.equals("3D");
        Collections.shuffle(currentShapes);
        currentIndex = 0;
        attempt = 1;

        // 限制每个子任务只有三个题目
        int maxQuestions = 4;
        if (currentShapes.size() > maxQuestions) {
            currentShapes = currentShapes.subList(0, maxQuestions);
        }
        if (currentIndex < currentShapes.size()) {
            currentShape = currentShapes.get(currentIndex);
            showShape();
        }
    }

    private void showShape() {
        String imgPath = "images/" + currentShape.getImageFilename();
        URL imageUrl = getClass().getClassLoader().getResource(imgPath);

        if (imageUrl != null) {
            // 加载原始图像
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image originalImage = originalIcon.getImage();

            // 获取面板的可用大小
            Dimension panelSize = img.getSize();
            if (panelSize.width <= 0 || panelSize.height <= 0) {
                // 如果面板大小尚未确定，使用默认大小
                panelSize = new Dimension(400, 400);
            }

            // 计算缩放比例
            int imgWidth = originalImage.getWidth(null);
            int imgHeight = originalImage.getHeight(null);
            double widthRatio = (double) panelSize.width / imgWidth;
            double heightRatio = (double) panelSize.height / imgHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            // 缩放图像
            int newWidth = (int) (imgWidth * ratio);
            int newHeight = (int) (imgHeight * ratio);
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            // 设置缩放后的图像
            img.setIcon(new ImageIcon(scaledImage));
        } else {
            System.out.println("Image not found: " + imgPath);
            img.setIcon(null);
            img.setText("Image Not Found");
        }

        output.setText("<html>What is the name of this shape?<br>" +
                "Your answer: ");
    }

    private void handleShapeAnswer() {
        String answer = input.getText().trim();
        input.setText("");
        if (checkAnswer(answer, currentShape.getName())) {
            int points = calculatePoints();
            scoreManager.addScore(points);
            score.setText("points: " + scoreManager.getScore());
            System.out.println("✅ Correct! You earned " + points + " points.");

            String encouragement = getRandomEncouragement();
            output.setText("<html>✅ Correct! +" + points + " points.<br>" + encouragement + "</html>");

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
        if (attempt == 1) {
            return isAdvanced ? 6 : 3;
        } else if (attempt == 2) {
            return isAdvanced ? 4 : 2;
        } else if (attempt == 3) {
            return isAdvanced ? 2 : 1;
        }
        return 0;
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
                "Click 'Return to Home' to go back.</html>");

        input.setEnabled(false);
        input.removeKeyListener(keyAdapter);
        nextButton.setVisible(false);
        goHomeButton.setVisible(true);
    }
}