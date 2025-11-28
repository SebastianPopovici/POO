package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.dao.UtilisateurDAO;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Connexion extends Application {

    private Stage primaryStage;
    private Utilisateur utilisateurConnecte;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        afficherPageConnexion();
    }

    public void afficherPageConnexion() {
        VBox root = new VBox(30);
        root.setPadding(new Insets(60));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0b1a29;");

        Label titre = new Label("🎮 LABYRINTHE Game 🎮");
        titre.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #00d4ff;");

        Label sousTitre = new Label("Accès au système");
        sousTitre.setStyle("-fx-font-size: 20px; -fx-text-fill: #85929e;");

        VBox formulaire = new VBox(20);
        formulaire.setAlignment(Pos.CENTER_LEFT);
        formulaire.setPadding(new Insets(40));
        formulaire.setMaxWidth(450);
        formulaire.setStyle("-fx-background-color: #1a2c3e; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);");

        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-font-size: 14px;";

        Label usernameLabel = new Label("Nom d'utilisateur");
        usernameLabel.setStyle(labelStyle);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Votre identifiant");
        usernameField.setStyle("-fx-font-size: 15px; -fx-padding: 10; -fx-background-color: #2b3b4d; -fx-text-fill: #ffffff; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0;");

        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setStyle(labelStyle);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe sécurisé");
        passwordField.setStyle("-fx-font-size: 15px; -fx-padding: 10; -fx-background-color: #2b3b4d; -fx-text-fill: #ffffff; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0;");

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(Double.MAX_VALUE);

        Button btnConnexion = new Button("CONNEXION");
        btnConnexion.setPrefWidth(250);
        btnConnexion.setStyle("-fx-background-color: #00d4ff; -fx-text-fill: #0b1a29; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15px 30px; -fx-background-radius: 8;");

        btnConnexion.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("⚠️ Veuillez renseigner l'identifiant et le mot de passe.");
                messageLabel.setStyle("-fx-text-fill: #ff6347; -fx-font-weight: bold;");
                return;
            }

            UtilisateurDAO dao = new UtilisateurDAO();
            utilisateurConnecte = dao.authentifier(username, password);

            if (utilisateurConnecte != null) {
                messageLabel.setText("✅ Connexion réussie. Redirection...");
                messageLabel.setStyle("-fx-text-fill: #39ff31; -fx-font-weight: bold;");

                // Redirect using Platform.runLater to ensure FX thread
                Platform.runLater(this::allerVersInterface);

            } else {
                messageLabel.setText("❌ Identifiant ou mot de passe incorrect");
                messageLabel.setStyle("-fx-text-fill: #ff6350; -fx-font-weight: bold;");
            }
        });

        passwordField.setOnAction(e -> btnConnexion.fire());

        HBox buttonContainer = new HBox(btnConnexion);
        buttonContainer.setAlignment(Pos.CENTER);

        HBox messageContainer = new HBox(messageLabel);
        messageContainer.setAlignment(Pos.CENTER);

        formulaire.getChildren().addAll(
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                buttonContainer, messageContainer
        );

        root.getChildren().addAll(titre, sousTitre, formulaire);

        Scene scene = new Scene(root, 650, 600);
        primaryStage.setTitle("Connexion Sécurisée - Labyrinthe Network");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void allerVersInterface() {
        if (utilisateurConnecte.isAdmin()) {
            AdminInterface adminInterface = new AdminInterface(primaryStage, utilisateurConnecte);
            adminInterface.afficher();
        } else {
            JoueurInterface joueurInterface = new JoueurInterface(primaryStage, utilisateurConnecte);
            joueurInterface.afficher();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
