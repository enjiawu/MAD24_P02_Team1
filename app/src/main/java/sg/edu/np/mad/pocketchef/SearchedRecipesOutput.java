package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import sg.edu.np.mad.pocketchef.Adapters.SearchedRecipesAdapter;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Listener.SearchRecipeListener;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipeApiResponse;

public class SearchedRecipesOutput extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String EXTRA_RECIPE_ID = "id";
    private ProgressBar progressBar;
    private RequestManager requestManager;
    private RecyclerView recyclerSearchedRecpies;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search;
    private String query;
    private String excludeIngredients;
    private Integer minCarbs;
    private Integer maxCarbs;
    private Integer minProtein;
    private Integer maxProtein;
    private Integer minCalories;
    private Integer maxCalories;
    private String diet;
    private String intolerances;
    private Button expandSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_searched_recipes_output);
        setupViews();
        setupListeners();

        //Getting user inputs
        Intent receivingEnd = getIntent();
        query = receivingEnd.getStringExtra("query");
        excludeIngredients = receivingEnd.getStringExtra("excludeIngredients");
        minCarbs = receivingEnd.getIntExtra("minCarbs", 0);
        maxCarbs = receivingEnd.getIntExtra("maxCarbs", 1444444444);
        minProtein = receivingEnd.getIntExtra("minProtein", 0);
        maxProtein = receivingEnd.getIntExtra("maxProtein", 1444444444);
        minCalories = receivingEnd.getIntExtra("minCalories", 0);
        maxCalories = receivingEnd.getIntExtra("maxCalories", 1444444444);
        diet = receivingEnd.getStringExtra("diet");
        intolerances = receivingEnd.getStringExtra("intolerances");
        requestManager = new RequestManager(this);
        fetchSearchedRecipes();
    }

    private void setupViews() {
        //progressBar = findViewById(R.id.progressBar);
        recyclerSearchedRecpies = findViewById(R.id.recycler_searched_recipes);
        expandSearchButton = findViewById(R.id.expand_search_button);
        // Navigation Menu set up
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_recipes = navigationView.getMenu().findItem(R.id.nav_recipes);
        nav_search = navigationView.getMenu().findItem(R.id.nav_search);
        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(SearchedRecipesOutput.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(SearchedRecipesOutput.this);
        navigationView.setCheckedItem(nav_home);
    }

    // Setting up listeners
    public void setupListeners(){
        expandSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchedRecipesOutput.this, AdvancedSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    //Reading User Inputs and searching for the recipe
    public void fetchSearchedRecipes() {
        //progressBar.setVisibility(View.VISIBLE); // Making the progress bar visible as the recipes get searched
        requestManager.getSearchedRecipes(new SearchRecipeListener() {
            @Override
            public void didFetch(SearchedRecipeApiResponse response, String message) {
                if (response != null && response.getRecipes() != null) {
                    //progressBar.setVisibility(View.GONE);
                    setupSearchedRecipeRecyclerView(response);
                } else {
                    Log.d("apiworking", "response or recipes is null");
                }
            }

            @Override
            public void didError(String message) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchedRecipesOutput.this, message, Toast.LENGTH_SHORT).show();
                Log.d("apiworking", "got error");
            }
        }, query, excludeIngredients, minCarbs, maxCarbs, minProtein, maxProtein, minCalories, maxCalories, diet, intolerances);
    }

    private void setupSearchedRecipeRecyclerView(SearchedRecipeApiResponse response) {
        recyclerSearchedRecpies.setHasFixedSize(true);
        recyclerSearchedRecpies.setLayoutManager(new GridLayoutManager(SearchedRecipesOutput.this, 1));
        SearchedRecipesAdapter searchedRecipesAdapter = new SearchedRecipesAdapter(SearchedRecipesOutput.this, response.getRecipes(), recipeClickListener);
        recyclerSearchedRecpies.setAdapter(searchedRecipesAdapter);
    }

    private final RecipeClickListener recipeClickListener = id -> startActivity(new Intent(SearchedRecipesOutput.this, RecipeDetailsActivity.class)
            .putExtra(EXTRA_RECIPE_ID, id));

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(SearchedRecipesOutput.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            // Nothing happens
        } else if (itemId == R.id.nav_search) {
            Intent intent2 = new Intent(SearchedRecipesOutput.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent2);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}