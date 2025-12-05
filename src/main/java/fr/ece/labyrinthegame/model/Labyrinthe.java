package fr.ece.labyrinthegame.model;

public class Labyrinthe {
    private int id;
    private String jsonGrid;

    public Labyrinthe(int id, String jsonGrid) {
        this.id = id;
        this.jsonGrid = jsonGrid;
    }

    public int getId() {
        return id;
    }

    public String getJsonGrid() {
        return jsonGrid;
    }


    public void setJsonGrid(String jsonGrid) {
        this.jsonGrid = jsonGrid;
    }


}