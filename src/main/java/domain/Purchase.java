package main.java.domain;
import java.time.LocalDateTime;

public class Purchase {
    private final Integer id;
    private final Integer playerId;
    private final Integer gameId;
    private final LocalDateTime purchaseDate;
    private final double pricePaid;

    public Purchase(Integer id, Integer playerId, Integer gameId, LocalDateTime purchaseDate, double pricePaid) {
        this.id = id;
        this.playerId = playerId;
        this.gameId = gameId;
        this.purchaseDate = purchaseDate;
        this.pricePaid = pricePaid;
    }

    public Integer getId() { return id; }
    public Integer getPlayerId() { return playerId; }
    public Integer getGameId() { return gameId; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public double getPricePaid() { return pricePaid; }
}