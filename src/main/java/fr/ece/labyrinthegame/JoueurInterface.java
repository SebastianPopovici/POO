package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.Controllers.MazeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import fr.ece.labyrinthegame.model.Utilisateur;

import java.io.IOException;

public class JoueurInterface {

    private Stage stage;
    private Utilisateur joueur;

    public JoueurInterface(Stage stage, Utilisateur joueur) {
        this.stage = stage;
        this.joueur = joueur;
    }

    public void afficher() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/labyrinthegame/MainGame.fxml"));
            Parent root = loader.load();

            MazeController controller = loader.getController();
            controller.setPlayer(joueur);

            // --- AJOUT DU BOUTON DECONNEXION ---
            Button logoutButton = new Button("Déconnexion");
            logoutButton.setStyle("-fx-font-size: 16px; -fx-background-color: #d9534f; -fx-text-fill: white;");
            logoutButton.setOnAction(e -> {
                try {
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fr/ece/labyrinthegame/login.fxml"));
                    Parent loginRoot = loginLoader.load();

                    Scene loginScene = new Scene(loginRoot);
                    stage.setScene(loginScene);
                    stage.setTitle("Login");
                    stage.show();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la déconnexion.");
                    alert.showAndWait();
                }
            });

            // On place le bouton au-dessus du jeu
            StackPane container = new StackPane();
            container.getChildren().addAll(root, logoutButton);
            StackPane.setMargin(logoutButton, new javafx.geometry.Insets(20));

            Scene scene = new Scene(container);
            stage.setScene(scene);
            stage.setTitle("Labyrinth Game - Player: " + joueur.getUsername());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot load game interface.");
            alert.showAndWait();
        }
    }
}
