package fr.ece.labyrinthegame;

import fr.ece.labyrinthegame.dao.MazeDAO;
import fr.ece.labyrinthegame.dao.UtilisateurDAO;
import fr.ece.labyrinthegame.model.Labyrinthe;
import fr.ece.labyrinthegame.model.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

        TableView<Utilisateur> tableUtilisateurs = new TableView<>();
        tableUtilisateurs.setStyle("-fx-background-color: #1a2c3e; -fx-control-inner-background: #1a2c3e; -fx-text-fill: #FFEB3B;");

        TableColumn<Utilisateur, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Utilisateur, String> colUsername = new TableColumn<>("Nom d'utilisateur");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setPrefWidth(150);

        TableColumn<Utilisateur, String> colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setPrefWidth(100);

        tableUtilisateurs.getColumns().addAll(colId, colUsername, colRole);

        ObservableList<Utilisateur> utilisateurs = FXCollections.observableList(utilisateurDAO.getAllUtilisateurs());
        tableUtilisateurs.setItems(utilisateurs);
        tableUtilisateurs.setStyle(
                "-fx-background-color: #e6f2ff;" + // Fond bleu clair
                        "-fx-control-inner-background: #e6f2ff;" +
                        "-fx-text-fill: #000;" + // Texte en noir
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: monospace;" // Police monospace pour le JSON
        );

        HBox boutonsAction = new HBox(15);
        boutonsAction.setAlignment(Pos.CENTER);
        boutonsAction.setPadding(new Insets(15, 0, 0, 0));

        Button btnSupprimer = new Button("SUPPRIMER");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 6;");
        btnSupprimer.setOnAction(e -> {
            Utilisateur selected = tableUtilisateurs.getSelectionModel().getSelectedItem();
            if (selected != null && utilisateurDAO.supprimerUtilisateur(selected.getId())) {
                utilisateurs.remove(selected);
                afficherMessage(root, "✅ Utilisateur supprimé avec succès !", true);
            } else {
                afficherMessage(root, "❌ Échec de la suppression.", false);
            }
        });

        Button btnPromouvoir = new Button("PROMOUVOIR ADMIN");
        btnPromouvoir.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 6;");
        btnPromouvoir.setOnAction(e -> {
            Utilisateur selected = tableUtilisateurs.getSelectionModel().getSelectedItem();
            if (selected != null && utilisateurDAO.changerRole(selected.getId(), "ADMIN")) {
                selected.setRole("ADMIN");
                tableUtilisateurs.refresh();
                afficherMessage(root, "✅ Utilisateur promu administrateur !", true);
            } else {
                afficherMessage(root, "❌ Échec de la promotion.", false);
            }
        });

        boutonsAction.getChildren().addAll(btnSupprimer, btnPromouvoir);

        root.getChildren().addAll(titre, btnRetour, tableUtilisateurs, boutonsAction);

        Scene scene = new Scene(root, 800, 600);
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

        TableView<Labyrinthe> tableLabyrinthes = new TableView<>();
        tableLabyrinthes.setStyle("-fx-background-color: #1a2c3e; -fx-control-inner-background: #1a2c3e; -fx-text-fill: #FFEB3B;");

        TableColumn<Labyrinthe, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Labyrinthe, String> colGrid = new TableColumn<>("Grille (JSON)");
        colGrid.setCellValueFactory(new PropertyValueFactory<>("grid"));
        colGrid.setPrefWidth(400);

        tableLabyrinthes.getColumns().addAll(colId, colGrid);

        ObservableList<Labyrinthe> labyrinthes = FXCollections.observableList(mazeDAO.getAllMazes());
        tableLabyrinthes.setItems(labyrinthes);
        tableLabyrinthes.setStyle(
                "-fx-background-color: #e6f2ff;" + // Fond bleu clair
                        "-fx-control-inner-background: #e6f2ff;" +
                        "-fx-text-fill: #000000;" + // Texte en noir
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: monospace;" // Police monospace pour le JSON
        );


        HBox boutonsAction = new HBox(15);
        boutonsAction.setAlignment(Pos.CENTER);
        boutonsAction.setPadding(new Insets(15, 0, 0, 0));

        Button btnSupprimer = new Button("SUPPRIMER");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 6;");
        btnSupprimer.setOnAction(e -> {
            Labyrinthe selected = tableLabyrinthes.getSelectionModel().getSelectedItem();
            if (selected != null && mazeDAO.supprimerMaze(selected.getId())) {
                labyrinthes.remove(selected);
                afficherMessage(root, "✅ Labyrinthe supprimé avec succès !", true);
            } else {
                afficherMessage(root, "❌ Échec de la suppression.", false);
            }
        });

        Button btnAfficher = new Button("AFFICHER");
        btnAfficher.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 6;");
        btnAfficher.setOnAction(e -> {
            Labyrinthe selected = tableLabyrinthes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                afficherLabyrinthe(selected.getGrid());
            } else {
                afficherMessage(root, "⚠️ Veuillez sélectionner un labyrinthe.", false);
            }
        });

        boutonsAction.getChildren().addAll(btnSupprimer, btnAfficher);

        root.getChildren().addAll(titre, btnRetour, tableLabyrinthes, boutonsAction);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    // Affiche la grille d'un labyrinthe
    private void afficherLabyrinthe(String gridJson) {
        Stage stage = new Stage();
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0b1a29;");

        Label titre = new Label("GRILLE DU LABYRINTHE");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FF5733;");

        TextArea gridArea = new TextArea(gridJson);
        gridArea.setEditable(false);
        gridArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14px; -fx-background-color: #1a2c3e; -fx-text-fill: #03A9F4;");

        gridArea.setPrefSize(400, 300);

        Button btnFermer = new Button("FERMER");
        btnFermer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 6;");
        btnFermer.setOnAction(e -> stage.close());

        root.getChildren().addAll(titre, gridArea, btnFermer);

        Scene scene = new Scene(root, 500, 400);
        stage.setTitle("Visualisation du Labyrinthe");
        stage.setScene(scene);
        stage.show();
    }

    // Affiche un message en bas de l'interface
    private void afficherMessage(VBox root, String message, boolean succes) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: " + (succes ? "#2ecc71" : "#e74c3c") + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(Double.MAX_VALUE);

        for (int i = root.getChildren().size() - 1; i >= 0; i--) {
            if (root.getChildren().get(i) instanceof Label) {
                Label label = (Label) root.getChildren().get(i);
                if (label.getStyle().contains("-fx-text-fill:")) {
                    root.getChildren().remove(i);
                }
            }
        }

        root.getChildren().add(messageLabel);
    }
}
