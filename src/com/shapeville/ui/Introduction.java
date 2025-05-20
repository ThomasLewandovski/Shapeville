package com.shapeville.ui;

import javax.swing.*;
import java.awt.*;

public class Introduction {
    private JDialog dialog;

    public Introduction(JFrame parentFrame) {
        // 主面板
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(new Color(255, 248, 220)); // 米黄色

        // ===== 顶部吉祥物图标 =====
        JPanel mascotPanel = new JPanel();
        mascotPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        mascotPanel.setOpaque(false); // 透明背景，继承主背景色

        String[] mascotPaths = {
                "images/Raccoon.png",
                "images/Kuromi.png",
                "images/Pikachu.png",
                "images/Totoro.png",
                "images/Fox.png",
                "images/Bunny.png"
        };

        for (String path : mascotPaths) {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(path));
            Image scaledImage = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            mascotPanel.add(imageLabel);
        }

        // ===== 内容部分 =====
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Welcome to Shapeville!");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // 可滚动的长文本内容
        JLabel scrollableMessage = new JLabel("<html><div style='text-align: left;'>"
                + "<b>Main Castle Screen</b><br>"
                + "1. Click \"Start new game\" to begin a brand new adventure<br>"
                + "2. Click \"Continue\" to resume your last progress<br>"
                + "3. Click \"Score Display\" to view your star collection progress<br><br>"

                + "<b>Basic Tasks (for Year 1–2)</b><br>"
                + "<u>Task 1: Shape Detective</u><br>"
                + "- Identify the shape friends like ○ △ □...<br>"
                + "- You have 3 guessing chances for each<br>"
                + "- Returns to castle after all shapes are completed<br><br>"

                + "<u>Task 2: Angle Challenge</u><br>"
                + "- Enter magic angle values (must be multiples of 10)<br>"
                + "- Judge the type of angle:<br>"
                + "&lt; 90° → Sharp Angle<br>"
                + "= 90° → Right Angle<br>"
                + "&gt; 90° → Wide Angle<br>"
                + "&gt; 180° → Super Big Angle<br><br>"

                + "<b>Advanced Tasks (for Year 3–4)</b><br>"
                + "<u>Task 3: Area Master</u><br>"
                + "- Randomly generated shape parameters:<br>"
                + "Rectangle → length × width<br>"
                + "Triangle → base × height ÷ 2<br>"
                + "- 3-minute time limit<br>"
                + "- After 3 wrong tries, solution is shown<br><br>"

                + "<u>Task 4: Circle Dance</u><br>"
                + "- Choose to calculate Area or Perimeter<br>"
                + "Area = π × r²<br>"
                + "Perimeter = 2 × π × r<br>"
                + "- Random radius from 1 to 20<br><br>"

                + "<b>Special Challenges</b><br>"
                + "<u>Compound Shape Puzzle</u><br>"
                + "- Combine shapes like building blocks<br>"
                + "- Solve within 5 minutes<br>"
                + "- After 3 errors, formula will be shown<br><br>"

                + "<u>Sector Mystery</u><br>"
                + "- Choose from 8 types of sectors<br>"
                + "- Use formula: (θ ÷ 360) × π × r²<br><br>"

                + "<b>Scoring System</b><br>"
                + "<u>For Basic Tasks (Tasks 1–4):</u><br>"
                + "✔ 1st Try → ★★★ (3 points)<br>"
                + "✔ 2nd Try → ★★ (2 points)<br>"
                + "✔ 3rd Try → ★ (1 point)<br><br>"

                + "<u>For Advanced Tasks (Bonus 1–2):</u><br>"
                + "✔ 1st Try → ★★★★★★ (6 points)<br>"
                + "✔ 2nd Try → ★★★★ (4 points)<br>"
                + "✔ 3rd Try → ★★ (2 points)<br><br>"

                + "✨ Positive feedback appears after each correct answer!<br><br>"

                + "<b>Tips</b><br>"
                + "• Click \"Introduction\" to review this guide anytime<br>"
                + "• Green progress bar shows your progress<br>"
                + "• Completed tasks turn light green<br>"
                + "• Stuck? Don’t worry — the solution will appear to help you learn!<br><br>"

                + "Ready for your geometric adventure? Let’s go!"
                + "</div></html>");

        scrollableMessage.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        scrollableMessage.setVerticalAlignment(SwingConstants.TOP);

        JScrollPane scrollPane = new JScrollPane(scrollableMessage);
        scrollPane.setPreferredSize(new Dimension(450, 180));
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(scrollPane);

        // ===== 关闭按钮 =====
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        closeButton.setBackground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dialog.dispose());

        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(closeButton);

        // ===== 组装面板 =====
        panel.add(mascotPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        dialog = new JDialog(parentFrame, "Introduction", false);
        dialog.setContentPane(panel);
        dialog.setSize(520, 400); // 可调大小以适应内容
        dialog.setLocationRelativeTo(parentFrame);
    }

    public void show() {
        dialog.setVisible(true);
    }
}