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
        // 设置面板布局为网格布局，3行2列，间距为20像素
        panel = new JPanel();
        scores = scores_detail;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        panel.setLayout(new GridLayout(3, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 初始化标签数组
        mascotLabels = new JLabel[6];
        scoreLabels = new JLabel[6];

        // 加载并显示吉祥物和分数
        loadMascotsAndScores();

        // 添加返回按钮
        JButton backButton = new JButton("返回");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "startPanel");
            }
        });
        panel.add(backButton);
    }

    private void loadMascotsAndScores() {
        for (int i = 0; i < 6; i++) {
            // 创建吉祥物面板
            JPanel mascotPanel = new JPanel();
            mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
            mascotPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

            // 加载吉祥物图片
            try {
                BufferedImage image = ImageIO.read(new File(mascotPaths[i]));
                Image scaledImage = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                mascotLabels[i] = new JLabel(new ImageIcon(scaledImage));
            } catch (IOException e) {
                // 如果图片加载失败，显示默认文本
                mascotLabels[i] = new JLabel("图片加载失败: " + mascotNames[i]);
                e.printStackTrace();
            }

            // 添加吉祥物名称
            JLabel nameLabel = new JLabel(mascotNames[i]);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // 添加任务分数标签
            JPanel scoresPanel = new JPanel(new GridLayout(4, 1));
            scoreLabels[i] = new JLabel("任务" + (i + 1) + ": " + scores[i] + "分");
            scoreLabels[i].setFont(new Font("Arial", Font.PLAIN, 12));
            scoresPanel.add(scoreLabels[i]);

            // 将组件添加到吉祥物面板
            mascotPanel.add(mascotLabels[i]);
            mascotPanel.add(Box.createVerticalStrut(5));
            mascotPanel.add(nameLabel);
            mascotPanel.add(Box.createVerticalStrut(10));
            mascotPanel.add(scoresPanel);

            // 将吉祥物面板添加到主面板
            panel.add(mascotPanel);
        }
    }

    // 更新分数的方法
    public void updateScores(int[] newScores) {
        if (newScores.length == 6) {
            scores = newScores;
            // 更新显示
            for (int i = 0; i < 6; i++) {
                scoreLabels[i].setText("任务" + (i + 1) + ": " + scores[i] + "分");
            }
        }
    }
}