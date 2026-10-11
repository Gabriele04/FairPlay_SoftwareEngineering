package main.java.presentation;

import main.java.application.CatalogService;
import main.java.application.CheckoutService;
import main.java.application.ReviewService;
import main.java.application.CreditCardPayment;
import main.java.application.PayPalPayment;
import main.java.application.PaymentStrategy;
import main.java.domain.Game;
import main.java.domain.GameStateEnum;
import main.java.domain.Player;
import main.java.domain.Review;

import java.util.List;
import java.util.Scanner;

public class PlayerController {
    private final Player loggedPlayer;
    private final CatalogService catalogService;
    private final CheckoutService checkoutService;
    private final ReviewService reviewService;
    private final Scanner scanner;

    public PlayerController(Player loggedPlayer, CatalogService catalogService, CheckoutService checkoutService, ReviewService reviewService, Scanner scanner) {
        this.loggedPlayer = loggedPlayer;
        this.catalogService = catalogService;
        this.checkoutService = checkoutService;
        this.reviewService = reviewService;
        this.scanner = scanner;
    }

    public void showDashboard() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- DASHBOARD PLAYER (" + loggedPlayer.getUsername() + ") ---");
            System.out.println("1. Sfoglia Catalogo");
            System.out.println("2. Lascia una Recensione");
            System.out.println("3. I Miei Acquisti");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> browseCatalog();
                case "2" -> leaveReview();
                case "3" -> checkoutService.getPlayerPurchases(loggedPlayer)
                        .forEach(p -> System.out.printf("Acquisto #%d | %s | €%.2f\n", p.getId(), catalogService.getGameById(p.getGameId()).getTitle(), p.getPricePaid()));
                case "0" -> exit = true;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    private void browseCatalog() {
        List<Game> games = catalogService.getApprovedCatalog();
        if (games.isEmpty()) {
            System.out.println("Nessun gioco disponibile.");
            return;
        }

        games.forEach(g -> System.out.printf("[%d] %s - €%.2f - %d%% - %.2f%%\n", g.getId(), g.getTitle(), g.getPrice(), g.getDiscount(), g.getFinalPrice()));
        System.out.print("\nInserisci l'ID del gioco da visionare (oppure 0 per tornare indietro): ");
        int gameId = Integer.parseInt(scanner.nextLine());
        if (gameId == 0)  return;
        Game selectedGame = catalogService.getGameById(gameId);
        if (selectedGame.getState() != GameStateEnum.APPROVED) {
            System.out.println("Gioco non trovato!");
            return;
        }

        System.out.println("1. Mostra recensioni\n2. Acquista\n0. Torna indietro");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": {
                List<Review> reviews = reviewService.getGameReviews(gameId);
                if (reviews.isEmpty()) {
                    System.out.println("Nessuna recensione disponibile!");
                    break;
                }
                reviews.forEach(r -> System.out.printf(" %d | %s | %s", r.getVote(), r.getText(), r.getReviewDate()));
                break;
            }
            case "2":{
                startCheckout(selectedGame);
            }
        }
    }

    private void startCheckout(Game game) {
        System.out.println("\nScegli il metodo di pagamento per " + game.getTitle() + " (€" + game.getPrice() + "):");
        System.out.println("1. Carta di Credito\n2. PayPal");
        System.out.print("Scelta: ");
        String method = scanner.nextLine();

        PaymentStrategy strategy = null;
        if ("1".equals(method)) {
            System.out.print("Numero Carta (16 cifre): ");
            String card = scanner.nextLine();
            System.out.print("CVV: ");
            String cvv = scanner.nextLine();
            strategy = new CreditCardPayment(card, cvv);
        } else if ("2".equals(method)) {
            System.out.print("Email PayPal: ");
            String email = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();
            strategy = new PayPalPayment(email, pass);
        }

        if (strategy != null && checkoutService.buyGame(game.getId(), strategy, loggedPlayer)) {
            System.out.println("Acquisto completato con successo! (" + game.getFinalPrice() + ")");
        } else {
            System.out.println("Errore: pagamento fallito.");
        }
    }

    private void leaveReview() {
        List<Game> games = catalogService.getPurchasedGames(loggedPlayer);
        if (games.isEmpty()) {
            System.out.println("Nessun gioco disponibile!");
            return;
        }
        games.forEach(g -> System.out.printf("[%d] %s - €%.2f\n", g.getId(), g.getTitle(), g.getPrice()));
        System.out.print("\nInserisci l'ID del gioco da recensire (oppure 0 per tornare indietro): ");
        int gameId = Integer.parseInt(scanner.nextLine());
        if (gameId == 0)  return;
        System.out.print("Voto (1-5): ");
        int vote = Integer.parseInt(scanner.nextLine());
        System.out.print("Commento: ");
        String text = scanner.nextLine();

        System.out.println(reviewService.addReview(gameId, vote, text, loggedPlayer));
    }
}
