package fr.ece.labyrinthegame.Controllers;

import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
            Scene scene = new Scene(loader.load());

            MazeController controller = loader.getController();
            controller.setPlayer(joueur);
            controller.connectScene(scene);

            stage.setScene(scene);
            stage.setTitle("Labyrinthe - Joueur");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

