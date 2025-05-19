package com.shapeville.ui;

import com.shapeville.data.ShapeData.*;
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
import java.util.List;
import java.util.Set;

public class Main {
    // 定义存档文件名
    private static final String SAVE_FILE = "src/main/resources/Archive/shapeville_save.dat";

    // 成员变量定义
//    private JLabel counter1;
    private JLabel counter2;
    private ScoreManager scoreManager;
    private boolean[] taskCompletionStatus = new boolean[6];
    private int[] is_played_task1 = new int[2];

    // 进度条和按钮作为成员变量
    private JProgressBar task1ProgressBar;
    private JProgressBar task2ProgressBar;
    private JProgressBar task3ProgressBar;
    private JProgressBar task4ProgressBar;
    private JProgressBar bonus1ProgressBar;
    private JProgressBar bonus2ProgressBar;

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
    // 游戏状态封装类（需实现Serializable）

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

        // 积分器使用存档中的分数
        /*counter1 = new JLabel("积分：");
        counter1.setBounds(10, 0, 60, 30);
        mainpanel.add(counter1);*/

        // 开始按钮
        JButton startButton = new JButton("Start a new game!");
        startButton.setBounds(300, 400, 180, 30);
        mainpanel.add(startButton);

        JButton continueButton = new JButton("Continue Archive!");
        continueButton.setBounds(300, 450, 180, 30);
        mainpanel.add(continueButton);

        // 创建第二个界面：关卡选择，管理进入各个task的通道及积分器
        JPanel startpanel = new JPanel(null);
        counter2 = new JLabel("积分：");
        counter2.setBounds(10, 0, 60, 30);
        startpanel.add(counter2);

        // 添加返回按钮：从 startPanel 回到 mainPanel
        JButton backToMainButton = new JButton("Back");
        backToMainButton.setBounds(10, 500, 100, 30); // 你可以自定义位置
        startpanel.add(backToMainButton);
        backToMainButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "mainPanel");
        });

        // 4个tasks按钮
        task1Button = new JButton("task1:");
        task2Button = new JButton("task2:");
        task3Button = new JButton("task3:");
        task4Button = new JButton("task4:");
        task1Button.setBounds(100, 100, 150, 100);
        task2Button.setBounds(250, 100, 150, 100);
        task3Button.setBounds(400, 100, 150, 100);
        task4Button.setBounds(550, 100, 150, 100);
        startpanel.add(task1Button);
        startpanel.add(task2Button);
        startpanel.add(task3Button);
        startpanel.add(task4Button);

        // 进度条
        task1ProgressBar = new JProgressBar(0, 8); // 4个小题
        task1ProgressBar.setBounds(100, 200, 150, 20); // 位于按钮下方
        task1ProgressBar.setStringPainted(true); // 显示百分比
        startpanel.add(task1ProgressBar);

        task2ProgressBar = new JProgressBar(0, 4); // 4个小题
        task2ProgressBar.setBounds(250, 200, 150, 20); // 位于按钮下方
        task2ProgressBar.setStringPainted(true); // 显示百分比
        startpanel.add(task2ProgressBar);

        task3ProgressBar = new JProgressBar(0, 4); // 4个小题
        task3ProgressBar.setBounds(400, 200, 150, 20); // 位于按钮下方
        task3ProgressBar.setStringPainted(true); // 显示百分比
        startpanel.add(task3ProgressBar);

        task4ProgressBar = new JProgressBar(0, 2); // 4个小题
        task4ProgressBar.setBounds(550, 200, 150, 20); // 位于按钮下方
        task4ProgressBar.setStringPainted(true); // 显示百分比
        startpanel.add(task4ProgressBar);

        // bonus按钮
        JButton bonus1Button = new JButton("bonus1");
        JButton bonus2Button = new JButton("bonus2");
        bonus1Button.setBounds(150, 300, 250, 100);
        bonus2Button.setBounds(400, 300, 250, 100);
        startpanel.add(bonus1Button);
        startpanel.add(bonus2Button);

        bonus1ProgressBar = new JProgressBar(0, 6); // 4个小题
        bonus1ProgressBar.setBounds(150, 400, 250, 20); // 位于按钮下方
        bonus1ProgressBar.setStringPainted(true); // 显示百分比
        startpanel.add(bonus1ProgressBar);

        bonus2ProgressBar = new JProgressBar(0, 8); // 4个小题
        bonus2ProgressBar.setBounds(400, 400, 250, 20); // 位于按钮下方
        bonus2ProgressBar.setStringPainted(true); // 显示百分比
        startpanel.add(bonus2ProgressBar);

        // 在创建按钮后添加样式设置
        task1Button.setOpaque(true);
        task2Button.setOpaque(true);
        task3Button.setOpaque(true);
        task4Button.setOpaque(true);
        bonus1Button.setOpaque(true);
        bonus2Button.setOpaque(true);

        task1Button.setContentAreaFilled(false);
        task2Button.setContentAreaFilled(false);
        task3Button.setContentAreaFilled(false);
        task4Button.setContentAreaFilled(false);
        bonus1Button.setContentAreaFilled(false);
        bonus2Button.setContentAreaFilled(false);

        // 将面板添加到卡片面板
        cardPanel.add(mainpanel, "mainPanel");
        cardPanel.add(startpanel, "startPanel");

        scoreManager = new ScoreManager();
        // 添加按钮点击事件监听器
        // 切换至开始界面
        startButton.addActionListener(e -> {
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
            cardLayout.show(cardPanel, "continuePanel");
        });

        // 任务1
        task1 = new Task1ShapeIdentification(scoreManager, is_played_task1);
        cardPanel.add(task1.task1, "task1");
        task1.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
