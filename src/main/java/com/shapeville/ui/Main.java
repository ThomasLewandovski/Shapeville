package com.shapeville.ui;

import static com.shapeville.manager.ArchiveManager.*;

import com.shapeville.manager.ScoreManager;
import com.shapeville.tasks.Task1ShapeIdentification;
import com.shapeville.tasks.Task2AngleIdentification;
import com.shapeville.tasks.Task3VolumeSurfaceCalculator;
import com.shapeville.tasks.Task4CircleArea;
import com.shapeville.tasks.Bonus1CompoundShapeArea;
import com.shapeville.tasks.Bonus2SectorAreaCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashSet;

public class Main {
    // 定义存档文件名
    private static final String SAVE_FILE = "src/main/resources/Archive/shapeville_save.dat";

    // 成员变量定义
    private JLabel counter2 = new JLabel();
    private ScoreManager scoreManager;
    private boolean[] taskCompletionStatus = new boolean[6];
    private int[] is_played_task1 = new int[2];
    private int[] score_for_tasks = new int[6];

    // 进度条和按钮作为成员变量
    private JProgressBar task1ProgressBar;
    private JProgressBar task2ProgressBar;
    private JProgressBar task3ProgressBar;
    private JProgressBar task4ProgressBar;
    private JProgressBar bonus1ProgressBar;
    private JProgressBar bonus2ProgressBar;

    // 新增积分进度条和比值标签
    private JProgressBar scoreProgressBar;
    private JLabel scoreRatioLabel;

    private JButton task1Button;
    private JButton task2Button;
    private JButton task3Button;
    private JButton task4Button;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private static Task1ShapeIdentification task1;
    private static Task2AngleIdentification task2;
    private static Task3VolumeSurfaceCalculator task3;
    private static Task4CircleArea task4;
    private static Bonus1CompoundShapeArea bonus1;
    private static Bonus2SectorAreaCalculator bonus2;

    public static void main(String[] args) {
        // 禁用输入法特殊处理
        System.setProperty("java.awt.im.useInputMethodKeys", "false");
        System.setProperty("apple.awt.im.disable", "true"); // 新增 macOS 专用属性
        // 创建Main实例
        Main main = new Main();
        main.initializeGame();
    }

