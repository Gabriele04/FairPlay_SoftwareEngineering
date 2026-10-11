package main.java.persistence;

import main.java.domain.Purchase;

import java.util.List;

public interface PurchaseDAO {
    boolean save(Purchase purchase);
    Purchase findById(int id);
    List<Purchase> findByPlayerId(int playerId);
    List<Purchase> findByGameId(int gameId);
    boolean hasPlayerPurchasedGame(int playerId, int gameId);
}