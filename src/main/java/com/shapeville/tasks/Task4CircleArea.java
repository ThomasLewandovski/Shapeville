package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Task4CircleArea {
    private static final String[] MODES = {"Area", "Circumference"};
    public boolean[] completedModes;
    public int Completed=0;
    public int scores;
    private boolean isCurrentModeFailed = false; // ✅ 标记答错三次但尚未跳转

    public JPanel task4;
    private JPanel modeSelectionPanel;
    private JPanel calculationPanel;
    private JButton[] modeButtons = new JButton[2];

    public JLabel scorelable;
    private JLabel questionLabel;
    private JTextField input;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel formulaLabel;
    private DrawCirclePanel drawPanel;

    public ScoreManager scoreManager;
    public Runnable onReturnHome;
    public Runnable onComplete;

    public int currentMode;
    public int radius;
    public int attempts;

    private JLabel mascotSpeech;
    private JLabel mascotImage;

    public Task4CircleArea(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.completedModes = new boolean[2];

        task4 = new JPanel(new CardLayout());
        task4.setBackground(new Color(255, 250, 205)); // 米黄色背景

        createModeSelectionPanel();
        createCalculationPanel();

        task4.add(modeSelectionPanel, "modeSelection");
        task4.add(calculationPanel, "calculation");

        ((CardLayout) task4.getLayout()).show(task4, "modeSelection");
    }

    private void createModeSelectionPanel() {
        // 整体背景为米黄色
        modeSelectionPanel = new JPanel(new BorderLayout());
        modeSelectionPanel.setBackground(new Color(255, 250, 205));

        // 左侧公式面板
        JPanel formulaPanel = createGuidePanel();
        formulaPanel.setPreferredSize(new Dimension(250, 400));
        formulaPanel.setBackground(new Color(255, 250, 205)); // 米黄色

        // 中间按钮区
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        modePanel.setBackground(new Color(255, 250, 205));

        for (int i = 0; i < MODES.length; i++) {
            JButton modeButton = new JButton(MODES[i]);
            modeButtons[i] = modeButton;
            int modeIndex = i;

            modeButton.setPreferredSize(new Dimension(120, 40));
            modeButton.setFont(new Font("Arial", Font.BOLD, 16));

            modeButton.addActionListener(e -> {
                if (!completedModes[modeIndex]) {
                    currentMode = modeIndex;
                    ((CardLayout) task4.getLayout()).show(task4, "calculation");
                    start();
                }
            });

            modeButton.setEnabled(!completedModes[modeIndex]);
            modePanel.add(modeButton);
        }

        // 返回主菜单按钮
        JButton homeButton = new JButton("Back to Main");
        homeButton.setFont(new Font("Arial", Font.PLAIN, 13));
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });

        // 吉祥物区域：右下角 Totoro + 气泡
        JPanel mascotPanel = new JPanel();
        mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
        mascotPanel.setOpaque(false);

        JLabel mascotSpeech = new JLabel("<html><div style='padding:10px; background:#fff8dc; border:1px solid #aaa; border-radius:10px;'>Please select a mode to start!</div></html>");
        mascotSpeech.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        mascotSpeech.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel mascotImageLabel;
        try {
            ImageIcon totoroIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Totoro.png"));
            Image scaled = totoroIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            mascotImageLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception ex) {
            mascotImageLabel = new JLabel("🐾");
        }
        mascotImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mascotPanel.add(Box.createVerticalStrut(10));
        mascotPanel.add(mascotSpeech);
        mascotPanel.add(Box.createVerticalStrut(10));
        mascotPanel.add(mascotImageLabel);

        // 吉祥物包装底部对齐
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setBackground(new Color(255, 250, 205));
        eastPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        eastPanel.add(mascotPanel, BorderLayout.SOUTH);

        // 组装整个选择界面
        modeSelectionPanel.add(formulaPanel, BorderLayout.WEST);
        modeSelectionPanel.add(modePanel, BorderLayout.CENTER);
        modeSelectionPanel.add(eastPanel, BorderLayout.EAST);
        modeSelectionPanel.add(homeButton, BorderLayout.SOUTH);
    }

    private void createCalculationPanel() {
        calculationPanel = new JPanel(new BorderLayout(10, 10));
        calculationPanel.setBackground(new Color(255, 250, 205));

        // 顶部区域
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(255, 250, 205));
        scorelable = new JLabel("Score: 0");
        scorelable.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(scorelable, BorderLayout.NORTH);
        topPanel.add(questionLabel, BorderLayout.CENTER);

        // 中部输入区域
        JPanel middlePanel = new JPanel(new GridBagLayout());
        middlePanel.setBackground(new Color(255, 250, 205));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        input = new JTextField();
        input.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.7;
        middlePanel.add(input, gbc);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        middlePanel.add(submitButton, gbc);

        feedbackLabel = new JLabel();
        feedbackLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        middlePanel.add(feedbackLabel, gbc);

        formulaLabel = new JLabel();
        formulaLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        middlePanel.add(formulaLabel, gbc);

        // 吉祥物 + 图形区域
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBackground(new Color(255, 250, 205));

        drawPanel = new DrawCirclePanel();
        drawPanel.setPreferredSize(new Dimension(300, 300));
        drawPanel.setBackground(Color.WHITE);

        // 吉祥物区域（右下角）
        JPanel mascotPanel = new JPanel();
        mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
        mascotPanel.setBackground(new Color(255, 250, 205));

        mascotSpeech = new JLabel("<html><div style='padding:8px;background:#fff8dc;border:1px solid #aaa;border-radius:10px;'>Let's go!</div></html>");
        mascotSpeech.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        mascotSpeech.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            ImageIcon totoro = new ImageIcon(getClass().getClassLoader().getResource("images/Totoro.png"));
            Image scaled = totoro.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            mascotImage = new JLabel(new ImageIcon(scaled));
        } catch (Exception e) {
            mascotImage = new JLabel("Totoro");
        }
        mascotImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        mascotPanel.add(Box.createVerticalStrut(10));
        mascotPanel.add(mascotSpeech);
        mascotPanel.add(Box.createVerticalStrut(10));
        mascotPanel.add(mascotImage);

        // Bottom 内含绘图区域和 Totoro Panel
        bottomWrapper.add(drawPanel, BorderLayout.CENTER);
        bottomWrapper.add(mascotPanel, BorderLayout.EAST);

        // 按钮区
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(255, 250, 205));
        JButton backButton = new JButton("Back to Mode Select");
        backButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        buttonPanel.add(backButton);

        backButton.addActionListener(e -> {
            if (isCurrentModeFailed) {
                completeCurrentMode();
                isCurrentModeFailed = false;
            } else {
                refreshModeButtons();
                ((CardLayout) task4.getLayout()).show(task4, "modeSelection");
            }
        });

        bottomWrapper.add(buttonPanel, BorderLayout.SOUTH);

        calculationPanel.add(topPanel, BorderLayout.NORTH);
        calculationPanel.add(middlePanel, BorderLayout.CENTER);
        calculationPanel.add(bottomWrapper, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> checkAnswer());
    }

    public void start() {
        input.setText("");
        feedbackLabel.setText("");
        formulaLabel.setText("");
        drawPanel.setVisible(false);
        radius = new Random().nextInt(20) + 1;
        attempts = 1;
        isCurrentModeFailed = false;

        String modeText = currentMode == 0 ?
                "Area（π≈3.14）" :
                "Circumference（π≈3.14）";
        questionLabel.setText("The radius of a circle = " + radius + "，calculating" + modeText);
        drawPanel.setMode(currentMode);
    }

    public void checkAnswer() {
        String userInput = input.getText().trim();
        double correctValue = currentMode == 0
                ? 3.14 * radius * radius
                : 2 * 3.14 * radius;

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
                Completed++;
                scoreManager.addScore(points);

                scores+=points;
                scorelable.setText("Score: " + scores);
                feedbackLabel.setText("Correct! Obtaining " + points + " marks");

                completeCurrentMode();

            } else {
                handleWrongAnswer(correctValue);
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid number.");
        }
    }

    private void handleWrongAnswer(double correctValue) {
        if (attempts == 3) {
            Completed++;
            feedbackLabel.setText("All attempts have been exhausted");

            // Totoro 出来说正确答案
            mascotSpeech.setText("<html><div style='padding:10px;background:#ffe0e0;border:1px solid #cc0000;border-radius:10px;'>Oops! The correct formula is:<br>" +
                    formulaLabelFor(currentMode, radius, correctValue) + "</div></html>");

            drawPanel.setRadius(radius);
            drawPanel.setVisible(true);
            isCurrentModeFailed = true; // ✅ 标记当前模式已失败
        } else {
            feedbackLabel.setText("Wrong, remaining attempts: " + (3 - attempts));
            mascotSpeech.setText("<html><div style='padding:8px;background:#fff3cd;border:1px solid #ffcc00;border-radius:10px;'>Try again! You can do it </div></html>");
            attempts++;
        }
    }

    private void completeCurrentMode() {
        completedModes[currentMode] = true;

        if (modeButtons[currentMode] != null) {
            modeButtons[currentMode].setEnabled(false);
        }

        refreshModeButtons();

        if (completedModes[0] && completedModes[1]) {
            if (onComplete != null) onComplete.run();
        }

        ((CardLayout) task4.getLayout()).show(task4, "modeSelection");
    }

    private void refreshModeButtons() {
        for (int i = 0; i < modeButtons.length; i++) {
            if (modeButtons[i] != null) {
                modeButtons[i].setEnabled(!completedModes[i]);
            }
        }
    }

    private JPanel createGuidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Circle Formula Guide"));
        panel.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Basic Circle Properties");
        title.setFont(new Font("Arial", Font.BOLD, 12));
        title.setForeground(new Color(0, 102, 204));

        panel.add(Box.createVerticalStrut(10));
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(createFormulaLabel("Radius (r) = Diameter (D) ÷ 2", new Color(255, 140, 0)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormulaLabel("Area = π × r²", Color.DARK_GRAY));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createFormulaLabel("Circumference = 2 × π × r", new Color(0, 102, 204)));
        panel.add(createFormulaLabel("Circumference = π × D", new Color(0, 102, 204)));

        return panel;
    }

    private JLabel createFormulaLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    static class DrawCirclePanel extends JPanel {
        private int radius = 0;
        private int mode = 0;

        public void setMode(int mode) {
            this.mode = mode;
            repaint();
        }

        public void setRadius(int radius) {
            this.radius = radius;
            repaint();
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

            g2.setColor(Color.BLUE);
            g2.drawOval(centerX - maxRadius, centerY - maxRadius, maxRadius * 2, maxRadius * 2);
            g2.setColor(Color.RED);
            g2.drawLine(centerX, centerY, centerX + maxRadius, centerY);

            String formula = mode == 0
                    ? "Area = π×r² = " + String.format("%.2f", 3.14 * radius * radius)
                    : "Circumference = 2πr = " + String.format("%.2f", 2 * 3.14 * radius);

            g2.setColor(Color.BLACK);
            g2.drawString("r = " + radius, centerX + maxRadius + 10, centerY + 5);
            g2.drawString(formula, centerX - maxRadius, centerY + maxRadius + 20);
        }
    }

    private String formulaLabelFor(int mode, int radius, double correct) {
        return mode == 0
                ? "π × " + radius + "² = " + String.format("%.2f", correct)
                : "2 × π × " + radius + " = " + String.format("%.2f", correct);
    }
}