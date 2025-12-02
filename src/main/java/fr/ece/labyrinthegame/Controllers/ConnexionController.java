package fr.ece.labyrinthegame.Controllers;


import fr.ece.labyrinthegame.AdminInterface;
import fr.ece.labyrinthegame.dao.UtilisateurDAO;
import fr.ece.labyrinthegame.model.Utilisateur;
import fr.ece.labyrinthegame.Controllers.MazeController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ConnexionController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;


    // Called when the LOGIN button is clicked
    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Enter username and password.");
            return;
        }

        UtilisateurDAO dao = new UtilisateurDAO();
        Utilisateur user = dao.authentifier(username, password);

        if (user == null) {
            messageLabel.setText("Invalid username or password.");
            return;
        }

        // Get current stage from one of the fields
        Stage stage = (Stage) usernameField.getScene().getWindow();

        // Admin case
        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            AdminInterface adminUI = new AdminInterface(stage, user);
            adminUI.afficher();
            return;
        }

        // Player case → load MazeGame.fxml
        if (user.getRole().equalsIgnoreCase("JOUEUR")) {
            loadMazeInterface(stage, user);
            return;
        }

        // Unknown role
        messageLabel.setText("Unknown role: " + user.getRole());
    }


    private void loadMazeInterface(Stage stage, Utilisateur joueur) { // Added Utilisateur joueur
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/labyrinthegame/MainGame.fxml"));
            BorderPane root = loader.load();

            MazeController controller = loader.getController();
            Scene scene = new Scene(root);

            // ***********************************************
            // CRITICAL FIX: Pass the authenticated player!
            controller.setPlayer(joueur);
            // ***********************************************

            controller.connectScene(scene);

            stage.setTitle("Maze Game");
            stage.setScene(scene);
            stage.setResizable(false);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading Maze interface.");
        }
    }
}
