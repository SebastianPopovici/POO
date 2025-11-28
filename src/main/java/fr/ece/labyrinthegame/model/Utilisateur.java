package fr.ece.labyrinthegame.model;

public class Utilisateur {

    private int id;
    private String username;
    private String password;
    private String role; //admin ou joueur

    public Utilisateur() {}

    public Utilisateur(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // secured password
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // verification (roles)
    public boolean isAdmin() {
        return role != null && role.equals("ADMIN");
    }

    public boolean isJoueur() {
        return role != null && role.equals("JOUEUR");
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}