//            counter1.setText("积分：" + scoreManager.getScore());
            counter2.setText("积分：" + scoreManager.getScore());
            is_played_task1 = task1.getIs_played_task1(); // 更新游戏状态

            task1ProgressBar.setValue(task1.isIdentifiedShapes);

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
//            counter1.setText("积分：" + scoreManager.getScore());
            counter2.setText("积分：" + scoreManager.getScore());

            task2ProgressBar.setValue(task2.identifiedTypes.size());
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
            task3Button.setBackground(new Color(144, 238, 144)); // 浅绿色
            task3Button.setEnabled(false);
        };

        task3.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
//            counter1.setText("积分：" + scoreManager.getScore());
            counter2.setText("积分：" + scoreManager.getScore());

            task3ProgressBar.setValue(task3.CompletedShapes.size());
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

        // 在 task4Button 的监听器中添加重置逻辑
        task4Button.addActionListener(e -> {
            if (!taskCompletionStatus[3]) {
                // 关键修改：重置到模式选择界面
                ((CardLayout)task4.task4.getLayout()).show(task4.task4, "modeSelection");
                cardLayout.show(cardPanel, "task4");
            } else {
                JOptionPane.showMessageDialog(null,
                        "该模块已完成，无法再次进入",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 保持原有的 onComplete 回调设置不变
        task4.onComplete = () -> {
            taskCompletionStatus[3] = true;
            task4Button.setBackground(new Color(144, 238, 144)); // 浅绿色
            task4Button.setEnabled(false);
        };

        // 保持原有的 onReturnHome 回调
        task4.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
//            counter1.setText("积分：" + scoreManager.getScore());
            counter2.setText("积分：" + scoreManager.getScore());
            task4ProgressBar.setValue(task4.Completed);
        };

        // 奖励任务1
        bonus1 = new Bonus1CompoundShapeArea(scoreManager);
        cardPanel.add(bonus1.taskPanel, "bonus1");
        bonus1.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
//            counter1.setText("积分：" + scoreManager.getScore());
            counter2.setText("积分：" + scoreManager.getScore());

            bonus1ProgressBar.setValue(bonus1.completedTasks);
        };
        bonus1Button.addActionListener(e -> {
            cardLayout.show(cardPanel, "bonus1");
        });

        // 奖励任务2
        bonus2 = new Bonus2SectorAreaCalculator(scoreManager);
        cardPanel.add(bonus2.taskPanel, "bonus2");
        bonus2.onReturnHome = () -> {
            cardLayout.show(cardPanel, "startPanel");
//            counter1.setText("积分：" + scoreManager.getScore());
            counter2.setText("积分：" + scoreManager.getScore());

            bonus2ProgressBar.setValue(bonus2.completedTasks);
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
                        "是否保存当前游戏进度？",
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
                task1.scoreManager.getScore()
        );
//        System.out.println(task1.scoreManager.getScore());
        saveTask2State(
                task2.identifiedTypes,
                task2.waitingForAngleInput,
                task2.scoreManager.getScore()
        );

        saveTask3State(
                task3.CompletedShapes,
                task3.scoreManager.getScore()
        );

        saveTask4State(
                task4.completedModes,
                task4.currentMode,
                task4.radius,
                task4.attempts,
                task4.scoreManager.getScore()
        );

        saveBonus1State(
                bonus1.completedTasks,
                bonus1.currentShapeId,
                bonus1.attemptCount,
                bonus1.scoreManager.getScore()
        );

        saveBonus2State(
                bonus2.completedTasks,
                bonus2.completed,
                bonus2.scoreManager.getScore()
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
//        counter1.setText("积分：" + state.score);
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
        task1.scoreManager.setScore(state.score);
        task1.score.setText("points: " + state.score);

        System.out.println(task1.scoreManager.getScore());
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
        task2.scoreManager.setScore(state.score);
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
//        System.out.println(state.completedShapes);
        task3.CompletedShapes = state.CompletedShapes;
        task3.scoreManager.setScore(state.score);
        task3.score.setText("points: " + state.score);
    }

    // 任务4状态恢复方法
    private void useTask4State(task4State state) {
        if (state == null) return;

        // 更新任务4状态
        task4.completedModes = state.completedModes.clone();
        task4.currentMode = state.currentMode;
        task4.radius = state.radius;
        task4.attempts = state.attempts;
        task4.scoreManager.setScore(state.score);
        task4.score.setText("points: " + state.score);

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
        bonus1.currentShapeId = state.currentShapeId;
        bonus1.attemptCount = state.attemptCount;
        bonus1.scoreManager.setScore(state.score);
        bonus1.score.setText("points: " + state.score);
    }

    // 奖励任务2状态恢复方法
    private void useBonus2State(bonus2State state) {
        if (state == null) return;

        // 更新奖励任务2状态
        bonus2.completedTasks = state.completedTasks;
        bonus2.completed = state.completed;
        bonus2.scoreManager.setScore(state.score);
        bonus2.scoreLabel.setText("points: " + state.score);

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
}