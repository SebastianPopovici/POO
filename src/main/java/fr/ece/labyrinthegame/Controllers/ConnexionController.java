package fr.ece.labyrinthegame.Controllers;

import fr.ece.labyrinthegame.dao.UtilisateurDAO;
import fr.ece.labyrinthegame.model.Utilisateur;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ConnexionController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Enter username and password.");
            return;
        }

        UtilisateurDAO dao = new UtilisateurDAO();
        Utilisateur user = dao.authentifier(username, password);

        if (user == null) {
            messageLabel.setText("Invalid username or password.");
            return;
        }

        Stage stage = (Stage) usernameField.getScene().getWindow();

        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            showAdminInterface(dao);
            return;
        }

        if (user.getRole().equalsIgnoreCase("JOUEUR")) {
            loadMazeInterface(stage, user);
            return;
        }

        messageLabel.setText("Unknown role: " + user.getRole());
    }

    private void loadMazeInterface(Stage stage, Utilisateur joueur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ece/labyrinthegame/MainGame.fxml"));
            VBox root = loader.load();

            MazeController controller = loader.getController();
            Scene scene = new Scene(root);

            controller.setPlayer(joueur);

            stage.setTitle("Maze Game");
            stage.setScene(scene);
            stage.setResizable(false);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading Maze interface.");
        }
    }

    // ----------------- NEW: Admin Interface -----------------
    private void showAdminInterface(UtilisateurDAO dao) {
        Stage adminStage = new Stage();
        adminStage.setTitle("Admin Interface");

        ListView<String> userListView = new ListView<>();
        Button deleteBtn = new Button("Delete User");
        Button roleBtn = new Button("Change Role");
        Button refreshBtn = new Button("Refresh List");

        VBox vbox = new VBox(10, new Label("Users Management"), userListView, deleteBtn, roleBtn, refreshBtn);
        vbox.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");

        // Load users into the list
        Runnable loadUsers = () -> {
            userListView.getItems().clear();
            List<Utilisateur> users = dao.getAllJoueurs(); // only JOUEUR
            for (Utilisateur u : users) {
                userListView.getItems().add(u.getId() + " - " + u.getUsername() + " (" + u.getRole() + ")");
            }
        };
        loadUsers.run();

        // Delete user
        deleteBtn.setOnAction(e -> {
            int index = userListView.getSelectionModel().getSelectedIndex();
            if (index < 0) return;
            List<Utilisateur> users = dao.getAllJoueurs();
            Utilisateur u = users.get(index);
            if (dao.supprimerJoueur(u.getId())) loadUsers.run();
            else showAlert("Failed to delete user.");
        });

        // Change role
        roleBtn.setOnAction(e -> {
            int index = userListView.getSelectionModel().getSelectedIndex();
            if (index < 0) return;
            List<Utilisateur> users = dao.getAllJoueurs();
            Utilisateur u = users.get(index);
            String newRole = u.getRole().equals("JOUEUR") ? "MODERATOR" : "JOUEUR"; // example
            if (dao.changerRole(u.getId(), newRole)) loadUsers.run();
            else showAlert("Failed to change role.");
        });

        // Refresh list
        refreshBtn.setOnAction(e -> loadUsers.run());

        adminStage.setScene(new Scene(vbox, 400, 400));
        adminStage.show();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
