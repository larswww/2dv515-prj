package a2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MovieRatings {
    private static MovieRatings movieRatings = new MovieRatings();
    private HashMap<String, Movie> movieDb = new HashMap<>();
    private HashMap<String, User> userDb = new HashMap<>();
    private HashMap<String, User> itemUserDb = new HashMap<>();
    private HashMap<String, String> idTitle = new HashMap<>();


    public HashMap<String, User> getItemUserDb() {
        return itemUserDb;
    }


    private MovieRatings() {

    }

    public static MovieRatings getInstance() {
        return movieRatings;
    }

    public HashMap<String, Movie> movieDb() { return movieDb; }

    public HashMap<String, User> userDb() { return userDb; }

    public void setItemUserDb(HashMap<String, User> udb) { this.itemUserDb = udb; }


    public void addUser(User u) {
        if (userDb.get(u.id()) != null) return;
        userDb.put(u.id(), u);
    }

    public User getUser(String id) {
        return userDb.get(id);
    }

    public User itemUser(String id) {
        return itemUserDb.get(id);
    }

    public void addMovie(Movie m) {
        if (movieDb.get(m.id()) != null) return;
        idTitle.put(m.title(), m.id());

        movieDb.put(m.id(), m);
    }

    public String getMovieId(String title) { return idTitle.get(title); }

    public Movie getMovie(String id) {
        return movieDb.get(id);
    }

    public void setMovieDb(HashMap<String,Movie> movieDb) {
        this.movieDb = movieDb;

    }
}
