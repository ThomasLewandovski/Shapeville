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
            "C:\\Users\\16937\\Shapeville\\src\\main\\resources\\images\\Pikachu.png",
            "C:\\Users\\16937\\Shapeville\\src\\main\\resources\\images\\Fox.png",
            "C:\\Users\\16937\\Shapeville\\src\\main\\resources\\images\\Kuromi.png",
            "C:\\Users\\16937\\Shapeville\\src\\main\\resources\\images\\Raccoon.png",
            "C:\\Users\\16937\\Shapeville\\src\\main\\resources\\images\\Bunny.png",
            "C:\\Users\\16937\\Shapeville\\src\\main\\resources\\images\\Totoro.png"
    };

    // 吉祥物名称
    private String[] mascotNames = {"皮卡丘", "狐狸", "Kuromi", "浣熊", "兔子", "龙猫"};

    private CardLayout cardLayout;
    private JPanel cardPanel;

    public score_detail(int[] scores_detail, CardLayout cardLayout, JPanel cardPanel) {
        // 设置主面板布局为边框布局
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scores = scores_detail;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        // 初始化标签数组
        mascotLabels = new JLabel[6];
        scoreLabels = new JLabel[6];

        // 创建主内容面板，使用3行2列的网格布局
        JPanel contentPanel = new JPanel(new GridLayout(2, 3, 20, 20));

        // 加载并显示吉祥物和分数
        loadMascotsAndScores(contentPanel);

        // 将内容面板添加到主面板的中央
        panel.add(contentPanel, BorderLayout.CENTER);

        // 添加返回按钮到左下角
        JButton backButton = new JButton("返回");
        backButton.setPreferredSize(new Dimension(100, 30)); // 设置按钮大小
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "startPanel");
            }
        });

        // 创建底部面板，使用FlowLayout居左对齐
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(backButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadMascotsAndScores(JPanel parentPanel) {
        for (int i = 0; i < 6; i++) {
            // 创建吉祥物面板
            JPanel mascotPanel = new JPanel();
            mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
            mascotPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            mascotPanel.setBackground(Color.WHITE); // 设置背景色为白色

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