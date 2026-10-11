package main.java.application;

import main.java.domain.Game;
import main.java.domain.GameStateEnum;
import main.java.domain.Player;
import main.java.domain.Purchase;
import main.java.domain.User;
import main.java.persistence.GameDAO;
import main.java.persistence.GameDAOImpl;
import main.java.persistence.PurchaseDAO;
import main.java.persistence.PurchaseDAOImpl;

import java.util.Collections;
import java.util.List;

public class CheckoutService {
    private final GameDAO gameDAO;
    private final PurchaseDAO purchaseDAO;

    public CheckoutService() {
        this.gameDAO = new GameDAOImpl();
        this.purchaseDAO = new PurchaseDAOImpl();
    }

    public CheckoutService(GameDAO gameDAO, PurchaseDAO purchaseDAO) {
        this.gameDAO = gameDAO;
        this.purchaseDAO = purchaseDAO;
    }

    public boolean buyGame(int gameId, PaymentStrategy paymentStrategy, User currentUser) {
        if (!(currentUser instanceof Player)) {
            return false;
        }

        Game game = gameDAO.findById(gameId);
        if (game == null || game.getState() != GameStateEnum.APPROVED) {
            return false;
        }

        if (purchaseDAO.hasPlayerPurchasedGame(currentUser.getId(), gameId)) {
            return false;
        }

        if (paymentStrategy.pay(game.getFinalPrice())) {
            Purchase purchase = new Purchase(null, currentUser.getId(), gameId, null, game.getFinalPrice());
            return purchaseDAO.save(purchase);
        }

        return false;
    }

    public List<Purchase> getPlayerPurchases(User currentUser) {
        if (!(currentUser instanceof Player)) {
            return Collections.emptyList();
        }
        return purchaseDAO.findByPlayerId(currentUser.getId());
    }
}