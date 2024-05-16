package sg.edu.np.mad.pocketchef.Models;

import java.util.ArrayList;
import java.util.List;

public class SearchedRecipeApiResponse {
    private List<SearchedRecipe> recipes;

    public List<SearchedRecipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<SearchedRecipe> recipes) {
        this.recipes = recipes;
    }
}
