package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.dao.MazeDAO;
import fr.ece.labyrinthegame.dao.UtilisateurDAO;
import fr.ece.labyrinthegame.model.Labyrinthe;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class AdminInterface {

    private Stage stage;
    private Utilisateur admin;

    private UtilisateurDAO userDao = new UtilisateurDAO();
    private MazeDAO mazeDao = new MazeDAO();

    private TableView<Utilisateur> userTable;
    private ObservableList<Utilisateur> userList;

    private TableView<Labyrinthe> mazeTable;
    private ObservableList<Labyrinthe> mazeList;

    public AdminInterface(Stage stage, Utilisateur admin) {
        this.stage = stage;
        this.admin = admin;
    }

    public void afficher() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #0b1a29;");


        Label title = new Label("ADMIN PANEL - " + admin.getUsername());
        title.setStyle("-fx-background-color: #0b1a;");

        // ------------------- USER TABLE -------------------
        userTable = new TableView<>();
        TableColumn<Utilisateur, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Utilisateur, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));

        TableColumn<Utilisateur, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        userTable.getColumns().addAll(idCol, usernameCol, roleCol);
        userTable.setPrefHeight(200);
        refreshUserList();

        // Add User Section
        HBox addUserBox = new HBox(10);
        addUserBox.setAlignment(Pos.CENTER);

        TextField newUserField = new TextField();
        newUserField.setPromptText("New username");

        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Password");

        Button addUserBtn = new Button("Add Player");
        addUserBtn.setOnAction(e -> {
            String username = newUserField.getText().trim();
            String password = newPassField.getText().trim();
            if (!username.isEmpty() && !password.isEmpty()) {
                boolean success = userDao.creerJoueur(username, password);
                if (success) {
                    refreshUserList();
                    newUserField.clear();
                    newPassField.clear();
                } else {
                    showAlert("Error", "Could not create player. Maybe username exists?");
                }
            }
        });

        addUserBox.getChildren().addAll(newUserField, newPassField, addUserBtn);

        // Delete User Button
        Button deleteUserBtn = new Button("Delete Selected Player");
        deleteUserBtn.setOnAction(e -> {
            Utilisateur selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean success = userDao.supprimerJoueur(selected.getId());
                if (success) refreshUserList();
                else showAlert("Error", "Could not delete player.");
            }
        });

        // ------------------- MAZE TABLE -------------------
        mazeTable = new TableView<>();
        TableColumn<Labyrinthe, Integer> mazeIdCol = new TableColumn<>("ID");
        mazeIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Labyrinthe, String> mazeGridCol = new TableColumn<>("Grid");
        mazeGridCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrid()));

        mazeTable.getColumns().addAll(mazeIdCol, mazeGridCol);
        mazeTable.setPrefHeight(200);
        refreshMazeList();

        // Add Maze Section
        HBox addMazeBox = new HBox(10);
        addMazeBox.setAlignment(Pos.CENTER);

        TextField mazeIdField = new TextField();
        mazeIdField.setPromptText("Maze ID (integer)");

        TextField mazeGridField = new TextField();
        mazeGridField.setPromptText("Grid JSON (e.g., [[0,1],[2,0]])");

        Button addMazeBtn = new Button("Add Maze");
        addMazeBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(mazeIdField.getText().trim());
                String grid = mazeGridField.getText().trim();
                if (!grid.isEmpty()) {
                    boolean success = mazeDao.addMaze(id, grid);
                    if (success) {
                        showAlert("Success", "Maze added!");
                        mazeIdField.clear();
                        mazeGridField.clear();
                        refreshMazeList();
                    } else showAlert("Error", "Failed to add maze.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Maze ID must be an integer.");
            }
        });

        addMazeBox.getChildren().addAll(mazeIdField, mazeGridField, addMazeBtn);

        // Delete Maze Button
        Button deleteMazeBtn = new Button("Delete Selected Maze");
        deleteMazeBtn.setOnAction(e -> {
            Labyrinthe selected = mazeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean success = mazeDao.supprimerMaze(selected.getId());
                if (success) refreshMazeList();
                else showAlert("Error", "Could not delete maze.");
            }
        });

        // ------------------- ADD ALL COMPONENTS -------------------
        root.getChildren().addAll(
                title,
                new Label("Users"),
                userTable,
                addUserBox,
                deleteUserBtn,
                new Label("Mazes"),
                mazeTable,
                addMazeBox,
                deleteMazeBtn
        );

        Scene scene = new Scene(root, 700, 700);
        stage.setScene(scene);
        stage.setTitle("Admin Panel");
        stage.show();
    }

    private void refreshUserList() {
        List<Utilisateur> joueurs = userDao.getAllJoueurs();
        userList = FXCollections.observableArrayList(joueurs);
        userTable.setItems(userList);
    }

    private void refreshMazeList() {
        List<Labyrinthe> mazes = mazeDao.getAllMazes();
        mazeList = FXCollections.observableArrayList(mazes);
        mazeTable.setItems(mazeList);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}