package movieRecs;

import a2.*;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


public class LoadDataset {
    private MovieRatings mr;
    private String dataSetPath = "/Users/mbp/Documents/Code/2dv515/Project/initial/data/";

    public LoadDataset(MovieRatings mr, String prefix) {
        this.mr = mr;
        dataSetPath += prefix;
        movies();
        users();
        transformDataSet();
    }

    public void addUserRating(String uid, String mid, Double rating) {
        User u = mr.getUser(uid);
        if (u == null) {
            u = new User(uid);
            mr.addUser(u);
        }

        Movie m = mr.getMovie(mid);
        Rating r = new Rating(m, u, rating);
        m.linkRatings(r, u);

    }

    private void loadFilesToMemory(){}

    private void movies(){
        String movieFile = dataSetPath + "movies.csv";
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(movieFile))) {
            br.readLine();

            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] movie = line.split(",");
                String genres = movie[2];
                Movie m = new Movie(movie[1], movie[0], genres);
                mr.addMovie(m);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void users(){
        String ratingsFile = dataSetPath + "ratings.csv";
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(ratingsFile))) {
            br.readLine();

            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] rating = line.split(","); // userId,movieId,rating,timestamp
                String userId = rating[0];
                String movieId = rating[1];
                double score = Double.parseDouble(rating[2]);
                //todo save timestamp if you want?
                User u = mr.getUser(userId);
                if (u == null) {
                    u = new User(userId);
                    mr.addUser(u);
                }

                Movie m = mr.getMovie(movieId);
                Rating r = new Rating(m, u, score);
                m.linkRatings(r, u);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public boolean loadItemBased() {
        boolean result = false;
        String itemScoresFile = dataSetPath + "/ITBCF_EUCLIDEAN_DISTANCE.csv";
        HashMap<String, User> itemUserDb = new HashMap<>();


        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(itemScoresFile))) {

            while ((line = br.readLine()) != null) {
                String[] scores = line.split(";");

                User u = itemUserDb.get(scores[0]);
                if (u == null) {
                    u = new User(scores[0]);
                    itemUserDb.put(scores[0], u);
                }

                List<Score> algoScores = u.getAlgoScores(Algo.EUCLIDEAN_DISTANCE);
                if (algoScores == null) {
                    algoScores = new ArrayList<Score>();
                }
                Score s = new Score(new User(scores[2]), Double.parseDouble(scores[1]), Algo.EUCLIDEAN_DISTANCE);
                algoScores.add(s);
                u.setAlgoScore(Algo.EUCLIDEAN_DISTANCE, algoScores);
            }
            mr.setItemUserDb(itemUserDb);
            result = true;
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }

    private void ratings(){}

    private void transformDataSet() {
        HashMap<String, User> n_recs = new HashMap<>();
        HashMap<String, Movie> n_movies = new HashMap<>();

        Iterator<User> it = mr.userDb().values().iterator();

        while (it.hasNext()) {
            User usr = it.next();

            for (Rating rank : usr.getRatings()) {
                String item = rank.movie().title();
                String uid = rank.user().id();
                double score = rank.score();

                if (!n_recs.containsKey(item)) n_recs.put(item, new User(item));
                if (!n_movies.containsKey(rank.user().id())) n_movies.put(uid, new Movie(uid, uid, "reversed"));

                User n_usr = n_recs.get(item);
                Movie n_movie = n_movies.get(uid);
                Rating r = new Rating(n_movie, n_usr, score);
                rank.movie().linkRatings(r, n_usr);
            }

        }

        mr.setItemUserDb(n_recs);
    }

    public void saveItemData(HashMap<String, User> itemUserDb) {
        StringBuilder result = new StringBuilder();

        for (String key : itemUserDb.keySet()) {
            User u = itemUserDb.get(key);
            for (Score s : u.getAlgoScores(Algo.EUCLIDEAN_DISTANCE)) {
                result.append(u.id()).append(";").append(s.score()).append(";").append(s.user().id()).append("\n");
            }
        }

        PrintLinesToFile(result.toString(), "ITBCF_EUCLIDEAN_DISTANCE", "");

    }


    public String PrintLinesToFile(String lines, String fileNameStart, String dbDir) {
        String timeStamp = new SimpleDateFormat("dd.HH.mm.ss").format(new Date());

        File file = new File( this.dataSetPath + dbDir);
        file = new File(file, fileNameStart + ".csv");

        try {
            if (!file.exists()) file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            fw.write(lines);
            fw.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return Paths.get(file.toString()).toString();
    }



}
