package a2;

import java.util.*;

public class EuclideanDistance extends AlgoCalc {

    public EuclideanDistance(ArrayList<Score> result, User toTest, HashMap<String, User> ratings) {
        super(result, toTest, ratings);
    }

    @Override
    protected void calculation(String id, User b) {
        if (!id.equals(toTest.id())) result.add(new Score(b, calc_Euclidean(toTest, b), Algo.EUCLIDEAN_DISTANCE));
    }

    private double calc_Euclidean(User A, User B) {
        double sim = 0.0;
        int cnt_sim = 0;

        for (Rating rA: A.ratings) {
                for (Rating rB: B.ratings) {

                    if (rA.matches(rB)) {
                        sim += Math.pow(rA.score - rB.score, 2);
                        cnt_sim++;
                    }
                }
        }

        if (cnt_sim == 0) {
            return 0;
        }

        return 1.0 / (1.0 + sim);
    }
}
