package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.Controllers.MazeController;
import fr.ece.labyrinthegame.dao.ScoreDAO;
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

    // Timer & score
    private Label timerLabel;
    private Label scoreLabel;
    private Timeline timeline;
    private int remainingTime = 60;
    private int score = 60;

    private ScoreDAO scoreDAO = new ScoreDAO();

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

        // Header
        HBox header = new HBox(30);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15));

        Label titre = new Label("LABYRINTHE GAME - JEU");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");

        timerLabel = new Label("Temps : 60s");
        timerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");

        scoreLabel = new Label("Score : 60");
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

        // Center content
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
        canvasContainer.setStyle("-fx-background-color: #808080; -fx-padding: 10; -fx-background-radius: 8;");
        Canvas canvas = new Canvas(650, 600);
        canvas.setFocusTraversable(true);
        canvasContainer.getChildren().add(canvas);

        centerContent.getChildren().addAll(playBtn, canvasContainer, statusLabel);
        root.setCenter(centerContent);

        Scene scene = new Scene(root, 700, 850);
        stage.setScene(scene);
        stage.setTitle("Labyrinthe Game - Player: " + joueur.getUsername());
        stage.show();

        // Maze controller
        mazeController = new MazeController();
        mazeController.setPlayer(joueur);
        mazeController.bindCanvas(canvas, statusLabel, playBtn);
        mazeController.connectScene(scene);

        // STOP TIMER WHEN PLAYER WINS
        mazeController.setOnWinCallback(() -> {
            if (timeline != null) timeline.stop();
            score = remainingTime;
            scoreLabel.setText("Score : " + score);
            saveScore();

            // Reset timer and start new countdown
            resetTimer(statusLabel);
        });

        // PLAY BUTTON
        playBtn.setOnAction(e -> {
            startNewGame(statusLabel);
        });

        Platform.runLater(canvas::requestFocus);
    }

    // Start a new game
    private void startNewGame(Label statusLabel) {
        remainingTime = 60;
        score = 60;
        timerLabel.setText("Temps : 60s");
        scoreLabel.setText("Score : 60");

        if (timeline != null) timeline.stop();

        mazeController.startGame();
        resetTimer(statusLabel);
    }

    // Reset timer and start countdown
    private void resetTimer(Label statusLabel) {
        remainingTime = 60;
        score = 60;
        timerLabel.setText("Temps : 60s");
        scoreLabel.setText("Score : 60");

        startTimer(statusLabel);
    }

    // TIMER COUNTDOWN LOGIC 60 → 0
    private void startTimer(Label statusLabel) {
        if (timeline != null) timeline.stop();

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            remainingTime--;

            Platform.runLater(() -> {
                timerLabel.setText("Temps : " + remainingTime + "s");
                score = remainingTime;
                scoreLabel.setText("Score : " + score);

                if (remainingTime <= 0) {
                    timeline.stop();
                    timerLabel.setText("Temps : 0s");
                    statusLabel.setText("Temps écoulé ! Game Over");
                    mazeController.disableMovement();
                    saveScore();

                    // Automatically reset for next game
                    resetTimer(statusLabel);
                    mazeController.startGame();
                }
            });
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    // Save current score to database
    private void saveScore() {
        if (joueur != null && score > 0) {
            joueur.setScore(joueur.getScore() + score); // add current game score
            scoreDAO.addScore(joueur,score); // update DB
        }
    }
}
