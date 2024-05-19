package sg.edu.np.mad.pocketchef;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private static Database instance;
    private List<FavoriteRecipe> favoriteRecipes;

    private Database() {
        favoriteRecipes = new ArrayList<>();
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public List<FavoriteRecipe> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public FavoriteRecipe getFavoriteRecipeById(int id) {
        for (FavoriteRecipe recipe : favoriteRecipes) {
            if (recipe.getId() == id) {
                return recipe;
            }
        }
        return null;
    }

    public void addFavoriteRecipe(FavoriteRecipe recipe) {
        favoriteRecipes.add(recipe);
    }

    public void updateFavoriteRecipe(int id, String name) {
        for (FavoriteRecipe recipe : favoriteRecipes) {
            if (recipe.getId() == id) {
                recipe.setName(name);
                // Update other fields if there are any
                break;
            }
        }
    }

    public void deleteFavoriteRecipe(FavoriteRecipe recipe) {
        favoriteRecipes.remove(recipe);
    }

    public List<String> getFavoriteCategories() {
        // Implement logic to fetch favorite categories from the database
        return new ArrayList<>(); // Placeholder, replace with actual implementation
    }

    public void addFavoriteCategory(String category) {
        // Implement logic to add a new favorite category to the database
    }

    public void addRecipeToCategory(int recipeId, String category) {
        // Implement logic to add a recipe to a specific category in the database
    }
}