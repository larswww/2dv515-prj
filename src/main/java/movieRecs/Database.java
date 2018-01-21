package movieRecs;

import a2.Algo;
import a2.Movie;
import a2.Score;
import a2.User;
import org.sqlite.util.StringUtils;

import java.sql.*;
import java.util.*;
import javax.xml.crypto.Data;

public class Database {
    private static Database db = new Database();
    private Connection c = null;
    private Statement stmt = null;

    private Database() {
        // connect
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:data/new-new.db");
            createTables();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");

    }

    public static Database getInstance() {
        return db;
    }


    public void createTables() throws SQLException {

        stmt = c.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS ratings " +
                "(userId INT PRIMARY KEY     NOT NULL," +
                " movieId        INT    NOT NULL, " +
                " rating         TEXT     NOT NULL, " +
                " timestamp      TEXT)";
        stmt.executeUpdate(sql);
        stmt.close();

        stmt = c.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS euclidean_distance " +
                "(movie TEXT NOT NULL," +
                "score TEXT NOT NULL," +
                "item TEXT NOT NULL);" +
                "CREATE UNIQUE INDEX IF NOT EXISTS movie_score on euclidean_distance (movie, item);";
        stmt.executeUpdate(sql);
        stmt.close();

        stmt = c.createStatement();

        stmt = c.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS pearson_correlation " +
                "(movie TEXT NOT NULL," +
                "score TEXT NOT NULL," +
                "item TEXT NOT NULL);" +
                "CREATE UNIQUE INDEX IF NOT EXISTS movie_score on pearson_correlation (movie, item);";
        stmt.executeUpdate(sql);
        stmt.close();

        stmt = c.createStatement();

//        sql = "CREATE TABLE IF NOT EXISTS algo_scores " +
//                "(euclidean_distance TEXT," +
//                "pearson_correlation TEXT," +
//                "item TEXT NOT NULL," +
//                "CONSTRAINT movie_item UNIQUE (item, movie));";
//        stmt.executeUpdate(sql);
//        stmt.close();

        stmt = c.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS users " +
                "(movie TEXT NOT NULL," +
                "score TEXT NOT NULL," +
                "item TEXT NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void buildMovieIndex(HashMap<String, Movie> movieDb) {
        PreparedStatement ps = null;

        try {
            for (String id : movieDb.keySet()) {
                ps = c.prepareStatement("INSERT OR IGNORE INTO algo_scores (movie,item) VALUES(?,?);");
                c.setAutoCommit(false);

                String movie = movieDb.get(id).title();

                for (String idi: movieDb.keySet()) {
                    String item = movieDb.get(idi).title();
                    if (movie.equals(item)) continue;
                    ps.setString(1, replaceString(movie));
                    ps.setString(2, replaceString(item));
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
                c.commit();
            }
        } catch (SQLException e) {
            if (ps != null) System.out.println(ps.toString());
            e.printStackTrace();
        }

    }

    public HashMap<String, String> checkForScores(HashMap<String, User> moviesAsUsers, Algo a) {

        /*
        Builds an index of movies not in the DB by reducing the result hashmap to a map of only those title and id pairs
        that arent found by the WHERE
         */
        HashMap<String, String> result = new HashMap<>();
        int size = moviesAsUsers.size();
        boolean gotResults = false;


        String firstTitle = null;
        for (String id : moviesAsUsers.keySet()) {
            if (firstTitle == null) firstTitle = moviesAsUsers.get(id).id();
            result.put(moviesAsUsers.get(id).id(), id);
        }

        try {
            stmt = c.createStatement();
            String sql = "SELECT movie FROM " + a.toString().toLowerCase() + " WHERE item='" + firstTitle + "';";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                gotResults = true;
                String row = rs.getString("movie");
                String unreplaced = unreplaceString(row);
                result.remove(unreplaceString(rs.getString("movie")));

            }
            if (gotResults) result.remove(firstTitle);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void insertAlgoScore(User user, Algo a) {
        PreparedStatement ps = null;

        try {
            ps = c.prepareStatement("INSERT INTO " + a.toString().toLowerCase() + " (movie, score, item) VALUES (?,?,?)");
            c.setAutoCommit(false);
            for (Score s : user.getAlgoScores(a)) {
                ps.setString(1, replaceString(user.id()));
                ps.setString(2, Double.toString(s.score()));
                ps.setString(3, replaceString(s.user().id()));
                ps.addBatch();
            }

            ps.executeBatch();
            ps.close();
            c.commit();

        } catch (SQLException e) {
            System.out.println(ps.toString());
            e.printStackTrace();
        }

    }


    public void insertScore(User user, Algo a) {
        PreparedStatement ps = null;

        try {
            ps = c.prepareStatement("UPDATE algo_scores SET " + a.toString().toLowerCase() + " = ? WHERE movie = ? AND item = ?");
            c.setAutoCommit(false);
            for (Score s : user.getAlgoScores(a)) {
                ps.setString(1, Double.toString(s.score()));
                ps.setString(2, replaceString(user.id()));
                ps.setString(3, replaceString(s.user().id()));
            }

            ps.executeUpdate();
            c.commit();

        } catch (SQLException e) {
            System.out.println(ps.toString());
            e.printStackTrace();
        }
    }

    public boolean hasAlgoScore(String movie, Algo a) {
        movie = replaceString(movie);

        try {
            stmt = c.createStatement();
            String sql = "SELECT EXISTS(SELECT 1 FROM " + a.toString().toLowerCase() + " WHERE movie='" + movie + "' LIMIT 1);";
            ResultSet rs = stmt.executeQuery(sql);
            boolean result = rs.getInt(1) == 1;
            rs.close();
            stmt.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return false;
    }

    public List<Score> getAlgoScores(String movie, Algo a, HashSet<String> rated) {
        List<Score> algoScores = new ArrayList<>();
        movie = replaceString(movie);
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = rated.iterator();
        while (it.hasNext()) sb.append("'").append(replaceString(it.next())).append("'").append(",");


        String ratedMoviesToFilter = sb.toString().substring(0, sb.toString().length() - 1);

        try {

            // SELECT movie,score FROM euclidean_distance WHERE movie='Snakes on a Plane' AND item NOT IN ('You Me and Dupree','Superman Returns', 'Snakes on a Plane') AND NOT score='0.0';
            stmt = c.createStatement();
            String sql = "SELECT score, item FROM " + a.toString().toLowerCase() + " WHERE movie='" + movie + "' AND item NOT IN (" + ratedMoviesToFilter + ") AND NOT score='0.0';";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String item = unreplaceString(rs.getString("item"));
                double score  = Double.parseDouble(rs.getString("score"));
                User u = new User(item);
                Score s = new Score(u, score, a);
                algoScores.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return algoScores;

    }

    // convert and unconvert escaped values for SQL
    private String replaceString(String s) {
        return s.replace("'", "''''").replace("\"", "\\\"" );
    }

    private String unreplaceString(String s) {
        return s.replace("''''", "'").replace("''", "'").replace("\\\"", "\"");
    }


}
