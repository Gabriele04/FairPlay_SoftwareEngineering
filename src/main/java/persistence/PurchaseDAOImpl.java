package main.java.persistence;

import main.java.domain.Purchase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAOImpl implements PurchaseDAO {

    private Purchase mapResultSetToPurchase(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int playerId = resultSet.getInt("id_player");
        int gameId = resultSet.getInt("id_game");
        Timestamp timestamp = resultSet.getTimestamp("purchase_date");
        LocalDateTime purchaseDate = (timestamp != null) ? timestamp.toLocalDateTime() : null;
        double pricePaid = resultSet.getDouble("price_paid");

        return new Purchase(id, playerId, gameId, purchaseDate, pricePaid);
    }

    @Override
    public boolean save(Purchase purchase) {
        String query = "INSERT INTO purchases (id_player, id_game, price_paid) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, purchase.getPlayerId());
            statement.setInt(2, purchase.getGameId());
            statement.setDouble(3, purchase.getPricePaid());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Purchase findById(int id) {
        String query = "SELECT id, id_player, id_game, purchase_date, price_paid FROM purchases WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToPurchase(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Purchase> findByGameId(int gameId){
        List<Purchase> purchases = new ArrayList<>();
        String query = "SELECT id, id_player, id_game, purchase_date, price_paid FROM purchases WHERE id_game = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, gameId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    purchases.add(mapResultSetToPurchase(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }

    @Override
    public List<Purchase> findByPlayerId(int playerId) {
        List<Purchase> purchases = new ArrayList<>();
        String query = "SELECT id, id_player, id_game, purchase_date, price_paid FROM purchases WHERE id_player = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    purchases.add(mapResultSetToPurchase(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }

    @Override
    public boolean hasPlayerPurchasedGame(int playerId, int gameId) {
        String query = "SELECT 1 FROM purchases WHERE id_player = ? AND id_game = ?";

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
}