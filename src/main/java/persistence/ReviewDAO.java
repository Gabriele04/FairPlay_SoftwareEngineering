package main.java.persistence;

import main.java.domain.Review;

import java.util.List;

public interface ReviewDAO {
    boolean save(Review review);
    Review findById(int id);
    List<Review> findByGameId(int gameId);
    List<Review> findByPlayerId(int playerId);
    boolean hasPlayerReviewedGame(int playerId, int gameId);
    boolean rejectReview(int reviewId);
}