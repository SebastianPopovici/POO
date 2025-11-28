package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.dao.DatabaseConnection;

import java.sql.Connection;

public class DBConnection {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();

        if (conn != null) {
            System.out.println(" Connexion good !");
        } else {
            System.out.println("no connexion.");
        }
    }
}