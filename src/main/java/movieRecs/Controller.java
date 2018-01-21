package movieRecs;

import a2.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;

@CrossOrigin
@RestController
public class Controller {
    private MovieRatings mr = MovieRatings.getInstance();
    private LoadDataset ld = new LoadDataset(mr, "");
    private Database db = Database.getInstance();
    private RecommendationsController ctrl = new RecommendationsController(mr, ld, db);


    @RequestMapping("/")
    public String index(@RequestParam(value="algo") String algo,
                        @RequestParam(value="user") String user,
                        @RequestParam(value="recs") String recs,
                        @RequestParam(value="itembased") String item,
                        @RequestParam(value="norm", defaultValue ="false") String norm) {
        User u = mr.getUser(user);
        Algo a = Algo.valueOf(algo);
        ctrl.weightedScores(u, a);

        if (norm.equals("true")) {
            HashMap<Double, Movie> normalized = ctrl.normalizeScores(u, a);
            u.setRecommendation(a, normalized);
        }

        return u.recommendationString(a, Integer.parseInt(recs));
    }

    @RequestMapping("/itembased")
    public String ibcf(@RequestParam(value="norm", defaultValue ="false") String norm,
                       @RequestParam(value="user") String user,
                       @RequestParam(value="algo") String algo) {
        User u = mr.getUser(user);
        Algo a = Algo.valueOf(algo);
        ctrl.calculateItemScores(u, a);
        a = Algo.valueOf("IBCF_" + a.toString());
        return u.recommendationString(a, 50);
    }

}