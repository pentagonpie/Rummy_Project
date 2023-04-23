package com.rummy.server.app;

import com.rummy.server.app.helpers.GameMoveExecutor;
import com.rummy.server.app.helpers.GameMoveValidator;
import com.rummy.shared.MoveValidationResult;
import com.rummy.shared.*;
import com.rummy.shared.gameMove.GameMove;
import com.rummy.shared.gameMove.GameMoveEventType;

import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl implements RummyServer {
    private final Map<String, Game> _games;
    private final Map<String, Player> _connectedPlayers;
    private Database mydb;

    public ServerImpl() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
        this._games = new HashMap<>();
        this._connectedPlayers = new HashMap<>();
        
        
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("url:");
        String url = myObj.nextLine();  
        
        System.out.println("user:");
        String user = myObj.nextLine();  
        
        System.out.println("password:");
        String password = myObj.nextLine();  
        this.mydb = new Database( url,  user,  password);
    }

    @Override
    public String getPlayerName(String id) throws RemoteException {
        return this._connectedPlayers.get(id).getUserName();
    }

    @Override
    public int createUser(String name, String password) throws RemoteException {
        int result = this.mydb.createPlayer(name, password);

        return result;
    }

    public int getScore(String userId) throws RemoteException{
        return this.mydb.getPlayerScore( Integer.parseInt(userId));
        
    }
    
    public void setScore(String userId, int newScore) throws RemoteException{
         this.mydb.setPlayerScore( Integer.parseInt(userId),newScore);
        
    }
    
    //returns user id if login is correct
    @Override
    public String login(String username, String password, RummyClient client) throws RemoteException {

        String userId;

        int id = this.mydb.getID(username);
        //System.out.println("user id is " + id);
        //no such user
        if (id == -1) {
            return null;
        }

        userId = Integer.toString(id);
        if(this.mydb.getPlayerOnline(Integer.parseInt(userId))==1){
            return "loggedInAlready";
            
        }

        if (this.mydb.checkPassword(username, password)) {
            this._connectedPlayers.put(userId, new Player(userId, username, client));
            this.mydb.setPlayerOnline(Integer.parseInt(userId), 1);
            return userId;
        }
        return null;
    }

    @Override
    public void logout(String userId) throws RemoteException {
        this._connectedPlayers.remove(userId);
        this.mydb.setPlayerOnline(Integer.parseInt(userId), 0);
    }

    private ArrayList<Card> shuffle(ArrayList<Card> cards) {
        Random random = new Random();
        for (int i = 0; i < cards.size(); i++) {
            int randomIndex = random.nextInt(cards.size());
            Card temp = cards.get(i);
            cards.set(i, cards.get(randomIndex));
            cards.set(randomIndex, temp);
        }

        return cards;
    }
    
    @Override
    public boolean checkGameActive(int id) throws RemoteException{
        if(this.mydb.getGameActive(id)==1){
            return true;
        }
        return false;
    }

    private ArrayList<Card> generateDeck() {
        ArrayList<Card> deck = new ArrayList<>();
        for (int cardValue = 1; cardValue <= 13; cardValue++) {
            for (Suit suit : Suit.values()) {
                deck.add(new Card(cardValue, suit));
            }
        }

        //return shuffle(deck);
        return deck;
    }

    @Override
    public Game createNewGame(String gameName, String playerId) throws RemoteException {
        Player creator = this._connectedPlayers.get(playerId);
        System.out.println("hello createNewGame1");
        if (creator == null) {
            return null;
        }

        final int CARDS_PER_PLAYER = 14;

        System.out.println("playerID from createnewgame: " + playerId);
        System.out.println("hello createNewGame2");

        if (this.mydb.createGame(Integer.parseInt(playerId), gameName) == -1) {
            System.out.println("Error creating game");
            return new Game(gameName, creator.getUserId(), "-1");
        }
        int gameID = this.mydb.getGameID(gameName);
        System.out.println("in createnewgame getting from database gameid " + gameID);

        ArrayList<Card> deck = generateDeck();
        ArrayList<Card> player1Cards = new ArrayList<>(deck.subList(0, CARDS_PER_PLAYER));
        ArrayList<Card> player2Cards = new ArrayList<>(deck.subList(CARDS_PER_PLAYER, CARDS_PER_PLAYER * 2));
        ArrayList<Card> discardPile = new ArrayList<>(deck.subList(CARDS_PER_PLAYER * 2, CARDS_PER_PLAYER * 2 + 1));
        deck = new ArrayList<>(deck.subList(CARDS_PER_PLAYER * 2 + 1, deck.size()));
        ArrayList<ArrayList<Card>> board = new ArrayList<>();

        GameState gameState = new GameState(player1Cards, player2Cards, deck, discardPile, board);
        Game createdGame = new Game(gameName, creator.getUserId(), gameState, Integer.toString(gameID));
        createdGame.addPlayer(creator.getUserId());
        this._games.put(createdGame.getId(), createdGame);

        return createdGame;
    }

    @Override
    public void joinGame(String gameName, String playerId) throws RemoteException {
        Player player = this._connectedPlayers.get(playerId);
        Game game = this._games.values().stream()
                .filter(g -> g.getName().equals(gameName))
                .findFirst()
                .orElse(null);

        if (game == null || player == null) {
            return;
        }

        
        int gameID = this.mydb.getGameID(gameName);
        if(this.mydb.getGameActive(gameID) != 0){
            return;
            
        }
        game.addPlayer(player.getUserId());
        
        this.mydb.addPlayerGame(Integer.parseInt(playerId), gameID);
        this.mydb.setGameActive(gameID, 1);

        game.getPlayersIds().forEach(_playerId -> {
            Player playerToNotify = this._connectedPlayers.get(_playerId);
            try {
                playerToNotify.getClient().handleGameStart(game);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void exitGame(String gameName, String playerId) throws RemoteException {
        Player player = this._connectedPlayers.get(playerId);
        Game game = this._games.values().stream()
                .filter(g -> g.getName().equals(gameName))
                .findFirst()
                .orElse(null);

        if (game == null || player == null) {
            return;
        }

        game.getPlayersIds().forEach(_playerId -> {
            Player playerToNotify = this._connectedPlayers.get(_playerId);
            try {
                playerToNotify.getClient().handleGameEnd(game, GameEndReason.PLAYER_DISCONNECTED);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public ArrayList<Game> getGames() throws RemoteException {
        return new ArrayList<>(this._games.values());
    }

    private boolean isGameEnded(Game game) {
        return game.getGameState().getCards1().size() == 0 || game.getGameState().getCards2().size() == 0;
    }

    @Override
    public MoveValidationResult addGameMove(GameMove gameMove) throws RemoteException {
        Game game = this._games.get(gameMove.getGameId());

        MoveValidationResult moveValidationResult = GameMoveValidator.isValidMove(game, gameMove);

        System.out.println("move valid: " + moveValidationResult);

        if (!moveValidationResult.isValid()) {
            return moveValidationResult;
        }

        Game gameAfterMove = GameMoveExecutor.executeGameMove(game, gameMove);
        gameAfterMove.getGameState().setLastMove(gameMove);


        if (gameMove.getGameMoveEventType() == GameMoveEventType.DISCARD) {
            gameAfterMove.nextTurn();
        }

        game.getPlayersIds().forEach(_playerId -> {
            Player playerToNotify = this._connectedPlayers.get(_playerId);
            try {
                playerToNotify.getClient().handleGameMove(gameAfterMove);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        if (isGameEnded(gameAfterMove)) {
            game.getPlayersIds().forEach(_playerId -> {
                Player playerToNotify = this._connectedPlayers.get(_playerId);
                try {
                    playerToNotify.getClient().handleGameEnd(gameAfterMove, GameEndReason.PLAYER_WON);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return new MoveValidationResult(true, 0);
    }

    @Override
    public void deleteGame(String gameID) throws RemoteException {
        this.mydb.deleteGame(Integer.parseInt(gameID));
        this._games.remove(gameID);
    }
}
