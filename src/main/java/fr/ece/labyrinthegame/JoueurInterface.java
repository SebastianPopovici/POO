package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.Controllers.MazeController;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class JoueurInterface {

    private Stage stage;
    private Utilisateur joueur;
    private MazeController mazeController;

    public JoueurInterface(Stage stage, Utilisateur joueur) {
        this.stage = stage;
        this.joueur = joueur;
    }

    public void afficher() {
        // Root layout
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0b1a29, #1a2c3e);");

        // Title
        Label titre = new Label("LABYRINTHE GAME");
        titre.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");

        // Status label
        Label statusLabel = new Label("Press PLAY to start");
        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        // Play button
        Button playBtn = new Button("PLAY");
        playBtn.setStyle("-fx-background-color: #00d4ff; -fx-text-fill: #0b1a29; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 8;");

        // Canvas container
        StackPane canvasContainer = new StackPane();
        canvasContainer.setStyle("-fx-background-color: #F2D16D; -fx-padding: 10; -fx-background-radius: 8;");
        Canvas canvas = new Canvas(650, 600);
        canvasContainer.getChildren().add(canvas);

        // Add components to root
        root.getChildren().addAll(titre, playBtn, canvasContainer, statusLabel);

        // Scene
        Scene scene = new Scene(root, 700, 850);
        stage.setScene(scene);
        stage.setTitle("Labyrinthe Game - Player: " + joueur.getUsername());
        stage.show();

        // Initialize MazeController manually
        mazeController = new MazeController();
        mazeController.setPlayer(joueur);

        // Bind the manually created UI components
        mazeController.bindCanvas(canvas, statusLabel, playBtn);

        // Connect scene for key input
        mazeController.connectScene(scene);

        // Ensure canvas has focus for key controls
        Platform.runLater(canvas::requestFocus);
    }
}
