package com.shapeville.manager;

import com.shapeville.data.ShapeData;
import com.shapeville.data.ShapeData.*;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArchiveManager {
    private static final String TASK1_SAVE_FILE = "Archive/task1_saved_file.dat";
    private static final String TASK2_SAVE_FILE = "Archive/task2_saved_file.dat";
    private static final String TASK3_SAVE_FILE = "Archive/task3_saved_file.dat";
    private static final String TASK4_SAVE_FILE = "Archive/task4_saved_file.dat";
    private static final String BONUS1_SAVE_FILE = "Archive/bonus1_saved_file.dat";
    private static final String BONUS2_SAVE_FILE = "Archive/bonus2_saved_file.dat";

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

        public task1State(
                int isIdentifiedShapes,
                int[] is_played_task1,
                int score

        ) {

            this.isIdentifiedShapes = isIdentifiedShapes;
            this.is_played_task1 = is_played_task1.clone();
            this.score = score;

        }
    }

    public static class task2State implements Serializable {
        private static final long serialVersionUID = 3L;

        public Set<String> identifiedTypes;
        public boolean waitingForAngleInput;
        public int score;

        public task2State(
                Set<String> identifiedTypes,
                boolean waitingForAngleInput,
                int score
        ) {
            this.identifiedTypes = new HashSet<>(identifiedTypes); // 克隆集合以确保状态隔离
            this.waitingForAngleInput = waitingForAngleInput;
            this.score = score;
        }
    }

    public static class task3State implements Serializable {
        private static final long serialVersionUID = 4L;

        public Set<String> CompletedShapes;
        public int score;

        public task3State(Set<String> CompletedShapes, int score) {

            this.CompletedShapes = CompletedShapes; // 克隆集合
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
            int score

    ) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TASK1_SAVE_FILE))) {
            task1State state = new task1State(
                    isIdentifiedShapes,
                    is_played_task1,
                    score
            );
            oos.writeObject(state);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Task1存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void saveTask2State(
            Set<String> identifiedTypes,
            boolean waitingForAngleInput,int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TASK2_SAVE_FILE))) {
            task2State state = new task2State(identifiedTypes, waitingForAngleInput,score);
            oos.writeObject(state);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Task2存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void saveTask3State(Set<String> completedShapes, int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TASK3_SAVE_FILE))) {
            task3State state = new task3State(completedShapes, score);
            oos.writeObject(state);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Task3存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void saveTask4State(boolean[] completedModes, int currentMode, int radius, int attempts, int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TASK4_SAVE_FILE))) {
            task4State state = new task4State(completedModes, currentMode, radius, attempts, score);
            oos.writeObject(state);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Task4存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void saveBonus1State(int completedTasks, int currentShapeId, int attemptCount, int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(BONUS1_SAVE_FILE))) {
            bonus1State state = new bonus1State(completedTasks, currentShapeId, attemptCount, score);
            oos.writeObject(state);
            } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Bonus1存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void saveBonus2State(int completedTasks, int currentShapeId, int attemptCount, int score) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(BONUS2_SAVE_FILE))) {
            bonus2State state = new bonus2State(completedTasks, currentShapeId, attemptCount, score);
            oos.writeObject(state);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Bonus2存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static task1State loadTask1State() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TASK1_SAVE_FILE))) {
            return (task1State) ois.readObject();
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "加载Task1存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static task2State loadTask2State() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TASK2_SAVE_FILE))) {
            return (task2State) ois.readObject();
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "加载Task2存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static task3State loadTask3State() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TASK3_SAVE_FILE))) {
            return (task3State) ois.readObject();
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "加载Task3存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static task4State loadTask4State() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TASK4_SAVE_FILE))) {
            return (task4State) ois.readObject();
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "加载Task4存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static bonus1State loadBonus1State() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BONUS1_SAVE_FILE))) {
            return (bonus1State) ois.readObject();
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "加载Bonus1存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static bonus2State loadBonus2State() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BONUS2_SAVE_FILE))) {
            return (bonus2State) ois.readObject();
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "加载Bonus2存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
