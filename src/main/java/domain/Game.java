package main.java.domain;

public class Game {
    private final Integer id;
    private String title;
    private String description;
    private double price;
    private int discount;
    private GameStateEnum state;
    private final Integer publisherId;

    public Game(Integer id, String title, String description, double price, int discount, GameStateEnum state, Integer publisherId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.state = state;
        this.publisherId = publisherId;
    }

    public double getFinalPrice() {
        return price * (1.0 - (discount / 100.0));
    }

    public void approve() { this.state = GameStateEnum.APPROVED; }
    public void reject() { this.state = GameStateEnum.REJECTED; }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }
    public GameStateEnum getState() { return state; }
    public Integer getPublisherId() { return publisherId; }
}