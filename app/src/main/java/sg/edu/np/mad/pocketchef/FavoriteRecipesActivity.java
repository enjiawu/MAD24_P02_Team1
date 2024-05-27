package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.FavoriteRecipeAdapter;
import sg.edu.np.mad.pocketchef.Models.Database;
import sg.edu.np.mad.pocketchef.Models.FavoriteRecipe;

public class FavoriteRecipesActivity extends AppCompatActivity {
    RecyclerView recyclerViewFavorites;
    FavoriteRecipeAdapter favoriteRecipeAdapter;
    List<FavoriteRecipe> favoriteRecipes; // Replace with your model class for favorite recipes
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_recipes);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        recyclerViewFavorites = findViewById(R.id.recycler_view_favorites);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        loadFavoriteRecipes();
    }

    private void loadFavoriteRecipes() {
        // Replace with actual data fetching logic
        favoriteRecipes = getFavoriteRecipes();
        favoriteRecipeAdapter = new FavoriteRecipeAdapter(this, favoriteRecipes);
        recyclerViewFavorites.setAdapter(favoriteRecipeAdapter);
    }

    private List<FavoriteRecipe> getFavoriteRecipes() {
        // Replace with actual logic to fetch favorite recipes
        return Database.getInstance().getFavoriteRecipes();
    }

    public void onEditFavorite(FavoriteRecipe recipe) {
        // Logic to edit favorite recipe
        Intent intent = new Intent(this, EditFavoriteActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        startActivity(intent);
    }

    public void onDeleteFavorite(FavoriteRecipe recipe) {
        // Logic to delete favorite recipe
        Database.getInstance().deleteFavoriteRecipe(recipe);
        loadFavoriteRecipes();
        Toast.makeText(this, "Recipe removed from favorites", Toast.LENGTH_SHORT).show();
    }
}
