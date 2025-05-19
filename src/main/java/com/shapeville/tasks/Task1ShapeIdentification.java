package com.shapeville.tasks;

import com.shapeville.data.ShapeData;
import com.shapeville.data.ShapeData.ShapeItem;
import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task1ShapeIdentification {
    public int[] is_played_task1;
    public int isIdentifiedShapes;
    private final String[] encouragements = {
            "Well done!",
            "Excellent!",
            "You're a shape master!",
            "Great job!",
            "Smart thinking!",
            "Keep it up!"
    };
    public ScoreManager scoreManager;
    public Runnable onReturnHome;
    public JButton nextButton;
    public JButton goHomeButton;
    public JButton goBackButton;
    public JButton btn2D;
    public JButton btn3D;
    public JPanel task1;
    public JLabel img;
    public JLabel output;
    public JTextField input;
    public JLabel score;
    private KeyAdapter keyAdapter;
    public Runnable onComplete;

    public List<ShapeItem> currentShapes;
    public ShapeItem currentShape;
    public int currentIndex = 0;
    public int attempt = 1;
    public boolean isAdvanced = false;
    public boolean isSubtaskStarted = false;
    public boolean isSubtaskCompleted = false;

    // 定义CardLayout的面板标识符
    private static final String MODE_SELECTION = "MODE_SELECTION";
    private static final String QUESTION = "QUESTION";
    private static final String RESULT = "RESULT";

    private CardLayout cardLayout;
    private JPanel cardPanel; // 主内容面板

    private String getRandomEncouragement() {
        int idx = (int) (Math.random() * encouragements.length);
        return encouragements[idx];
    }

    public Task1ShapeIdentification(ScoreManager scoreManager, int[] is_played_task1) {
        this.scoreManager = scoreManager;
        this.is_played_task1 = is_played_task1.clone();

        task1 = new JPanel(new BorderLayout(10, 10));
        task1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部面板（分数和提示）
        JPanel topPanel = new JPanel(new BorderLayout());
        score = new JLabel("points:" + scoreManager.getScore());
        score.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(score, BorderLayout.NORTH);

        output = new JLabel();
        output.setFont(new Font("Arial", Font.PLAIN, 16));
        output.setVerticalAlignment(JLabel.TOP);
        output.setHorizontalAlignment(JLabel.CENTER);
        output.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(output, BorderLayout.CENTER);

        task1.add(topPanel, BorderLayout.NORTH);

        // 使用CardLayout管理不同视图
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        task1.add(cardPanel, BorderLayout.CENTER);

        // 初始化各视图面板
        initModeSelectionPanel();
        initQuestionPanel();
        initResultPanel();

        // 底部按钮面板
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.insets = new Insets(5, 5, 5, 5);
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;

        // 主页/返回按钮
        goHomeButton = new JButton(" Return to Home");
        goHomeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.gridwidth = 1;
        gbcBottom.weightx = 0.5;
        bottomPanel.add(goHomeButton, gbcBottom);

        // 下一题按钮
        nextButton = new JButton("Next Question ▶");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 14));
        nextButton.setVisible(false);
        gbcBottom.gridx = 1;
        gbcBottom.gridy = 0;
        gbcBottom.gridwidth = 1;
        gbcBottom.weightx = 0.5;
        bottomPanel.add(nextButton, gbcBottom);

        task1.add(bottomPanel, BorderLayout.SOUTH);

        // 按钮事件处理
        goHomeButton.addActionListener(e -> {
            if (!isSubtaskStarted) {
                // 在模式选择界面点击Home，返回主菜单
                if (onReturnHome != null) onReturnHome.run();
            } else {
                // 在答题界面点击Home，返回模式选择
                showModeSelection();
            }
        });

        nextButton.addActionListener(e -> {
            currentIndex++;
            attempt = 1;
            if (currentIndex < currentShapes.size()) {
                currentShape = currentShapes.get(currentIndex);
                showShape();
                cardLayout.show(cardPanel, QUESTION);
            } else {
                finishTask();
            }
        });

        // 键盘事件
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && isSubtaskStarted && !isSubtaskCompleted) {
                    handleShapeAnswer();
                }
            }
        };
        input.addKeyListener(keyAdapter);

        // 初始化显示模式选择界面
        showModeSelection();
    }

    private void initModeSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 2D/3D选择按钮
        btn2D = new JButton("2D Shapes");
        btn2D.setFont(new Font("Arial", Font.PLAIN, 16));
        btn2D.addActionListener(e -> selectShapeType("2D"));
        panel.add(btn2D, gbc);

        gbc.gridy = 1;
        btn3D = new JButton("3D Shapes");
        btn3D.setFont(new Font("Arial", Font.PLAIN, 16));
        btn3D.addActionListener(e -> selectShapeType("3D"));
        panel.add(btn3D, gbc);

        cardPanel.add(panel, MODE_SELECTION);
    }

    private void initQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // 图像面板
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

        panel.add(imagePanel, BorderLayout.CENTER);

        // 输入框
        input = new JTextField();
        input.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(input, BorderLayout.SOUTH);

        cardPanel.add(panel, QUESTION);
    }

    private void initResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel resultLabel = new JLabel();
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        resultLabel.setHorizontalAlignment(JLabel.CENTER);
        resultLabel.setVerticalAlignment(JLabel.CENTER);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(resultLabel, BorderLayout.CENTER);

        cardPanel.add(panel, RESULT);
    }

    public void showModeSelection() {
        isSubtaskStarted = false;
        isSubtaskCompleted = false;
        img.setIcon(null);
        input.setText("");

        output.setText("<html> Task 1: Identify 2D / 3D Shapes<br><br>" +
                "Select the type of shapes you want to practice:</html>");

        // 显示模式选择面板
        cardLayout.show(cardPanel, MODE_SELECTION);

        // 检查是否已达到最大练习次数
        if (is_played_task1[0] >= 3) {
            btn2D.setEnabled(false);
            btn2D.setText("2D Shapes (Completed)");
        } else {
            btn2D.setEnabled(true);
            btn2D.setText("2D Shapes");
        }

        if (is_played_task1[1] >= 3) {
            btn3D.setEnabled(false);
            btn3D.setText("3D Shapes (Completed)");
        } else {
            btn3D.setEnabled(true);
            btn3D.setText("3D Shapes");
        }

        // 更新Home按钮文本
        goHomeButton.setText(" Return to Home");
        nextButton.setVisible(false);

        score.setText("points: " + scoreManager.getScore());
    }

    private void selectShapeType(String type) {
        if ((type.equals("2D") && is_played_task1[0] >= 3) ||
                (type.equals("3D") && is_played_task1[1] >= 3)) {
            JOptionPane.showMessageDialog(null,
                    "<html>This module is completed.<br>Please try other modules.</html>",
                    "Prompt", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        startSubtask(type);

        // 更新Home按钮文本为返回
        goHomeButton.setText("🔙 Back to Selection");
    }

    private void startSubtask(String type) {
        currentShapes = new ArrayList<>(type.equals("2D") ? ShapeData.getAll2DShapes() : ShapeData.getAll3DShapes());
        Collections.shuffle(currentShapes);
        isAdvanced = type.equals("3D");
        currentIndex = 0;
        attempt = 1;
        isSubtaskStarted = true;
        isSubtaskCompleted = false;

        int maxQuestions = type.equals("2D") ?
                4 - is_played_task1[0] : 4 - is_played_task1[1];

        if (currentShapes.size() > maxQuestions) {
            currentShapes = currentShapes.subList(0, maxQuestions);
        }

        if (!currentShapes.isEmpty()) {
            currentShape = currentShapes.get(currentIndex);
            showShape();
            cardLayout.show(cardPanel, QUESTION);
        } else {
            finishTask();
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

            double ratio = Math.min(
                    (double) panelSize.width / originalImage.getWidth(null),
                    (double) panelSize.height / originalImage.getHeight(null)
            );
            int newWidth = (int) (originalImage.getWidth(null) * ratio);
            int newHeight = (int) (originalImage.getHeight(null) * ratio);
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            img.setIcon(new ImageIcon(scaledImage));
        } else {
            img.setIcon(null);
            img.setText("Image Not Found");
        }

        output.setText("<html>What is the name of this shape?<br>" +
                "Your answer: </html>");
        input.setEnabled(true);
        input.requestFocus();
        nextButton.setVisible(false);
    }

    private void handleShapeAnswer() {
        String answer = input.getText().trim();
        input.setText("");

        if (checkAnswer(answer, currentShape.getName())) {
            updatePlayCount();
            int points = calculatePoints();
            scoreManager.addScore(points);
            score.setText("points: " + scoreManager.getScore());
            output.setText("<html> Correct! +" + points + " points.<br>" +
                    getRandomEncouragement() + "</html>");
            input.setEnabled(false);
            isIdentifiedShapes++;
            nextButton.setVisible(true);
        } else {
            attempt++;
            if (attempt <= 3) {
                output.setText("Incorrect. Try again.");
            } else {
                updatePlayCount();
                output.setText("The correct answer was: " + currentShape.getName());
                input.setEnabled(false);
                nextButton.setVisible(true);
                isIdentifiedShapes++;
            }
        }
    }

    private void updatePlayCount() {
        if (currentShape instanceof ShapeData.Shape2D) {
            is_played_task1[0]++;
        } else if (currentShape instanceof ShapeData.Shape3D) {
            is_played_task1[1]++;
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
        isSubtaskCompleted = true;

        // 更新结果面板
        JLabel resultLabel = (JLabel) ((JPanel) cardPanel.getComponent(2)).getComponent(0);
        String taskType = isAdvanced ? "3D" : "2D";
        resultLabel.setText("<html> Task Complete! (" + taskType + " Shapes)<br>" +
                " Total Score: <b>" + scoreManager.getScore() + "</b> points<br><br>" +
                "Click 'Back to Selection' to try another module or 'Return to Home' to exit.</html>");
        output.setText("");

        cardLayout.show(cardPanel, RESULT);
        nextButton.setVisible(false);

        if (onComplete != null) {
            onComplete.run();
        }
    }
}