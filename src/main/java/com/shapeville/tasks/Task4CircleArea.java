package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class   Task4CircleArea {
    // 在类顶部新增常量
    private static final String[] MODES = {"Area", "Perimeter"};
    private JPanel modeSelectionPanel;
    private boolean[] completedModes = new boolean[2];


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
    private int currentMode;

    public int radius;
    public int attempts;

    public Task4CircleArea(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.completedModes = new boolean[2];
        this.currentMode = 0;

        drawPanel = new DrawCirclePanel();

        // 主面板改为卡片布局
        task4 = new JPanel(new CardLayout());

        // 创建模式选择面板
        createModeSelectionPanel();
        // 创建计算面板（稍后通过模式选择触发）
        createCalculationPanel();

        task4.add(modeSelectionPanel, "modeSelection");
        ((CardLayout) task4.getLayout()).show(task4, "modeSelection");


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
        submitButton.addActionListener(e -> checkAnswer());
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) {
                onReturnHome.run();
            }
        });

        start();
    }

    // 新增方法：创建模式选择界面
    private void createModeSelectionPanel() {
        modeSelectionPanel = new JPanel(new BorderLayout());

        // 替换原有的图片面板为文字说明面板
        JPanel formulaPanel = createGuidePanel();
        formulaPanel.setPreferredSize(new Dimension(250, 400));

        // 模式选择按钮
        JPanel modePanel = new JPanel(new GridLayout(2, 1, 20, 20));
        for (int i = 0; i < MODES.length; i++) {
            JButton modeButton = new JButton(MODES[i]);
            modeButton.setFont(new Font("Arial", Font.BOLD, 24));
            int modeIndex = i;
            modeButton.addActionListener(e -> {
                if (!completedModes[modeIndex]) {
                    currentMode = modeIndex;
                    ((CardLayout)task4.getLayout()).show(task4, "calculation");
                    start();
                }
            });
            modeButton.setEnabled(!completedModes[modeIndex]);
            modePanel.add(modeButton);
        }

        // 返回主页按钮
        JButton homeButton = new JButton("返回主页");
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });

        // 保持原有布局结构
        modeSelectionPanel.add(formulaPanel, BorderLayout.WEST);
        modeSelectionPanel.add(modePanel, BorderLayout.CENTER);
        modeSelectionPanel.add(homeButton, BorderLayout.SOUTH);



    }

    // 修改后的start方法
    public void start() {
        input.setText("");
        feedbackLabel.setText("");
        formulaLabel.setText("");
        drawPanel.setVisible(false);
        radius = new Random().nextInt(20) + 1;
        attempts = 1;

        String modeText = currentMode == 0 ?
            "面积（π≈3.14）" :
            "周长（π≈3.14）";
        questionLabel.setText("🟢 圆形半径 = " + radius + "，计算" + modeText);
        drawPanel.setMode(currentMode);
    }

    private void handleWrongAnswer(double correctValue) {
        if (attempts == 3) {
            feedbackLabel.setText("❌ 已用尽所有尝试次数");
            formulaLabel.setText(currentMode == 0 ?
                    "公式：π×r² = 3.14×" + radius + "×" + radius + " = " + String.format("%.2f", correctValue) :
                    "公式：2πr = 2×3.14×" + radius + " = " + String.format("%.2f", correctValue));
            drawPanel.setRadius(radius);
            drawPanel.setVisible(true);
        } else {
            feedbackLabel.setText("❌ 错误，剩余尝试次数：" + (3 - attempts));
        }
    }

    // 新增文字说明面板创建方法
    private JPanel createGuidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("圆形公式指南"));
        panel.setBackground(new Color(245, 245, 245));

        // 标题
        JLabel title = new JLabel("圆形基本属性公式");
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        title.setForeground(new Color(0, 102, 204));

        // 半径说明
        JLabel radiusLabel = createFormulaLabel("半径 (r) = 直径 (D) ÷ 2", new Color(255, 140, 0));

        // 面积公式
        JLabel areaLabel = createFormulaLabel("面积 = π × r²", Color.DARK_GRAY);

        // 周长公式
        JLabel circumLabel1 = createFormulaLabel("周长 = 2 × π × r", new Color(0, 102, 204));
        JLabel circumLabel2 = createFormulaLabel("周长 = π × D", new Color(0, 102, 204));

        // 添加组件
        panel.add(Box.createVerticalStrut(10));
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(radiusLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(areaLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(circumLabel1);
        panel.add(circumLabel2);

        return panel;
    }


    // 辅助方法：创建统一样式的公式标签
    private JLabel createFormulaLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // 修改后的checkAnswer方法
    public void checkAnswer() {
        String userInput = input.getText().trim();
        double correctValue = currentMode == 0 ?
            3.14 * radius * radius :
            2 * 3.14 * radius;

        try {
            double userAnswer = Double.parseDouble(userInput);
            double diff = Math.abs(userAnswer - correctValue);
            if (diff <= 0.01) {
                int points = switch (attempts) {
                    case 1 -> 3;
                    case 2 -> 2;
                    case 3 -> 1;
                    default -> 0;
                };
                scoreManager.addScore(points);
                score.setText("分数: " + scoreManager.getScore());
                feedbackLabel.setText("✅ 正确！获得 " + points + " 分");

                // 标记当前模式已完成
                completedModes[currentMode] = true;

                // 检查是否全部完成
                if (completedModes[0] && completedModes[1]) {
                    if (onComplete != null) onComplete.run();
                }

                // 返回模式选择界面
                ((CardLayout)task4.getLayout()).show(task4, "modeSelection");

            } else {
                handleWrongAnswer(correctValue);
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("❌ 请输入有效数字");
        }
    }


    // 修改后的DrawCirclePanel类
    static class DrawCirclePanel extends JPanel {
        private int radius = 0;
        private int mode = 0;

        public void setMode(int mode) {
            this.mode = mode;
            repaint();
        }
        public void setRadius(int radius) {
            this.radius = radius;
            repaint(); // 更新绘制
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int maxRadius = Math.min(getWidth(), getHeight()) / 2 - 20;

            // 画圆和半径
            g2.setColor(Color.BLUE);
            g2.drawOval(centerX - maxRadius, centerY - maxRadius, maxRadius * 2, maxRadius * 2);
            g2.setColor(Color.RED);
            g2.drawLine(centerX, centerY, centerX + maxRadius, centerY);

            // 标注内容
            String formula = mode == 0 ?
                "面积 = π×r² = " + String.format("%.2f", 3.14 * radius * radius) :
                "周长 = 2πr = " + String.format("%.2f", 2 * 3.14 * radius);

            g2.setColor(Color.BLACK);
            g2.drawString("r = " + radius, centerX + maxRadius + 10, centerY + 5);
            g2.drawString(formula, centerX - maxRadius, centerY + maxRadius + 20);
        }
    }

    // 新增返回模式选择界面的方法
    private void createCalculationPanel() {
        // 保留原有布局结构，修改以下部分：
        // 将homeButton改为backButton
        JButton backButton = new JButton("返回选择");
        backButton.addActionListener(e -> {
            ((CardLayout)task4.getLayout()).show(task4, "modeSelection");
        });

        // 修改底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(drawPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 将修改后的面板添加到卡片布局
        JPanel calculationPanel = new JPanel(new BorderLayout());
        // ... 原有布局代码 ...
        calculationPanel.add(bottomPanel, BorderLayout.SOUTH);
        task4.add(calculationPanel, "calculation");
    }

}