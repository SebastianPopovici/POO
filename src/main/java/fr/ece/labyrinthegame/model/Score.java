package fr.ece.labyrinthegame.model;

public class Score {

        private String playerName;
        private int value;

        public Score(String playerName, int value) {
            this.playerName = playerName;
            this.value = value;
        }

        public String getPlayerName() { return playerName; }
        public int getValue() { return value; }

        @Override
        public String toString() {
            return playerName + ": " + value;
        }
    }

