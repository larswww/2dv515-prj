package a2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class PearsonCorrelation extends AlgoCalc {

    public PearsonCorrelation(ArrayList<Score> result, User toTest, HashMap<String, User> ratings) {
        super(result, toTest, ratings);
    }

    @Override
    protected void calculation(String id, User b) {
        if (!id.equals(toTest.id())) result.add(new Score(b, calc_Pearson(toTest, b), Algo.PEARSON_CORRELATION));
    }

    private double calc_Pearson(User a, User b) {
        double sum1 = 0.0;
        double sum2 = 0.0;
        double sum1sq = 0.0;
        double sum2sq = 0.0;
        double pSum = 0.0;
        int n = 0;

        for (Rating rA : a.ratings) {

            for (Rating rB : b.ratings) {

                if (rA.matches(rB)) {
                    sum1 += rA.score;
                    sum2 += rB.score;
                    sum1sq += Math.pow(rA.score, 2);
                    sum2sq += Math.pow(rB.score, 2);
                    pSum += rA.score * rB.score;
                    n++;
                }
            }
        }

        // if no ratings in common
        if (n == 0) return 0;

        // pearson score calculation
        double num = pSum - (sum1 * sum2  / n);
        double den = Math.sqrt((sum1sq - Math.pow(sum1, 2) / n) * (sum2sq - Math.pow(sum2, 2) / n));

        if (den == 0) return 0;


        return num / den;
    }
}