    private void initializeGame() {
        // 创建主窗口
        JFrame frame = new JFrame("Shapeville");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 禁用默认关闭操作
        frame.setSize(800, 600);

        // 创建卡片布局和面板
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 创建第一个界面：主界面
        // 自定义带背景图的主界面面板
        JPanel mainpanel = new JPanel(null) {
            Image bg = new ImageIcon(getClass().getClassLoader().getResource("images/initial_background.png")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this); // 拉伸填满整个面板
                }
            }
        };
        JButton openIntroButton = new JButton("Introduction");
        openIntroButton.setBounds(310,300,180,30);
        openIntroButton.addActionListener(e -> {
            // 创建并显示Introduction对话框
            Introduction intro = new Introduction(frame);
            intro.show();
        });
        mainpanel.add(openIntroButton);

        // 开始按钮
        JButton startButton = new JButton("Start a new game!");
        startButton.setBounds(310, 350, 180, 30);
        mainpanel.add(startButton);

        JButton continueButton = new JButton("Continue Archive!");
        continueButton.setBounds(310, 400, 180, 30);
        mainpanel.add(continueButton);

        // 创建第二个界面：关卡选择
        JPanel startpanel = new JPanel(null) {
            Image bg = new ImageIcon(getClass().getClassLoader().getResource("images/startpanel_background.png")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this); // 拉伸填满整个面板
                }
            }
        };

        resizeMainPanel(mainpanel);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeMainPanel(mainpanel);  // 别忘了主界面
            }
        });

        // 创建积分进度条（最大值153）
        scoreProgressBar = new JProgressBar(0, 153);
        scoreProgressBar.setBounds(10, 10, 150, 25);
        scoreProgressBar.setStringPainted(false);
        scoreProgressBar.setForeground(new Color(0, 150, 0)); // 绿色进度条
        startpanel.add(scoreProgressBar);

        // 创建比值标签
        scoreRatioLabel = new JLabel("0/153");
        scoreRatioLabel.setBounds(170, 10, 100, 25);
        scoreRatioLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        startpanel.add(scoreRatioLabel);

        JButton score_display = new JButton("Score Display");
        score_display.setBounds(600, 0, 180, 30);
        startpanel.add(score_display);

        score_detail scoreDetail = new score_detail(score_for_tasks, cardLayout, cardPanel);
        JPanel scorePanel = scoreDetail.panel;

        score_display.addActionListener(e -> {
            scoreDetail.updateScores(score_for_tasks);
            cardLayout.show(cardPanel, "score_display");
        });

        //在这里也写一个相同的introduction
        JButton openIntroButton2 = new JButton("Introduction");
        openIntroButton2.setBounds(30,520,180,30);
        startpanel.add(openIntroButton2);
        openIntroButton2.addActionListener(e -> {
            // 创建并显示Introduction对话框
            Introduction intro = new Introduction(frame);
            intro.show();
        });
        startpanel.add(openIntroButton2);

        //在这里，也就是introduction的右边，新增一个restart按钮，其作用跟mainpanel的start按钮一样
        JButton restartButton = new JButton("Restart");
        restartButton.setBounds(230, 520, 180, 30);
        startpanel.add(restartButton);
        //这里是restart逻辑：
        restartButton.addActionListener(e -> {
            // 始终清除本地存档
            delete_archive();

            // 重置总分
            scoreManager.resetScore();
            counter2.setText("积分：0");

            // 重置任务状态
            taskCompletionStatus = new boolean[6];
            is_played_task1 = new int[2];
            score_for_tasks = new int[6];

            // 重置进度条
            task1ProgressBar.setValue(0);
            task2ProgressBar.setValue(0);
            task3ProgressBar.setValue(0);
            task4ProgressBar.setValue(0);
            bonus1ProgressBar.setValue(0);
            bonus2ProgressBar.setValue(0);
            scoreProgressBar.setValue(0);
            scoreRatioLabel.setText("0/153");

            // 重置按钮状态
            task1Button.setEnabled(true);
            task2Button.setEnabled(true);
            task3Button.setEnabled(true);
            task4Button.setEnabled(true);

            Color resetColor = new Color(255, 228, 196);
            task1Button.setBackground(resetColor);
            task2Button.setBackground(resetColor);
            task3Button.setBackground(resetColor);
            task4Button.setBackground(resetColor);

            // 重置任务 1
            task1.isIdentifiedShapes = 0;
            task1.is_played_task1 = new int[2];
            task1.scores = 0;
            task1.scorelabel.setText("points: 0");

            // 重置任务 2
            task2.identifiedTypes.clear();
            task2.waitingForAngleInput = false;
            task2.scores = 0;
            task2.scoreLabel.setText("points: 0");

            // 重置任务 3
            task3.CompletedShapes.clear();
            task3.scores = 0;
            task3.scorelable.setText("points: 0");

            // 重置任务 4
            task4.completedModes = new boolean[2];
            //task4.currentMode = "";
            task4.radius = 0;
            task4.attempts = 0;
            task4.scores = 0;
            task4.scorelable.setText("points: 0");

            // 重置 Bonus 1
            bonus1.completedTasks = 0;
            bonus1.completedShapes = new int[6];
            bonus1.scores = 0;
            bonus1.scorelable.setText("points: 0");
            for (int i = 0; i < 6; i++) {
                JButton btn = bonus1.shapeButtons.get(i);
                btn.setEnabled(true);
                btn.setBackground(resetColor);
            }

            // 重置 Bonus 2
            bonus2.completedTasks = 0;
            bonus2.completed = new boolean[9];
            bonus2.scores = 0;
            bonus2.scorelable.setText("Scores: 0");
            bonus2.scoreLabel.setText("Scores: 0");
            for (int i = 1; i <= 8; i++) {
                if (bonus2.shapeButtons.containsKey(i)) {
                    JButton btn = bonus2.shapeButtons.get(i);
                    btn.setEnabled(true);
                    btn.setBackground(resetColor);
                }
            }

            // ✅ 弹出提示 —— 无论是否有存档，都会执行
            JOptionPane.showMessageDialog(
                    frame,
                    "Already restarted a new game!",
                    "Restart Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // 4个task按钮居中横排
        task1Button = new JButton("task1:");
        task2Button = new JButton("task2:");
        task3Button = new JButton("task3:");
        task4Button = new JButton("task4:");

        task1Button.setBounds(100, 140, 150, 100);
        task2Button.setBounds(275, 140, 150, 100);
        task3Button.setBounds(450, 140, 150, 100);
        task4Button.setBounds(625, 140, 150, 100);

        startpanel.add(task1Button);
        startpanel.add(task2Button);
        startpanel.add(task3Button);
        startpanel.add(task4Button);

        // 对应进度条（居中在按钮下方）
        task1ProgressBar = new JProgressBar(0, 8);
        task1ProgressBar.setBounds(100, 250, 150, 20);
        task1ProgressBar.setStringPainted(true);
        startpanel.add(task1ProgressBar);

        task2ProgressBar = new JProgressBar(0, 4);
        task2ProgressBar.setBounds(275, 250, 150, 20);
        task2ProgressBar.setStringPainted(true);
        startpanel.add(task2ProgressBar);

        task3ProgressBar = new JProgressBar(0, 4);
        task3ProgressBar.setBounds(450, 250, 150, 20);
        task3ProgressBar.setStringPainted(true);
        startpanel.add(task3ProgressBar);

        task4ProgressBar = new JProgressBar(0, 2);
        task4ProgressBar.setBounds(625, 250, 150, 20);
        task4ProgressBar.setStringPainted(true);
        startpanel.add(task4ProgressBar);

        // bonus按钮水平对称放置在下方
        JButton bonus1Button = new JButton("bonus1");
        JButton bonus2Button = new JButton("bonus2");

        bonus1Button.setBounds(100, 320, 250, 100);
        bonus2Button.setBounds(450, 320, 250, 100);

        startpanel.add(bonus1Button);
        startpanel.add(bonus2Button);

        // bonus进度条
        bonus1ProgressBar = new JProgressBar(0, 6);
        bonus1ProgressBar.setBounds(100, 430, 250, 20);
        bonus1ProgressBar.setStringPainted(true);
        startpanel.add(bonus1ProgressBar);

        bonus2ProgressBar = new JProgressBar(0, 8);
        bonus2ProgressBar.setBounds(450, 430, 250, 20);
        bonus2ProgressBar.setStringPainted(true);
        startpanel.add(bonus2ProgressBar);

        // 样式工具方法
        Font taskFont = new Font("Comic Sans MS", Font.BOLD, 16);

        JButton[] allButtons = {
                task1Button, task2Button, task3Button, task4Button,
                bonus1Button, bonus2Button, startButton, score_display, restartButton,
                openIntroButton, openIntroButton2, continueButton
        };

        for (JButton btn : allButtons) {
            btn.setFont(taskFont);
            btn.setBackground(new Color(255, 228, 196)); // bisque
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            btn.setContentAreaFilled(true);
            btn.setOpaque(true);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        // 将面板添加到卡片面板
        cardPanel.add(mainpanel, "mainPanel");
        cardPanel.add(startpanel, "startPanel");
        cardPanel.add(scorePanel, "score_display");

        scoreManager = new ScoreManager();

        // 初始化积分进度条
        updateScoreProgress();

        // 添加按钮点击事件监听器
        startButton.addActionListener(e -> {
            delete_archive();
            cardLayout.show(cardPanel, "startPanel");
            GameState gameState = new GameState(taskCompletionStatus, is_played_task1,
                    0, 0, 0, 0, 0,
                    0, 0);
            useGameState(gameState);
        });

        continueButton.addActionListener(e -> {
            GameState gameState = loadGameState();

            // 处理存档加载失败的情况
            if (gameState == null) {
                // 新建默认状态
                gameState = new GameState(taskCompletionStatus, is_played_task1, 0, 0, 0, 0, 0, 0, 0);
            }
            useTasksState();
            useGameState(gameState);
            cardLayout.show(cardPanel, "startPanel"); // 修改为显示startPanel
        });

        // 在 frame 初始化之后添加：
        frame.setResizable(true);  // 允许缩放
        resizeComponents(startpanel);  // 初始化设置
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents(startpanel);
            }
        });

        // 任务1
        task1 = new Task1ShapeIdentification(scoreManager, is_played_task1);
        cardPanel.add(task1.task1, "task1");
        task1.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter2.setText("积分：" + scoreManager.getScore());
            is_played_task1 = task1.getIs_played_task1();
            score_for_tasks[0] = task1.scores;
            task1ProgressBar.setValue(task1.isIdentifiedShapes);
            updateScoreProgress(); // 更新积分进度条

            // 检查任务是否完成
            if (task1.is_played_task1[0] == 1 && task1.is_played_task1[1] == 1) {
                taskCompletionStatus[0] = true;
                task1Button.setBackground(new Color(144, 238, 144));
                task1Button.setEnabled(false);
            }
        };

        task1Button.addActionListener(e -> {
            if (is_played_task1[0] == 0 || is_played_task1[1] == 0) {
                cardLayout.show(cardPanel, "task1");
            }
        });

        // 任务2
        task2 = new Task2AngleIdentification(scoreManager);
        cardPanel.add(task2.task2, "task2");

        task2.onComplete = () -> {
            taskCompletionStatus[1] = true;
            task2Button.setBackground(new Color(144, 238, 144));
            task2Button.setEnabled(false);
        };

        task2.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter2.setText("积分：" + scoreManager.getScore());
            task2ProgressBar.setValue(task2.identifiedTypes.size());
            score_for_tasks[1] = task2.scores;
            updateScoreProgress(); // 更新积分进度条
        };

        task2Button.addActionListener(e -> {
            if (!taskCompletionStatus[1]) {
                cardLayout.show(cardPanel, "task2");
            } else {
                JOptionPane.showMessageDialog(null, "该模块已完成，无法再次进入", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 任务3
        task3 = new Task3VolumeSurfaceCalculator(scoreManager);
        cardPanel.add(task3.task3, "task3");

        task3.onComplete = () -> {
            taskCompletionStatus[2] = true;
            task3Button.setBackground(new Color(144, 238, 144));
            task3Button.setEnabled(false);
        };

        task3.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter2.setText("points: " + scoreManager.getScore());
            score_for_tasks[2] = task3.scores;
            task3ProgressBar.setValue(task3.CompletedShapes.size());
            updateScoreProgress(); // 更新积分进度条
        };

        task3Button.addActionListener(e -> {
            if (!taskCompletionStatus[2]) {
                cardLayout.show(cardPanel, "task3");
            } else {
                JOptionPane.showMessageDialog(null,
                        "该模块已完成，无法再次进入",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 任务4
        task4 = new Task4CircleArea(scoreManager);
        cardPanel.add(task4.task4, "task4");

        task4Button.addActionListener(e -> {
            if (!taskCompletionStatus[3]) {
                ((CardLayout)task4.task4.getLayout()).show(task4.task4, "modeSelection");
                cardLayout.show(cardPanel, "task4");
            } else {
                JOptionPane.showMessageDialog(null,
                        "该模块已完成，无法再次进入",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        task4.onComplete = () -> {
            taskCompletionStatus[3] = true;
            task4Button.setBackground(new Color(144, 238, 144));
            task4Button.setEnabled(false);
        };

        task4.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter2.setText("积分：" + scoreManager.getScore());
            score_for_tasks[3] = task4.scores;
            task4ProgressBar.setValue(task4.Completed);
            updateScoreProgress(); // 更新积分进度条
        };

        // 奖励任务1
        bonus1 = new Bonus1CompoundShapeArea(scoreManager);
        cardPanel.add(bonus1.taskPanel, "bonus1");
        bonus1.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter2.setText("积分：" + scoreManager.getScore());
            score_for_tasks[4] = bonus1.scores;
            bonus1ProgressBar.setValue(bonus1.completedTasks);
            updateScoreProgress(); // 更新积分进度条
        };
        bonus1Button.addActionListener(e -> {
            cardLayout.show(cardPanel, "bonus1");
        });

        // 奖励任务2
        bonus2 = new Bonus2SectorAreaCalculator(scoreManager);
        cardPanel.add(bonus2.taskPanel, "bonus2");
        bonus2.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
            counter2.setText("积分：" + scoreManager.getScore());
            score_for_tasks[5] = bonus2.scores;
            bonus2ProgressBar.setValue(bonus2.completedTasks);
            updateScoreProgress(); // 更新积分进度条
        };
        bonus2Button.addActionListener(e -> {
            cardLayout.show(cardPanel, "bonus2");
        });

        // 添加窗口关闭事件处理
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showOptionDialog(
                        frame,
                        "<html>您当前的得分是:"+scoreManager.getScore() + "<br>是否保存当前游戏进度？</html>",
                        "退出确认",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"保存并退出", "不保存退出", "取消"},
                        "保存并退出"
                );

                if (choice == JOptionPane.YES_OPTION) {
                    // 保存游戏状态
                    saveGameState(
                            taskCompletionStatus,
                            is_played_task1,
                            scoreManager.getScore(),
                            task1.isIdentifiedShapes,
                            task2.identifiedTypes.size(),
                            task3.CompletedShapes.size(),
                            task4.Completed,
                            bonus1.completedTasks,
                            bonus2.completedTasks
                    );
                    frame.dispose();
                } else if (choice == JOptionPane.NO_OPTION) {
                    frame.dispose();
                }
            }
        });

        // 将卡片面板添加到主窗口
        frame.add(cardPanel);
        frame.setVisible(true);
    }

    /**
     * 更新积分进度条和比值标签
     */
    private void updateScoreProgress() {
        int currentScore = scoreManager.getScore();
        scoreProgressBar.setValue(currentScore);
        scoreRatioLabel.setText(currentScore + "/153");
    }

    /**
     * 保存游戏状态到文件
     */
    private static void saveGameState(boolean[] taskCompletionStatus,
                                      int[] is_played_task1,
                                      int score,
                                      int task1Progress,
                                      int task2Progress,
                                      int task3Progress,
                                      int task4Progress,
                                      int bonus1Progress,
                                      int bonus2Progress) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            GameState state = new GameState(
                    taskCompletionStatus,
                    is_played_task1,
                    score,
                    task1Progress,
                    task2Progress,
                    task3Progress,
                    task4Progress,
                    bonus1Progress,
                    bonus2Progress
            );
            oos.writeObject(state);
            JOptionPane.showMessageDialog(null, "游戏进度已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
        saveTask1State(
                task1.isIdentifiedShapes,
                task1.getIs_played_task1(),
                task1.scores
        );
        saveTask2State(
                task2.identifiedTypes,
                task2.waitingForAngleInput,
                task2.scores
        );

        saveTask3State(
                task3.CompletedShapes,
                task3.scores
        );

        saveTask4State(
                task4.completedModes,
                task4.currentMode,
                task4.radius,
                task4.attempts,
                task4.scores
        );

        saveBonus1State(
                bonus1.completedTasks,
                bonus1.completedShapes,
                bonus1.scores
        );

        saveBonus2State(
                bonus2.completedTasks,
                bonus2.completed,
                bonus2.scores
        );
    }

    /**
     * 从文件加载游戏状态
     */
    private static GameState loadGameState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (GameState) ois.readObject();
        } catch (FileNotFoundException ex) {
            // 新游戏，无存档
            return null;
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "加载存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * 更新任务按钮状态
     */
    private void updateTaskButtonStatus(JButton button, boolean[] status, int index) {
        if (status[index]) {
            button.setBackground(new Color(144, 238, 144));
            button.setEnabled(false);
        }
    }

    /**
     * 使用游戏状态恢复游戏
     */
    private void useGameState(GameState state) {
        // 更新积分显示
        counter2.setText("积分：" + state.score);
        scoreManager.setScore(state.score);

        // 更新任务完成状态
        taskCompletionStatus = state.taskCompletionStatus;

        // 更新任务1的游戏进度
        is_played_task1 = state.is_played_task1.clone();

        // 更新进度条显示
        task1ProgressBar.setValue(state.task1Progress);
        task2ProgressBar.setValue(state.task2Progress);
        task3ProgressBar.setValue(state.task3Progress);
        task4ProgressBar.setValue(state.task4Progress);
        bonus1ProgressBar.setValue(state.bonus1Progress);
        bonus2ProgressBar.setValue(state.bonus2Progress);

        // 更新积分进度条
        updateScoreProgress();

        // 更新任务按钮状态
        updateTaskButtonStatus(task1Button, taskCompletionStatus, 0);
        updateTaskButtonStatus(task2Button, taskCompletionStatus, 1);
        updateTaskButtonStatus(task3Button, taskCompletionStatus, 2);
        updateTaskButtonStatus(task4Button, taskCompletionStatus, 3);

        // 显示关卡选择面板
        cardLayout.show(cardPanel, "startPanel");
    }

    private void useTasksState(){
        task1State state1 = loadTask1State();
        useTask1State(state1);
        task2State state2 = loadTask2State();
        useTask2State(state2);
        task3State state3 = loadTask3State();
        useTask3State(state3);
        task4State state4 = loadTask4State();
        useTask4State(state4);
        bonus1State state5 = loadBonus1State();
        useBonus1State(state5);
        bonus2State state6 = loadBonus2State();
        useBonus2State(state6);
    }

    // 任务1状态恢复方法
    private void useTask1State(task1State state) {
        if (state == null) return;

        // 更新任务1状态
        task1.isIdentifiedShapes = state.isIdentifiedShapes;
        task1.is_played_task1 = state.is_played_task1.clone();
        task1.scoreManager.setScore(scoreManager.getScore());
        task1.scorelabel.setText("points: " + state.score);

        // 检查任务是否完成
        if (state.isIdentifiedShapes >= 8) {
            taskCompletionStatus[0] = true;
            task1Button.setBackground(new Color(144, 238, 144));
            task1Button.setEnabled(false);
        }
    }

    // 任务2状态恢复方法
    private void useTask2State(task2State state) {
        if (state == null) return;
        task2.identifiedTypes = new HashSet<>(state.identifiedTypes);
        task2.waitingForAngleInput = state.waitingForAngleInput;
        task2.scoreManager.setScore(scoreManager.getScore());
        task2.scoreLabel.setText("points: " + state.score);

        // 检查任务是否完成
        if (state.identifiedTypes.size() >= 4) {
            taskCompletionStatus[1] = true;
            task2Button.setBackground(new Color(144, 238, 144));
            task2Button.setEnabled(false);
        }
    }

    // 任务3状态恢复方法
    private void useTask3State(task3State state) {
        if (state == null) return;

        // 更新任务3状态
        task3.CompletedShapes = state.CompletedShapes;
        task3.scoreManager.setScore(scoreManager.getScore());
        task3.scorelable.setText("points: " + state.score);
    }

    // 任务4状态恢复方法
    private void useTask4State(task4State state) {
        if (state == null) return;

        // 更新任务4状态
        task4.completedModes = state.completedModes.clone();
        task4.currentMode = state.currentMode;
        task4.radius = state.radius;
        task4.attempts = state.attempts;
        task4.scoreManager.setScore(scoreManager.getScore());
        task4.scorelable.setText("points: " + state.score);

        // 更新任务4进度条
        int progress = 0;
        for (boolean completed : state.completedModes) {
            if (completed) progress++;
        }
        task4ProgressBar.setValue(progress);

        // 检查任务是否完成
        boolean allCompleted = true;
        for (boolean completed : state.completedModes) {
            if (!completed) {
                allCompleted = false;
                break;
            }
        }
        if (allCompleted) {
            taskCompletionStatus[3] = true;
            task4Button.setBackground(new Color(144, 238, 144));
            task4Button.setEnabled(false);
        }
    }

    // 奖励任务1状态恢复方法
    private void useBonus1State(bonus1State state) {
        if (state == null) return;

        // 更新奖励任务1状态
        bonus1.completedTasks = state.completedTasks;
        bonus1.completedShapes = state.completedShapes;
        bonus1.scoreManager.setScore(scoreManager.getScore());
        bonus1.scores = state.score;
        bonus1.scorelable.setText("points: " + state.score);

        for(int i = 0; i < 6; i++) {
            JButton btn = bonus1.shapeButtons.get(i);
            if (bonus1.completedShapes[i] == 1) {
                btn.setEnabled(false);
            }
        }
    }

    // 奖励任务2状态恢复方法
    private void useBonus2State(bonus2State state) {
        if (state == null) return;

        // 更新奖励任务2状态
        bonus2.completedTasks = state.completedTasks;
        bonus2.completed = state.completed;
        bonus2.scoreManager.setScore(scoreManager.getScore());
        bonus2.scores = state.score;
        bonus2.scoreLabel.setText("Scores: " + state.score);
        bonus2.scorelable.setText("Scores: " + state.score);

        for(int i = 1;i<=8;i++){
            if(bonus2.completed[i]){
                if (bonus2.shapeButtons.containsKey(i)) {
                    JButton btn = bonus2.shapeButtons.get(i);
                    btn.setEnabled(false);
                    btn.setBackground(Color.LIGHT_GRAY);
                }
            }
        }
    }

    private void resizeMainPanel(JPanel mainpanel) {
        int width = mainpanel.getWidth();
        int height = mainpanel.getHeight();

        for (Component comp : mainpanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String text = button.getText();
                if (text.contains("Start")) {
                    button.setBounds(width / 2 - width / 8, height * 2 / 3, width / 4, height / 15);
                } else if (text.contains("Continue")) {
                    button.setBounds(width / 2 - width / 8, height * 2 / 3 + height / 12 + 10, width / 4, height / 15);
                }else{
                    button.setBounds(width / 2 - width / 8, height * 2 / 3 + height / 6 + 20, width / 4, height / 15);
                }
            }
        }
    }

    private void resizeComponents(JPanel startpanel) {
        int width = startpanel.getWidth();
        int height = startpanel.getHeight();

        // 左上角“积分”进度条和比值标签
        if (scoreProgressBar != null) {
            scoreProgressBar.setBounds(width / 80, height / 50, width / 5, height / 20);
        }
        if (scoreRatioLabel != null) {
            scoreRatioLabel.setBounds(width / 80 + width / 5 + 5, height / 50, width / 8, height / 20);
        }

        // 返回按钮
        Component backButton = null;
        Component detailButton = null;
        for (Component c : startpanel.getComponents()) {
            if (c instanceof JButton && "Back".equals(((JButton) c).getText())) {
                backButton = c;
                break;
            }
            if (c instanceof JButton && "Score Display".equals(((JButton) c).getText())) {
                detailButton = c;
                break;
            }
        }
        if (backButton != null) {
            backButton.setBounds(width / 80, height*7/8, width / 8, height / 20);
        }
        if (detailButton != null) {
            detailButton.setBounds(width * 6/7, 0, width / 7, height / 20);
        }

        // 任务按钮 task1-task4
        int buttonW = width / 6;
        int buttonH = height / 6;
        int spacingX = width / 20;
        int baseY = height / 4;
        int barH = height / 30;

        if (task1Button != null) task1Button.setBounds(spacingX*2, baseY, buttonW, buttonH);
        if (task2Button != null) task2Button.setBounds(3 * spacingX + buttonW, baseY, buttonW, buttonH);
        if (task3Button != null) task3Button.setBounds(4 * spacingX + 2 * buttonW, baseY, buttonW, buttonH);
        if (task4Button != null) task4Button.setBounds(5 * spacingX + 3 * buttonW, baseY, buttonW, buttonH);

        if (task1ProgressBar != null) task1ProgressBar.setBounds(spacingX*2, baseY + buttonH + 5, buttonW, barH);
        if (task2ProgressBar != null) task2ProgressBar.setBounds(3 * spacingX + buttonW, baseY + buttonH + 5, buttonW, barH);
        if (task3ProgressBar != null) task3ProgressBar.setBounds(4 * spacingX + 2 * buttonW, baseY + buttonH + 5, buttonW, barH);
        if (task4ProgressBar != null) task4ProgressBar.setBounds(5 * spacingX + 3 * buttonW, baseY + buttonH + 5, buttonW, barH);

        // Bonus按钮
        int bonusY = baseY + buttonH + barH + height / 20;
        int bonusW = width / 3;
        int bonusH = buttonH;

        Component[] components = startpanel.getComponents();
        JButton bonus1Button = null, bonus2Button = null;
        JProgressBar b1bar = null, b2bar = null;
        for (Component c : components) {
            if (c instanceof JButton && "bonus1".equals(((JButton) c).getText())) bonus1Button = (JButton) c;
            if (c instanceof JButton && "bonus2".equals(((JButton) c).getText())) bonus2Button = (JButton) c;
            if (c instanceof JProgressBar && ((JProgressBar) c).getMaximum() == 6) b1bar = (JProgressBar) c;
            if (c instanceof JProgressBar && ((JProgressBar) c).getMaximum() == 8) b2bar = (JProgressBar) c;
        }

        if (bonus1Button != null) bonus1Button.setBounds(spacingX * 3, bonusY, bonusW, bonusH);
        if (bonus2Button != null) bonus2Button.setBounds(spacingX * 3 + bonusW + spacingX, bonusY, bonusW, bonusH);

        if (b1bar != null) b1bar.setBounds(spacingX * 3, bonusY + bonusH + 5, bonusW, barH);
        if (b2bar != null) b2bar.setBounds(spacingX * 3 + bonusW + spacingX, bonusY + bonusH + 5, bonusW, barH);
    }
}