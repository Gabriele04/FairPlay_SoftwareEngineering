package persistence;

import main.java.domain.*;
import main.java.persistence.DatabaseConnection;
import main.java.persistence.GameDAOImpl;
import main.java.persistence.ReviewDAOImpl;
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

class ReviewDAOImplTest {

    private ReviewDAOImpl reviewDAO;
    private GameDAOImpl gameDAO;
    private UserDAOImpl userDAO;

    private final String testMailPlayer = "player_review_test@store.com";
    private final String testMailPublisher = "pub_review_test@store.com";
    private final String testTitle = "Review Test Game";

    private int playerId;
    private int gameId;

    @BeforeEach
    void setUp() {
        reviewDAO = new ReviewDAOImpl();
        gameDAO = new GameDAOImpl();
        userDAO = new UserDAOImpl();

        cleanUpTestData();

        userDAO.saveUser(testMailPublisher, PasswordHasher.hashMD5("passwordona"), RoleEnum.PUBLISHER, "Ubi");
        userDAO.saveUser(testMailPlayer, PasswordHasher.hashMD5("passwordona"), RoleEnum.PLAYER, "Playerone");

        User pub = userDAO.findByEmail(testMailPublisher);
        User player = userDAO.findByEmail(testMailPlayer);

        assertNotNull(pub, "ERRORE: Il publisher dovrebbe essere stato trovato.");
        assertNotNull(player, "ERRORE: Il player dovrebbe essere stato trovato.");

        playerId = player.getId();

        Game game = new Game(null, testTitle, "Descrizionciona", 50, 10, GameStateEnum.APPROVED, pub.getId());
        gameDAO.save(game);

        List<Game> pubGames = gameDAO.findByPublisherId(pub.getId());
        assertFalse(pubGames.isEmpty(), "ERRORE: Il gioco dovrebbe essere stato salvato.");
        gameId = pubGames.getFirst().getId();
    }

    @AfterEach
    void tearDown() {
        cleanUpTestData();
    }

    private void cleanUpTestData() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String deleteReviews = "DELETE FROM reviews WHERE id_player IN (SELECT id FROM users WHERE email = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(deleteReviews)) {
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
    void testSaveAndFindReviews() {
        Review review = new Review(null, playerId, gameId, 5, "Finntastico!", LocalDateTime.now());

        boolean isSaved = reviewDAO.save(review);
        assertTrue(isSaved, "ERRORE: La recensione dovrebbe essere stata salvata.");

        boolean hasReviewed = reviewDAO.hasPlayerReviewedGame(playerId, gameId);
        assertTrue(hasReviewed, "ERRORE: Dovrebbe segnalare che lo user ha già recensito il gioco.");

        List<Review> reviews = reviewDAO.findByGameId(gameId);
        assertFalse(reviews.isEmpty(), "ERRORE: La recensione dovrebbe essere presente");
        assertEquals(5, reviews.getFirst().getVote());
        assertEquals("Finntastico!", reviews.getFirst().getText());
    }

    @Test
    void testRejectReview() {
        Review review = new Review(null, playerId, gameId, 1, "Orripilante...", LocalDateTime.now());
        reviewDAO.save(review);

        List<Review> reviews = reviewDAO.findByGameId(gameId);
        assertFalse(reviews.isEmpty());
        int reviewId = reviews.getFirst().getId();

        boolean isRejected = reviewDAO.rejectReview(reviewId);
        assertTrue(isRejected, "ERRORE: La recensione dovrebbe essere stata rejectata (non so come si dica in italiano...)"); //TODO tradurre
    }
}