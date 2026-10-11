package application;


import main.java.application.CatalogService;
import main.java.domain.*;
import main.java.persistence.GameDAO;
import main.java.persistence.PurchaseDAO;
import main.java.persistence.ReviewDAO;
import main.java.persistence.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogServiceTest {

    private GameDAO mockGameDAO;
    private UserDAO mockUserDAO;
    private ReviewDAO mockReviewDAO;
    private PurchaseDAO mockPurchaseDAO;
    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        mockGameDAO = mock(GameDAO.class);
        mockUserDAO = mock(UserDAO.class);
        mockReviewDAO = mock(ReviewDAO.class);

        catalogService = new CatalogService(mockGameDAO, mockUserDAO, mockReviewDAO, mockPurchaseDAO);
    }

    @Test
    void testApproveGameSuccess() {
        Admin admin = new Admin(1, "admin@test.com", "password");
        Game pendingGame = new Game(5, "Giuco", "Descrizionone", 50, 10, GameStateEnum.PENDING, 2);

        when(mockGameDAO.findById(5)).thenReturn(pendingGame);
        when(mockGameDAO.updateStatus(5, GameStateEnum.APPROVED)).thenReturn(true);

        boolean result = catalogService.approveGame(5, admin);

        assertTrue(result, "ERRORE: L'admin deve poter accettare i giochi");
        verify(mockGameDAO, times(1)).updateStatus(5, GameStateEnum.APPROVED);
    }

    @Test
    void testApproveGameFailure() {
        Player player = new Player(1, "player@test.com", "Player", "password");
        Game pendingGame = new Game(5, "RPG Adventure", "Desc", 50, 10, GameStateEnum.PENDING, 2);

        when(mockGameDAO.findById(5)).thenReturn(pendingGame);

        boolean result = catalogService.approveGame(5, player);

        assertFalse(result, "ERRORE: Player non deve poter accettare giochi");
        verify(mockGameDAO, never()).updateStatus(anyInt(), any(GameStateEnum.class));
    }

    @Test
    void testSetDiscountSuccess() {
        Publisher publisher = new Publisher(2, "pub@test.com", "Ubi", "password");
        Game game = new Game(5, "Space Shooter", "Desc", 50, 10, GameStateEnum.APPROVED, 2);

        when(mockGameDAO.findById(5)).thenReturn(game);
        when(mockGameDAO.updateDiscount(5, 20)).thenReturn(true);

        boolean result = catalogService.setDiscount(5, 20, publisher);

        assertTrue(result, "ERRORE: Lo sconto dovrebbe essere applicato");
        verify(mockGameDAO, times(1)).updateDiscount(5, 20);
    }
}