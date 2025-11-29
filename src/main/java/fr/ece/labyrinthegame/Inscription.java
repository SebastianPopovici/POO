package fr.ece.labyrinthegame;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import fr.ece.labyrinthegame.dao.UtilisateurDAO;

public class Inscription {

    public final Stage stage;

    public Inscription(Stage stage) {
        this.stage = stage;
    }

    public void afficher() {
        // Conteneur principal
        VBox root = new VBox(30);
        root.setPadding(new Insets(60));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0b1a29;");

        Label titre = new Label("Créer un Compte Joueur");
        titre.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #00d4ff");
        Label sousTitre = new Label("Create your own count  ");
        sousTitre.setStyle("-fx-font-size: 20px; -fx-text-fill: #85929e;");


        // Champs de saisie
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choisissez un nom d'utilisateur");
        usernameField.setStyle("-fx-font-size: 15px; -fx-padding: 10; -fx-background-color: #2b3b4d; -fx-text-fill: #ffffff; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0; -fx-background-radius: 0;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choisissez un mot de passe");
        passwordField.setStyle("-fx-font-size: 15px; -fx-padding: 10; -fx-background-color: #2b3b4d; -fx-text-fill: #ffffff; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0; -fx-background-radius: 0;");

        // Message d'erreur/succès
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);

        Button btnValider = new Button("S'ENREGISTRER");
        btnValider.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        btnValider.setPrefWidth(250);
        btnValider.setAlignment(Pos.CENTER);
        btnValider.setStyle("-fx-background-color: #00d4ff; " +
                "-fx-text-fill: #0b1a29; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 15px 30px; " +
                "-fx-background-radius: 8;");


                                            // Bouton pour revenir à la page de connexion
        Button btnRetour = new Button("Retour Connexion");
        //style  btn
        btnRetour.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        btnRetour.setPrefWidth(250);
        btnRetour.setAlignment(Pos.CENTER);
        btnRetour.setStyle("-fx-background-color: #00d4ff; " +
                "-fx-text-fill: #0b1a29; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 15px 30px; " +
                "-fx-background-radius: 8;");

        btnRetour.setOnAction(e -> {

            Connexion connexionApp = new Connexion();
            connexionApp.start(stage);
        });

        // ACTION : Enregistrement dans la Base de Données
        btnValider.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("⚠️ Veuillez remplir tous les champs.");
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            UtilisateurDAO dao = new UtilisateurDAO();

            // Appelle la méthode DAO pour créer le joueur avec le rôle 'JOUEUR'
            if (dao.creerJoueur(username, password)) {
                messageLabel.setText("✅ Compte créé avec succès! Vous pouvez vous connecter.");
                messageLabel.setStyle("-fx-text-fill: #2ecc71;");
                usernameField.clear();
                passwordField.clear();
            } else {
                // Échec si le nom d'utilisateur est déjà pris ou s'il y a une erreur BD
                messageLabel.setText("❌ Échec de la création. Le nom d'utilisateur est peut-être déjà utilisé.");
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        root.getChildren().addAll(titre, usernameField, passwordField, btnValider, messageLabel, btnRetour);

        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Inscription Joueur");
        stage.setScene(scene);
        stage.show();
    }
}
