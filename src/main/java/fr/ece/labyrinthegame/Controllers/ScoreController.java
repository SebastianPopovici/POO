package fr.ece.labyrinthegame.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.util.List;
import fr.ece.labyrinthegame.model.Score;
import fr.ece.labyrinthegame.dao.ScoreManager;

public class ScoreController {
    @FXML
    private ListView<String> scoreListView;
    @FXML
    private Button returnButton;

    @FXML
    public void initialize() {
        // Example: fetch top 10 players with role "player" and username filter "%" (all usernames)
        List<Score> topScores = ScoreManager.getInstance().getTopScores( 10);
        for (Score score : topScores) {
            scoreListView.getItems().add(score.toString());
        }
    }

    @FXML
    private void returnToMenu() {
        Stage stage = (Stage) returnButton.getScene().getWindow();
        // TODO: replace with code to return to your main menu
    }
}
