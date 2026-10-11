package main.java.domain;

public class Player extends User {
    private String username;

    public Player(Integer id, String email, String username, String password) {
        super(id, email, RoleEnum.PLAYER, password);
        this.username = username;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}