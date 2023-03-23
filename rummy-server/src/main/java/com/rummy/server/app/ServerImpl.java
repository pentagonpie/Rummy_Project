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

    public ServerImpl() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
        this._games = new HashMap<>();
        this._connectedPlayers = new HashMap<>();
    }
    

    @Override
    public String getPlayerName(String id) throws RemoteException{
        
        return this._connectedPlayers.get(id).getUserName();
        
    }

    @Override
    public String login(String username, String password, RummyClient client) throws RemoteException {
        
        String userId1,userId2;
        
        int id1 = Database.getID("nadav");
        int id2 = Database.getID("tom");
        userId1 = Integer.toString(id1);
        userId2 = Integer.toString(id2);
        
        
        if(id1 == -1){
            userId1 = UUID.randomUUID().toString();
        }
        
        if(id2 == -1){
            userId2 = UUID.randomUUID().toString();
        }


        Map<String, User> usersMap = Map.of(
                userId1, new User(userId1, "nadav", "123456"),
                userId2, new User(userId2, "tom", "123456")
        );

        System.out.println("username "+ username + " id " +  Database.getID(username) );
        
        User user = usersMap.values().stream()
                .filter(u -> u.getUserName().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        boolean isAuthorized = user != null;

        if (!isAuthorized) {
            return null;
        }

        this._connectedPlayers.put(user.getId(), new Player(user.getId(), user.getUserName(), client));

        return user.getId();
    }

    @Override
    public void logout(String userId) throws RemoteException {}

    private ArrayList<Card> suffle(ArrayList<Card> cards) {
        Random random = new Random();
        for (int i = 0; i < cards.size(); i++) {
            int randomIndex = random.nextInt(cards.size());
            Card temp = cards.get(i);
            cards.set(i, cards.get(randomIndex));
            cards.set(randomIndex, temp);
        }

        return cards;
    }

    private ArrayList<Card> generateDeck() {
        ArrayList<Card> deck = new ArrayList<>();
        for (int cardValue = 1; cardValue <= 13; cardValue++) {
            for (Suit suit : Suit.values()) {
                deck.add(new Card(cardValue, suit));
            }
        }

        return suffle(deck);
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



        if(Database.createGame(Integer.parseInt(playerId),gameName) == -1){
            System.out.println("Error creating game");
            return new Game(gameName, creator.getUserId(), "-1" );
        }
        int gameID = Database.getGameID(gameName);
        System.out.println("in createnewgame getting from database gameid "+gameID);


        ArrayList<Card> deck = generateDeck();
        ArrayList<Card> player1Cards = new ArrayList<>(deck.subList(0, CARDS_PER_PLAYER));
        ArrayList<Card> player2Cards = new ArrayList<>(deck.subList(CARDS_PER_PLAYER, CARDS_PER_PLAYER * 2));
        ArrayList<Card> discardPile = new ArrayList<>(deck.subList(CARDS_PER_PLAYER * 2, CARDS_PER_PLAYER * 2 + 1));
        deck = new ArrayList<>(deck.subList(CARDS_PER_PLAYER * 2 + 1, deck.size()));
        ArrayList<ArrayList<Card>> board = new ArrayList<>();

        GameState gameState = new GameState(player1Cards, player2Cards, deck, discardPile, board);
        Game createdGame = new Game(gameName, creator.getUserId(), gameState, Integer.toString(gameID) );
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

        game.addPlayer(player.getUserId());
        System.out.println("adding player to game from joinGame");
        Database.addPlayerGame( Integer.parseInt(playerId), Database.getGameID(gameName) );

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
                playerToNotify.getClient().handleGameEnd(game);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public ArrayList<Game> getGames() throws RemoteException {
        return new ArrayList<>(this._games.values());
    }
    
    

    @Override
    public MoveValidationResult addGameMove(GameMove gameMove) throws RemoteException {
        Game game = this._games.get(gameMove.getGameId());

        MoveValidationResult isValidMove = GameMoveValidator.isValidMove(game, gameMove);

        System.out.println("move valid: " + isValidMove);
        if (!isValidMove.isValid()) {
            return isValidMove;
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
        return new MoveValidationResult(true,0);
    }

    @Override
    public void deleteGame(Game game) throws RemoteException {
        
        Database.deleteGame(Integer.parseInt(game.getId()));
        this._games.remove(game.getId());
    }
    
    @Override
    public void nextTurn(Game game) throws RemoteException{
        System.out.println("Sending to all user signal next turn");

        if (game == null ) {
            return;
        }

        game.getPlayersIds().forEach(_playerId -> {
            Player playerToNotify = this._connectedPlayers.get(_playerId);
            try {
                playerToNotify.getClient().handleNextTurn(game);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
