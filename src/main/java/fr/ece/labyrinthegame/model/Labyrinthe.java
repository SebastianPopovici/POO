package fr.ece.labyrinthegame.model;

public class Labyrinthe {
    private int id;
    private String grid;

    public Labyrinthe(int id, String grid) {
        this.id = id;
        this.grid = grid;
    }

    public int getId() {
        return id;
    }

    public String getGrid() {
        return grid;
    }


    }
