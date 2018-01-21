package a2;

import movieRecs.Database;
import movieRecs.LoadDataset;
import org.junit.Before;
import org.junit.Test;


import java.util.*;

import static junit.framework.TestCase.assertEquals;


public class TestCalculations {
    private MovieRatings mr = MovieRatings.getInstance();
    private LoadDataset ld = new LoadDataset(mr, "test");
    private Database db = Database.getInstance();
    private RecommendationsController ctrl = new RecommendationsController(mr, ld, db);

    @Test
    public void SimpleAverageNotSeen() {


    }

    @Test
    public void LectureEuclideanDistance() {
        User toby = mr.getUser("Toby");
        ctrl.calculateFor(toby, Algo.EUCLIDEAN_DISTANCE, mr.userDb());
        List<Score> euclidean = toby.getAlgoScores(Algo.EUCLIDEAN_DISTANCE);

        assertEquals(0.3076923076923077, euclidean.get(0).score());
//        assertEquals(0.23529411764705882, euclidean.get(1).score());
    }

    @Test
    public void LecturePearsonCorrelation() {
        User toby = mr.getUser("Toby");
        ctrl.calculateFor(toby, Algo.PEARSON_CORRELATION, mr.userDb());
        List<Score> pearson = toby.getAlgoScores(Algo.PEARSON_CORRELATION);

        assertEquals(0.9912407071619299, pearson.get(0).score());
        assertEquals(0.9244734516419049, pearson.get(1).score());
        assertEquals(0.8934051474415647, pearson.get(2).score());
    }

    @Test
    public void LectureWeightedPearson() {
        User u = mr.getUser("Toby");
        Algo a = Algo.valueOf("PEARSON_CORRELATION");
        ctrl.weightedScores(u, a);
        System.out.println(u.recommendationString(a, 50));
        System.out.println("Expected: WS Night 3.35, WS Lady 2.83, WS Luck 2.53");

//        HashMap<Double, ArrayList<Movie>> normalisedPearson = ctrl.normalizeScores(u, a);
//        u.setRecommendation(a, normalisedPearson);
        System.out.println(u.recommendationString(a, 50));

        a = Algo.valueOf("EUCLIDEAN_DISTANCE");
        ctrl.weightedScores(u, a);
        System.out.println(u.recommendationString(a, 50));
        System.out.println("Expected: WS Night 3.35, WS Lady 2.85, Ws Luck 2.45");

//        HashMap<Double, ArrayList<Movie>> normaliszedEuclidean = ctrl.normalizeScores(u, a);
//        u.setRecommendation(a, normaliszedEuclidean);
        System.out.println(u.recommendationString(a, 50));

    }

