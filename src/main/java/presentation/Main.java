package main.java.presentation;

import main.java.application.AuthenticationService;
import main.java.application.CatalogService;
import main.java.application.CheckoutService;
import main.java.application.ReviewService;
import main.java.domain.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PasswordHasher passwordHasher = new PasswordHasher();
        Scanner scanner = new Scanner(System.in);

        AuthenticationService authService = new AuthenticationService(passwordHasher);
        CatalogService catalogService = new CatalogService();
        CheckoutService checkoutService = new CheckoutService();
        ReviewService reviewService = new ReviewService();

        AuthController authController = new AuthController(authService, scanner);

        label:
        while (true) {
            User user = authController.start();
            switch (user) {
                case null:
                    System.out.println("Arrivederci!");
                    break label;
                case Player player:
                    PlayerController playerController = new PlayerController(player, catalogService, checkoutService, reviewService, scanner);
                    playerController.showDashboard();
                    break;
                case Publisher publisher:
                    PublisherController publisherController = new PublisherController(publisher, catalogService, reviewService, scanner);
                    publisherController.showDashboard();
                    break;
                case Admin admin:
                    AdminController adminController = new AdminController(admin, catalogService, reviewService, scanner);
                    adminController.showDashboard();
                    break;
                default:
                    break;
            }

            authService.logout();
        }
    }
}