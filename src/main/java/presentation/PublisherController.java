package main.java.presentation;

import main.java.application.CatalogService;
import main.java.application.ReviewService;
import main.java.domain.Game;
import main.java.domain.Publisher;
import main.java.domain.Purchase;
import main.java.domain.Review;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class PublisherController {
    private final Publisher loggedPublisher;
    private final CatalogService catalogService;
    private final ReviewService reviewService;
    private final Scanner scanner;

    public PublisherController(Publisher loggedPublisher, CatalogService catalogService, ReviewService reviewService, Scanner scanner) {
        this.loggedPublisher = loggedPublisher;
        this.catalogService = catalogService;
        this.reviewService = reviewService;
        this.scanner = scanner;
    }

    public void showDashboard() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- DASHBOARD PUBLISHER (" + loggedPublisher.getUsername() + ") ---");
            System.out.println("1. Pubblica gioco");
            System.out.println("2. Visualizza giochi");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> publishNewGame();
                case "2" -> browseGames();
                case "0" -> exit = true;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    private void publishNewGame() {
        System.out.print("Titolo: ");
        String title = scanner.nextLine();
        System.out.print("Descrizione: ");
        String desc = scanner.nextLine();
        System.out.print("Prezzo €: ");
        String input_price = scanner.nextLine();
        if (title.isEmpty() || desc.isEmpty() || input_price.isEmpty()) {
            System.out.println("Tutti i campi sono obbligatori");
            return;
        }
        double price = Double.parseDouble(input_price.replace(',', '.'));
        if (price < 0) {
            System.out.println("Il prezzo non può essere negativo.");
            return;
        };

        if (catalogService.publishGame(title, desc, price, loggedPublisher)) {
            System.out.println("Gioco pubblicato (in attesa di approvazzione)!");
        } else {
            System.out.println("Errore nella pubblicazione.");
        }
    }

    private void browseGames() {
        catalogService.getPublisherGames(loggedPublisher)
                .forEach(g -> System.out.printf("[%d] %s | €%.2f | %d%% | %s |Stato: %s\n", g.getId(), g.getTitle(), g.getPrice(), g.getDiscount() , g.getDescription() ,g.getState()));
        System.out.println("Inserisci l'ID del gioco da visionare/modificare (oppure 0 per tornare indietro)");
        int gameId = Integer.parseInt(scanner.nextLine());
        if (gameId == 0) return;
        Game selectedGame = catalogService.getGameById(gameId);
        if (selectedGame == null || !Objects.equals(selectedGame.getPublisherId(), loggedPublisher.getId())){
            System.out.println("Gioco non trovato!");
            return;
        }
        System.out.println("1. Mostra recensioni\n2. Modifica\n3. Statistiche\n0. Torna indietro");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": {
                List<Review> reviews = reviewService.getGameReviews(gameId);
                if (reviews.isEmpty()) {
                    System.out.println("Nessuna recensione disponibile!");
                    break;
                }
                reviews.forEach(r -> System.out.printf("%d | %d | %s | %s",r.getId(), r.getVote(), r.getText(), r.getReviewDate()));
                System.out.print("\nInserisci l'ID della recensione da eliminare (0 per uscire): ");
                int reviewId = Integer.parseInt(scanner.nextLine());
                if (reviewId == 0) return;
                if(reviewService.rejectReview(reviewId, loggedPublisher)) System.out.println("Recensione elimanata!");
                else System.out.println("Recensione non presente!");
                break;
            }
            case "2":{
                System.out.println("Scegli l'operazione da eseguire (0 per uscire):\n1. Modifica titolo\n2. Modifica descrizione\n3. Modifica sconto");
                int operation = Integer.parseInt(scanner.nextLine());
                switch (operation) {
                    case 1: {
                        System.out.println("Inserisci il titolo:");
                        String titolo = scanner.nextLine();
                        if(catalogService.modifyGame(gameId, loggedPublisher, 1, titolo)) System.out.println("Titolo modificato!");
                        break;
                    }
                    case 2: {
                        System.out.println("Inserisci la descrizione:");
                        String descrizione = scanner.nextLine();
                        if(catalogService.modifyGame(gameId, loggedPublisher, 2, descrizione)) System.out.println("Descrizione modificata!");
                        break;
                    }
                    case 3: {
                        System.out.println("Inserisci lo sconto:");
                        int discount = scanner.nextInt();
                        if(catalogService.modifyGame(gameId, loggedPublisher, 3, String.valueOf(discount))) System.out.println("Sconto modificato!");
                    }
                }
            }
            case "3":{
                System.out.println("Statistiche gioco:");
                List<Purchase> purchases = catalogService.getStatisticsGame(gameId, loggedPublisher);
                if (purchases.isEmpty()) {
                    System.out.println("Nessuna statistica disponibile!");
                } else {
                    purchases.forEach(p -> System.out.printf("%s | %.2f$",p.getPurchaseDate(), p.getPricePaid()));
                    System.out.printf("\nAcquisti: %d | Totale: %.2f$", purchases.size(), purchases.stream().mapToDouble(Purchase::getPricePaid).sum());
                }
            }
        }

    }
}