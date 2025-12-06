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
    private ScoreDAO scoreDAO = new ScoreDAO();

    private Runnable onWinCallback;

    public void setOnWinCallback(Runnable r) { this.onWinCallback = r; }

    @FXML
    public void initialize() {
        if (canvas != null) {
            canvas.setFocusTraversable(true);
            canvas.setOnKeyPressed(this::handleKeyPress);
            canvas.widthProperty().addListener(e -> draw());
            canvas.heightProperty().addListener(e -> draw());
        }

        if (playBtn != null) {
            playBtn.setOnAction(e -> {
                startGame();
                requestCanvasFocus();
            });
        }
    }

    public void setPlayer(Utilisateur joueur) {
        this.joueur = joueur;
        Platform.runLater(this::requestCanvasFocus);
    }

    public void bindCanvas(Canvas canvas, Label statusLabel, Button playBtn) {
        this.canvas = canvas;
        this.status = statusLabel;
        this.playBtn = playBtn;

        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::handleKeyPress);
        canvas.widthProperty().addListener(e -> draw());
        canvas.heightProperty().addListener(e -> draw());
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

        // Ensure canvas keeps focus for continuous movement
        Platform.runLater(() -> canvas.requestFocus());
    }

    public void startGame() {
        if (playBtn != null) {
            playBtn.setDisable(true);
            playBtn.setText("Playing");
        }
        loadMazeFromDatabase();
        resetPlayer();
        draw();
        requestCanvasFocus();
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

        // Check bounds
        if (nc < 0 || nr < 0 || nc >= cols || nr >= rows) return;

        int tile = maze[nr][nc];
        boolean canPassWalls = System.currentTimeMillis() < invisibleUntil;

        // Check wall
        if (tile == 1 && !canPassWalls) return;

        // Exit logic
        if (tile == 3) {
            if (!keyCollected) {
                if (status != null) status.setText("Exit locked — find the key!");
                return;
            }
            gameWon = true;
            playerCol = nc;
            playerRow = nr;
            if (status != null) status.setText("You escaped!");
            if (onWinCallback != null) onWinCallback.run();
            showLeaderboard();
            return;
        }

        // Update player position
        playerCol = nc;
        playerRow = nr;

        // Collect key
        if (tile == 2) {
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
        double offsetX = (canvas.getWidth() - cols * cellSize) / 2;
        double offsetY = (canvas.getHeight() - rows * cellSize) / 2;

        // Background
        gc.setFill(Color.web("#00d4ff"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Maze
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

        // Player
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

    private void showLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/labyrinthegame/score.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Leaderboard");
            stage.setScene(new Scene(root));
            stage.initOwner(canvas.getScene().getWindow());
            stage.show();

            if (playBtn != null) {
                playBtn.setText("Play Again");
                playBtn.setDisable(false);
                playBtn.setOnAction(e -> {
                    stage.close();
                    startGame();
                    requestCanvasFocus();
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disableMovement() {
        gameWon = true;
    }
}
