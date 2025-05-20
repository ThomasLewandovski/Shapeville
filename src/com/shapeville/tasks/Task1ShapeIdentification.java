package com.shapeville.tasks;

import com.shapeville.data.ShapeData;
import com.shapeville.data.ShapeData.ShapeItem;
import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Task1ShapeIdentification {
    public int[] is_played_task1;
    public int isIdentifiedShapes;
    public Set<String> identifiedShapes = new HashSet<>();
    private final String[] encouragements = {
            "Well done!",
            "Excellent!",
            "You're a shape master!",
            "Great job!",
            "Smart thinking!",
            "Keep it up!"
    };
    public ScoreManager scoreManager;
    public int scores=0;
    public Runnable onReturnHome;
    public JButton nextButton;
    public JButton goHomeButton;
    public JButton btn2D;
    public JButton btn3D;
    public JPanel task1;
    public JLabel img;
    public JLabel output;
    public JTextField input;
    public JLabel scorelabel;
    private KeyAdapter keyAdapter;
    public Runnable onComplete;
    public JButton submitButton;

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
    private JLabel mascotLabel;

    private String getRandomEncouragement() {
        int idx = (int) (Math.random() * encouragements.length);
        return encouragements[idx];
    }

    public Task1ShapeIdentification(ScoreManager scoreManager, int[] is_played_task1) {
        this.scoreManager = scoreManager;
        this.is_played_task1 = is_played_task1.clone();
        Color backgroundColor = new Color(255, 250, 200);

        task1 = new JPanel(new BorderLayout(10, 10));
        task1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task1.setBackground(new Color(255, 250, 240)); // Floral White

        // 顶部面板（分数和提示）
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(backgroundColor); // 顶部背景设为米黄色

        scorelabel = new JLabel("points:" + scores);
        scorelabel.setFont(new Font("Arial", Font.BOLD, 16));
        scorelabel.setOpaque(true); // 让标签背景生效
        scorelabel.setBackground(backgroundColor);    // 标签背景设为相同色

        topPanel.add(scorelabel, BorderLayout.NORTH);

        output = new JLabel();
        output.setFont(new Font("Arial", Font.PLAIN, 16));
        output.setVerticalAlignment(JLabel.TOP);
        output.setHorizontalAlignment(JLabel.CENTER);
        output.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(output, BorderLayout.CENTER);

        task1.add(topPanel, BorderLayout.NORTH);

        JLabel mascot = new JLabel();
        ImageIcon raccoonIcon = new ImageIcon(getClass().getClassLoader().getResource("resouces/images/Raccoon.png"));
        Image raccoonImage = raccoonIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        //mascot.setIcon(new ImageIcon(raccoonImage));
        //mascot.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        //mascot.setHorizontalAlignment(SwingConstants.LEFT);
        //mascot.setVerticalAlignment(SwingConstants.TOP);

        // 输出标签（吉祥物说话）
        output = new JLabel();
        output.setFont(new Font("Arial", Font.PLAIN, 16));
        output.setVerticalAlignment(SwingConstants.TOP);
        output.setHorizontalAlignment(SwingConstants.LEFT);
        output.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel speechPanel = new JPanel(new BorderLayout());
        speechPanel.setBackground(backgroundColor);
        //speechPanel.add(score, BorderLayout.NORTH);
        speechPanel.add(output, BorderLayout.CENTER);

        topPanel.add(mascot, BorderLayout.WEST);
        topPanel.add(speechPanel, BorderLayout.CENTER);

        //task1.add(topPanel, BorderLayout.NORTH);


        // 使用CardLayout管理不同视图
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(backgroundColor);
        task1.add(cardPanel, BorderLayout.CENTER);

        cardPanel.setBackground(new Color(255, 239, 190));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // 预防缝隙

        // 初始化各视图面板
        initModeSelectionPanel();
        initQuestionPanel();
        initResultPanel();

        // 底部按钮面板
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(backgroundColor);
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.insets = new Insets(5, 5, 5, 5);
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;

        // 主页/返回按钮
        goHomeButton = new JButton(" Return to Home");
        styleButton(goHomeButton);
        //goHomeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.gridwidth = 1;
        gbcBottom.weightx = 0.5;
        bottomPanel.add(goHomeButton, gbcBottom);

        // 下一题按钮
        nextButton = new JButton("Next Question");
        styleButton(nextButton);
        //nextButton.setFont(new Font("Arial", Font.PLAIN, 14));
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
        panel.setBackground(new Color(255, 239, 190));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;

        // 左侧吉祥物
        JLabel mascot = new JLabel();
        mascot.setIcon(new ImageIcon(
                new ImageIcon(getClass().getClassLoader().getResource("resouces/images/Raccoon.png"))
                        .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)
        ));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        panel.add(mascot, gbc);

        // 右侧按钮面板（竖排）
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(new Color(255, 239, 190));
        mascot.setOpaque(true);
        mascot.setBackground(new Color(255, 239, 190));

        btn2D = new JButton("2D Shapes");
        btn2D.setFont(new Font("Arial", Font.BOLD, 18));
        btn2D.addActionListener(e -> selectShapeType("2D"));
        buttonPanel.add(btn2D);

        btn3D = new JButton("3D Shapes");
        btn3D.setFont(new Font("Arial", Font.BOLD, 18));
        btn3D.addActionListener(e -> selectShapeType("3D"));
        buttonPanel.add(btn3D);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(buttonPanel, gbc);

        cardPanel.add(panel, MODE_SELECTION);
    }
    private void initQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(255, 239, 190));

        // 图像面板
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setBackground(new Color(255, 239, 190));
        img = new JLabel();
        img.setHorizontalAlignment(JLabel.CENTER);
        img.setVerticalAlignment(JLabel.CENTER);
        img.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        imagePanel.add(img, gbc);

        // 吉祥物 + 输出区域
        JPanel feedbackPanel = new JPanel(new BorderLayout(5, 5));
        feedbackPanel.setBackground(new Color(255, 239, 190));

        mascotLabel = new JLabel();
        mascotLabel.setIcon(new ImageIcon(
                new ImageIcon(getClass().getClassLoader().getResource("resouces/images/Raccoon.png"))
                        .getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        mascotLabel.setHorizontalAlignment(SwingConstants.LEFT);

        output = new JLabel(" ", JLabel.LEFT);
        output.setFont(new Font("Arial", Font.PLAIN, 16));
        output.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        output.setVerticalAlignment(SwingConstants.TOP);

        feedbackPanel.add(mascotLabel, BorderLayout.WEST);
        feedbackPanel.add(output, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(new Color(255, 239, 190));
        center.add(imagePanel, BorderLayout.CENTER);
        center.add(feedbackPanel, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);

        // 输入框
        input = new JTextField();
        input.setFont(new Font("Arial", Font.PLAIN, 16));
        input.setBackground(new Color(255, 239, 190));
        input.setPreferredSize(new Dimension(200, 30));
        panel.add(input, BorderLayout.SOUTH);

        cardPanel.add(panel, QUESTION);

        // 创建提交按钮
        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(255, 228, 196));
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // 添加点击事件
        submitButton.addActionListener(e -> {
            if (isSubtaskStarted && !isSubtaskCompleted) {
                handleShapeAnswer();
            }
        });

        // 将输入框和提交按钮一起放入一个面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(255, 239, 190));
        inputPanel.add(input, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);

        // 加入主面板底部
        panel.add(inputPanel, BorderLayout.SOUTH);
    }

    private void initResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel resultLabel = new JLabel();
        panel.setBackground(new Color(255, 239, 190));
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
        if (is_played_task1[0] > 3) {
            btn2D.setEnabled(false);
            btn2D.setText("2D Shapes (Completed)");
        } else {
            btn2D.setEnabled(true);
            btn2D.setText("2D Shapes");
        }

        if (is_played_task1[1] > 3) {
            btn3D.setEnabled(false);
            btn3D.setText("3D Shapes (Completed)");
        } else {
            btn3D.setEnabled(true);
            btn3D.setText("3D Shapes");
        }

        // 更新Home按钮文本
        goHomeButton.setText(" Return to Home");
        nextButton.setVisible(false);

        scorelabel.setText("points: " + scoreManager.getScore());
    }

    private void selectShapeType(String type) {
        if ((type.equals("2D") && is_played_task1[0] > 3) ||
                (type.equals("3D") && is_played_task1[1] > 3)) {
            JOptionPane.showMessageDialog(null,
                    "<html>This module is completed.<br>Please try other modules.</html>",
                    "Prompt", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        startSubtask(type);

        // 更新Home按钮文本为返回
        goHomeButton.setText("Back to Selection");
    }
    private void startSubtask(String type) {
        // 获取所有对应类型的形状（2D或3D）
        currentShapes = new ArrayList<>(type.equals("2D") ? ShapeData.getAll2DShapes() : ShapeData.getAll3DShapes());


        // 过滤掉已识别的形状（保留未在 identifiedShapes 中的项）
        List<ShapeItem> filteredShapes = new ArrayList<>();
        for (ShapeItem shape : currentShapes) {
            if (!identifiedShapes.contains(shape.getName())) { // 使用相同的唯一标识判断
                filteredShapes.add(shape);
            }
        }

        // 处理剩余可选择的形状
        currentShapes = new ArrayList<>(filteredShapes);
        Collections.shuffle(currentShapes); // 打乱顺序

        // 计算最大问题数（未完成的次数）
        int maxQuestions = type.equals("2D") ?
                4 - is_played_task1[0] : 4 - is_played_task1[1];

        // 截取到最大问题数，但需确保不超过过滤后的数量
        int actualMax = Math.min(currentShapes.size(), maxQuestions);
        if (currentShapes.size() > actualMax) {
            currentShapes = currentShapes.subList(0, actualMax);
        }

        // 初始化当前索引和形状
        currentIndex = 0;
        attempt = 1;
        isSubtaskStarted = true;
        isSubtaskCompleted = false;

        if (!currentShapes.isEmpty()) {
            currentShape = currentShapes.get(currentIndex);
            showShape();
            cardLayout.show(cardPanel, QUESTION);
        } else {
            // 无可用形状时提示或结束任务
            JOptionPane.showMessageDialog(null,
                    "No new shapes available for this type!",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            finishTask();
        }
    }

    private void showShape() {
        submitButton.setEnabled(true);
        String imgPath = "resouces/images/" + currentShape.getImageFilename();
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

    // 替换原有的 handleShapeAnswer 方法
    private void handleShapeAnswer() {
        String answer = input.getText().trim();
        input.setText("");

        if (checkAnswer(answer, currentShape.getName())) {
            submitButton.setEnabled(false); // 答对或答错三次后禁用
            updatePlayCount();
            int points = calculatePoints();
            scoreManager.addScore(points);
            scores+=points;
            scorelabel.setText("points: " + scores);

            output.setText("<html><div style='padding:5px;border:2px solid rgb(255,239,190);background:#fff;border-radius:10px;'>"
                    + "Correct! +" + points + " points<br>" + getRandomEncouragement() + "</div></html>");
            input.setEnabled(false);
            identifiedShapes.add(currentShape.getName()); // 假设名称唯一，可替换为ID或其他唯一属性
            isIdentifiedShapes++;
            nextButton.setVisible(true);
        } else {
            attempt++;
            if (attempt <= 3) {
                output.setText("<html><div style='padding:5px;border:2px solid rgb(255,239,190);background:#fff;border-radius:10px;'>"
                        + "Incorrect. Try again.</div></html>");
            } else {
                submitButton.setEnabled(false); // 答对或答错三次后禁用
                updatePlayCount();
                output.setText("<html><div style='padding:5px;border:2px solid rgb(255,239,190);background:#fff;border-radius:10px;'>"
                        + "The correct answer was: <b>" + currentShape.getName() + "</b></div></html>");
                input.setEnabled(false);
                nextButton.setVisible(true);
                isIdentifiedShapes++;
                identifiedShapes.add(currentShape.getName()); // 假设名称唯一，可替换为ID或其他唯一属性

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

    public void onComplete() {

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

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(255, 228, 196)); // 更深一点的米色
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

}