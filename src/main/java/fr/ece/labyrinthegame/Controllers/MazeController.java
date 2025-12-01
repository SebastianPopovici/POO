package fr.ece.labyrinthegame.Controllers;

import fr.ece.labyrinthegame.dao.MazeDAO;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.application.Platform;

public class MazeController {

    @FXML private Canvas canvas;
    @FXML private Label status;
    @FXML private Button playBtn;

    private Scene scene;
    private int[][] maze;
    private int rows, cols;

    private int playerCol, playerRow;
    private boolean keyCollected, gameWon;

    // Temps jusqu'à l'invisibilité (en millisecondes)
    private long invisibleUntil = 0;

    private Utilisateur joueur;

    @FXML
    public void initialize() {
        // Associer l'action du bouton de jeu à la méthode startGame
        if (playBtn != null) {
            playBtn.setOnAction(e -> startGame());
        }

        // Redessiner le labyrinthe lorsque la taille du Canvas change
        if (canvas != null) {
            canvas.widthProperty().addListener((obs, oldV, newV) -> draw());
            canvas.heightProperty().addListener((obs, oldV, newV) -> draw());
        }
    }

    public void setPlayer(Utilisateur joueur) {
        this.joueur = joueur;
        // Démarrez le jeu après que l'interface a été complètement chargée
        Platform.runLater(() -> startGame());
    }

    // Connecte la Scène pour gérer les événements clavier globaux
    public void connectScene(Scene sc) {
        this.scene = sc;

        if (canvas != null) {
            canvas.setFocusTraversable(true);
            canvas.requestFocus();
        }

        scene.setOnMouseClicked(e -> {
            if (canvas != null) canvas.requestFocus();
        });

        scene.setOnKeyPressed(this::handleKeyPress);
    }

    private void handleKeyPress(KeyEvent e) {
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
        if (status != null) {
            status.setText("Find the key.");
        }
    }

    private void loadMazeFromDatabase() {
        try {
            MazeDAO dao = new MazeDAO();
            // L'accès à la base de données doit être géré avec des threads si nécessaire
            maze = dao.getRandomMaze();
            rows = maze.length;
            cols = maze[0].length;
        } catch (Exception e) {
            e.printStackTrace();
            if (status != null) {
                status.setText("Could not load maze from DB.");
            }
        }
    }

    private void movePlayer(int dcol, int drow) {
        int nc = playerCol + dcol;
        int nr = playerRow + drow;

        // Limiter le joueur à l'intérieur du labyrinthe
        if (nc < 0 || nr < 0 || nc >= cols || nr >= rows) return;

        int tile = maze[nr][nc];
        // Vérifier l'état d'invisibilité
        boolean canPassWalls = System.currentTimeMillis() < invisibleUntil;

        // Si c'est un mur (1) ET que le joueur n'est pas invisible, on ne bouge pas
        if (tile == 1 && !canPassWalls) return;

        // Gérer la tuile de sortie (3)
        if (tile == 3) {
            if (keyCollected) {
                gameWon = true;
                if (status != null) status.setText("You escaped! Congratulations.");
                playerCol = nc;
                playerRow = nr;
            } else {
                if (status != null) status.setText("Exit locked — find the key!");
                return; // Ne pas bouger
            }
        } else {
            // Déplacer le joueur
            playerCol = nc;
            playerRow = nr;
        }

        // Gérer la tuile de clé (2)
        if (tile == 2) {
            keyCollected = true;
            maze[nr][nc] = 0; // Enlever la clé
            if (status != null) status.setText("Key collected! Find the exit.");
        } else if (!gameWon) {
            if (status != null) {
                status.setText(keyCollected ? "Find the exit." : "Find the key.");
            }
        }

        draw();
    }

