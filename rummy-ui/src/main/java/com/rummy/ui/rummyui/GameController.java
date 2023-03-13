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
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

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
        
        imageView.setOnMousePressed(e -> {
        pressedImage(e, imageFileName);
        });
        
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

    
    double x = 0.0, y = 0.0;  
    
    public void pressedImage(MouseEvent e,String imageFileName){
        System.out.println(imageFileName);
    }
    
    public void updateMousePosition(MouseEvent e){
       
        
        System.out.println("Mouse press:");
        x = e.getSceneX();
        y = e.getSceneY();
        System.out.println(x);
        System.out.println(y);
        
        
        double maxX,maxY,minX,minY;
        maxX = hboxOpponentCards.boundsInLocalProperty().getValue().getMaxX();
        maxY = hboxOpponentCards.boundsInLocalProperty().getValue().getMaxY();
        minX = hboxOpponentCards.boundsInLocalProperty().getValue().getMinX();
        minY = hboxOpponentCards.boundsInLocalProperty().getValue().getMinY();
        //System.out.println("getMaxX" + maxX);
        //System.out.println("getMaxY" + maxY);
        //System.out.println("getMinX" + minX);
        //System.out.println("getMinY" + minY);
        hboxOpponentCards.onMousePressedProperty();
        if(x > minX && x < maxX && y > minY && y < maxY){
            //System.out.println("Inside");
        }else{
            //System.out.println("outside");
        }
        
        
    }
    
    @FXML
    void initialize() {
        this.game = DataManager.getGame();

        
        mainGrid.setOnMousePressed(e -> {
        updateMousePosition(e);
        });

        //hboxOpponentCards.onMousePressedProperty()

                
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
