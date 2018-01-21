package a2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Movie {
    private String title;
    private HashMap<Rating, List<User>> ratings = new HashMap<>();
    private String genres;
    private String id;

    public Movie(String title, String id, String genres) {
        this.title = title;
        this.id = id;
        this.genres = genres;
    }

    public void linkRatings(Rating r, User u) {
        List<User> lu = ratings.get(u);
        u.addRating(r);

        if (lu == null) {
            List<User> ratingsUserList = new ArrayList<>();
            ratingsUserList.add(u);
            ratings.put(r, ratingsUserList);
        } else {
            if (!lu.contains(u)) lu.add(u); // todo this means a user rating can't be updated so fix that here later if needed
        }
    }

    public HashMap<Rating, List<User>> ratings() { return ratings; }

    public String title() { return this.title; }

    public String id() { return this.id; }

    public String genres() { return this.genres; }

    @Override
    public String toString() { return this.title; }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Movie)) return false;
        Movie m = (Movie) o;
        return this.title.equals(m.title);

    }
}
