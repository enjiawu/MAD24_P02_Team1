package sg.edu.np.mad.pocketchef.Models;

import java.util.ArrayList;
import java.util.List;

public class SearchedRecipeApiResponse {
    private List<SearchedRecipe> results;
    private int offset;
    private int number;
    private int totalResults;

    public List<SearchedRecipe> getRecipes() {
        return results;
    }

    public void setRecipes(List<SearchedRecipe> results) {
        this.results = results;
    }
}
