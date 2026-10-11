package main.java.domain;

public class Admin extends User {
    public Admin(Integer id, String email, String password) {
        super(id, email, RoleEnum.ADMIN, password);
    }
}
