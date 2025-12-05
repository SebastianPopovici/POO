package fr.ece.labyrinthegame.Controllers;

import fr.ece.labyrinthegame.dao.MazeDAO;
import fr.ece.labyrinthegame.dao.ScoreDAO;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class MazeController {
    @FXML private Canvas canvas;
    @FXML private Label status;
    @FXML private Button playBtn;
    private Scene scene;
    private int[][] maze;
    private int rows, cols;
    private int playerCol, playerRow;
    private boolean keyCollected, gameWon;
    private long invisibleUntil = 0;
    private Utilisateur joueur;
    private ScoreDAO scoreDAO = new ScoreDAO(); // DAO for scores

    @FXML
    public void initialize() {
        if (playBtn != null) {
            playBtn.setText("Play");
            playBtn.setOnAction(e -> {
                startGame();
                requestCanvasFocus();
            });
        }
        if (canvas != null) {
            canvas.widthProperty().addListener(e -> draw());
            canvas.heightProperty().addListener(e -> draw());
            canvas.setFocusTraversable(true);
            canvas.setOnKeyPressed(this::handleKeyPress);
        }
    }

    public void setPlayer(Utilisateur joueur) {
        this.joueur = joueur;
        Platform.runLater(() -> {
            startGame();
            requestCanvasFocus();
        });
    }

    public void bindCanvas(Canvas canvas, Label statusLabel, Button playBtn) {
        this.canvas = canvas;
        this.status = statusLabel;
        this.playBtn = playBtn;

        // Bind events
        canvas.setFocusTraversable(true);
        canvas.widthProperty().addListener(e -> draw());
        canvas.heightProperty().addListener(e -> draw());

        playBtn.setOnAction(e -> startGame());

        // Key input
        canvas.setOnKeyPressed(this::handleKeyPress);
    }

    public void connectScene(Scene sc) {
        this.scene = sc;
        sc.setOnMouseClicked(e -> requestCanvasFocus());
    }

    private void requestCanvasFocus() {
        if (canvas == null) return;
        canvas.setFocusTraversable(true);
        Platform.runLater(canvas::requestFocus);
    }

    private void handleKeyPress(KeyEvent e) {
        if (gameWon) return;

        int dcol = 0, drow = 0;
        if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) drow = -1;
        else if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) drow = 1;
        else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) dcol = -1;
        else if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) dcol = 1;
        else return;

        movePlayer(dcol, drow);
    }

    public void startGame() {
        if (playBtn != null) {
            playBtn.setDisable(true); // disable during game
            playBtn.setText("Playing");
        }
        loadMazeFromDatabase();
        resetPlayer();
        draw();
    }

    private void resetPlayer() {
        playerCol = 1;
        playerRow = 1;
        keyCollected = false;
        gameWon = false;
        invisibleUntil = 0;
        if (status != null) status.setText("Find the key.");
    }

    private void loadMazeFromDatabase() {
        try {
            MazeDAO dao = new MazeDAO();
            maze = dao.getRandomMaze();
            rows = maze.length;
            cols = maze[0].length;
        } catch (Exception e) {
            e.printStackTrace();
            if (status != null) status.setText("Could not load maze from DB.");
        }
    }

    private void movePlayer(int dcol, int drow) {
        int nc = playerCol + dcol;
        int nr = playerRow + drow;

        if (nc < 0 || nr < 0 || nc >= cols || nr >= rows) return;

        int tile = maze[nr][nc];
        boolean canPassWalls = System.currentTimeMillis() < invisibleUntil;

        if (tile == 1 && !canPassWalls) return;

        if (tile == 3) { // Exit
            if (!keyCollected) {
                if (status != null) status.setText("Exit locked — find the key!");
                return;
            }
            gameWon = true;
            playerCol = nc;
            playerRow = nr;
            if (status != null) status.setText("You escaped!");

            // Update score and show leaderboard
            updateScore();
            showLeaderboard();
        } else {
            playerCol = nc;
            playerRow = nr;
        }

        if (tile == 2) { // Key
            keyCollected = true;
            maze[nr][nc] = 0;
            if (status != null) status.setText("Key collected! Find the exit.");
        }

        draw();
    }

    private void draw() {
        if (maze == null || canvas == null) return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double cellSize = Math.min(canvas.getWidth() / cols, canvas.getHeight() / rows);
        double mazeW = cols * cellSize;
        double mazeH = rows * cellSize;
        double offsetX = (canvas.getWidth() - mazeW) / 2;
        double offsetY = (canvas.getHeight() - mazeH) / 2;

        gc.setFill(Color.web("#00d4ff"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = offsetX + c * cellSize;
                double y = offsetY + r * cellSize;
                int t = maze[r][c];
                gc.setFill(Color.web("#F2D16D"));
                gc.fillRect(x, y, cellSize, cellSize);
                if (t == 1) gc.setFill(Color.web("#C19A6B"));
                if (t == 2) gc.setFill(Color.web("#FFD700"));
                if (t == 3) gc.setFill(keyCollected ? Color.web("#DAA520") : Color.web("#8B4513"));
                if (t != 0) gc.fillRect(x, y, cellSize, cellSize);
                gc.setStroke(Color.web("#B58B45"));
                gc.strokeRect(x, y, cellSize, cellSize);
            }
        }

        drawPlayer(gc, cellSize, offsetX, offsetY);
    }

    private void drawPlayer(GraphicsContext gc, double cellSize, double offsetX, double offsetY) {
        double px = offsetX + playerCol * cellSize + cellSize / 2.0;
        double py = offsetY + playerRow * cellSize + cellSize / 2.0;
        boolean isInvisible = System.currentTimeMillis() < invisibleUntil;
        gc.setStroke(isInvisible ? Color.web("#000000", 0.35) : Color.BLACK);
        gc.setFill(isInvisible ? Color.web("#5D3FD3", 0.5) : Color.web("#5D3FD3"));
        gc.setLineWidth(isInvisible ? 1 : 2);
        gc.strokeOval(px - cellSize * 0.15, py - cellSize * 0.3, cellSize * 0.3, cellSize * 0.3);
        gc.strokeLine(px, py - cellSize * 0.06, px, py + cellSize * 0.25);
        gc.strokeLine(px, py, px - cellSize * 0.18, py - cellSize * 0.06);
        gc.strokeLine(px, py, px + cellSize * 0.18, py - cellSize * 0.06);
        gc.strokeLine(px, py + cellSize * 0.25, px - cellSize * 0.18, py + cellSize * 0.44);
        gc.strokeLine(px, py + cellSize * 0.25, px + cellSize * 0.18, py + cellSize * 0.44);
    }

    public void activateInvisibility(long durationMillis) {
        invisibleUntil = System.currentTimeMillis() + durationMillis;
        draw();
    }

    // ------------------- SCORE LOGIC -------------------
    private void updateScore() {
        if (joueur != null) {
            scoreDAO.addScore(joueur, 100); // Add 100 points for completing maze
        }
    }

    private void showLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/labyrinthegame/score.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Leaderboard");
            stage.setScene(new Scene(root));
            stage.initOwner(canvas.getScene().getWindow()); // attach to main window
            stage.show();

            // Enable Play Again button
            if (playBtn != null) {
                playBtn.setText("Play Again");
                playBtn.setDisable(false);
                playBtn.setOnAction(e -> {
                    stage.close();  // close leaderboard
                    startGame();    // restart game
                    requestCanvasFocus();
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void movePlayerUp() { System.out.println("↑ Haut"); }
    public void movePlayerDown() { System.out.println("↓ Bas"); }
    public void movePlayerLeft() { System.out.println("← Gauche"); }
    public void movePlayerRight() { System.out.println("→ Droite"); }
}
