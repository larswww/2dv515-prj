package a2;

public class Score {
    User user;
    private Double score;
    Enum<Algo> type;

    public Score(User user, Double score, Enum<Algo> type) {
        this.user = user;
        this.score = score;
        this.type = type;
    }

    public double score() { return score; }
    public User user() { return user; }
    public Enum<Algo> type() { return type; }
}
