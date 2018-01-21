package a2;

import java.util.*;

public class User {
    private String id;

    public ArrayList<Rating> getRatings() {
        return ratings;
    }

    ArrayList<Rating> ratings = new ArrayList<>();
    private ArrayList<Movie> unseen = new ArrayList<>();
    private HashMap<Algo, List<Score>> algoScores = new HashMap<>();
    private HashMap<Algo, HashMap<Double, Movie>> recommendations = new HashMap<>();


    public User(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public void addRating(Rating rating) {
        if (!ratings.contains(rating)) {
            ratings.add(rating);
            unseen.remove(rating.movie());
        }
    }

    public void addRating(String item, double score) {
        Movie m = new Movie(item, this.id, "item-based");
        Rating r = new Rating(m, this, score);
        addRating(r);
    }

    public double getMovieRating(Movie m) {
        for (Rating r : ratings) {
            if (r.movie() == m) return r.score;

            // iterate
            for (Rating ro : m.ratings().keySet()) {
                if (r.movie().toString().equals(ro.toString())) return r.score;


            }
        }

        return -1;
    }

    public void setAlgoScore(Algo a, List<Score> s) {
        algoScores.put(a, s);
    }

    public List<Score> getAlgoScores(Algo a) {
        List<Score> as = algoScores.get(a);
        if (as != null) as.sort(Comparator.comparingDouble(Score::score).reversed());
        return as;
    }

    public void addUnseen(Movie m) {

        // check if the user already has rated the movie in which case the user has already seen it
        for (Rating r : ratings) {
            if (r.movie() == m) return;
        }

        if (!unseen.contains(m)) unseen.add(m);
    }

    public ArrayList<Movie> unseen() {
        return unseen;
    }

    public void setRecommendation(Algo a, HashMap<Double, Movie> rec) {
        recommendations.put(a, rec);
    }

    public HashMap<Double, Movie> getRecommendations(Algo a) {
        HashMap<Double, Movie> recs = recommendations.get(a);
        return recommendations.get(a);
    }

    @Override
    public String toString() {
        return id;
    }

    public String algoString(Algo a, Integer desiredRecs) {
        List<Score> scores = algoScores.get(a);
        int count = 0;
        StringBuilder result = new StringBuilder();
        for (Score s : scores) {
            if (count++ >= desiredRecs) break;
            result.append(s.user().id).append(" : ").append(s.score());
        }
        return result.toString();
    }

    public String recommendationString(Algo a, Integer desiredRecs) {
        StringBuilder result = new StringBuilder();
        result.append(a).append("\n");
        result.append(id).append("\n");

        HashMap<Double, Movie> rec = recommendations.get(a);
        Iterator<Double> scores = new TreeSet<>(rec.keySet()).descendingIterator();

        int results = 0;
        outer:
        while (scores.hasNext()) {
            double score = scores.next();
            Movie m = rec.get(score);
            if (results == desiredRecs) break outer;
            result.append(m.title()).append(",").append(m.id()).append(",").append(m.genres()).append(",").append(score).append("\n");
            results++;
        }

        return result.toString();
    }
}
