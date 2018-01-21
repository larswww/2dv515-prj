package a2;

import movieRecs.Database;
import movieRecs.LoadDataset;

public class Main {

    public static void main(String[] args) {
	// write your code here
        MovieRatings mr = MovieRatings.getInstance();
        SeedScript sr = new SeedScript(mr);
        sr.lectureData();
        LoadDataset ld = new LoadDataset(mr, "");
        Database db = Database.getInstance();
        RecommendationsController ctrl = new RecommendationsController(mr, ld, db);
        ctrl.calculateForAll();


        // add users
        // add movies

    }
}
