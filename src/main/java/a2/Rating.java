package a2;

import java.util.HashMap;

public class Rating {
    private Movie movie;
    private User user;
    double score;
    private HashMap<Algo, Double> ratingTypes = new HashMap<>();

    public Rating(Movie m, User u, double score) {
        this.movie = m;
        this.user = u;
        if (score != -1) this.score = score;
    }

    public Movie movie() { return movie; }

    public User user() { return user; }

    public void setScore(Algo a, double s) {
        ratingTypes.put(a, s);
    }

    public double getScore(Algo a) {
        return ratingTypes.get(a);
    }

    boolean matches(Rating b) {
        return this.movie.equals(b.movie);
    }

    @Override
    public String toString() {
        return user.toString();
    }

    public double score() {
        return score;
    }
}
