package a2;

public class SeedScript {
    private MovieRatings mr;
    private String[] users = {"Lisa", "Gene", "Claudia", "Mick", "Jack", "Toby"};
    private String[] movies = {"Lady in the Water", "Snakes on a Plane", "Just My Luck", "Superman Returns", "You, Me and Dupree", "The Night Listener"};
    private double[] lisa = {2.5, 3.5, 3.0, 3.5, 2.5, 3.0};
    private double[] gene = {3.0, 3.5, 1.5, 5.0, 3.5, 3.0};
    //private double[] mike = {2.5, 3.0, -1, 3.5, -1, 4.0}; removed mike from users array and userScores array
    private double[] claudia = {-1, 3.5, 3.0, 4.0, 2.5, 4.5};
    private double[] mick = {3.0, 4.0, 2.0, 3.0, 2.0, 3.0};
    private double[] jack = {3.0, 4.0, -1, 5.0, 3.5, 3.0};
    private double[] toby = {-1, 4.5, -1, 4.0, 1.0, -1};
    private double[][] userScores = {lisa, gene, claudia, mick, jack, toby};
    //todo how to handle not yet rated?

    public SeedScript(MovieRatings mr) {
        this.mr = mr;

    }

    public void lectureData() {
        int y = 0;

        for (String title: movies) {
            Movie m = new Movie(title, "1", "genres"); //hardcoded last 2 params after changes for movieLens dataset
            mr.addMovie(m);
        }

        for (String id: users) {
            User u = new User(id);
            mr.addUser(u);

            for (int i = 0; i < movies.length; i++) {
                Movie m = mr.getMovie(movies[i]);
                if (userScores[y][i] != -1) {
                    Rating r = new Rating(m, u, userScores[y][i]);
                    m.linkRatings(r, u);
                }
            }

            y++;
        }
    }
}
