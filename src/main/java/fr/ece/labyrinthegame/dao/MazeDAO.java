package fr.ece.labyrinthegame.dao;

import com.google.gson.Gson;
import fr.ece.labyrinthegame.model.Labyrinthe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static fr.ece.labyrinthegame.dao.DatabaseConnection.getConnection;

public class MazeDAO {

    private final String url = "jdbc:mysql://localhost:3306/Labyrinthe";
    private final String user = "root";
    private final String pass = "";

    // ------------------ ADD MAZE ------------------
    public boolean addMaze(int id, String gridJson) {
        String sql = "INSERT INTO mazes (id, grid) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, gridJson);
            int affected = stmt.executeUpdate();
            return affected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------ GET RANDOM MAZE ------------------
    public int[][] getRandomMaze() {
        String sql = "SELECT grid FROM mazes";
        List<String> grids = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                grids.add(rs.getString("grid"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (grids.isEmpty()) return null;

        // Pick a random grid
        Random random = new Random();
        String gridJson = grids.get(random.nextInt(grids.size()));

        // Convert JSON string to int[][]
        return parseGridJson(gridJson);
    }

    // ------------------ HELPER: parse JSON string ------------------
    private int[][] parseGridJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, int[][].class);
    }
    public boolean supprimerMaze(int id) {
        String sql = "DELETE FROM mazes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Labyrinthe> getAllMazes() {
        List<Labyrinthe> mazes = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, grid FROM maze")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String jsonGrid = rs.getString("grid");
                mazes.add(new Labyrinthe(id, jsonGrid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mazes;
    }


}