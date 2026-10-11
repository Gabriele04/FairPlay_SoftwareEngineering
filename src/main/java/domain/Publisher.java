package main.java.domain;

public class Publisher extends User {
    private final String username;
    public Publisher(Integer id, String email, String username, String password) {
        super(id, email, RoleEnum.PUBLISHER, password);
        this.username = username;
    }
    public String getUsername() { return username; }
}
