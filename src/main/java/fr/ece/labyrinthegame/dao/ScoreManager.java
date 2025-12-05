package fr.ece.labyrinthegame.dao;

import fr.ece.labyrinthegame.model.Score;
import fr.ece.labyrinthegame.model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreManager {

    private static ScoreManager instance;
    private final ScoreDAO scoreDAO;

    private ScoreManager() {
        scoreDAO = new ScoreDAO();
    }

    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    public int getScore(Utilisateur user) {
        return scoreDAO.getScore(user);
    }

    public void addScore(Utilisateur user, int points) {
        scoreDAO.addScore(user, points);
    }

    // Fixed method: fetch top scores directly from utilisateurs table
    public List<Score> getTopScores(int limit) {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT username, score FROM utilisateurs " +
                "WHERE role != 'admin' " +   // exclude admins
                "ORDER BY score DESC LIMIT ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/labyrinthe", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                scores.add(new Score(rs.getString("username"), rs.getInt("score")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }


}
