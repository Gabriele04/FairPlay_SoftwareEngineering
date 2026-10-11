package main.java.persistence;

import main.java.domain.User;
import main.java.domain.RoleEnum;

public interface UserDAO {
    User authenticate(String email, String password);
    boolean saveUser(String email, String password, RoleEnum role, String username);
    User findByEmail(String email);
    User findByUsername(String username);
    User findById(int id);
}