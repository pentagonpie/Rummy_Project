package com.rummy.ui.rummyui;

import com.rummy.shared.*;
import com.rummy.shared.gameMove.GameMove;
import com.rummy.shared.gameMove.GameMoveEventType;
import com.rummy.ui.gameEvents.GameEndedEventListener;
import com.rummy.ui.gameEvents.GameEventsManager;
import com.rummy.ui.gameEvents.GameMoveEventListener;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.util.ArrayList;

public class GameController implements GameEndedEventListener, GameMoveEventListener {
    @FXML
    protected GridPane mainGrid;

    @FXML
    protected AnchorPane anchorPane;

    @FXML
    protected Label label_opponent;

    @FXML
    protected Label label_user;

    @FXML
    protected Label helpLabel;

    @FXML
    protected HBox hboxMyCards;

    @FXML
    protected HBox hboxCardBoard1;

    @FXML
    protected HBox hboxCardBoard2;

    @FXML
    protected HBox hboxOpponentCards;

    @FXML
    protected ImageView imgDeck;

    @FXML
    protected ImageView imgDiscardPile;

    @FXML
    protected Button btnDrawFromDeck;

    @FXML
    protected Button btnDrawFromDiscardPile;

    @FXML
    protected Button btnDiscard;

    @FXML
    protected Button btnMeld;

    @FXML
    private Button backButton;

    @FXML
    private Button btnAddToSeries;

    private final RMIClient rmiClient;

    private ArrayList<Card> selectedCards;

    private boolean isAddToSeriesMode = false;

    public GameController() throws NotBoundException, RemoteException {
        this.rmiClient = RMIClient.getInstance();
        GameEventsManager.register(this);
        selectedCards = new ArrayList<>();
    }

    private void onSelectMyCard(ImageView imageView, Card card) {
        if (!this.canSelectCards() || isAddToSeriesMode) {
            return;
        }

        if (selectedCards.contains(card)) {
            selectedCards.remove(card);
            StackPane.setMargin(imageView, new Insets(0, 0, 0, Constants.CARD_IMAGE_LEFT_MARGIN));
        } else {
            selectedCards.add(card);
            StackPane.setMargin(imageView, new Insets(0, 0, 50, Constants.CARD_IMAGE_LEFT_MARGIN));
        }

        updateButtonsState();
    }

