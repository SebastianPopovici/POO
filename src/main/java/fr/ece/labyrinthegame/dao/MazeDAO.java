package fr.ece.labyrinthegame.dao;

import fr.ece.labyrinthegame.model.Labyrinthe;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeDAO {

    private final String url = "jdbc:mysql://localhost:3306/labyrinthe";
    private final String user = "root";
    private final String pass = "";

    private final Gson gson = new Gson();

    // ------------------ ADD MAZE ------------------
    public boolean addMaze(int id, String gridJson) {
        String sql = "INSERT INTO maze (grille) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gridJson);
            int affected = stmt.executeUpdate();
            return affected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------ GET RANDOM MAZE ------------------
    public int[][] getRandomMaze() {
        String sql = "SELECT grille FROM maze";
        List<String> grids = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                grids.add(rs.getString("grille"));
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
        return gson.fromJson(json, int[][].class);
    }

    // ------------------ DELETE MAZE ------------------
    public boolean supprimerMaze(int id) {
        String sql = "DELETE FROM maze WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------ GET ALL MAZES ------------------
    public List<Labyrinthe> getAllMazes() {
        List<Labyrinthe> mazes = new ArrayList<>();
        String sql = "SELECT * FROM maze";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                mazes.add(new Labyrinthe(rs.getInt("id"), rs.getString("grille")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mazes;
    }
}
