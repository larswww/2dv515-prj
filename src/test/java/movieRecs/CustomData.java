package movieRecs;

import a2.*;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class CustomData {
    private MovieRatings mr;
    private LoadDataset ld;
    private RecommendationsController ctrl;
    private Database db = Database.getInstance();

    @Before
    public void loadLars() {
        mr = MovieRatings.getInstance();
        ld = new LoadDataset(mr, "");
        ctrl = new RecommendationsController(mr, ld, db);

        ld.addUserRating("Lars", "296", 5.0);
        ld.addUserRating("Lars", "48304", 5.0);
        ld.addUserRating("Lars", "6957", 4.3);
        ld.addUserRating("Lars", "3198", 4.0);
        ld.addUserRating("Lars", "33794", 4.0);
        ld.addUserRating("Lars", "1089", 5.0);
        ld.addUserRating("Lars", "6", 5.0);

    }

//    @Test
//    public void forMe() {
//        User u = mr.getUser("Lars");
//        Algo a = Algo.valueOf("PEARSON_CORRELATION");
//        ctrl.weightedScores(u, a);
//        System.out.println(u.recommendationString(a, 50));
//
//        HashMap<Double, ArrayList<Movie>> normalisedPearson = ctrl.normalizeScores(u, a);
//        u.setRecommendation(a, normalisedPearson);
//        System.out.println(u.recommendationString(a, 50));
//
//
//        a = Algo.valueOf("EUCLIDEAN_DISTANCE");
//        ctrl.weightedScores(u, a);
//        System.out.println(u.recommendationString(a, 50));
//
//        HashMap<Double, ArrayList<Movie>> normalisedEuclidean = ctrl.normalizeScores(u, a);
//        u.setRecommendation(a, normalisedEuclidean);
//        System.out.println(u.recommendationString(a, 50));
//
//    }
}
