package persistence;


import main.java.domain.PasswordHasher;
import main.java.domain.RoleEnum;
import main.java.domain.User;
import main.java.persistence.DatabaseConnection;
import main.java.persistence.UserDAOImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOImplTest {

    private UserDAOImpl userDAO;
    private final String testMail = "user@test.com";

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
        cleanUpTestData();
    }

    @AfterEach
    void tearDown() {
        cleanUpTestData();
    }

    private void cleanUpTestData() {
        String sql = "DELETE FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, testMail);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSaveAndFindUser() {
        boolean isSaved = userDAO.saveUser(testMail, PasswordHasher.hashMD5("passwordona"), RoleEnum.PLAYER, "Playerone");

        assertTrue(isSaved, "ERRORE: L'utente dovrebbe essere stato salvato.");

        User retrievedUser = userDAO.findByEmail(testMail);

        assertNotNull(retrievedUser, "ERRORE: L'utente dovrebbe essere stato trovato.");
        assertEquals(testMail, retrievedUser.getEmail());
        assertEquals(RoleEnum.PLAYER, retrievedUser.getRole());
    }

    @Test
    void testAuthenticate() {
        userDAO.saveUser(testMail, PasswordHasher.hashMD5("passwordona"), RoleEnum.PUBLISHER, "Ubi");

        User authenticatedUser = userDAO.authenticate(testMail, "passwordona");
        User failedUser = userDAO.authenticate(testMail, "passwordina");

        assertNotNull(authenticatedUser, "ERRORE: L'utente dovrebbe essere autenticato.");
        assertEquals(RoleEnum.PUBLISHER, authenticatedUser.getRole());

        assertNull(failedUser, "ERRORE: L'utente non dovrebbe essere autenticato.");
    }
}