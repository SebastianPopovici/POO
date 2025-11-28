package fr.ece.labyrinthegame;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import fr.ece.labyrinthegame.model.Utilisateur;

public class JoueurInterface {

    private Stage stage;
    private Utilisateur joueur;

    public JoueurInterface(Stage stage, Utilisateur joueur) {
        this.stage = stage;
        this.joueur = joueur;
    }

    public void afficher() {
        // Conteneur simple
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        // Label de confirmation unique
        Label confirmation = new Label("INTERFACE JOUEUR - Bienvenue: " + joueur.getUsername());

        root.getChildren().add(confirmation);

        // Créer la Scène et l'afficher
        Scene scene = new Scene(root, 400, 200);
        stage.setScene(scene);
        stage.setTitle("Joueur Confirme");
    }
}