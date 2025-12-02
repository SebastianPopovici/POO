package fr.ece.labyrinthegame.dao;
import java.io.*;
import java.util.*;
import java.sql.*;
import fr.ece.labyrinthegame.model.Score;

public class ScoreManager {
    private static ScoreManager instance;
    private List<Score> scores;
    private static final String SCORE_FILE = "scores.dat"; // Fichier de sauvegarde

    private ScoreManager() {
        scores = new ArrayList<>();
        loadScores(); // Charge les scores au démarrage
    }

    public static synchronized ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    // Sauvegarde les scores dans un fichier
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORE_FILE))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Charge les scores depuis un fichier
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(SCORE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                scores = (List<Score>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addScore(Score score) {
        scores.add(score);
        saveScores(); // Sauvegarde après chaque ajout
    }

    public List<Score> getTopScores(int limit) {
        scores.sort(Comparator.comparingInt(Score::getValue).reversed());
        return scores.subList(0, Math.min(limit, scores.size()));
    }
}
