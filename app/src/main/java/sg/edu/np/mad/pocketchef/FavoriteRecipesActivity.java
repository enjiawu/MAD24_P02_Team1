package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.Database;
import sg.edu.np.mad.pocketchef.Models.FavoriteRecipe;

// activity to display favorite recepies
public class FavoriteRecipesActivity extends AppCompatActivity {
    RecyclerView recyclerViewFavorites;

    List<FavoriteRecipe> favoriteRecipes;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_recipes);

        // initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        // initialize recyclerview
        recyclerViewFavorites = findViewById(R.id.recycler_view_favorites);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        // load favorite recipes
        loadFavoriteRecipes();
    }

    // method to load recipes
    private void loadFavoriteRecipes() {
        // Replace with actual data fetching logic
        favoriteRecipes = getFavoriteRecipes();

    }

    // method to fetch favorite recipes form database
    private List<FavoriteRecipe> getFavoriteRecipes() {
        // Replace with actual logic to fetch favorite recipes
        return Database.getInstance().getFavoriteRecipes();
    }
}
