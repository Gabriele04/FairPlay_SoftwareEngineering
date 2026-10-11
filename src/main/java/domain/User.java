package main.java.domain;

public abstract class User {
    private final Integer id;
    private final String email;
    private final RoleEnum role;
    private final String password;

    public User(Integer id, String email, RoleEnum role, String password) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public RoleEnum getRole() { return role; }
}