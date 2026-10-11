package main.java.presentation;

import main.java.application.CatalogService;
import main.java.application.ReviewService;
import main.java.domain.Admin;
import main.java.domain.Game;
import main.java.domain.Review;
import main.java.persistence.ReviewDAO;

import java.util.List;
import java.util.Scanner;

public class AdminController {
    private final Admin loggedAdmin;
    private final CatalogService catalogService;
    private final ReviewService reviewService;
    private final Scanner scanner;

    public AdminController(Admin loggedAdmin, CatalogService catalogService, ReviewService reviewService, Scanner scanner) {
        this.loggedAdmin = loggedAdmin;
        this.catalogService = catalogService;
        this.reviewService = reviewService;
        this.scanner = scanner;
    }

    public void showDashboard() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- DASHBOARD ADMIN ---");
            System.out.println("1. Valuta giochi in attesa");
            System.out.println("2. Modera giochi");
            System.out.println("3. Modera recensioni");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> reviewPendingGames();
                case "2" -> reviewGames();
                case "3" -> reviewReviews();
                case "0" -> exit = true;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    private void reviewPendingGames() {
        List<Game> pending = catalogService.getPendingCatalog(loggedAdmin);
        if (pending.isEmpty()) {
            System.out.println("Nessun gioco in attesa di approvazione.");
            return;
        }

        pending.forEach(g -> System.out.printf("[%d] %s | €%.2f | %d%% | %s |Stato: %s\n", g.getId(), g.getTitle(), g.getPrice(), g.getDiscount() , g.getDescription() ,g.getState()));

        System.out.print("\nInserisci l'ID del gioco da valutare (0 per uscire): ");
        int gameId = Integer.parseInt(scanner.nextLine());
        if (gameId == 0) return;

        System.out.println("1. Approva\n2. Rifiuta\nScelta:");
        String decision = scanner.nextLine();

        if ("1".equals(decision)) {
            if (catalogService.approveGame(gameId, loggedAdmin)) System.out.println("Gioco approvato!");
            else System.out.println("Gioco non esistene o già approvato!");
        } else if ("2".equals(decision)) {
            if(catalogService.getApprovedCatalog().contains(catalogService.getGameById(gameId))) {
                System.out.println("Gioco non in pending!");
                return;
            }
            if( catalogService.rejectGame(gameId, loggedAdmin)) System.out.println("Gioco rifiutato!");
            else System.out.println("Gioco non esistente o già rifiutato!");
        }
    }

    private void reviewGames() {
        List<Game> games = catalogService.getApprovedCatalog();
        if (games.isEmpty()) {
            System.out.println("Nessun gioco disponibile.");
            return;
        }
        games.forEach(g -> System.out.printf("[%d] %s | €%.2f | %d%% | %s |Stato: %s\n", g.getId(), g.getTitle(), g.getPrice(), g.getDiscount() , g.getDescription() ,g.getState()));
        System.out.print("\nInserisci l'ID del gioco da bloccare (0 per uscire): ");
        int gameId = Integer.parseInt(scanner.nextLine());
        if (gameId == 0) return;
        catalogService.rejectGame(gameId, loggedAdmin);
    }

    private void reviewReviews() {
        List<Game> games = catalogService.getApprovedCatalog();
        if (games.isEmpty()) {
            System.out.println("Nessun gioco disponibile.");
            return;
        }
        games.forEach(g -> System.out.printf("[%d] %s | €%.2f | %d%% | %s |Stato: %s\n", g.getId(), g.getTitle(), g.getPrice(), g.getDiscount() , g.getDescription() ,g.getState()));
        System.out.print("\nInserisci l'ID del gioco di cui moderare le recensioni (0 per uscire): ");
        int gameId = Integer.parseInt(scanner.nextLine());
        if (gameId == 0) return;
        Game game = catalogService.getGameById(gameId);
        if (game == null) return;
        List<Review> reviews = reviewService.getGameReviews(gameId);
        if  (reviews.isEmpty()) return;
        reviews.forEach(r -> System.out.printf("[%d] %d | %s | %s", r.getId(), r.getVote() ,r.getText(), r.getReviewDate()));
        System.out.print("\nInserisci l'ID della recensione da eliminare (0 per uscire): ");
        int reviewId = Integer.parseInt(scanner.nextLine());
        if (reviewId == 0) return;
        if(reviewService.rejectReview(reviewId, loggedAdmin)) System.out.println("Recensione elimanata!");
        else System.out.println("Recensione non presente!");
    }
}