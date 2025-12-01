module fr.ece.labyrinthegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;

    // Allow FXMLLoader to access FXML controllers
    opens fr.ece.labyrinthegame.Controllers to javafx.fxml;
    opens fr.ece.labyrinthegame to javafx.fxml; // if you have FXML in root package

    // Export packages if other modules need them
    exports fr.ece.labyrinthegame;
    exports fr.ece.labyrinthegame.Controllers; // optional if needed outside module
}
