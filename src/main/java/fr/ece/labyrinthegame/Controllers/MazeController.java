package fr.ece.labyrinthegame.Controllers;

import fr.ece.labyrinthegame.dao.MazeDAO;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.Scene;

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

    @FXML
    public void initialize() {
        playBtn.setOnAction(e -> startGame());


        canvas.widthProperty().addListener((obs, oldV, newV) -> draw());
        canvas.heightProperty().addListener((obs, oldV, newV) -> draw());
    }

    public void setPlayer(Utilisateur joueur) {
        this.joueur = joueur;

        javafx.application.Platform.runLater(() -> startGame());
    }

    public void connectScene(Scene sc) {
        this.scene = sc;

        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        scene.setOnMouseClicked(e -> canvas.requestFocus());

        scene.setOnKeyPressed(e -> {
            if (gameWon) return;

            int dcol = 0, drow = 0;
            KeyCode code = e.getCode();

            switch (code) {
                case UP, W -> drow = -1;
                case DOWN, S -> drow = 1;
                case LEFT, A -> dcol = -1;
                case RIGHT, D -> dcol = 1;
            }

            if (dcol != 0 || drow != 0)
                movePlayer(dcol, drow);
        });
    }

    private void startGame() {
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
        status.setText("Find the key.");
    }

    private void loadMazeFromDatabase() {
        try {
            MazeDAO dao = new MazeDAO();
            maze = dao.getRandomMaze();
            rows = maze.length;
            cols = maze[0].length;

        } catch (Exception e) {
            e.printStackTrace();
            status.setText("Could not load maze from DB.");
        }
    }


    private void movePlayer(int dcol, int drow) {
        int nc = playerCol + dcol;
        int nr = playerRow + drow;

        // Clamp player inside maze
        if (nc < 0 || nr < 0 || nc >= cols || nr >= rows) return;

        int tile = maze[nr][nc];
        boolean canPassWalls = System.currentTimeMillis() < invisibleUntil;

        if (tile == 1 && !canPassWalls) return;

        if (tile == 3) {
            if (keyCollected) {
                gameWon = true;
                status.setText("You escaped! Congratulations.");
                playerCol = nc;
                playerRow = nr;
            } else {
                status.setText("Exit locked — find the key!");
                return;
            }
        } else {
            playerCol = nc;
            playerRow = nr;
        }

        if (tile == 2) {
            keyCollected = true;
            maze[nr][nc] = 0;
            status.setText("Key collected! Find the exit.");
        } else if (!gameWon) {
            status.setText(keyCollected ? "Find the exit." : "Find the key.");
        }

        draw();
    }

    private void draw() {
        if (maze == null) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 1. Determine square cell size to fit maze in canvas
        double cellSize = Math.min(canvas.getWidth() / cols, canvas.getHeight() / rows);

        // 2. Center the maze (This is what prevents it from being squished to one side)
        double mazeWidth = cols * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (canvas.getWidth() - mazeWidth) / 2;
        double offsetY = (canvas.getHeight() - mazeHeight) / 2;

        // 3. Draw the canvas background
        gc.setFill(Color.web("#F2D16D")); // Background color
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());


        // 4. Draw maze tiles (Walls in squares)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = offsetX + c * cellSize;
                double y = offsetY + r * cellSize;
                int t = maze[r][c]; // Tile type: 0=floor, 1=wall, 2=key, 3=exit

                // Draw Floor/Tile background
                gc.setFill(Color.web("#F2D16D")); // Floor color
                gc.fillRect(x, y, cellSize, cellSize);

                // Draw Wall
                if (t == 1) {
                    gc.setFill(Color.web("#C19A6B")); // Wall color
                    gc.fillRect(x, y, cellSize, cellSize); // Wall in a solid square
                }

                // Draw Key
                if (t == 2) {
                    gc.setFill(Color.web("#FFD700")); // Key color
                    gc.fillOval(x + cellSize*0.25, y + cellSize*0.25, cellSize*0.5, cellSize*0.5);
                }

                // Draw Exit
                if (t == 3) {
                    gc.setFill(keyCollected ? Color.web("#DAA520") : Color.web("#8B4513"));
                    gc.fillRect(x + cellSize*0.15, y + cellSize*0.15, cellSize*0.7, cellSize*0.7);
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x + cellSize*0.48, y + cellSize*0.15, cellSize*0.08, cellSize*0.7);
                }

                // Optional: Draw cell border for clarity
                gc.setStroke(Color.web("#B58B45"));
                gc.strokeRect(x, y, cellSize, cellSize);
            }
        }

        // 5. Draw player inside the maze on the calculated cell coordinates
        double px = offsetX + playerCol * cellSize + cellSize / 2.0;
        double py = offsetY + playerRow * cellSize + cellSize / 2.0;

        // Player drawing code (using your existing graphics code)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        // ... (rest of the player drawing) ...
        gc.strokeOval(px - cellSize*0.15, py - cellSize*0.3, cellSize*0.3, cellSize*0.3); // head
        gc.strokeLine(px, py - cellSize*0.06, px, py + cellSize*0.25); // body
        gc.strokeLine(px, py, px - cellSize*0.18, py - cellSize*0.06); // left arm
        gc.strokeLine(px, py, px + cellSize*0.18, py - cellSize*0.06); // right arm
        gc.strokeLine(px, py + cellSize*0.25, px - cellSize*0.18, py + cellSize*0.44); // left leg
        gc.strokeLine(px, py + cellSize*0.25, px + cellSize*0.18, py + cellSize*0.44); // right leg


    }




    public void activateInvisibility(long durationMillis) {
        invisibleUntil = System.currentTimeMillis() + durationMillis;
    }
}