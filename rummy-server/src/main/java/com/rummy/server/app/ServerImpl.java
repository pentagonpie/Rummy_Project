package com.rummy.server.app;

import static com.rummy.server.app.Database.createGame;
import com.rummy.shared.*;

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
        ArrayList<Card> dock = new ArrayList<>();
        for (int cardValue = 1; cardValue <= 13; cardValue++) {
            for (Suit suit : Suit.values()) {
                dock.add(new Card(cardValue, suit));
            }
        }

        return suffle(dock);
    }

    @Override
    public Game createNewGame(String gameName, String playerId) throws RemoteException {
        Player creator = this._connectedPlayers.get(playerId);
        System.out.println("hello createNewGame1");
        if (creator == null) {
            return null;
        }

        final int CARDS_PER_PLAYER = 14;
        

        
        System.out.println(playerId);
        System.out.println("hello createNewGame2");
        Database.createGame(Integer.parseInt(playerId));
        //Database.createGame(1);
        
        ArrayList<Card> deck = generateDeck();
        ArrayList<Card> player1Cards = new ArrayList<>(deck.subList(0, CARDS_PER_PLAYER));
        ArrayList<Card> player2Cards = new ArrayList<>(deck.subList(CARDS_PER_PLAYER, CARDS_PER_PLAYER * 2));
        deck = new ArrayList<>(deck.subList(CARDS_PER_PLAYER * 2, deck.size()));
        ArrayList<ArrayList<Card>> board = new ArrayList<>();

        GameState gameState = new GameState(deck, player1Cards, player2Cards, board);
        Game createdGame = new Game(gameName, creator.getUserId(), gameState);
        createdGame.addPlayer(creator.getUserId());
        this._games.put(createdGame.getId(), createdGame);

        return createdGame;
    }

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
    public ArrayList<Game> getGames() throws RemoteException {
        return new ArrayList<>(this._games.values());
    }
}
