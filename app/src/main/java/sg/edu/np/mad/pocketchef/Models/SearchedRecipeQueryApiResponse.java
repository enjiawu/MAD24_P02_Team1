package sg.edu.np.mad.pocketchef.Models;

import java.util.ArrayList;
import java.util.List;

public class SearchedRecipeQueryApiResponse {
    private List<SearchedQueryRecipe> results;
    private int offset;
    private int number;
    private int totalResults;

    public List<SearchedQueryRecipe> getRecipes() {
        return results;
    }

    public void setRecipes(List<SearchedQueryRecipe> results) {
        this.results = results;
    }
}
