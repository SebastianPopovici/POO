package fr.ece.labyrinthegame.dao;

import fr.ece.labyrinthegame.model.Utilisateur;

import java.sql.*;

public class ScoreDAO {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/labyrinthe", "root", "");
    }


    public int getScore(Utilisateur user) {
        String sql = "SELECT score FROM utilisateurs WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // default 0 if user not found
    }

    // Add score to existing score
    public void addScore(Utilisateur user, int points) {
        int current = getScore(user);
        String sql = "UPDATE utilisateurs SET score = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, current + points);
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
