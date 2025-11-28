package fr.ece.labyrinthegame.dao;

import fr.ece.labyrinthegame.model.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    // Authentification
    public Utilisateur authentifier(String username, String password) {
        String query = "SELECT * FROM utilisateurs WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Créer un nouveau joueur
    public boolean creerJoueur(String username, String password) {
        String query = "INSERT INTO utilisateurs (username, password, role) VALUES (?, ?, 'JOUEUR')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer tous les joueurs
    public List<Utilisateur> getAllJoueurs() {
        List<Utilisateur> joueurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateurs WHERE role = 'JOUEUR'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                joueurs.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return joueurs;
    }

    // Supprimer un joueur
    public boolean supprimerJoueur(int id) {
        String query = "DELETE FROM utilisateurs WHERE id = ? AND role = 'JOUEUR'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }
}
