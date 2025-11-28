package fr.ece.labyrinthegame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import fr.ece.labyrinthegame.dao.UtilisateurDAO;
import fr.ece.labyrinthegame.model.Utilisateur;



public class Connexion extends Application {

    private Stage primaryStage;
    private Utilisateur utilisateurConnecte = null;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        afficherPageConnexion();
    }

    public void afficherPageConnexion() {
        // Background Dark Blue de Grey
        VBox root = new VBox(30);
        root.setPadding(new Insets(60));
        root.setAlignment(Pos.CENTER);
        //  Dark color
        root.setStyle("-fx-background-color: #0b1a29;"); // Bleu très foncé

        // Title
        Label titre = new Label("🎮 LABYRINTHE Maze Game 🎮");
        titre.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #00d4ff;"); // Cyan lumineux

        Label sousTitre = new Label("Accès au système");
        sousTitre.setStyle("-fx-font-size: 20px; -fx-text-fill: #85929e;"); // Gris doux

        // Formulaire (Carte blanche/grise claire)
        VBox formulaire = new VBox(20);
        formulaire.setAlignment(Pos.CENTER_LEFT); // Alignement gauche pour les champs
        formulaire.setPadding(new Insets(40));
        formulaire.setMaxWidth(450);
        // Style de la carte de connexion (avec ombre douce)
        formulaire.setStyle("-fx-background-color: #1a2c3e; " + // Bleu gris foncé
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);" // Ombre
        );

        // Styles pour Labels
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-font-size: 14px;"; // Texte blanc

        // Champ Username
        Label usernameLabel = new Label("Nom d'utilisateur");
        usernameLabel.setStyle(labelStyle);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Votre identifiant");
        usernameField.setStyle("-fx-font-size: 15px; -fx-padding: 10; -fx-background-color: #2b3b4d; -fx-text-fill: #ffffff; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0; -fx-background-radius: 0;"); // Champ sombre, ligne cyan

        // Champ Password
        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setStyle(labelStyle);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe sécurisé");
        passwordField.setStyle("-fx-font-size: 15px; -fx-padding: 10; -fx-background-color: #2b3b4d; -fx-text-fill: #ffffff; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0; -fx-background-radius: 0;"); // Champ sombre, ligne cyan

        // Message d'erreur/succès
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(Double.MAX_VALUE);

        // Bouton de connexion
        Button btnConnexion = new Button("CONNEXION");
        btnConnexion.setPrefWidth(250);
        btnConnexion.setAlignment(Pos.CENTER);
        // Style du btn
        btnConnexion.setStyle("-fx-background-color: #00d4ff; " +
                "-fx-text-fill: #0b1a29; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 15px 30px; " +
                "-fx-background-radius: 8;"
        );

        
        // Le bloc setOnAction reste inchangé car c'est la logique métier (l'action)
        btnConnexion.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("⚠️ Veuillez renseigner l'identifiant et le mot de passe.");
                messageLabel.setStyle("-fx-text-fill: #ff6347; -fx-font-weight: bold;"); // Rouge tomate pour erreur
                return;
            }

            // Tentative de connexion (DAO interaction)
            UtilisateurDAO dao = new UtilisateurDAO();
            utilisateurConnecte = dao.authentifier(username, password);

            if (utilisateurConnecte != null) {
                // ✅ CONNEXION RÉUSSIE
                messageLabel.setText("✅ Connexion réussie. Redirection...");
                messageLabel.setStyle("-fx-text-fill: #39ff318; -fx-font-weight: bold;"); // Vert fluo pour succès

                // Redirection après 2 secondS
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            allerVersInterface();
                        });
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        ex.printStackTrace();
                    }
                }).start();

            } else {
                // Not connected
                messageLabel.setText("❌ Not found erreur 404 😂😂😂😂.");
                messageLabel.setStyle("-fx-text-fill: #ff6350; -fx-font-weight: bold;"); // Rouge tomate pour erreur
            }
        });

        // Enter btn
        passwordField.setOnAction(e -> btnConnexion.fire());

        // Style centerer a btn
        HBox buttonContainer = new HBox(btnConnexion);
        buttonContainer.setAlignment(Pos.CENTER);

        HBox messageContainer = new HBox(messageLabel);
        messageContainer.setAlignment(Pos.CENTER);

        // ADD tout au formulaire
        formulaire.getChildren().addAll(
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                buttonContainer, messageContainer
        );

        // Ajouter au root
        root.getChildren().addAll(titre, sousTitre, formulaire);

        // Créer la scène
        Scene scene = new Scene(root, 650, 600);
        primaryStage.setTitle("Connexion Sécurisée - Labyrinthe Network");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

   // redirection to Interface User(admin/joueur)
    private void allerVersInterface() {
        if (utilisateurConnecte.isAdmin()) {
            System.out.println("🔑 Ouverture interface ADMIN");
            // interface Admin
            //  classe exists??
            AdminInterface adminInterface = new AdminInterface(primaryStage, utilisateurConnecte);
            adminInterface.afficher();
        } else {
            System.out.println("🎮 Ouverture interface JOUEUR");
            // interface Joueur
            //  classe exists??
            JoueurInterface joueurInterface = new JoueurInterface(primaryStage, utilisateurConnecte);
            joueurInterface.afficher();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
