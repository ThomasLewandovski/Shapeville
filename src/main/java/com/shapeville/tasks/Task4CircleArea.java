package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Task4CircleArea {
    private static final String[] MODES = {"Area", "Perimeter"};
    private boolean[] completedModes = new boolean[2];

    public JPanel task4;
    private JPanel modeSelectionPanel;
    private JPanel calculationPanel;

    private JLabel score;
    private JLabel questionLabel;
    private JTextField input;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel formulaLabel;
    private DrawCirclePanel drawPanel;

    private ScoreManager scoreManager;
    public Runnable onReturnHome;
    public Runnable onComplete;

    private int currentMode;
    public int radius;
    public int attempts;

    public Task4CircleArea(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.completedModes = new boolean[2];

        task4 = new JPanel(new CardLayout());

        createModeSelectionPanel();
        createCalculationPanel();

        task4.add(modeSelectionPanel, "modeSelection");
        task4.add(calculationPanel, "calculation");

        ((CardLayout) task4.getLayout()).show(task4, "modeSelection");
    }

    private void createModeSelectionPanel() {
        modeSelectionPanel = new JPanel(new BorderLayout());

        JPanel formulaPanel = createGuidePanel();
        formulaPanel.setPreferredSize(new Dimension(250, 400));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        for (int i = 0; i < MODES.length; i++) {
            JButton modeButton = new JButton(MODES[i]);
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

        JButton homeButton = new JButton("Back to Main");
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });

        modeSelectionPanel.add(formulaPanel, BorderLayout.WEST);
        modeSelectionPanel.add(modePanel, BorderLayout.CENTER);
        modeSelectionPanel.add(homeButton, BorderLayout.SOUTH);
    }

    private void createCalculationPanel() {
        calculationPanel = new JPanel(new BorderLayout(10, 10));

        // Top
        JPanel topPanel = new JPanel(new BorderLayout());
        score = new JLabel("Score: 0");
        score.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(score, BorderLayout.NORTH);
        topPanel.add(questionLabel, BorderLayout.CENTER);

        // Middle
        JPanel middlePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        input = new JTextField();
        input.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.7;
        middlePanel.add(input, gbc);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        middlePanel.add(submitButton, gbc);

        feedbackLabel = new JLabel();
        feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        middlePanel.add(feedbackLabel, gbc);

        formulaLabel = new JLabel();
        formulaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        middlePanel.add(formulaLabel, gbc);

        // Bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        drawPanel = new DrawCirclePanel();
        drawPanel.setPreferredSize(new Dimension(300, 300));
        drawPanel.setBackground(Color.WHITE);
        bottomPanel.add(drawPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Mode Select");
        backButton.addActionListener(e -> {
            ((CardLayout) task4.getLayout()).show(task4, "modeSelection");
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Put together
        calculationPanel.add(topPanel, BorderLayout.NORTH);
        calculationPanel.add(middlePanel, BorderLayout.CENTER);
        calculationPanel.add(bottomPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> checkAnswer());
    }

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

                completedModes[currentMode] = true;
                if (completedModes[0] && completedModes[1]) {
                    if (onComplete != null) onComplete.run();
                }

                ((CardLayout) task4.getLayout()).show(task4, "modeSelection");
            } else {
                handleWrongAnswer(correctValue);
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("❌ 请输入有效数字");
        }
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
            attempts++;
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

            String formula = mode == 0 ?
                    "面积 = π×r² = " + String.format("%.2f", 3.14 * radius * radius) :
                    "周长 = 2πr = " + String.format("%.2f", 2 * 3.14 * radius);

            g2.setColor(Color.BLACK);
            g2.drawString("r = " + radius, centerX + maxRadius + 10, centerY + 5);
            g2.drawString(formula, centerX - maxRadius, centerY + maxRadius + 20);
        }
    }
}