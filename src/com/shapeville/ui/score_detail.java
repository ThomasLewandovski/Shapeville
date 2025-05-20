package com.shapeville.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class score_detail {

    JPanel panel;
    private JLabel[] mascotLabels;
    private JLabel[] scoreLabels;

    // 任务分数数据（示例数据）
    private int[] scores;

    // 吉祥物图片路径
    private String[] mascotPaths = {
            "resources/images/Raccoon.png",
            "resources/images/Kuromi.png",
            "resources/images/Pikachu.png",
            "resources/images/Totoro.png",
            "resources/images/Fox.png",
            "resources/images/Bunny.png"
    };

    // 吉祥物名称
    private String[] mascotNames = {"Task1", "Task2", "Task3", "Task4", "Bonus1", "Bonus2"};

    private CardLayout cardLayout;
    private JPanel cardPanel;

    public score_detail(int[] scores_detail, CardLayout cardLayout, JPanel cardPanel) {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(255, 248, 220)); // 米黄色背景

        this.scores = scores_detail;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        mascotLabels = new JLabel[6];
        scoreLabels = new JLabel[6];

        JPanel contentPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        contentPanel.setOpaque(false);
        loadMascotsAndScores(contentPanel);
        panel.add(contentPanel, BorderLayout.CENTER);

        // ✅ bottom panel
        JButton backButton = new JButton("← Back");
        backButton.setPreferredSize(new Dimension(100, 36));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "startPanel"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);

        panel.add(bottomPanel, BorderLayout.SOUTH); // ✅ FIXED: add bottom panel
    }

    private void loadMascotsAndScores(JPanel parentPanel) {
        for (int i = 0; i < 6; i++) {
            // 创建吉祥物面板
            JPanel mascotPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 200));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }
            };
            mascotPanel.setOpaque(false);
            mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
            mascotPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            // 加载吉祥物图片
            try {
                BufferedImage image = ImageIO.read(new File(mascotPaths[i]));
                Image scaledImage = image.getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                mascotLabels[i] = new JLabel(new ImageIcon(scaledImage));
            } catch (IOException e) {
                // 如果图片加载失败，显示默认文本
                mascotLabels[i] = new JLabel("图片加载失败: " + mascotNames[i]);
                e.printStackTrace();
            }

            // 设置图片标签居中
            mascotLabels[i].setAlignmentX(Component.CENTER_ALIGNMENT);

            // 添加吉祥物名称
            JLabel nameLabel = new JLabel(mascotNames[i]);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameLabel.setForeground(Color.BLUE); // 设置名称为蓝色

            // 添加任务分数标签
            scoreLabels[i] = new JLabel("scores: " + scores[i]);
            scoreLabels[i].setFont(new Font("Arial", Font.PLAIN, 14));
            scoreLabels[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            scoreLabels[i].setForeground(Color.RED); // 设置分数为红色

            // 将组件添加到吉祥物面板
            mascotPanel.add(mascotLabels[i]);
            mascotPanel.add(Box.createVerticalStrut(5));
            mascotPanel.add(nameLabel);
            mascotPanel.add(Box.createVerticalStrut(5));
            mascotPanel.add(scoreLabels[i]);
            mascotPanel.add(Box.createVerticalStrut(10));

            // 添加阴影效果
            mascotPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            // 将吉祥物面板添加到主面板
            parentPanel.add(mascotPanel);
        }
    }

    // 更新分数的方法
    public void updateScores(int[] newScores) {
        if (newScores.length == 6) {
            scores = newScores;
            // 更新显示
            for (int i = 0; i < 6; i++) {
                scoreLabels[i].setText("scores: " + scores[i]);
            }
        }
    }
}