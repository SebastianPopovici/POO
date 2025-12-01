package fr.ece.labyrinthegame.model;

public class Utilisateur
{

    private int id;
    private String username;
    private String password;
    private String role; // ADMIN or JOUEUR

    public Utilisateur() {}

    public Utilisateur(int id, String username, String password, String role)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    // ✅ Return plain String, not ObservableValue
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Verification
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isJoueur() {
        return "JOUEUR".equalsIgnoreCase(role);
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