    @Test
    public void transformDataSet() {
//        HashMap<String, User> n_recs = new HashMap<>();
//        HashMap<String, Movie> n_movies = new HashMap<>();
//
//        Iterator<User> it = mr.userDb().values().iterator();
//
//        while (it.hasNext()) {
//            User usr = it.next();
//
//            for (Rating rank : usr.ratings) {
//                String item = rank.movie().title();
//                String uid = rank.user().id();
//                double score = rank.score;
//
//                if (!n_recs.containsKey(item)) n_recs.put(item, new User(item));
//                if (!n_movies.containsKey(rank.user().id())) n_movies.put(uid, new Movie(uid, uid, "reversed"));
//
//                User n_usr = n_recs.get(item);
//                Movie n_movie = n_movies.get(uid);
//                Rating r = new Rating(n_movie, n_usr, score);
//                rank.movie().linkRatings(r, n_usr);
//            }
//
//        }
//
////        n_recs.forEach((id, user) -> {
////            mr.addUser(user);
////        });
//
//
//        mr.setItemUserDb(n_recs);
//
//        for (String u : mr.getItemUserDb().keySet()) {
//            ctrl.calculateFor(mr.itemUser(u), Algo.EUCLIDEAN_DISTANCE, mr.getItemUserDb());
//        }
//        mr.setMovieDb(n_movies);

//        User litw = mr.itemUser("Lady in the Water");
////        assertEquals("Lisa", litw.ratings.get(4).movie().title());
////        assertEquals(2.5, litw.ratings.get(4).score);
////
////        assertEquals("Gene", litw.ratings.get(2).movie().title());
////        assertEquals(3.0, litw.ratings.get(2).score);
//
//
//        User soap = mr.itemUser("Snakes on a Plane");
////        assertEquals("Lisa", soap.ratings.get(6).movie().title());
////        assertEquals(3.5, soap.ratings.get(6).score);
//
//        System.out.println("\nTop 4 similar items for " + litw.id());
//        System.out.println(litw.algoString(Algo.EUCLIDEAN_DISTANCE, 4));
//        HashSet<String> empty = new HashSet<>();
//
//        db.getAlgoScores("Lady in the Water", Algo.EUCLIDEAN_DISTANCE, empty);
//
//        System.out.println("Expected: Dupree 0.4, Night 0.286, Luck 0.222\n");
//
//
//        System.out.println("\nTop 3 similar items for " + soap.id());
//        System.out.println(soap.algoString(Algo.EUCLIDEAN_DISTANCE, 3));
//        System.out.println("Expected: Lady 0.222, Night 0.182, Superman 0.167\n");


        System.out.println("\nTop 3 user matches for Toby (Euclidean)");
        User toby = mr.getUser("Toby");
        ctrl.weightedScores(toby, Algo.EUCLIDEAN_DISTANCE);
        System.out.println(toby.algoString(Algo.EUCLIDEAN_DISTANCE, 3));
        System.out.print("Expected: Mick: 0.308, Mike: 0.286, Claudia: 0.235\n");


        System.out.print("\nRecommending movies using IB CF ");
        ctrl.calculateItemScores(toby, Algo.EUCLIDEAN_DISTANCE);
        System.out.println(toby.recommendationString(Algo.IBCF_EUCLIDEAN_DISTANCE, 3));
        System.out.println("Expected: WR Night 3.183, WR Lady 2.598, WR Luck 2.473");

        System.out.print("\nRecommending movies using IB CF Pearson");
        ctrl.calculateItemScores(toby, Algo.PEARSON_CORRELATION);
        System.out.println(toby.recommendationString(Algo.IBCF_PEARSON_CORRELATION, 3));
        System.out.println("Expected: WR Night 3.183, WR Lady 2.598, WR Luck 2.473");

//        User supermanReturns = mr.getUser("Superman Returns");
//        ctrl.weightedScores(supermanReturns, Algo.PEARSON_CORRELATION);
//        HashMap<Double, ArrayList<Movie>> normalized = ctrl.normalizeScores(supermanReturns, Algo.PEARSON_CORRELATION);
//        supermanReturns.setRecommendation(Algo.PEARSON_CORRELATION, normalized);
//        System.out.println(supermanReturns.recommendationString(Algo.PEARSON_CORRELATION, 50));
//
//
//        Algo a = Algo.valueOf("EUCLIDEAN_DISTANCE");
//        supermanReturns = mr.getUser("Lady in the Water");
//        ctrl.calculateFor(supermanReturns, a);
//        List<Score> euclidean = supermanReturns.getAlgoScores(a);
//        ctrl.weightedScores(supermanReturns, a);
//        System.out.println(supermanReturns.recommendationString(a, 50));
//
//        User snakesOnAPlane = mr.getUser("Snakes on a Plane");
//        ctrl.calculateFor(snakesOnAPlane, a);
//        List<Score> euclideanSP = snakesOnAPlane.getAlgoScores(a);
//        ctrl.weightedScores(snakesOnAPlane, a);
//        System.out.println(snakesOnAPlane.recommendationString(a, 50));


    }


    public String controllerCall(String user, String algo, String norm, String recs) {
        User u = mr.getUser(user);
        Algo a = Algo.valueOf(algo);
        ctrl.weightedScores(u, a);


        return u.recommendationString(a, Integer.parseInt(recs));
    }
}
