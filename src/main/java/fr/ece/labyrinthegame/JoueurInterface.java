package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.Controllers.MazeController;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JoueurInterface {
    private Stage stage;
    private Utilisateur joueur;
    private MazeController mazeController;

    // Labels pour timer et score
    private Label timerLabel;
    private Label scoreLabel;
    private Timeline timeline;
    private int elapsedSeconds = 0;
    private int score = 0;

    public JoueurInterface(Stage stage, Utilisateur joueur) {
        this.stage = stage;
        this.joueur = joueur;
    }

    public void afficher() {
        Menu menu = new Menu(stage, joueur, this);
        menu.afficher();
    }

    public void afficherJeu() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0b1a29, #1a2c3e);");

        // Header avec titre, timer, score et bouton déconnexion
        HBox header = new HBox(30);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15));

        Label titre = new Label("LABYRINTHE GAME - JEU");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");

        timerLabel = new Label("Temps : 0s");
        timerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");

        scoreLabel = new Label("Score : 0");
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");

        Button btnDeconnexion = new Button("DÉCONNEXION");
        btnDeconnexion.setStyle(
                "-fx-background-color: #ff6347; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 16; " +
                        "-fx-background-radius: 8;"
        );
        btnDeconnexion.setOnAction(e -> {
            Connexion connexion = new Connexion();
            connexion.start(stage);
        });

        header.getChildren().addAll(titre, timerLabel, scoreLabel, btnDeconnexion);
        root.setTop(header);

        // Contenu principal
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setPadding(new Insets(30));

        Label statusLabel = new Label("Appuie sur PLAY pour commencer");
        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Button playBtn = new Button("PLAY");
        playBtn.setStyle(
                "-fx-background-color: #00d4ff; " +
                        "-fx-text-fill: #0b1a29; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-radius: 8;"
        );

        StackPane canvasContainer = new StackPane();
        canvasContainer.setStyle("-fx-background-color: #00d4ffaa; -fx-padding: 10; -fx-background-radius: 8;");
        Canvas canvas = new Canvas(650, 600);
        canvas.setFocusTraversable(true);
        canvasContainer.getChildren().add(canvas);

        centerContent.getChildren().addAll(playBtn, canvasContainer, statusLabel);
        root.setCenter(centerContent);

        Scene scene = new Scene(root, 700, 850);
        stage.setScene(scene);
        stage.setTitle("Labyrinthe Game - Player: " + joueur.getUsername());
        stage.show();

        // Initialisation du contrôleur
        mazeController = new MazeController();
        mazeController.setPlayer(joueur);
        mazeController.bindCanvas(canvas, statusLabel, playBtn);
        mazeController.connectScene(scene);

        // Action sur PLAY
        playBtn.setOnAction(e -> {
            statusLabel.setText("Le jeu commence !");
            canvas.requestFocus();
            startTimer(statusLabel); // démarrage du timer interactif
            mazeController.startGame();
        });

        // Gestion des flèches
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    mazeController.movePlayerUp();
                    incrementScore();
                    break;
                case DOWN:
                    mazeController.movePlayerDown();
                    incrementScore();
                    break;
                case LEFT:
                    mazeController.movePlayerLeft();
                    incrementScore();
                    break;
                case RIGHT:
                    mazeController.movePlayerRight();
                    incrementScore();
                    break;
                default:
                    break;
            }
        });

        Platform.runLater(canvas::requestFocus);
    }

    // Méthode pour démarrer le timer interactif
    private void startTimer(Label statusLabel) {
        elapsedSeconds = 0;
        timerLabel.setText("Temps : 0s");

        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    elapsedSeconds++;
                    timerLabel.setText("Temps : " + elapsedSeconds + "s");

                    if (elapsedSeconds >= 60) {
                        timeline.stop();
                        statusLabel.setText("Temps écoulé ! Game Over
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Méthode pour incrémenter le score
    private void incrementScore() {
        score++;
        scoreLabel.setText("Score : " + score);
    }
}