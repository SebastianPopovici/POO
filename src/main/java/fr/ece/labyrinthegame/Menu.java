package fr.ece.labyrinthegame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import fr.ece.labyrinthegame.model.Utilisateur;

public class Menu {
    private Stage stage;
    private Utilisateur utilisateur;
    private JoueurInterface joueurInterface;

    public Menu(Stage stage, Utilisateur utilisateur, JoueurInterface joueurInterface) {
        this.stage = stage;
        this.utilisateur = utilisateur;
        this.joueurInterface = joueurInterface;
    }

    public void afficher() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0b1a29, #1a2c3e);");

        Label titreLabel = new Label("LABYRINTHE GAME");
        titreLabel.setFont(Font.font("Arial", 40));
        titreLabel.setTextFill(Color.web("#00d4ff"));
        titreLabel.setStyle("-fx-font-weight: bold;");

        Label bienvenueLabel = new Label("Bienvenue, " + utilisateur.getUsername() + " !");
        bienvenueLabel.setFont(Font.font("Arial", 18));
        bienvenueLabel.setTextFill(Color.web("#85929e"));

        Button jouerButton = new Button("JOUER");
        styliserBouton(jouerButton, "#00d4ff", "#0b1a29");
        jouerButton.setOnAction(e -> {
            joueurInterface.afficherJeu(); // Appelle la méthode pour afficher l'interface de jeu
        });

        Button deconnexionButton = new Button("DÉCONNEXION");
        styliserBouton(deconnexionButton, "#ff6347", "#ffffff");
        deconnexionButton.setOnAction(e -> {
            new Connexion().start(stage); // Retour à la page de connexion
        });

        VBox boutonsContainer = new VBox(20);
        boutonsContainer.setAlignment(Pos.CENTER);
        boutonsContainer.getChildren().addAll(jouerButton, deconnexionButton);

        root.getChildren().addAll(titreLabel, bienvenueLabel, boutonsContainer);

        Scene scene = new Scene(root, 700, 600);
        stage.setScene(scene);
        stage.setTitle("Menu Principal - Labyrinthe Game");
        stage.show();
    }

    private void styliserBouton(Button bouton, String couleurFond, String couleurTexte) {
        bouton.setPrefWidth(250);
        bouton.setPrefHeight(50);
        bouton.setStyle(
                "-fx-background-color: " + couleurFond + "; " +
                        "-fx-text-fill: " + couleurTexte + "; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 10px 20px;"
        );
        bouton.setOnMouseEntered(e -> {
            bouton.setStyle(
                    "-fx-background-color: derive(" + couleurFond + ", 20%); " +
                            "-fx-text-fill: " + couleurTexte + "; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-padding: 10px 20px;"
            );
        });
        bouton.setOnMouseExited(e -> {
            bouton.setStyle(
                    "-fx-background-color: " + couleurFond + "; " +
                            "-fx-text-fill: " + couleurTexte + "; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-padding: 10px 20px;"
            );
        });
    }
}
