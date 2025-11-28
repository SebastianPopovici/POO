package fr.ece.labyrinthegame;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import fr.ece.labyrinthegame.model.Utilisateur;

public class AdminInterface {

    private Stage stage;
    private Utilisateur admin;

    public AdminInterface(Stage stage, Utilisateur admin) {
        this.stage = stage;
        this.admin = admin;
    }

    public void afficher() {
        // Conteneur simple
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        // Label de confirmation unique
        Label confirmation = new Label("INTERFACE ADMIN - Connexion réussie pour: " + admin.getUsername());

        root.getChildren().add(confirmation);

        // Créer la Scène et l'afficher
        Scene scene = new Scene(root, 400, 200);
        stage.setScene(scene);
        stage.setTitle("Admin Confirme");
    }

    // Toutes les autres méthodes (créerOnglet, genererLabyrinthe, etc.) sont supprimées.
}