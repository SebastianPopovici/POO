package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.dao.MazeDAO;
import fr.ece.labyrinthegame.dao.UtilisateurDAO;
import fr.ece.labyrinthegame.model.Labyrinthe;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminInterface {
    private final Stage primaryStage;
    private final Utilisateur admin;
    private final UtilisateurDAO utilisateurDAO;
    private final MazeDAO mazeDAO;

    public AdminInterface(Stage primaryStage, Utilisateur admin) {
        this.primaryStage = primaryStage;
        this.admin = admin;
        this.utilisateurDAO = new UtilisateurDAO();
        this.mazeDAO = new MazeDAO();
    }

    public void afficher() {
        afficherMenuPrincipal();
    }

    // Affiche le menu principal
    private void afficherMenuPrincipal() {
        VBox root = new VBox(30);
        root.setPadding(new Insets(60));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #0b1a29;");

        Label titre = new Label("PANNEAU ADMINISTRATEUR");
        titre.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #FF5733;");

        Label sousTitre = new Label("Bienvenue, " + admin.getUsername() + " !");
        sousTitre.setStyle("-fx-font-size: 20px; -fx-text-fill: #8BC34A;");

        Button btnDeconnexion = new Button("DÉCONNEXION");
        btnDeconnexion.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12px 30px; -fx-background-radius: 8;");
        btnDeconnexion.setOnAction(e -> {
            Connexion connexion = new Connexion();
            connexion.start(primaryStage);
        });

        VBox formulaire = new VBox(20);
        formulaire.setAlignment(Pos.CENTER);
        formulaire.setPadding(new Insets(40));
        formulaire.setMaxWidth(500);
        formulaire.setStyle("-fx-background-color: #1a2c3e; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);");

        Button btnGererUtilisateurs = new Button("GÉRER LES UTILISATEURS");
        btnGererUtilisateurs.setPrefWidth(300);
        btnGererUtilisateurs.setAlignment(Pos.CENTER);
        btnGererUtilisateurs.setStyle("-fx-background-color: #00d4ff; -fx-text-fill: #0b1a29; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15px 30px; -fx-background-radius: 8;");
        btnGererUtilisateurs.setOnAction(e -> afficherGestionUtilisateurs());

        Button btnGererLabyrinthes = new Button("GÉRER LES LABYRINTHES");
        btnGererLabyrinthes.setPrefWidth(300);
        btnGererLabyrinthes.setAlignment(Pos.CENTER);
        btnGererLabyrinthes.setStyle("-fx-background-color: #00d4ff; -fx-text-fill: #0b1a29; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15px 30px; -fx-background-radius: 8;");
        btnGererLabyrinthes.setOnAction(e -> afficherGestionLabyrinthes());

        formulaire.getChildren().addAll(btnGererUtilisateurs, btnGererLabyrinthes);

        root.getChildren().addAll(titre, sousTitre, btnDeconnexion, formulaire);

        Label messageLabel = new Label("Sélectionnez une action pour commencer.");
        messageLabel.setStyle("-fx-text-fill: #8BC34A; -fx-font-size: 16px; -fx-font-weight: bold;");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);
        root.getChildren().add(messageLabel);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Panneau Admin - " + admin.getUsername());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Affiche l'interface de gestion des utilisateurs
    private void afficherGestionUtilisateurs() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #0b1a29;");

        Label titre = new Label("GESTION DES UTILISATEURS");
        titre.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #FF5733;");

        Button btnRetour = new Button("← RETOUR AU MENU");
        btnRetour.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8px 20px; -fx-background-radius: 6;");
        btnRetour.setOnAction(e -> afficherMenuPrincipal());

        // Formulaire de Création d'Utilisateur
        VBox creationForm = new VBox(10);
        creationForm.setAlignment(Pos.CENTER);
        creationForm.setPadding(new Insets(20));
        creationForm.setStyle("-fx-background-color: #1a2c3e; -fx-background-radius: 10;");

        Label formTitle = new Label("Créer un Nouvel Utilisateur");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        usernameField.setStyle("-fx-background-color: #ffffff; -fx-padding: 8px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-padding: 8px;");

        ComboBox<String> roleComboBox = new ComboBox<>(FXCollections.observableArrayList("JOUEUR", "ADMIN"));
        roleComboBox.setValue("JOUEUR");
        roleComboBox.setStyle("-fx-background-color: #ffffff;");

        Button btnCreer = new Button("CRÉER L'UTILISATEUR");
        btnCreer.setStyle("-fx-background-color: #00d4ff; -fx-text-fill: #0b1a29; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 6;");
        btnCreer.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();
            if (username.isEmpty() || password.isEmpty() || role == null) {
                afficherMessage(root, "⚠️ Veuillez remplir tous les champs.", false);
                return;
            }
            if (utilisateurDAO.creerJoueur(username, password)) {
                usernameField.clear();
                passwordField.clear();
                roleComboBox.setValue("JOUEUR");
                afficherMessage(root, "✅ Utilisateur **" + username + "** créé avec succès en tant que **" + role + "** !", true);
            } else {
                afficherMessage(root, "❌ Échec de la création. Le nom d'utilisateur existe peut-être déjà.", false);
            }
        });

        creationForm.getChildren().addAll(formTitle, usernameField, passwordField, roleComboBox, btnCreer);

        // Assemblage de la Vue
        HBox topControls = new HBox(20);
        topControls.setAlignment(Pos.CENTER);
        topControls.getChildren().addAll(btnRetour);

        root.getChildren().addAll(titre, topControls, creationForm);

        Scene scene = new Scene(root, 800, 750);
        primaryStage.setScene(scene);
    }

    // Affiche l'interface de gestion des labyrinthes
    private void afficherGestionLabyrinthes() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #0b1a29;");

        Label titre = new Label("GESTION DES LABYRINTHES");
        titre.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #FF5733;");

        Button btnRetour = new Button("← RETOUR AU MENU");
        btnRetour.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8px 20px; -fx-background-radius: 6;");
        btnRetour.setOnAction(e -> afficherMenuPrincipal());

        // Formulaire de Création de Labyrinthe
        VBox creationForm = new VBox(10);
        creationForm.setAlignment(Pos.CENTER);
        creationForm.setPadding(new Insets(20));
        creationForm.setStyle("-fx-background-color: #1a2c3e; -fx-background-radius: 10;");

        Label formTitle = new Label("Ajouter un Nouveau Labyrinthe");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom du Labyrinthe");
        nomField.setStyle("-fx-background-color: #ffffff; -fx-padding: 8px;");

        TextField tailleField = new TextField();
        tailleField.setPromptText("Taille N (ex: 10 pour 10x10)");
        tailleField.setStyle("-fx-background-color: #ffffff; -fx-padding: 8px;");

        TextArea jsonArea = new TextArea();
        jsonArea.setPromptText("Contenu JSON de la grille du labyrinthe");
        jsonArea.setPrefRowCount(4);
        jsonArea.setStyle("-fx-background-color: #ffffff; -fx-padding: 8px;");

        Button btnCreer = new Button("CRÉER LE LABYRINTHE");
        btnCreer.setStyle("-fx-background-color: #00d4ff; -fx-text-fill: #0b1a29; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 6;");
        btnCreer.setOnAction(e -> {
            String nom = nomField.getText();
            String tailleStr = tailleField.getText();
            String jsonGrid = jsonArea.getText();
            if (nom.isEmpty() || tailleStr.isEmpty() || jsonGrid.isEmpty()) {
                afficherMessage(root, "⚠️ Veuillez remplir tous les champs du labyrinthe.", false);
                return;
            }
            int taille;
            try {
                taille = Integer.parseInt(tailleStr);
            } catch (NumberFormatException ex) {
                afficherMessage(root, "❌ La taille doit être un nombre valide.", false);
                return;
            }
            if (mazeDAO.addMaze(taille, jsonGrid)) {
                nomField.clear();
                tailleField.clear();
                jsonArea.clear();
                afficherMessage(root, "✅ Labyrinthe **" + nom + "** ajouté avec succès !", true);
            } else {
                afficherMessage(root, "❌ Échec de la création du labyrinthe.", false);
            }
        });

        creationForm.getChildren().addAll(formTitle, nomField, tailleField, jsonArea, btnCreer);

        // Assemblage de la Vue
        HBox topControls = new HBox(20);
        topControls.setAlignment(Pos.CENTER);
        topControls.getChildren().addAll(btnRetour);

        root.getChildren().addAll(titre, topControls, creationForm);

        Scene scene = new Scene(root, 900, 750);
        primaryStage.setScene(scene);
    }

    // Affiche un message en bas de l'interface
    private void afficherMessage(VBox root, String message, boolean succes) {
        clearMessage(root);
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: " + (succes ? "#2ecc71" : "#e74c3c") + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        root.getChildren().add(messageLabel);
    }

    // Supprime un message affiché précédemment
    private void clearMessage(VBox root) {
        for (int i = root.getChildren().size() - 1; i >= 0; i--) {
            if (root.getChildren().get(i) instanceof Label) {
                Label label = (Label) root.getChildren().get(i);
                if (label.getStyle().contains("-fx-text-fill:")) {
                    root.getChildren().remove(i);
                    break;
                }
            }
        }
    }
}
