package sg.edu.np.mad.pocketchef.Models;

import java.util.ArrayList;
import java.util.List;

public class SearchedRecipeQueryApiResponse {
    private ArrayList<SearchedRecipe> results;
    private int offset;
    private int number;
    private int totalResults;

    public ArrayList<SearchedRecipe> getRecipes() {
        return results;
    }

    public void setRecipes(ArrayList<SearchedRecipe> results) {
        this.results = results;
    }
}
