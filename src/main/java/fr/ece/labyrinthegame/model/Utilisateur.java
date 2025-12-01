package fr.ece.labyrinthegame.model;

public class Utilisateur {
    private int id;
    private String username;
    private String password;
    private String role; // "JOUEUR" ou "ADMIN"

    // Attributs spécifiques aux JOUEURS (ignores pour les ADMIN)
    private int x, y; // Position dans le labyrinthe
    private int vies;
    private int score;

    public Utilisateur(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;

        // Initialise les attributs du joueur UNIQUEMENT si c'est un JOUEUR
        if (role.equals("JOUEUR")) {
            this.x = 0;
            this.y = 0;
            this.vies = 3;
            this.score = 0;
        }
    }

    // Getters et Setters pour tous les attributs
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public void setPassword(String password) { this.password = password; }

    // Getters et Setters spécifiques aux JOUEURS
    public int getX() {
        if (!role.equals("JOUEUR")) throw new IllegalStateException("Seuls les joueurs ont une position X.");
        return x;
    }

    public int getY() {
        if (!role.equals("JOUEUR")) throw new IllegalStateException("Seuls les joueurs ont une position Y.");
        return y;
    }

    public void setPosition(int x, int y) {
        if (!role.equals("JOUEUR")) throw new IllegalStateException("Seuls les joueurs peuvent se déplacer.");
        this.x = x;
        this.y = y;
    }

    public int getVies() {
        if (!role.equals("JOUEUR")) throw new IllegalStateException("Seuls les joueurs ont des vies.");
        return vies;
    }

    public void perdreVie() {
        if (!role.equals("JOUEUR")) throw new IllegalStateException("Seuls les joueurs peuvent perdre des vies.");
        this.vies--;
    }

    public int getScore() {
        if (!role.equals("JOUEUR")) throw new IllegalStateException("Seuls les joueurs ont un score.");
        return score;
    }

    public void ajouterScore(int points) {
        if (!role.equals("JOUEUR")) throw new IllegalStateException("Seuls les joueurs peuvent gagner des points.");
        this.score += points;
    }

    public boolean isAdmin() {
        boolean admin = true;
        if (role.equals("JOUEUR")) {
            admin = false;
        }
        return admin;
    }
    // Dans Utilisateur.java
    public void setScore(int score) {
        if (!role.equals("JOUEUR")) throw new IllegalStateException("Seuls les joueurs ont un score.");
        this.score = score;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
