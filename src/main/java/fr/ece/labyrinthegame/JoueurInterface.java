package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.Controllers.MazeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

            Scene scene = new Scene(root);
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
