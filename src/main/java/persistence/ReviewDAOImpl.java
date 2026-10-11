package main.java.persistence;

import main.java.domain.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {

    private Review mapResultSetToReview(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int playerId = resultSet.getInt("id_player");
        int gameId = resultSet.getInt("id_game");
        int vote = resultSet.getInt("vote");
        String text = resultSet.getString("text");
        Timestamp timestamp = resultSet.getTimestamp("review_date");
        LocalDateTime reviewDate = (timestamp != null) ? timestamp.toLocalDateTime() : null;

        return new Review(id, playerId, gameId, vote, text, reviewDate);
    }

    @Override
    public boolean save(Review review) {
        String query = "INSERT INTO reviews (id_player, id_game, vote, text) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, review.getPlayerId());
            statement.setInt(2, review.getGameId());
            statement.setInt(3, review.getVote());
            statement.setString(4, review.getText());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Review findById(int id) {
        String query = "SELECT id, id_player, id_game, vote, text, review_date FROM reviews WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToReview(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Review> findByGameId(int gameId) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT id, id_player, id_game, vote, text, review_date FROM reviews WHERE id_game = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, gameId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reviews.add(mapResultSetToReview(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public List<Review> findByPlayerId(int playerId) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT id, id_player, id_game, vote, text, review_date FROM reviews WHERE id_player = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reviews.add(mapResultSetToReview(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public boolean hasPlayerReviewedGame(int playerId, int gameId) {
        String query = "SELECT 1 FROM reviews WHERE id_player = ? AND id_game = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, playerId);
            statement.setInt(2, gameId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rejectReview(int reviewId) {
        String query = "Delete from reviews WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, reviewId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}