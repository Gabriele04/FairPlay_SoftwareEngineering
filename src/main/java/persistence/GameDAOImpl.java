package main.java.persistence;

import main.java.domain.GameStateEnum;
import main.java.domain.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

public class GameDAOImpl implements GameDAO {

    private Game mapResultSetToGame(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        double price = resultSet.getDouble("price");
        int discount = resultSet.getInt("discount");
        GameStateEnum state = GameStateEnum.valueOf(resultSet.getString("state"));
        int publisherId = resultSet.getInt("id_publisher");

        return new Game(id, title, description, price, discount, state, publisherId);
    }

    @Override
    public boolean save(Game game) {
        String query = "INSERT INTO games (title, description, price, discount, state, id_publisher) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, game.getTitle());
            statement.setString(2, game.getDescription());
            statement.setDouble(3, game.getPrice());
            statement.setInt(4, game.getDiscount());
            statement.setString(5, game.getState().name());
            statement.setInt(6, game.getPublisherId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Game findById(int id) {
        String query = "SELECT id, title, description, price, discount, state, id_publisher FROM games WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToGame(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Game> findApproved() {
        List<Game> games = new ArrayList<>();
        String query = "SELECT id, title, description, price, discount, state, id_publisher FROM games WHERE state = 'APPROVED'";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                games.add(mapResultSetToGame(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    @Override
    public List<Game> findPending() {
        List<Game> games = new ArrayList<>();
        String query = "SELECT id, title, description, price, discount, state, id_publisher FROM games WHERE state = 'PENDING'";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                games.add(mapResultSetToGame(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    @Override
    public List<Game> findRejected() {
        List<Game> games = new ArrayList<>();
        String query = "SELECT id, title, description, price, discount, state, id_publisher FROM games WHERE state = 'REJECTED'";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                games.add(mapResultSetToGame(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    @Override
    public List<Game> findByPublisherId(int publisherId) {
        List<Game> games = new ArrayList<>();
        String query = "SELECT id, title, description, price, discount, state, id_publisher FROM games WHERE id_publisher = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, publisherId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    games.add(mapResultSetToGame(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    @Override
    public boolean updateStatus(int gameId, GameStateEnum state) {
        String query = "UPDATE games SET state = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, state.name());
            statement.setInt(2, gameId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Game> findByPurchased(int userId) {
        List<Game> games = new ArrayList<>();
        String query = "SELECT games.id, title, description, price, discount, state, id_publisher FROM games JOIN purchases ON games.id = purchases.id_game WHERE purchases.id_player = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    games.add(mapResultSetToGame(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return games;
    }

    @Override
    public boolean updateDiscount(int gameId, int discount) {
        String query = "UPDATE games SET discount = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, discount);
            statement.setInt(2, gameId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTitle(int gameId, String title) {
        String query = "UPDATE games SET title = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, title);
            statement.setInt(2, gameId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDescription(int gameId, String description) {
        String query = "UPDATE games SET description = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, description);
            statement.setInt(2, gameId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}