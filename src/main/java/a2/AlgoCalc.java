package a2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public abstract class AlgoCalc {
    ArrayList<Score> result;
    User toTest;

    public AlgoCalc(ArrayList<Score> result, User toTest, HashMap<String, User> ratings) {
        this.result = result;
        this.toTest = toTest;
        ratings.forEach(this::calculation);
        result.sort(Comparator.comparing(Score::score));
        Collections.reverse(result); //todo figure out how to change the sorting order instead ffs...
    }

    protected abstract void calculation(String id, User b);
}
