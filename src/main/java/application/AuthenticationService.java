package main.java.application;

import main.java.domain.PasswordHasher;
import main.java.domain.RoleEnum;
import main.java.domain.User;
import main.java.persistence.UserDAO;
import main.java.persistence.UserDAOImpl;

public class AuthenticationService {
    private final UserDAO userDAO;
    private final PasswordHasher passwordHasher;
    private User currentUser;

    public AuthenticationService(PasswordHasher passwordHasher) {
        this.userDAO = new UserDAOImpl();
        this.passwordHasher = passwordHasher;
    }

    public AuthenticationService(UserDAO userDAO, PasswordHasher passwordHasher) {
        this.userDAO = userDAO;
        this.passwordHasher = passwordHasher;
    }

    public boolean login(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user != null && PasswordHasher.hashMD5(password).equals(user.getPassword())) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean registerPlayer(String email, String password, String username) {
        if (userDAO.findByEmail(email) != null || userDAO.findByUsername(username) != null) {
            return false;
        }
        password = PasswordHasher.hashMD5(password);
        return userDAO.saveUser(email, password, RoleEnum.PLAYER, username);
    }

    public boolean registerPublisher(String email, String password, String username) {
        if (userDAO.findByEmail(email) != null || userDAO.findByUsername(username) != null) {
            return false;
        }
        return userDAO.saveUser(email, password, RoleEnum.PUBLISHER, username);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean registerAdmin(String email, String password) {
        if (currentUser == null || !(currentUser instanceof main.java.domain.Admin)) {
            return false;
        }
        if (userDAO.findByEmail(email) != null) {
            return false;
        }
        return userDAO.saveUser(email, password, RoleEnum.ADMIN, null);
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}