package a2;

import movieRecs.Database;
import movieRecs.LoadDataset;

import java.lang.reflect.Array;
import java.util.*;

public class RecommendationsController {
    private MovieRatings mr;
    private Database db;;

    public RecommendationsController(MovieRatings mr, LoadDataset ld, Database db) {
        this.mr = mr;
        this.db = db;
        calculateItemBased();

    }

    // for running all defined algos on all users in db
    public void calculateForAll() {

        System.out.println("calculating for " + mr.userDb().size() + " users");
        for (Algo a : Algo.values()) {
            mr.userDb().forEach((k, v) ->
                    calculateFor(v, a, mr.userDb()));
        }
        System.out.print("all users calculated");
    }

    public void calculateItemBased() {
//        db.buildMovieIndex(mr.movieDb());
        Algo[] algos = {Algo.PEARSON_CORRELATION, Algo.EUCLIDEAN_DISTANCE};

        for (Algo a: algos) {
            HashMap<String, String> moviesWithoutAlgoScores = db.checkForScores(mr.getItemUserDb(), a);

            // why didnt i just calculate both in one go??
            for (String title : moviesWithoutAlgoScores.keySet()) {
                User u = mr.itemUser(moviesWithoutAlgoScores.get(title));
                calculateFor(u, a, mr.getItemUserDb());
                db.insertAlgoScore(u, a);
                u.setAlgoScore(a, null);
            }
        }




//        for (String u : mr.getItemUserDb().keySet()) {
//            User user = mr.itemUser(u);
//            if (!db.hasAlgoScore(user.id(), Algo.PEARSON_CORRELATION)) { //todo just bulk check
//                calculateFor(user, Algo.PEARSON_CORRELATION, mr.getItemUserDb());
//                db.insertAlgoScore(user, Algo.PEARSON_CORRELATION);
//                    user.setAlgoScore(Algo.PEARSON_CORRELATION, null);
//            }
//
//        }
    }

    public void calculateFor(User u, Algo a, HashMap<String, User> db) {
        ArrayList<Score> result = new ArrayList<>();

        if (a == Algo.EUCLIDEAN_DISTANCE) {
            new EuclideanDistance(result, u, db);
        }

        if (a == Algo.PEARSON_CORRELATION) {
            new PearsonCorrelation(result, u, db);
        }

        u.setAlgoScore(a, result);
    }

    public void checkUnseen(User u) {
        if (u.unseen().size() == 0) mr.movieDb().forEach((k, v) -> u.addUnseen(v));
    }

    public void weightedScores(User u, Algo a) {
        checkUnseen(u);

        List<Score> userSimilarityScores = u.getAlgoScores(a);

        if (userSimilarityScores == null) { // calculate using IB CF
            calculateFor(u, a, mr.userDb());
            userSimilarityScores = u.getAlgoScores(a);
        }

        u.setRecommendation(a, CalculatedWeightedScores(u, a, userSimilarityScores));
    }

    public void calculateItemScores(User user, Algo algo) {
        checkUnseen(user);
//        List<Score> userSimilarityScores = new ArrayList<>();
//        for (Movie m : user.unseen()) {
//            List<Score> all = mr.itemUser(m.title()).getAlgoScores(algo);
//
//            for (Rating r : user.ratings) {
//                for (Score s : all) if (s.user().id().equals(r.movie().title())) userSimilarityScores.add(s);
//            }
//        }
        Algo key = null;
        if (algo == Algo.EUCLIDEAN_DISTANCE) key = Algo.IBCF_EUCLIDEAN_DISTANCE;
        if (algo == Algo.PEARSON_CORRELATION) key = Algo.IBCF_PEARSON_CORRELATION;

        user.setRecommendation(key, itemBasedCFScores(user, algo));
        System.out.println(user.recommendationString(key, 50));
        user.setRecommendation(key, normalizeScores(user, key));

    }


