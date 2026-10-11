package application;

import main.java.application.CheckoutService;
import main.java.application.PaymentStrategy;
import main.java.domain.Game;
import main.java.domain.GameStateEnum;
import main.java.domain.Player;
import main.java.domain.Purchase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.java.persistence.GameDAO;
import main.java.persistence.PurchaseDAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutServiceTest {

    private GameDAO mockGameDAO;
    private PurchaseDAO mockPurchaseDAO;
    private PaymentStrategy mockPaymentStrategy;
    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        mockGameDAO = mock(GameDAO.class);
        mockPurchaseDAO = mock(PurchaseDAO.class);
        mockPaymentStrategy = mock(PaymentStrategy.class);

        checkoutService = new CheckoutService(mockGameDAO, mockPurchaseDAO);
    }

    @Test
    void testBuyGameSuccess() {
        Player player = new Player(1, "player@test.com", "Player", "hashedPassword");
        Game game = new Game(5, "Giuoco", "Descrizionone", 50.0, 10, GameStateEnum.APPROVED, 2);

        when(mockGameDAO.findById(5)).thenReturn(game);
        when(mockPaymentStrategy.pay(45.0)).thenReturn(true);
        when(mockPurchaseDAO.save(any(Purchase.class))).thenReturn(true);

        boolean outcome = checkoutService.buyGame(5, mockPaymentStrategy, player);

        assertTrue(outcome, "ERRORE: L'acquisto dovrebbe avere successo.");
        verify(mockPaymentStrategy, times(1)).pay(45.0);
        verify(mockPurchaseDAO, times(1)).save(any(Purchase.class));
    }

    @Test
    void testBuyGameFailure() {
        Player player = new Player(1, "player@test.com", "Player", "hashedPassword");
        Game game = new Game(5, "Giuoco", "Descrizionone", 50.0, 10, GameStateEnum.APPROVED, 2);

        when(mockGameDAO.findById(5)).thenReturn(game);
        when(mockPaymentStrategy.pay(50.0)).thenReturn(false);

        boolean outcome = checkoutService.buyGame(5, mockPaymentStrategy, player);

        assertFalse(outcome, "ERRORE: L'acquisto dovrebbe fallire.");
        verify(mockPurchaseDAO, never()).save(any(Purchase.class));
    }
}