    private void addImageToHBox(HBox hbox, String imageFileName, boolean mine, Card card, double leftMargin) {
        StackPane stackPane = new StackPane();
        Image image = new Image(getClass().getResourceAsStream("/com/rummy/ui/rummyui/Card_files/images/" + imageFileName + ".png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(Constants.CARD_IMAGE_HEIGHT);
        imageView.setFitWidth(Constants.CARD_IMAGE_WIDTH);

        stackPane.getChildren().add(imageView);
        StackPane.setMargin(imageView, new Insets(0, 0, 0, leftMargin));
        hbox.getChildren().add(stackPane);

        if (mine) {
            stackPane.setOnMousePressed(e -> {
                onSelectMyCard(imageView, card);
            });
        }
    }

    private void addSelectedCardsToSeries(Card pressedCard) {
        if (selectedCards.size() == 0) {
            return;
        }

        try {
            String playerId = DataManager.getPlayerId();
            String gameId = DataManager.getGame().getId();
            GameMove gameMove = new GameMove(playerId, GameMoveEventType.MELD, this.selectedCards, pressedCard, gameId);
            rmiClient.addGameMove(gameMove);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void addImageToBoardHBox(HBox hbox, String imageFileName, Card card, double leftMargin) {
        StackPane stackPane = new StackPane();
        Image image = new Image(getClass().getResourceAsStream("/com/rummy/ui/rummyui/Card_files/images/" + imageFileName + ".png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(Constants.CARD_IMAGE_HEIGHT);
        imageView.setFitWidth(Constants.CARD_IMAGE_WIDTH);

        stackPane.getChildren().add(imageView);
        StackPane.setMargin(imageView, new Insets(0, 0, 0, leftMargin));
        hbox.getChildren().add(stackPane);

        stackPane.setOnMousePressed(e -> {
            if (this.isAddToSeriesMode) {
                addSelectedCardsToSeries(card);
            }
        });
    }

    private void addMyCardsToBoard(HBox hbox) {
        getMyCards().forEach(card -> {
            final String fileName = card.getValue() + "_" + card.getSuit();
            this.addImageToHBox(hbox, fileName, true, card, Constants.CARD_IMAGE_LEFT_MARGIN);
        });
    }

    private void addOpponentCardsToBoard(HBox hbox) {
        getOpponentCards().forEach(card -> {
            this.addImageToHBox(hbox, "back", false, card, Constants.CARD_IMAGE_LEFT_MARGIN);
        });
    }

    private void addSeriesToBoard(HBox hbox, ArrayList<Card> series) {
        boolean first = true;

        for (Card card : series) {
            final String fileName = card.getValue() + "_" + card.getSuit();
            double leftMargin = first ? Constants.MARGIN_BETWEEN_SERIES : Constants.CARD_IMAGE_LEFT_MARGIN_IN_SERIES;
            first = false;
            this.addImageToBoardHBox(hbox, fileName, card, leftMargin);
        }
    }

    private void addCardsToBoard(ArrayList<ArrayList<Card>> listSeries) {
        //System.out.println("addCardsToBoard");
        final int MAX_CARDS_PER_HBOX = 20;


        for (ArrayList<Card> series : listSeries) {
            boolean isHboxCardBoard1Full = hboxCardBoard1.getChildren().size() + series.size() > MAX_CARDS_PER_HBOX;
            if (isHboxCardBoard1Full) {
                addSeriesToBoard(hboxCardBoard2, series);
            } else {
                addSeriesToBoard(hboxCardBoard1, series);
            }
        }
    }

    //Check which player turn is it, if this player, return true
    public boolean myTurn() {
        Game game = DataManager.getGame();
        GameState gameState = game.getGameState();
        int turn = gameState.getTurn();
        final boolean isGameCreator = game.getCreator().equals(DataManager.getPlayerId());
        boolean myTurn = false;

        //creator turn
        if (turn == 0) {
            if (isGameCreator) {
                myTurn = true;
            }
        }
        //second player turn
        else {
            if (!isGameCreator) {
                myTurn = true;
            }
        }
        //System.out.println("creator: " + isGameCreator + " myTurn " + myTurn);
        return myTurn;
    }

    private boolean canSelectCards() {
        Game game = DataManager.getGame();
        boolean isMyTurn = myTurn();
        boolean isFirstMoveInTheGame = game.getGameState().getLastMove() == null;

        if (!isMyTurn || isFirstMoveInTheGame) {
            return false;
        }

        GameMoveEventType lastMoveType = game.getGameState().getLastMove().getGameMoveEventType();
        boolean lastMoveWasDiscard = lastMoveType == GameMoveEventType.DISCARD;

        return !lastMoveWasDiscard;
    }

    //Create box around my cards when it is my turn to emphesize it visually
    public void setMyBorder() {
        //System.out.println("setMyBorder");
        hboxMyCards.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        hboxOpponentCards.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: green;");

        GameMove lastMove = DataManager.getGame().getGameState().getLastMove();

        String moveInstruction = "Your Turn: ";

        if (lastMove == null || lastMove.getGameMoveEventType() == GameMoveEventType.DISCARD) {
            moveInstruction += "Draw card from deck or discard pile";
        } else if (isAddToSeriesMode) {
            moveInstruction += "Click on series to add cards to it";
        } else {
            moveInstruction += "Select Cards to meld, add to series, or discard";
        }

        helpLabel.setText(moveInstruction);

    }

    //Create box around opponent cards when it is my turn to emphesize it visually
    public void setBorderOpponent() {
        System.out.println("setBorderOpponent");
        hboxMyCards.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: green;");
        hboxOpponentCards.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        helpLabel.setText("Opponent turn");

    }

    //Set labels with names of players for me and opponent
    private void setLabelUser(Label label_name, String user) {
        label_name.setText(user);
    }

    private void setDiscardPile(ArrayList<Card> discardPile) {
        if (discardPile.size() == 0) {
            this.imgDiscardPile.setImage(null);
            return;
        }

        Card discardCard = discardPile.get(0);
        String imageFileName = discardCard.getValue() + "_" + discardCard.getSuit();
        Image image = new Image(getClass().getResourceAsStream("/com/rummy/ui/rummyui/Card_files/images/" + imageFileName + ".png"));
        this.imgDiscardPile.setImage(image);
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            Stage primaryStage = (Stage) mainGrid.getScene().getWindow();
            primaryStage.setMaximized(true);
            mainGrid.prefHeightProperty().bind(anchorPane.heightProperty());
            mainGrid.prefWidthProperty().bind(anchorPane.widthProperty());
            Game game = DataManager.getGame();
            String creatorName = this.rmiClient.getPlayerName(game.getCreator());
            String secondPlayerName = this.rmiClient.getPlayerName(game.getSecondPlayer());
            boolean isGameCreator = game.getCreator().equals(DataManager.getPlayerId());
            this.setLabelUser(label_opponent, isGameCreator ? secondPlayerName : creatorName);
            this.setLabelUser(label_user, DataManager.getUserName());

            resetAndInitGameUI();
        });
    }

    @FXML
    void onAddToSeriesPressed() {
        if (!canSelectCards() || !myTurn()) {
            return;
        }

        this.isAddToSeriesMode = !this.isAddToSeriesMode;

        setMyBorder();

        if (isAddToSeriesMode) {
            this.btnAddToSeries.setText("Finish Adding");
            disableButtons(btnDrawFromDeck, btnDrawFromDiscardPile, btnDiscard, btnMeld);
            enableButtons(btnAddToSeries);
        } else {
            this.btnAddToSeries.setText("Add to Series");
            updateButtonsState();
        }
    }

    private void closeAndBackToMainScreen() {
        FXMLLoader mainScreenLoader = new FXMLLoader(RummyApplication.class.getResource("mainScreen.fxml"));
        Stage mainScreenStage = new Stage();
        mainScreenStage.show();
        mainScreenStage.setMaximized(true);

        mainScreenStage.setOnCloseRequest(we -> {
            String userId = DataManager.getPlayerId();
            this.rmiClient.logout(userId);
            Platform.exit();
            System.exit(0);
        });

        try {
            mainScreenStage.setScene(new Scene(mainScreenLoader.load()));
            Stage primaryStage = (Stage) backButton.getScene().getWindow();
            primaryStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("IOException");
            alert.setHeaderText("Exception at game screen controller");
            alert.show();
        }
    }

    @Override
    public void onGameEnded(Game game, GameEndReason gameEndReason) {
        Platform.runLater(() -> {
            System.out.println("got onGameEnded in game controller ");

            if (gameEndReason == GameEndReason.PLAYER_DISCONNECTED) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Game ended");
                alert.setHeaderText("Opponent disconnected");
                alert.showAndWait();

                closeAndBackToMainScreen();

                return;
            }

            if (gameEndReason == GameEndReason.PLAYER_WON) {
                if (getMyCards().size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Game ended, You won!");
                    alert.setHeaderText("Game ended, You won!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Game ended, You lost! :(");
                    alert.setHeaderText("Game ended, You lost! :(");
                    alert.showAndWait();
                }

                closeAndBackToMainScreen();
            }
        });
    }

    @FXML
    void onDrawFromDeck() {
        System.out.println("draw from deck");

        if (!myTurn() || isAddToSeriesMode) {
            return;
        }
        try {
            String playerId = DataManager.getPlayerId();
            String gameId = DataManager.getGame().getId();

            GameMove gameMove = new GameMove(playerId, GameMoveEventType.DRAW_FROM_DECK, null, null, gameId);
            MoveValidationResult result = new MoveValidationResult(true, 0);
            try {
                result = rmiClient.addGameMove(gameMove);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            updateHelpLabel(result);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onDrawFromDiscardPile() {
        System.out.println("draw from discard pile");
        if (!myTurn() || isAddToSeriesMode) {
            return;
        }

        try {
            String playerId = DataManager.getPlayerId();
            String gameId = DataManager.getGame().getId();

            GameMove gameMove = new GameMove(playerId, GameMoveEventType.DRAW_FROM_DISCARD, null, null, gameId);


            MoveValidationResult result = new MoveValidationResult(true, 0);
            try {
                result = rmiClient.addGameMove(gameMove);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            updateHelpLabel(result);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void updateHelpLabel(MoveValidationResult result) {
        if (!result.isValid()) {
            System.out.println("setting new help text");
            switch (result.getErrorCode()) {
                case 1 -> helpLabel.setText("Invalid move");
                case 2 -> helpLabel.setText("Invalid draw");
                case 3 -> helpLabel.setText("trying to discard twice in row");
                case 4 -> helpLabel.setText("Invalid meld");
                case 5 -> helpLabel.setText("Not your turn");
                case 6 -> helpLabel.setText("Cannot discard");
                case 7 -> helpLabel.setText("Cards not same value");
                case 8 -> helpLabel.setText("Cards not same suit");
                case 9 -> helpLabel.setText("Series not raising value");
            }
        }
    }

    //1-general invalid move
    //2-invalid draw
    //3-trying to discard twice in row
    //4-invalid meld
    //5-not your turn
    //6 -cannot discard
    //7 - cards not same value
    //8 - cards not same suit
    //9 - series not raising values

    @FXML
    public void onDiscard() {
        if (!myTurn() || selectedCards.size() != 1 || isAddToSeriesMode) {
            return;
        }

        try {
            String playerId = DataManager.getPlayerId();
            String gameId = DataManager.getGame().getId();

            GameMove gameMove = new GameMove(playerId, GameMoveEventType.DISCARD, selectedCards, null, gameId);

            MoveValidationResult result = new MoveValidationResult(true, 0);
            try {
                result = rmiClient.addGameMove(gameMove);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            updateHelpLabel(result);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onMeld() {
        System.out.println("meld pressed");
        if (!myTurn() || selectedCards.size() == 0 || isAddToSeriesMode) {
            return;
        }

        try {
            String playerId = DataManager.getPlayerId();
            String gameId = DataManager.getGame().getId();

            System.out.println("in onMeld,selectedCards is  " + selectedCards);
            GameMove gameMove = new GameMove(playerId, GameMoveEventType.MELD, selectedCards, null, gameId);

            MoveValidationResult result = new MoveValidationResult(true, 0);
            try {
                result = rmiClient.addGameMove(gameMove);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            updateHelpLabel(result);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    public void deleteGame(Game game) {

        this.rmiClient.exitGame(game.getName(), DataManager.getPlayerId());
        this.rmiClient.deleteGame(game);
    }

    @FXML
    public void onBackButtonClick() {
        Game game = DataManager.getGame();
        deleteGame(game);
    }

    private ArrayList<Card> getMyCards() {
        Game game = DataManager.getGame();
        final boolean isGameCreator = game.getCreator().equals(DataManager.getPlayerId());
        ArrayList<Card> myCards = isGameCreator ? game.getGameState().getCards1() : game.getGameState().getCards2();
        return myCards;
    }

    private ArrayList<Card> getOpponentCards() {
        Game game = DataManager.getGame();
        final boolean isGameCreator = game.getCreator().equals(DataManager.getPlayerId());
        ArrayList<Card> opponentCards = isGameCreator ? game.getGameState().getCards2() : game.getGameState().getCards1();
        return opponentCards;
    }

    private void disableButtons(Button... buttons) {
        for (Button button : buttons) {
            button.setDisable(true);
        }
    }

    private void enableButtons(Button... buttons) {
        for (Button button : buttons) {
            button.setDisable(false);
        }
    }

    private void updateButtonsState() {
        boolean isMyTurn = myTurn();

        if (!isMyTurn) {
            disableButtons(btnDrawFromDeck, btnDrawFromDiscardPile, btnDiscard, btnMeld, btnAddToSeries);
            return;
        }

        if (isAddToSeriesMode) {
            disableButtons(btnDrawFromDeck, btnDrawFromDiscardPile, btnDiscard, btnMeld);
            enableButtons(btnAddToSeries);

            return;
        }

        Game game = DataManager.getGame();

        GameMove lastMove = game.getGameState().getLastMove();

        if (lastMove == null || lastMove.getGameMoveEventType() == GameMoveEventType.DISCARD) {
            disableButtons(btnDiscard, btnMeld, btnAddToSeries);
            enableButtons(btnDrawFromDeck, btnDrawFromDiscardPile);
        } else if (selectedCards.size() == 0) {
            disableButtons(btnDrawFromDeck, btnDrawFromDiscardPile, btnDiscard, btnMeld, btnAddToSeries);
        } else if (selectedCards.size() == 1) {
            enableButtons(btnDiscard, btnAddToSeries);
            disableButtons(btnDrawFromDeck, btnDrawFromDiscardPile, btnMeld);
        } else if (selectedCards.size() == 2) {
            enableButtons(btnAddToSeries);
            disableButtons(btnDrawFromDeck, btnDrawFromDiscardPile, btnDiscard, btnMeld);
        } else {
            enableButtons(btnMeld, btnAddToSeries);
            disableButtons(btnDrawFromDeck, btnDrawFromDiscardPile, btnDiscard);
        }
    }

    private void resetAndInitGameUI() {
        Game game = DataManager.getGame();
        GameState gameState = game.getGameState();
        ArrayList<ArrayList<Card>> boardCards = gameState.getBoard();
        hboxMyCards.getChildren().clear();
        hboxOpponentCards.getChildren().clear();
        hboxCardBoard1.getChildren().clear();
        hboxCardBoard2.getChildren().clear();

        addMyCardsToBoard(hboxMyCards);
        addCardsToBoard(boardCards);
        addOpponentCardsToBoard(hboxOpponentCards);

        setDiscardPile(gameState.getDiscardPile());
        selectedCards.clear();
        updateButtonsState();

        if (myTurn()) {
            setMyBorder();
        } else {
            setBorderOpponent();
        }
    }

    @Override
    public void onGameMove(Game game) {
        Platform.runLater(() -> {
            System.out.println("got onGameMove in game controller ");
            DataManager.setGame(game);
            resetAndInitGameUI();
        });
    }
}
