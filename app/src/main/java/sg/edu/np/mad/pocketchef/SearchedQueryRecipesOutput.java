package sg.edu.np.mad.pocketchef;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import sg.edu.np.mad.pocketchef.Adapters.RandomRecipeAdapter;
import sg.edu.np.mad.pocketchef.Adapters.SearchedQueryRecipesAdapter;
import sg.edu.np.mad.pocketchef.Adapters.SearchedRecipesAdapter;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Listener.SearchRecipeQueryListener;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipeQueryApiResponse;

public class SearchedQueryRecipesOutput extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String EXTRA_RECIPE_ID = "id";
    private static final int SCROLL_THRESHOLD = 2;

    RequestManager requestManager;
    RecyclerView recyclerView_query_recipes;
    ProgressBar progressBar;
    String searchQuery;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search, nav_logout, nav_profile, nav_favourites, nav_community, nav_pantry, nav_complex_search, nav_shoppinglist, nav_locationfinder;
    MaterialCardView cardView_no_recipe_found;
    MaterialTextView textView_no_recipe_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_searched_query_recipes_output);

        // Initialise views and listener
        setupViews();

        requestManager = new RequestManager(this);

        // Retrieve the search query from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SEARCH_QUERY")) {
            searchQuery = intent.getStringExtra("SEARCH_QUERY");
            fetchRecipes(searchQuery); // Fetch recipes based on the search query
        } else {
            Toast.makeText(this, "No search query found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViews() {
        recyclerView_query_recipes = findViewById(R.id.recycler_query_recipes);
        progressBar = findViewById(R.id.progressBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        cardView_no_recipe_found = findViewById(R.id.cardView_no_recipe_found);
        textView_no_recipe_found = findViewById(R.id.no_recipe_found);

        // Set up menu items
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_recipes = navigationView.getMenu().findItem(R.id.nav_recipes);
        nav_search = navigationView.getMenu().findItem(R.id.nav_search);
        nav_logout = navigationView.getMenu().findItem(R.id.nav_logout);
        nav_profile = navigationView.getMenu().findItem(R.id.nav_profile);
        nav_favourites = navigationView.getMenu().findItem(R.id.nav_favourites);
        nav_community = navigationView.getMenu().findItem(R.id.nav_community);
        nav_pantry = navigationView.getMenu().findItem(R.id.nav_pantry);
        nav_complex_search = navigationView.getMenu().findItem(R.id.nav_complex_search);
        nav_shoppinglist = navigationView.getMenu().findItem(R.id.nav_shoppinglist);
        nav_locationfinder = navigationView.getMenu().findItem(R.id.nav_locationfinder);

        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Method to fetch recipes based on the search query
    private void fetchRecipes(String query) {
        // Show a Snackbar message indicating that search is in progress
        Snackbar.make(findViewById(android.R.id.content), "Searching Recipes for: " + query, Snackbar.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        requestManager.getSearchedRecipesQuery(new SearchRecipeQueryListener() {
            @Override
            public void didFetch(SearchedRecipeQueryApiResponse response, String message) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "API Response: " + response.toString());
                setupSearchedQueryRecipeRecyclerView(response);
            }

            @Override
            public void didError(String message) {
                progressBar.setVisibility(View.GONE);
                String additionalMessage = "Please check API Key Quota";
                Toast.makeText(SearchedQueryRecipesOutput.this, message + ". " + additionalMessage, Toast.LENGTH_SHORT).show();
            }
        }, query); // Passing only the query parameter
    }

    //Setting up recycler view to show searched recipes
    private void setupSearchedQueryRecipeRecyclerView(SearchedRecipeQueryApiResponse response) {
        recyclerView_query_recipes.setHasFixedSize(true);

        if (response != null && response.getRecipes() != null) {
            recyclerView_query_recipes.setLayoutManager(new GridLayoutManager(SearchedQueryRecipesOutput.this, 1));
            SearchedQueryRecipesAdapter searchedQueryRecipesAdapter = new SearchedQueryRecipesAdapter(SearchedQueryRecipesOutput.this, response.getRecipes(), recipeClickListener);
            recyclerView_query_recipes.setAdapter(searchedQueryRecipesAdapter);
            // Log the JSON object
            Log.d(TAG, "Response JSON: " + new Gson().toJson(response));
            if (searchedQueryRecipesAdapter.getItemCount() == 0) {
                textView_no_recipe_found.setVisibility(View.VISIBLE);
                cardView_no_recipe_found.setVisibility(View.GONE);
            } else {
                textView_no_recipe_found.setVisibility(View.GONE);
                cardView_no_recipe_found.setVisibility(View.GONE);
            }

            progressBar.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "Response or recipes are null");
            textView_no_recipe_found.setVisibility(View.VISIBLE);
            cardView_no_recipe_found.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private final RecipeClickListener recipeClickListener = id ->
            startActivity(new Intent(this, RecipeDetailsActivity.class)
                    .putExtra(EXTRA_RECIPE_ID, id));

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(SearchedQueryRecipesOutput.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            // Nothing Happens
        } else if (itemId == R.id.nav_profile) {
            Intent intent2 = new Intent(SearchedQueryRecipesOutput.this, ProfileActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(SearchedQueryRecipesOutput.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            Intent intent4 = new Intent(SearchedQueryRecipesOutput.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(SearchedQueryRecipesOutput.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        } else if (itemId == R.id.nav_community) {
            Intent intent6 = new Intent(SearchedQueryRecipesOutput.this, CommunityActivity.class);
            finish();
            startActivity(intent6);
        } else if (itemId == R.id.nav_pantry) {
            Intent intent7 = new Intent(SearchedQueryRecipesOutput.this, PantryActivity.class);
            finish();
            startActivity(intent7);
        } else if (itemId == R.id.nav_complex_search) {
            Intent intent8 = new Intent(SearchedQueryRecipesOutput.this, ComplexSearchActivity.class);
            finish();
            startActivity(intent8);
        }
        //        } else if (itemId = R.id.nav_shoppinglist) {
//            Intent intent8 = new Intent(MainActivity.this, ShoppingListActivity.class);
//            finish();
//            startActivity(intent8);
//        } else if (itemId = R.id.nav_locationfinder) {
//            Intent intent9 = new Intent(MainActivity.this, LocationActivity.class);
//            finish();
//            startActivity(intent9);
//        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
