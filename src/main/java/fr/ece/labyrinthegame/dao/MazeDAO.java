package fr.ece.labyrinthegame.dao;

import fr.ece.labyrinthegame.model.Labyrinthe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        // Example: "[[0,1,0],[2,0,3]]"
        json = json.replaceAll("\\[|\\]", ""); // remove brackets
        String[] rows = json.split(",");

        // Count rows and columns
        int rowCount = (int) json.chars().filter(ch -> ch == ',').count() / (rows.length / rows.length) + 1;
        int colCount = rows.length / rowCount;

        int[][] maze = new int[rowCount][colCount];

        int r = 0, c = 0;
        for (String s : rows) {
            maze[r][c] = Integer.parseInt(s.trim());
            c++;
            if (c == colCount) {
                c = 0;
                r++;
            }
        }
        return maze;
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
        String sql = "SELECT * FROM mazes";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                mazes.add(new Labyrinthe(rs.getInt("id"), rs.getString("grid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mazes;
    }

}
