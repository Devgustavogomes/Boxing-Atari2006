package br.com.mvbos.lgj.boxing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingManager {

    private static final String FILE_NAME = "ranking.txt";
    private List<PlayerScore> scores;

    public RankingManager() {
        scores = new ArrayList<>();
        load();
    }

    public void addScore(String name, int score) {
        scores.add(new PlayerScore(name, score));
        Collections.sort(scores);
        if (scores.size() > 10) {
            scores.remove(scores.size() - 1);
        }
        save();
    }

    public List<PlayerScore> getTopScores() {
        return scores;
    }

    private void load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    scores.add(new PlayerScore(parts[0], Integer.parseInt(parts[1])));
                }
            }
            Collections.sort(scores);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (PlayerScore ps : scores) {
                bw.write(ps.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
