package main.java.presentation;

import main.java.application.AuthenticationService;
import main.java.domain.User;

import java.util.Scanner;

public class AuthController {
    private final AuthenticationService authService;
    private final Scanner scanner;

    public AuthController(AuthenticationService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    public User start() {
        while (true) {
            System.out.println("\n=== BENVENUTO NEL DIGITAL STORE ===");
            System.out.println("1. Login");
            System.out.println("2. Registrazione");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    User user = handleLogin();
                    if (user != null) return user;
                }
                case "2" -> handleRegistration();
                case "0" -> {
                    return null;
                }
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    private User handleLogin() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        if (authService.login(email, pass)) {
            System.out.println("Login effettuato!");
            return authService.getCurrentUser();
        }
        System.out.println("Credenziali errate.");
        return null;
    }

    private void handleRegistration() {
        System.out.println("\n1. Registrati come Player\n2. Registrati come Publisher");
        System.out.print("Scelta: ");
        String type = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();

        boolean success = false;
        if ("1".equals(type)) {
            success = authService.registerPlayer(email, pass, username);
        } else if ("2".equals(type)) {
            success = authService.registerPublisher(email, pass, username);
        }

        if (success) {
            System.out.println("Registrazione completata! Ora puoi effettuare il login.");
        } else {
            System.out.println("Errore durante la registrazione (Email o Username gia' in uso).");
        }
    }
}