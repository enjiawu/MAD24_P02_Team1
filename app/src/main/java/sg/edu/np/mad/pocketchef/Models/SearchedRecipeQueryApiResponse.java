package sg.edu.np.mad.pocketchef.Models;

import java.util.ArrayList;
import java.util.List;

public class SearchedRecipeQueryApiResponse {
    private ArrayList<Recipe> results;
    private int offset;
    private int number;
    private int totalResults;

    public ArrayList<Recipe> getRecipes() {
        return results;
    }

    public void setRecipes(ArrayList<Recipe> results) {
        this.results = results;
    }
}