    /**
     * Méthode principale de dessin, appelée à chaque mise à jour.
     */
    private void draw() {
        if (maze == null || canvas == null) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 1. Déterminer la taille de cellule pour adapter le labyrinthe au Canvas
        double cellSize = Math.min(canvas.getWidth() / cols, canvas.getHeight() / rows);

        // 2. Centrer le labyrinthe
        double mazeWidth = cols * cellSize;
        double mazeHeight = rows * cellSize;
        double offsetX = (canvas.getWidth() - mazeWidth) / 2;
        double offsetY = (canvas.getHeight() - mazeHeight) / 2;

        // 3. Dessiner le fond du Canvas
        gc.setFill(Color.web("#F2D16D")); // Fond
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());


        // 4. Dessiner les tuiles (Murs, Clé, Sortie)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = offsetX + c * cellSize;
                double y = offsetY + r * cellSize;
                int t = maze[r][c]; // Type de tuile

                // Dessin du sol
                gc.setFill(Color.web("#F2D16D"));
                gc.fillRect(x, y, cellSize, cellSize);

                // Dessin du Mur
                if (t == 1) {
                    gc.setFill(Color.web("#C19A6B"));
                    gc.fillRect(x, y, cellSize, cellSize);
                }

                // Dessin de la Clé
                if (t == 2) {
                    gc.setFill(Color.web("#FFD700"));
                    gc.fillOval(x + cellSize*0.25, y + cellSize*0.25, cellSize*0.5, cellSize*0.5);
                }

                // Dessin de la Sortie
                if (t == 3) {
                    gc.setFill(keyCollected ? Color.web("#DAA520") : Color.web("#8B4513"));
                    gc.fillRect(x + cellSize*0.15, y + cellSize*0.15, cellSize*0.7, cellSize*0.7);
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x + cellSize*0.48, y + cellSize*0.15, cellSize*0.08, cellSize*0.7);
                }

                // Bordure de cellule
                gc.setStroke(Color.web("#B58B45"));
                gc.strokeRect(x, y, cellSize, cellSize);
            }
        }

        // 5. Dessiner le joueur
        drawPlayer(gc, cellSize, offsetX, offsetY);
    }

    /**
     * Dessine l'avatar du joueur en tenant compte de l'état d'invisibilité.
     */
    private void drawPlayer(GraphicsContext gc, double cellSize, double offsetX, double offsetY) {

        double px = offsetX + playerCol * cellSize + cellSize / 2.0;
        double py = offsetY + playerRow * cellSize + cellSize / 2.0;

        // --- VÉRIFICATION DE L'INVISIBILITÉ ---
        boolean isInvisible = System.currentTimeMillis() < invisibleUntil;

        if (isInvisible) {
            // Lignes du joueur transparentes
            gc.setStroke(Color.web("#000000", 0.35));
            // Couleur de remplissage semi-transparente
            gc.setFill(Color.web("#5D3FD3", 0.5));

        } else {
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.web("#5D3FD3")); // Couleur normale (Violet)
        }

        gc.setLineWidth(isInvisible ? 1 : 2);

        // Tête (Oval)
        gc.strokeOval(px - cellSize*0.15, py - cellSize*0.3, cellSize*0.3, cellSize*0.3);

        // Corps
        gc.strokeLine(px, py - cellSize*0.06, px, py + cellSize*0.25);

        // Bras
        gc.strokeLine(px, py, px - cellSize*0.18, py - cellSize*0.06);
        gc.strokeLine(px, py, px + cellSize*0.18, py - cellSize*0.06);

        // Jambes
        gc.strokeLine(px, py + cellSize*0.25, px - cellSize*0.18, py + cellSize*0.44);
        gc.strokeLine(px, py + cellSize*0.25, px + cellSize*0.18, py + cellSize*0.44);

        // Effet visuel d'invisibilité (lueur subtile)
        if (isInvisible) {
            gc.setFill(Color.CYAN.deriveColor(1, 1, 1, 0.15));
            gc.fillOval(px - cellSize*0.3, py - cellSize*0.3, cellSize*0.6, cellSize*0.6);
        }
    }

    /**
     * Active le mode invisibilité pour une durée donnée.
     */
    public void activateInvisibility(long durationMillis) {
        this.invisibleUntil = System.currentTimeMillis() + durationMillis;
        draw(); // Redessiner immédiatement pour montrer l'effet
    }
}