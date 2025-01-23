package task3;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HighScores {
    private List<ScoreEntry> scores;
//    private final String filename = "/highscores.txt";
    private final String filename = "task3/src/main/resources/highscores.txt";

    public HighScores() {
        scores = new ArrayList<>();
        LoadScores();
    }

    public void LoadScores() {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    int time = Integer.parseInt(parts[2]);
                    scores.add(new ScoreEntry(name, score, time));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOE exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void SaveScores() {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
            for (ScoreEntry entry : scores) {
                out.write(entry.GetName() + "," + entry.GetScore() + "," + entry.GetTime() + "\n");
            }
        } catch (IOException e) {
            System.out.println("IOE exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void AddScore(String name, int score, int time) {
        scores.add(new ScoreEntry(name, score, time));
//        Collections.sort(scores); // ascending by default !!! not working atm
        if (scores.size() > 10) {
            scores.remove(scores.size() - 1); // remove last if > 10 on display
        }
        SaveScores();
    }

    public void ShowHighScores() {
        StringBuilder sb = new StringBuilder();
        for (ScoreEntry entry : scores) {
            sb.append(entry.GetName()).append(" - ").append(entry.GetScore()).append(" - ").append(entry.GetTime()).append("s\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    // 3 getter methods
    public List<Integer> GetScores() {
        List<Integer> scores = new ArrayList<>();
        for (ScoreEntry entry : this.scores) {
            scores.add(entry.GetScore());
        }
        return scores;
    }

    public List<String> GetNames() {
        List<String> names = new ArrayList<>();
        for (ScoreEntry entry : this.scores) {
            names.add(entry.GetName());
        }
        return names;
    }

    public List<Integer> GetTimes() {
        List<Integer> times = new ArrayList<>();
        for (ScoreEntry entry : this.scores) {
            times.add(entry.GetTime());
        }
        return times;
    }

    private class ScoreEntry {
        private String name;
        private int score;
        private int time;

        ScoreEntry(String name, int score, int time) {
            this.name = name;
            this.score = score;
            this.time = time;
        }

        public String GetName() {
            return name;
        }

        public int GetScore() {
            return score;
        }

        public int GetTime() {
            return time;
        }
    }
}
