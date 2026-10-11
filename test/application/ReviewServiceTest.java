package application;

import main.java.application.ReviewService;
import main.java.domain.Admin;
import main.java.domain.Player;
import main.java.domain.Review;
import main.java.persistence.PurchaseDAO;
import main.java.persistence.ReviewDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    private ReviewDAO mockReviewDAO;
    private PurchaseDAO mockPurchaseDAO;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        mockReviewDAO = mock(ReviewDAO.class);
        mockPurchaseDAO = mock(PurchaseDAO.class);
        reviewService = new ReviewService(mockReviewDAO, mockPurchaseDAO);
    }

    @Test
    void testAddReviewSuccess() {
        Player player = new Player(1, "player@test.com", "Player", "hashedPassword");
        int gameId = 10;

        when(mockPurchaseDAO.hasPlayerPurchasedGame(1, gameId)).thenReturn(true);
        when(mockReviewDAO.hasPlayerReviewedGame(1, gameId)).thenReturn(false);
        when(mockReviewDAO.save(any(Review.class))).thenReturn(true);

        String result = reviewService.addReview(gameId, 1, "Orribileeee!", player);

        assertEquals("Recensione salvata!", result);
        verify(mockReviewDAO, times(1)).save(any(Review.class));
    }

    @Test
    void testAddReviewFailure() {
        Player player = new Player(1, "player@test.com", "Gamer1", "hashedPassword");
        int gameId = 10;

        when(mockPurchaseDAO.hasPlayerPurchasedGame(1, gameId)).thenReturn(false);

        String result = reviewService.addReview(gameId, 5, "Non lo manco comprato...", player);

        assertEquals("Errore, non possiedi il gioco.", result);
        verify(mockReviewDAO, never()).save(any(Review.class));
    }

    @Test
    void testRejectReview() {
        Admin admin = new Admin(99, "admin@test.com", "hashedPassword");
        int reviewId = 5;

        when(mockReviewDAO.rejectReview(reviewId)).thenReturn(true);

        boolean success = reviewService.rejectReview(reviewId, admin);

        assertTrue(success, "ERRORE: L'admin deve poter eliminare recensioni");
        verify(mockReviewDAO, times(1)).rejectReview(reviewId);
    }
}