package com.rummy.ui.rummyui;


import com.rummy.shared.Card;
import com.rummy.shared.Game;
import com.rummy.shared.GameState;
import com.rummy.shared.MoveValidationResult;
import com.rummy.shared.gameMove.GameMove;
import com.rummy.shared.gameMove.GameMoveEventType;
import com.rummy.ui.gameEvents.GameEndedEventListener;
import com.rummy.ui.gameEvents.GameEventsManager;
import com.rummy.ui.gameEvents.GameMoveEventListener;
import com.rummy.ui.gameEvents.nextTurnEventListener;

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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.EventListener;


import javafx.event.EventHandler;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

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
    protected HBox hboxCardBoard;

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

    private final RMIClient rmiClient;

    private ArrayList<Card> selectedCards;

    public GameController() throws NotBoundException, RemoteException {
        this.rmiClient = RMIClient.getInstance();
        GameEventsManager.register((EventListener) this);
        selectedCards = new ArrayList<>();
    }

    private void onSelectMyCard(ImageView imageView, Card card) {
        if (!this.canSelectCards()) {
            return;
        }

        if (selectedCards.contains(card)) {
            selectedCards.remove(card);
            StackPane.setMargin(imageView, new Insets(0, 0, 0, Constants.CARD_IMAGE_MARGIN));
        } else {
            selectedCards.add(card);
            StackPane.setMargin(imageView, new Insets(0, 0, 50, Constants.CARD_IMAGE_MARGIN));
        }
    }

    private void addImageToHBox(HBox hbox, String imageFileName, boolean mine, Card card) {
        StackPane stackPane = new StackPane();
        Image image = new Image(getClass().getResourceAsStream("/com/rummy/ui/rummyui/Card_files/images/" + imageFileName + ".png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(Constants.CARD_IMAGE_HEIGHT);
        imageView.setFitWidth(Constants.CARD_IMAGE_WIDTH);

        stackPane.getChildren().add(imageView);
        StackPane.setMargin(imageView, new Insets(0, 0, 0, Constants.CARD_IMAGE_MARGIN));
        hbox.getChildren().add(stackPane);

        if (mine) {
            stackPane.setOnMousePressed(e -> {
                onSelectMyCard(imageView, card);
            });
        }
    }

    private void addMyCardsToBoard(HBox hbox, ArrayList<Card> cards) {
        cards.forEach(card -> {
            final String fileName = card.getValue() + "_" + card.getSuit();
            this.addImageToHBox(hbox, fileName, true, card);
        });
    }
    
    

    private void addOpponentCardsToBoard(HBox hbox, ArrayList<Card> cards) {
        cards.forEach(card -> {
            this.addImageToHBox(hbox, "back", false, card);
        });
    }

    private void addCardsToBoard(HBox hbox, ArrayList<ArrayList<Card>> listSeries) {
        System.out.println("addCardsToBoard");
        listSeries.forEach(series -> {
            series.forEach(card -> {
            final String fileName = card.getValue() + "_" + card.getSuit();
            this.addImageToHBox(hbox, fileName, true, card);
                System.out.println("adding to board " + fileName);
        });
        });
    }


    //Check which player turn is it, if this player, return true
    public boolean myTurn(Game game) {

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
        System.out.println("creator: " + isGameCreator + " myTurn " + myTurn);
        return myTurn;
    }

    private boolean canSelectCards() {
        Game game = DataManager.getGame();
        boolean isMyTurn = myTurn(game);
        boolean isFirstMoveInTheGame = game.getGameState().getLastMove() == null;

        if (!isMyTurn || isFirstMoveInTheGame) {
            return false;
        }

        GameMoveEventType lastMoveType = game.getGameState().getLastMove().getGameMoveEventType();
        boolean lastMoveWasDraw = lastMoveType == GameMoveEventType.DRAW_FROM_DECK || lastMoveType == GameMoveEventType.DRAW_FROM_DISCARD;

        return lastMoveWasDraw;
    }


    //print to screen name of pressed image
    public void pressedImage(MouseEvent e, String imageFileName) {
        System.out.println(imageFileName);

        Game game = DataManager.getGame();
        GameState gameState = game.getGameState();
        System.out.println("now turn is " + gameState.getTurn());


    }

    //Create box around my cards when it is my turn to emphesize it visually
    public void setMyBorder() {
        System.out.println("setMyBorder");
        hboxMyCards.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        hboxOpponentCards.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: green;");
        
        helpLabel.setText("Your Turn:");

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


    public void updateMousePosition(MouseEvent e) {
        //System.out.println("Mouse press:");



        double maxX, maxY, minX, minY;
        maxX = hboxOpponentCards.boundsInLocalProperty().getValue().getMaxX();
        maxY = hboxOpponentCards.boundsInLocalProperty().getValue().getMaxY();
        minX = hboxOpponentCards.boundsInLocalProperty().getValue().getMinX();
        minY = hboxOpponentCards.boundsInLocalProperty().getValue().getMinY();
        hboxOpponentCards.onMousePressedProperty();

    }



    private void setDiscardPile(ArrayList<Card> discardPile) {
        if (discardPile.size() == 0) {
            this.imgDiscardPile.setImage(null);
            return;
        }

        Card discardCard = discardPile.get(discardPile.size() - 1);
        String imageFileName = discardCard.getValue() + "_" + discardCard.getSuit();
        Image image = new Image(getClass().getResourceAsStream("/com/rummy/ui/rummyui/Card_files/images/" + imageFileName + ".png"));
        this.imgDiscardPile.setImage(image);
    }

    @FXML
    void initialize() {
        mainGrid.setOnMousePressed(e -> {
            updateMousePosition(e);
        });

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

            this.setDiscardPile(gameState.getDiscardPile());

            String secondPlayerName = this.rmiClient.getPlayerName(game.getSecondPlayer());
            String createrName = this.rmiClient.getPlayerName(game.getCreator());
            this.setLabelUser(label_opponent, isGameCreator ? secondPlayerName : createrName);
            this.setLabelUser(label_user, DataManager.getUserName());

            if (myTurn(game)) {
                setMyBorder();
            } else {
                setBorderOpponent();
            }
        });
    }

    @Override
    public void onGameEnded(Game game) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("got onGameEnded in game controller ");

                FXMLLoader mainScreenLoader = new FXMLLoader(RummyApplication.class.getResource("mainScreen.fxml"));
                Stage mainScreenStage = new Stage();
                mainScreenStage.show();
                mainScreenStage.setMaximized(true);
                
                mainScreenStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent we) {
                    
                    
                    Platform.exit();
                    System.exit(0);
                }
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
        });
    }

    @FXML
    void onDrawFromDeck() {
        System.out.println("draw from deck");
        if (myTurn(DataManager.getGame())) {
            try {
                String playerId = DataManager.getPlayerId();
                String gameId = DataManager.getGame().getId();

                GameMove gameMove = new GameMove(playerId, GameMoveEventType.DRAW_FROM_DECK, null, null, gameId);
                MoveValidationResult result = new MoveValidationResult(true,0);
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
    }

    @FXML
    void onDrawFromDiscardPile() {
        System.out.println("draw from discard pile");
        if (myTurn(DataManager.getGame())) {
            try {
                String playerId = DataManager.getPlayerId();
                String gameId = DataManager.getGame().getId();

                
                GameMove gameMove = new GameMove(playerId, GameMoveEventType.DRAW_FROM_DISCARD, null, null, gameId);
                
                
                MoveValidationResult result = new MoveValidationResult(true,0);
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
    }

    public void updateHelpLabel(MoveValidationResult result){
        if(!result.isValid()){
            System.out.println("setting new help text");
            switch(result.getErrorCode()){
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
        if (!myTurn(DataManager.getGame()) || selectedCards.size() != 1) {
            return;
        }

        try {
            String playerId = DataManager.getPlayerId();
            String gameId = DataManager.getGame().getId();

            GameMove gameMove = new GameMove(playerId, GameMoveEventType.DISCARD, selectedCards, null, gameId);
            
            MoveValidationResult result = new MoveValidationResult(true,0);
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
        if (!myTurn(DataManager.getGame())) {
            return;
        }

        try {
            String playerId = DataManager.getPlayerId();
            String gameId = DataManager.getGame().getId();

            System.out.println("in onMeld,selectedCards is  " + selectedCards);
            GameMove gameMove = new GameMove(playerId, GameMoveEventType.MELD, selectedCards, null, gameId);
            
            MoveValidationResult result = new MoveValidationResult(true,0);
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
    
    
    public void deleteGame(Game game){

        this.rmiClient.exitGame(game.getName(), DataManager.getPlayerId());
        this.rmiClient.deleteGame(game);
    }
    
    @FXML
    public void onBackButtonClick(){
            Platform.runLater(new Runnable() {
            @Override
            public void run() {
                
                Game game = DataManager.getGame();
                FXMLLoader mainScreenLoader = new FXMLLoader(RummyApplication.class.getResource("mainScreen.fxml"));
                Stage mainScreenStage = new Stage();
                mainScreenStage.show();
                mainScreenStage.setMaximized(true);
                deleteGame(game);

                mainScreenStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent we) {
                        Platform.exit();
                        System.exit(0);
                    }
                });        
                
                try {
                    mainScreenStage.setScene(new Scene(mainScreenLoader.load()));
                    Stage primaryStage = (Stage) backButton.getScene().getWindow();
                    primaryStage.close();
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("IOException");
                    alert.setHeaderText("Exception at create game screen controller");
                    alert.show();
                }
            }
        });
    }
    
    @Override
    public void onGameMove(Game game) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("got onGameMove in game controller ");
                DataManager.setGame(game);
                GameState gameState = game.getGameState();
                final boolean isGameCreator = game.getCreator().equals(DataManager.getPlayerId());
                ArrayList<Card> myCards = isGameCreator ? gameState.getCards1() : gameState.getCards2();
                ArrayList<Card> opponentCards = isGameCreator ? gameState.getCards2() : gameState.getCards1();
                
                ArrayList<ArrayList<Card>> boardCards =  gameState.getBoard();
                hboxMyCards.getChildren().clear();
                hboxOpponentCards.getChildren().clear();
                hboxCardBoard.getChildren().clear();
                
                addMyCardsToBoard(hboxMyCards, myCards);
                addCardsToBoard(hboxCardBoard, boardCards);
                addOpponentCardsToBoard(hboxOpponentCards, opponentCards);
                
                setDiscardPile(gameState.getDiscardPile());
                selectedCards.clear();

                if (myTurn(game)) {
                    setMyBorder();
                } else {
                    setBorderOpponent();
                }
            }
        });

    }
}
