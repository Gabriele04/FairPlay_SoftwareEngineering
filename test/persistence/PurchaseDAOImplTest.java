package persistence;


import main.java.domain.*;
import main.java.persistence.DatabaseConnection;
import main.java.persistence.GameDAOImpl;
import main.java.persistence.PurchaseDAOImpl;
import main.java.persistence.UserDAOImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseDAOImplTest {

    private PurchaseDAOImpl purchaseDAO;
    private GameDAOImpl gameDAO;
    private UserDAOImpl userDAO;

    private final String testMailPlayer = "player@test.com";
    private final String testMailPublisher = "publisher@test.com";
    private final String testTitle = "Giucone";

    private int playerId;
    private int gameId;

    @BeforeEach
    void setUp() {
        purchaseDAO = new PurchaseDAOImpl();
        gameDAO = new GameDAOImpl();
        userDAO = new UserDAOImpl();

        cleanUpTestData();

        userDAO.saveUser(testMailPublisher, PasswordHasher.hashMD5("passwordona"), RoleEnum.PUBLISHER, "Ubi");
        userDAO.saveUser(testMailPlayer, PasswordHasher.hashMD5("passwordona"), RoleEnum.PLAYER, "Playerone");

        User pub = userDAO.findByEmail(testMailPublisher);
        User player = userDAO.findByEmail(testMailPlayer);

        playerId = player.getId();

        Game game = new Game(null, testTitle, "Descrizionona", 50, 10, GameStateEnum.APPROVED, pub.getId());
        gameDAO.save(game);

        Game savedGame = gameDAO.findByPublisherId(pub.getId()).getFirst();
        gameId = savedGame.getId();
    }

    @AfterEach
    void tearDown() {
        cleanUpTestData();
    }

    private void cleanUpTestData() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String deletePurchases = "DELETE FROM purchases WHERE id_player IN (SELECT id FROM users WHERE email = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(deletePurchases)) {
                stmt.setString(1, testMailPlayer);
                stmt.executeUpdate();
            }

            String deleteGames = "DELETE FROM games WHERE title = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteGames)) {
                stmt.setString(1, testTitle);
                stmt.executeUpdate();
            }

            String deleteUsers = "DELETE FROM users WHERE email IN (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(deleteUsers)) {
                stmt.setString(1, testMailPlayer);
                stmt.setString(2, testMailPublisher);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSaveAndFindPurchase() {
        Purchase purchase = new Purchase(null, playerId, gameId, LocalDateTime.now(), 45);

        boolean isSaved = purchaseDAO.save(purchase);
        assertTrue(isSaved, "ERRORE: L'acquisto dovrebbe essere stato salvato.");

        boolean hasPurchased = purchaseDAO.hasPlayerPurchasedGame(playerId, gameId);
        assertTrue(hasPurchased, "ERRORE: L'utente dovrebbe essere in possesso del gioco.");

        List<Purchase> purchases = purchaseDAO.findByPlayerId(playerId);
        assertFalse(purchases.isEmpty());
        assertEquals(45, purchases.getFirst().getPricePaid(), 0.001);
    }
}