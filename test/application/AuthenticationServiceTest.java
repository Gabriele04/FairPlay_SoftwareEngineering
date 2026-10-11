package application;

import main.java.application.AuthenticationService;
import main.java.domain.PasswordHasher;
import main.java.domain.Player;
import main.java.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.persistence.UserDAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private UserDAO mockUserDAO;
    private AuthenticationService authService;
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        mockUserDAO = mock(UserDAO.class);
        passwordHasher = mock(PasswordHasher.class);
        authService = new AuthenticationService(mockUserDAO, passwordHasher);
    }

    @Test
    void testLoginSuccess() {
        String email = "player@test.com";
        String password = "nessunaPassword";
        User mockPlayer = new Player(1, email, "PlayerTest", PasswordHasher.hashMD5(password));

        when(mockUserDAO.findByEmail(email)).thenReturn(mockPlayer);

        boolean isLogged = authService.login(email, password);

        assertTrue(isLogged, "ERRORE: Il player dovrebbe essere loggato.");
        assertEquals(mockPlayer, authService.getCurrentUser(), "ERRORE: Il player corrente deve essere quello del mock e salvato, ma non lo è.");

        verify(mockUserDAO, times(1)).findByEmail(email);
    }

    @Test
    void testLoginFailure() {
        String email = "player@test.com";
        String wrongPassword = "sesquipedale";

        when(mockUserDAO.findByEmail(email)).thenReturn(null);

        boolean isLogged = authService.login(email, wrongPassword);

        assertFalse(isLogged, "ERRORE: Il player non dovrebbe essere loggato");
        assertNull(authService.getCurrentUser(), "ERRORE: Non deve restituire nessun utente");
    }
}