package main.java.domain;
import java.time.LocalDateTime;

public class Review {
    private final Integer id;
    private final Integer playerId;
    private final Integer gameId;
    private int vote;
    private String text;
    private final LocalDateTime reviewDate;

    public Review(Integer id, Integer playerId, Integer gameId, int vote, String text, LocalDateTime reviewDate) {
        this.id = id;
        this.playerId = playerId;
        this.gameId = gameId;
        this.vote = vote;
        this.text = text;
        this.reviewDate = reviewDate;
    }

    public boolean isValid() {
        return vote >= 1 && vote <= 5 && text != null && !text.isBlank();
    }

    public Integer getId() { return id; }
    public Integer getPlayerId() { return playerId; }
    public Integer getGameId() { return gameId; }
    public int getVote() { return vote; }
    public void setVote(int vote) {this.vote = vote;}
    public String getText() { return text; }
    public void setText(String text) {this.text = text;}
    public LocalDateTime getReviewDate() { return reviewDate; }
}