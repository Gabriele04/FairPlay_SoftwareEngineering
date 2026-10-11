package main.java.persistence;

import main.java.domain.Game;
import main.java.domain.GameStateEnum;
import main.java.domain.Player;
import main.java.domain.User;

import java.util.List;

public interface GameDAO {
    boolean save(Game game);
    Game findById(int id);
    List<Game> findApproved();
    List<Game> findPending();
    List<Game> findRejected();
    List<Game> findByPublisherId(int publisherId);
    List<Game> findByPurchased(int userId);
    boolean updateStatus(int gameId, GameStateEnum state);
    boolean updateDiscount(int gameId, int discount);
    boolean updateTitle(int gameId, String title);
    boolean updateDescription(int gameId, String description);
}