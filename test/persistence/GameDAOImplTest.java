package persistence;

import main.java.domain.*;
import main.java.persistence.DatabaseConnection;
import main.java.persistence.GameDAOImpl;
import main.java.persistence.UserDAOImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOImplTest {

    private GameDAOImpl gameDAO;
    private UserDAOImpl userDAO;

    private final String testMail = "publisher@test.com";
    private final String testTitle = "Giucone";
    private int testPublisherId;

    @BeforeEach
    void setUp() {
        gameDAO = new GameDAOImpl();
        userDAO = new UserDAOImpl();

        cleanUpTestData();

        userDAO.saveUser(testMail, PasswordHasher.hashMD5("passwordona"), RoleEnum.PUBLISHER, "Ubi");
        User publisher = userDAO.findByEmail(testMail);
        testPublisherId = publisher.getId();
    }

    @AfterEach
    void tearDown() {
        cleanUpTestData();
    }

    private void cleanUpTestData() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String deleteGames = "DELETE FROM games WHERE title = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteGames)) {
                stmt.setString(1, testTitle);
                stmt.executeUpdate();
            }

            String deleteUser = "DELETE FROM users WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteUser)) {
                stmt.setString(1, testMail);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSaveAndFindApprovedGame() {
        Game game = new Game(null, testTitle, "Descrizionciona", 50, 10, GameStateEnum.PENDING, testPublisherId);

        boolean isSaved = gameDAO.save(game);
        assertTrue(isSaved, "ERRORE: Il gioco dovrebbe essere stato salvato.");

        List<Game> pubGames = gameDAO.findByPublisherId(testPublisherId);
        assertFalse(pubGames.isEmpty(), "ERRORE: Il publisher dovrebbe avere almeno un gioco");
        Game retrievedGame = pubGames.getFirst();

        boolean isUpdated = gameDAO.updateStatus(retrievedGame.getId(), GameStateEnum.APPROVED);
        assertTrue(isUpdated, "ERRORE: L'aggiornamento di stato doveva essere stato fatto.");

        List<Game> approvedCatalog = gameDAO.findApproved();
        boolean foundInApproved = approvedCatalog.stream()
                .anyMatch(g -> g.getId().equals(retrievedGame.getId()));

        assertTrue(foundInApproved, "ERRORE: Il gioco dovrebbe essere presente nel catalogo (i giochi approvati).");
    }

    @Test
    void testUpdateDiscount() {
        Game newGame = new Game(null, testTitle, "Descrizionciona", 50, 10, GameStateEnum.APPROVED, testPublisherId);
        gameDAO.save(newGame);

        Game savedGame = gameDAO.findByPublisherId(testPublisherId).getFirst();

        boolean updatedDiscount = gameDAO.updateDiscount(savedGame.getId(), 20);
        assertTrue(updatedDiscount, "ERRORE: Lo sconto doveva essere aggiornato.");

        Game updatedGame = gameDAO.findById(savedGame.getId());
        assertEquals(20, updatedGame.getDiscount());
        assertEquals(40, updatedGame.getFinalPrice(), 0.001, "ERRORE: Il prezzo finale non torna.");
    }
}