package com.shapeville.manager;

import com.shapeville.data.ShapeData.*;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArchiveManager {
    private static final String TASK1_SAVE_FILE = "task1_saved_file.dat";
    private static final String TASK2_SAVE_FILE = "task2_saved_file.dat";
    private static final String TASK3_SAVE_FILE = "task3_saved_file.dat";
    private static final String TASK4_SAVE_FILE = "task4_saved_file.dat";
    private static final String BONUS1_SAVE_FILE = "bonus1_saved_file.dat";
    private static final String BONUS2_SAVE_FILE = "bonus2_saved_file.dat";

    public static class GameState implements Serializable {
        private static final long serialVersionUID = 1L;
        public boolean[] taskCompletionStatus;
        public int[] is_played_task1;
        public int score;
        public int task1Progress;
        public int task2Progress;
        public int task3Progress;
        public int task4Progress;
        public int bonus1Progress;
        public int bonus2Progress;

        public GameState(boolean[] taskCompletionStatus, int[] is_played_task1,
                         int score, int task1Progress, int task2Progress,
                         int task3Progress, int task4Progress, int bonus1Progress,
                         int bonus2Progress) {
            this.taskCompletionStatus = taskCompletionStatus.clone();
            this.is_played_task1 = is_played_task1.clone();
            this.score = score;
            this.task1Progress = task1Progress;
            this.task2Progress = task2Progress;
            this.task3Progress = task3Progress;
            this.task4Progress = task4Progress;
            this.bonus1Progress = bonus1Progress;
            this.bonus2Progress = bonus2Progress;
        }
    }

    public static class task1State implements Serializable {
        private static final long serialVersionUID = 2L;

        public int isIdentifiedShapes;
        public int[] is_played_task1;
        public int score;
        public boolean[] taskCompletionStatus;
        public boolean isAdvanced;
        public boolean isSubtaskStarted;
        public boolean isSubtaskCompleted;
        public int currentIndex;
        public int attempt;
        public List<ShapeItem> currentShapes;
        public String currentShapeName; // 保存当前形状名称而非整个对象

        public task1State(
                int isIdentifiedShapes,
                int[] is_played_task1,
                int score,
                boolean isAdvanced,
                boolean isSubtaskStarted,
                boolean isSubtaskCompleted,
                int currentIndex,
                int attempt,
                List<ShapeItem> currentShapes,
                ShapeItem currentShape) {

            this.isIdentifiedShapes = isIdentifiedShapes;
            this.is_played_task1 = is_played_task1.clone();
            this.score = score;
            this.taskCompletionStatus = taskCompletionStatus.clone();
            this.isAdvanced = isAdvanced;
            this.isSubtaskStarted = isSubtaskStarted;
            this.isSubtaskCompleted = isSubtaskCompleted;
            this.currentIndex = currentIndex;
            this.attempt = attempt;

            // 克隆当前形状列表
            this.currentShapes = new ArrayList<>();
            for (ShapeItem shape : currentShapes) {
                this.currentShapes.add(new ShapeItem(shape.getName(), shape.getImageFilename()));
            }

            // 保存当前形状的名称而非整个对象
            this.currentShapeName = currentShape != null ? currentShape.getName() : null;
        }
    }

    public static class task2State implements Serializable {
        private static final long serialVersionUID = 3L;

        public int result;
        public int currentAngle;
        public int attempt;
        public Set<String> identifiedTypes;
        public boolean waitingForAngleInput;

        public task2State(int result, int currentAngle, int attempt, Set<String> identifiedTypes, boolean waitingForAngleInput) {
            this.result = result;
            this.currentAngle = currentAngle;
            this.attempt = attempt;
            this.identifiedTypes = new HashSet<>(identifiedTypes); // 克隆集合以确保状态隔离
            this.waitingForAngleInput = waitingForAngleInput;
        }
    }

    public static class task3State implements Serializable {
        private static final long serialVersionUID = 4L;

        public String currentShape;
        public int param1;
        public int param2;
        public int param3;
        public int correctAnswer;
        public int attemptsLeft;
        public int timeRemaining;
        public Set<String> completedShapes;
        public int score;

        public task3State(String currentShape, int param1, int param2, int param3,
                          int correctAnswer, int attemptsLeft, int timeRemaining,
                          Set<String> completedShapes, int score) {
            this.currentShape = currentShape;
            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;
            this.correctAnswer = correctAnswer;
            this.attemptsLeft = attemptsLeft;
            this.timeRemaining = timeRemaining;
            this.completedShapes = new HashSet<>(completedShapes); // 克隆集合
            this.score = score;
        }
    }

    public static class task4State implements Serializable {
        private static final long serialVersionUID = 5L;

        public boolean[] completedModes;
        public int currentMode;
        public int radius;
        public int attempts;
        public int score;

        public task4State(boolean[] completedModes, int currentMode, int radius, int attempts, int score) {
            this.completedModes = completedModes.clone();
            this.currentMode = currentMode;
            this.radius = radius;
            this.attempts = attempts;
            this.score = score;
        }
    }

    public static class bonus1State implements Serializable {
        private static final long serialVersionUID = 6L;

        public int completedTasks;
        public int currentShapeId;
        public int attemptCount;
        public int score;

        public bonus1State(int completedTasks, int currentShapeId, int attemptCount, int score) {
            this.completedTasks = completedTasks;
            this.currentShapeId = currentShapeId;
            this.attemptCount = attemptCount;
            this.score = score;
        }
    }

    public static class bonus2State implements Serializable {
        private static final long serialVersionUID = 7L;

        public int completedTasks;
        public int currentShapeId;
        public int attemptCount;
        public int score;

        public bonus2State(int completedTasks, int currentShapeId, int attemptCount, int score) {
            this.completedTasks = completedTasks;
            this.currentShapeId = currentShapeId;
            this.attemptCount = attemptCount;
            this.score = score;
        }
    }

    public static void saveTask1State(
            int isIdentifiedShapes,
            int[] is_played_task1,
            int score,
            boolean isAdvanced,
            boolean isSubtaskStarted,
            boolean isSubtaskCompleted,
            int currentIndex,
            int attempt,
            List<ShapeItem> currentShapes,
            ShapeItem currentShape
    ) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TASK1_SAVE_FILE))) {
            task1State state = new task1State(
                    isIdentifiedShapes,
                    is_played_task1,
                    score,
                    isAdvanced,
                    isSubtaskStarted,
                    isSubtaskCompleted,
                    currentIndex,
                    attempt,
                    currentShapes,
                    currentShape
            );
            oos.writeObject(state);
            JOptionPane.showMessageDialog(null, "Task1进度已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Task1存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void saveTask2State(
            int result,
            int currentAngle,
            int attempt,
            Set<String> identifiedTypes,
            boolean waitingForAngleInput) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TASK2_SAVE_FILE))) {
            task2State state = new task2State(result, currentAngle, attempt, identifiedTypes, waitingForAngleInput);
            oos.writeObject(state);
            JOptionPane.showMessageDialog(null, "Task2进度已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Task2存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void saveTask3State(String currentShape, int param1, int param2, int param3,
                                       int correctAnswer, int attemptsLeft, int timeRemaining,
                                       Set<String> completedShapes, int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TASK3_SAVE_FILE))) {
            task3State state = new task3State(currentShape, param1, param2, param3, correctAnswer, attemptsLeft, timeRemaining, completedShapes, score);
            oos.writeObject(state);
            JOptionPane.showMessageDialog(null, "Task3进度已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Task3存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void saveTask4State(boolean[] completedModes, int currentMode, int radius, int attempts, int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TASK4_SAVE_FILE))) {
            task4State state = new task4State(completedModes, currentMode, radius, attempts, score);
            oos.writeObject(state);
            JOptionPane.showMessageDialog(null, "Task4进度已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Task4存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void saveBonus1State(int completedTasks, int currentShapeId, int attemptCount, int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(BONUS1_SAVE_FILE))) {
            bonus1State state = new bonus1State(completedTasks, currentShapeId, attemptCount, score);
            oos.writeObject(state);
            JOptionPane.showMessageDialog(null, "Bonus1进度已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Bonus1存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void saveBonus2State(int completedTasks, int currentShapeId, int attemptCount, int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(BONUS2_SAVE_FILE))) {
            bonus2State state = new bonus2State(completedTasks, currentShapeId, attemptCount, score);
            oos.writeObject(state);
            JOptionPane.showMessageDialog(null, "Bonus2进度已保存", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Bonus2存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
