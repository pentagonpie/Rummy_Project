package com.rummy.ui.rummyui;

import com.rummy.shared.Card;
import com.rummy.shared.Game;
import com.rummy.shared.GameState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GameController {
    private Game game;

    @FXML
    protected GridPane mainGrid;

    @FXML
    protected AnchorPane anchorPane;

    @FXML
    protected HBox hboxMyCards;

    @FXML
    protected HBox hboxOpponentCards;

    @FXML
    protected HBox hboxDeckCards;

    private void addImageToHBox(HBox hbox, String imageFileName) {
        StackPane stackPane = new StackPane();
        Image image = new Image(getClass().getResourceAsStream("/com/rummy/ui/rummyui/Card_files/images/" + imageFileName + ".png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(Constants.CARD_IMAGE_HEIGHT);
        imageView.setFitWidth(Constants.CARD_IMAGE_WIDTH);
        stackPane.getChildren().add(imageView);
        StackPane.setMargin(imageView, new Insets(0, 0, 0, Constants.CARD_IMAGE_MARGIN));
        hbox.getChildren().add(stackPane);
    }
    private void addMyCardsToBoard(HBox hbox, ArrayList<Card> cards) {
        cards.forEach(card -> {
            final String fileName = card.getValue() + "_" + card.getSuit();
            this.addImageToHBox(hbox, fileName);
        });
    }

    private void addOpponentCardsToBoard(HBox hbox, ArrayList<Card> cards) {
        cards.forEach(card -> {
            this.addImageToHBox(hbox, "back");
        });
    }

    @FXML
    void initialize() {
        this.game = DataManager.getGame();

        Platform.runLater(() -> {
            Stage primaryStage = (Stage) mainGrid.getScene().getWindow();
            primaryStage.setMaximized(true);
            mainGrid.prefHeightProperty().bind(anchorPane.heightProperty());
            mainGrid.prefWidthProperty().bind(anchorPane.widthProperty());
            Game game = DataManager.getGame();
            GameState gameState = game.getGameState();

            final boolean isGameCreator = game.getCreator().equals(DataManager.getPlayerId());

            ArrayList<Card> myCards = isGameCreator ? gameState.getCards1() : gameState.getCards2();
            this.addMyCardsToBoard(hboxMyCards, myCards);

            ArrayList<Card> opponentCards = isGameCreator ? gameState.getCards2() : gameState.getCards1();
            this.addOpponentCardsToBoard(hboxOpponentCards, opponentCards);
        });
    }
}