    public HashMap<Double, Movie> CalculatedWeightedScores(User user, Algo algo, List<Score> scores) {

        HashMap<Double, Movie> movieScores = new HashMap<>();

        for (Movie m : user.unseen()) {
            double sum = 0;
            double sum_sim = 0;
            double max = Double.MIN_VALUE;
            int raters = 0; //todo ? this should be a thing no?

            for (Score s : scores) {
                User u = s.user();

                double userScore = u.getMovieRating(m);
                if (userScore != -1) {
                    if (userScore > max) max = userScore; // save the max for normalization
                    sum += (userScore * s.score());
                    sum_sim += s.score();
                    raters++;
                }
            }

            if (raters != 0) { // to avoid calculating on/putting in movies that nobody has rated

                if (sum == 0.0) sum = 0.00001;
                if (algo == Algo.PEARSON_CORRELATION) {
                    sum = sum / max * 5.0;
                } else {
                    if (sum_sim == 0.0) sum_sim = 0.00001;
                    sum = sum / sum_sim;
                }

                movieScores.put(sum, m);
//                movieScores.putIfAbsent(sum, new ArrayList<Movie>());
//                movieScores.get(sum).add(m);
            }


        }
        return movieScores;
    }

    public HashMap<Double, Movie> normalizeScores(User u, Algo a) {
        HashMap<Double, Movie> ms = u.getRecommendations(a);
        HashMap<Double, Movie> normalized = new HashMap<>();
        Iterator<Double> scores = new TreeSet<>(ms.keySet()).descendingIterator();

        double max = Double.MIN_VALUE;

        while (scores.hasNext()) {
            double s = scores.next();
            if (s > max) max = s;
        }

        scores = new TreeSet<>(ms.keySet()).descendingIterator();

        if (max == 0.0) max = 0.00001;
        while (scores.hasNext()) {
            Double score = scores.next();
            if (a == Algo.PEARSON_CORRELATION || a == Algo.IBCF_PEARSON_CORRELATION) {
                normalized.put(score / max * 5.0, ms.get(score));
            } else {
                normalized.put(score / max, ms.get(score));
            }
        }

        return normalized;
    }

    public HashMap<Double, Movie> itemBasedCFScores(User u, Algo a) {
        HashMap<Double, Movie> movieScores = new HashMap<>();
        HashMap<String, Double> scores = new HashMap<>();
        HashMap<String, Double> totalSim = new HashMap<>();
        double adjustment = 1.0;
        if (a == Algo.IBCF_PEARSON_CORRELATION) adjustment = 5.0;

        HashSet<String> rated = new HashSet<>();
        ArrayList<Rating> usersRatings = u.getRatings();
        for (Rating r : usersRatings) rated.add(r.movie().title());

        // loop over items rated by this user
        for (Rating r : usersRatings) {

            // loop over items similar to this one (itemsim)
            List<Score> itemsim = db.getAlgoScores(r.movie().title(), a, rated);
            for (Score s : itemsim) {
                String item2 = s.user().id();
                if (item2.equals(r.movie().title())) continue;

                // weighted sum of rating x similarity
                scores.putIfAbsent(item2, 0.0);
                double score = scores.get(item2);
                score += s.score() * r.score();
                scores.put(item2, score);

                // sum of all similarities
                totalSim.putIfAbsent(item2, 0.0);
                double sim = totalSim.get(item2);
                sim += s.score();
                totalSim.put(item2, sim);

            }

        }

        for (String item : scores.keySet()) {
            String id = mr.getMovieId(item);
            Movie m = mr.getMovie(id);
            if (m == null) System.err.println(id + item);
            double s = scores.get(item);
            double sim = totalSim.get(item);
            if (s == 0.0) s = 0.00001;
            if (sim == 0.0) sim = 0.00001;
            double test = s / sim;
            movieScores.put(s / sim, mr.getMovie(id));
        }

        return movieScores;
    }
}
