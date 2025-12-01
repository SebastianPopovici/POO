package fr.ece.labyrinthegame.Controllers;

import fr.ece.labyrinthegame.dao.UtilisateurDAO;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ConnexionController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

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

        Stage stage = (Stage) usernameField.getScene().getWindow();

        if (user.getRole().equalsIgnoreCase("JOUEUR")) {
            loadMazeInterface(stage, user);
        } else {
            messageLabel.setText("Role not supported yet: " + user.getRole());
        }
    }

    private void loadMazeInterface(Stage stage, Utilisateur joueur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainGame.fxml"));
            BorderPane root = loader.load();
            MazeController controller = loader.getController();

            controller.setPlayer(joueur);

            Scene scene = new Scene(root);
            controller.connectScene(scene);

            stage.setScene(scene);
            stage.setTitle("Maze Game - Joueur");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Maze interface not found.");
        }
    }
}
