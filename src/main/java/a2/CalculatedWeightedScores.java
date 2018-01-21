package a2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalculatedWeightedScores {
    private User toCalc;
    Algo algo;

    public CalculatedWeightedScores(User user, Algo algo) {
        this.toCalc = user;
        this.algo = algo;
        List<Score> userSimilarityScores = user.getAlgoScores(algo);
        HashMap<Movie, Double> movieScores = new HashMap<>();

        for (Movie m: user.unseen()) {
            double sum = 0;
            double sum_sim = 0;
            int raters = 0;

            for (Score s: userSimilarityScores) {
                User u = s.user();
                double userScore = u.getMovieRating(m);
                if (userScore != -1) {
                    sum += (userScore * s.score());
                    sum_sim += s.score();
                    raters++;
                }
            }

            sum = sum / sum_sim;
            movieScores.put(m, sum);
        }
    }
}
