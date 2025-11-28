module fr.ece.labyrinthegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens fr.ece.labyrinthegame to javafx.fxml;
    exports fr.ece.labyrinthegame;
}