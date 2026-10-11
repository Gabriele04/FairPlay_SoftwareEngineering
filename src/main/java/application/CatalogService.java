package main.java.application;

import main.java.domain.*;
import main.java.persistence.*;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class CatalogService {
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final PurchaseDAO purchaseDAO;

    public CatalogService() {
        this.purchaseDAO = new PurchaseDAOImpl();
        this.gameDAO = new GameDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    public CatalogService(GameDAO gameDAO, UserDAO userDAO, ReviewDAO reviewDAO, PurchaseDAO purchaseDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
        this.purchaseDAO = purchaseDAO;
    }

    public boolean publishGame(String title, String description, double price, User currentUser) {
        if (!(currentUser instanceof Publisher)) {
            return false;
        }

        Game newGame = new Game(null, title, description, price, 0, GameStateEnum.PENDING, currentUser.getId());
        return gameDAO.save(newGame);
    }

    public boolean approveGame(int gameId, User currentUser) {
        if (!(currentUser instanceof Admin)) {
            return false;
        }
        if (getGameById(gameId) == null || getApprovedCatalog().contains(getGameById(gameId))) {
            return false;
        }
        return gameDAO.updateStatus(gameId, GameStateEnum.APPROVED);
    }

    public boolean rejectGame(int gameId, User currentUser) {
        if (!(currentUser instanceof Admin)) {
            return false;
        }
        if (getGameById(gameId) == null) {
            return false;
        }
        return gameDAO.updateStatus(gameId, GameStateEnum.REJECTED);
    }

    public boolean setDiscount(int gameId, int discount, User currentUser) {
        if (!(currentUser instanceof Publisher) || discount < 0 || discount > 100) {
            return false;
        }

        Game game = gameDAO.findById(gameId);
        if (game == null || !game.getPublisherId().equals(currentUser.getId())) {
            return false;
        }

        return gameDAO.updateDiscount(gameId, discount);
    }

    public List<Game> getApprovedCatalog() {
        return gameDAO.findApproved();
    }

    public List<Game> getRejectedCatalog() {
        return gameDAO.findRejected();
    }

    public List<Game> getPurchasedGames(User currentUser) {
        return gameDAO.findByPurchased(currentUser.getId());
    }

    public List<Game> getPendingCatalog(User currentUser) {
        if (!(currentUser instanceof Admin)) {
            return Collections.emptyList();
        }
        return gameDAO.findPending();
    }

    public List<Game> getPublisherGames(User currentUser) {
        if (!(currentUser instanceof Publisher)) {
            return Collections.emptyList();
        }
        return gameDAO.findByPublisherId(currentUser.getId());
    }

    public List<Game> getGamesByPublisherUsername(String username) {
        User publisher = userDAO.findByUsername(username);
        if (publisher == null || !(publisher instanceof Publisher)) {
            return Collections.emptyList();
        }
        return gameDAO.findByPublisherId(publisher.getId());
    }

    public Game getGameById(int gameId) {
        return gameDAO.findById(gameId);
    }

    public List<Purchase> getStatisticsGame(int gameId, User currentUser) {
        if (!(currentUser instanceof Publisher)) {
            return Collections.emptyList();
        }
        return purchaseDAO.findByGameId(gameId);
    }

    public boolean modifyGame(int gameId, User currentUser, int operation, String value) {
        if (!(currentUser instanceof Publisher)) return false;
        return switch (operation) {
            case 1 -> gameDAO.updateTitle(gameId, value);
            case 2 -> gameDAO.updateDescription(gameId, value);
            case 3 -> gameDAO.updateDiscount(gameId, Integer.parseInt(value));
            default -> false;
        };
    }


}