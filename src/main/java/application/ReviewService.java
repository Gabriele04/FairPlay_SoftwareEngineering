package main.java.application;

import main.java.domain.*;
import main.java.persistence.PurchaseDAO;
import main.java.persistence.PurchaseDAOImpl;
import main.java.persistence.ReviewDAO;
import main.java.persistence.ReviewDAOImpl;

import java.util.Collections;
import java.util.List;

public class ReviewService {
    private final ReviewDAO reviewDAO;
    private final PurchaseDAO purchaseDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAOImpl();
        this.purchaseDAO = new PurchaseDAOImpl();
    }

    public ReviewService(ReviewDAO reviewDAO, PurchaseDAO purchaseDAO) {
        this.reviewDAO = reviewDAO;
        this.purchaseDAO = purchaseDAO;
    }

    public String addReview(int gameId, int vote, String text, User currentUser) {
        if (!(currentUser instanceof Player)) {
            return "Errore";
        }

        if (vote < 1 || vote > 5) {
            return  "Errore, voto non valido, deve essere compreso tra 1 e 5.";
        }

        if (!purchaseDAO.hasPlayerPurchasedGame(currentUser.getId(), gameId)) {
            return "Errore, non possiedi il gioco.";
        }

        if (reviewDAO.hasPlayerReviewedGame(currentUser.getId(), gameId)) {
            return "Hai già recensito questo gioco";
        }

        Review review = new Review(null, currentUser.getId(), gameId, vote, text, null);
        return reviewDAO.save(review) ? "Recensione salvata!" : "Errore nel salvataggio della recensione";
    }

    public List<Review> getGameReviews(int gameId) {
        return reviewDAO.findByGameId(gameId);
    }

    public List<Review> getPlayerReviews(User currentUser) {
        if (!(currentUser instanceof Player)) {
            return Collections.emptyList();
        }
        return reviewDAO.findByPlayerId(currentUser.getId());
    }

    public boolean rejectReview(int reviewId, User currentUser) {
        if (!(currentUser instanceof Publisher) && !(currentUser instanceof Admin)) {
            return false;
        }
        return reviewDAO.rejectReview(reviewId);
    }
}