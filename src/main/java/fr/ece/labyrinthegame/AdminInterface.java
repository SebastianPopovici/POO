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

    // ------------------ Gestion des Utilisateurs ------------------
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

        ListView<String> userListView = new ListView<>();
        userListView.setPrefHeight(200);

        Runnable loadUsers = () -> {
            userListView.getItems().clear();
            for (Utilisateur u : utilisateurDAO.getAllJoueurs()) {
                userListView.getItems().add(u.getId() + " - " + u.getUsername() + " (" + u.getRole() + ")");
            }
        };
        loadUsers.run();

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setOnAction(e -> {
            int index = userListView.getSelectionModel().getSelectedIndex();
            if (index < 0) return;
            Utilisateur selectedUser = utilisateurDAO.getAllJoueurs().get(index);
            if (utilisateurDAO.supprimerJoueur(selectedUser.getId())) loadUsers.run();
            else afficherMessage(root, "❌ Échec de la suppression.", false);
        });

        Button btnChangerRole = new Button("Changer Rôle");
        btnChangerRole.setOnAction(e -> {
            int index = userListView.getSelectionModel().getSelectedIndex();
            if (index < 0) return;
            Utilisateur selectedUser = utilisateurDAO.getAllJoueurs().get(index);
            String newRole = selectedUser.getRole().equals("JOUEUR") ? "MODERATOR" : "JOUEUR";
            if (utilisateurDAO.changerRole(selectedUser.getId(), newRole)) loadUsers.run();
            else afficherMessage(root, "❌ Échec du changement de rôle.", false);
        });

        actionButtons.getChildren().addAll(btnSupprimer, btnChangerRole);

        VBox creationForm = new VBox(10);
        creationForm.setAlignment(Pos.CENTER);
        creationForm.setPadding(new Insets(20));
        creationForm.setStyle("-fx-background-color: #1a2c3e; -fx-background-radius: 10;");

        Label formTitle = new Label("Créer un Nouvel Utilisateur");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        ComboBox<String> roleComboBox = new ComboBox<>(FXCollections.observableArrayList("JOUEUR", "ADMIN"));
        roleComboBox.setValue("JOUEUR");

        Button btnCreer = new Button("CRÉER L'UTILISATEUR");
        btnCreer.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();
            if (username.isEmpty() || password.isEmpty() || role == null) {
                afficherMessage(root, "⚠️ Veuillez remplir tous les champs.", false);
                return;
            }
            boolean success = role.equals("JOUEUR") ? utilisateurDAO.creerJoueur(username, password)
                    : utilisateurDAO.changerRole(utilisateurDAO.getAllJoueurs().size() + 1, "ADMIN");
            if (success) {
                usernameField.clear();
                passwordField.clear();
                roleComboBox.setValue("JOUEUR");
                loadUsers.run();
                afficherMessage(root, "✅ Utilisateur " + username + " créé avec succès !", true);
            } else {
                afficherMessage(root, "❌ Échec de la création. Nom peut-être existant.", false);
            }
        });

        creationForm.getChildren().addAll(formTitle, usernameField, passwordField, roleComboBox, btnCreer);

        root.getChildren().addAll(titre, btnRetour, userListView, actionButtons, creationForm);

        Scene scene = new Scene(root, 800, 750);
        primaryStage.setScene(scene);
    }

    // ------------------ Gestion des Labyrinthes ------------------
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

        ListView<Labyrinthe> mazeListView = new ListView<>();
        mazeListView.setPrefHeight(250);

        // Custom cell to display ID, size, and JSON grid
        mazeListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Labyrinthe item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("ID: " + item.getId()  + " | Grid: " + item.getJsonGrid());
                }
            }
        });

        // Load Mazes
        Runnable loadMazes = () -> {
            mazeListView.getItems().clear();
            mazeListView.getItems().addAll(mazeDAO.getAllMazes());
        };
        loadMazes.run();

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setOnAction(e -> {
            Labyrinthe selectedMaze = mazeListView.getSelectionModel().getSelectedItem();
            if (selectedMaze == null) return;
            if (mazeDAO.supprimerMaze(selectedMaze.getId())) {
                loadMazes.run();
            } else {
                afficherMessage(root, "❌ Échec de la suppression.", false);
            }
        });

        actionButtons.getChildren().addAll(btnSupprimer);

        VBox creationForm = new VBox(10);
        creationForm.setAlignment(Pos.CENTER);
        creationForm.setPadding(new Insets(20));
        creationForm.setStyle("-fx-background-color: #1a2c3e; -fx-background-radius: 10;");

        Label formTitle = new Label("Ajouter un Nouveau Labyrinthe");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        TextField tailleField = new TextField();
        tailleField.setPromptText("Taille N (ex: 10 pour 10x10)");

        TextArea jsonArea = new TextArea();
        jsonArea.setPromptText("Contenu JSON de la grille du labyrinthe");
        jsonArea.setPrefRowCount(4);

        Button btnCreer = new Button("CRÉER LE LABYRINTHE");
        btnCreer.setOnAction(e -> {
            String tailleStr = tailleField.getText();
            String jsonGrid = jsonArea.getText();
            if (tailleStr.isEmpty() || jsonGrid.isEmpty()) {
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
                tailleField.clear();
                jsonArea.clear();
                loadMazes.run();
                afficherMessage(root, "✅ Labyrinthe ajouté avec succès !", true);
            } else {
                afficherMessage(root, "❌ Échec de la création du labyrinthe.", false);
            }
        });

        creationForm.getChildren().addAll(formTitle, tailleField, jsonArea, btnCreer);

        root.getChildren().addAll(titre, btnRetour, mazeListView, actionButtons, creationForm);

        Scene scene = new Scene(root, 900, 750);
        primaryStage.setScene(scene);
    }

    // ------------------ Messages ------------------
    private void afficherMessage(VBox root, String message, boolean succes) {
        clearMessage(root);
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: " + (succes ? "#2ecc71" : "#e74c3c") + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        root.getChildren().add(messageLabel);
    }

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
