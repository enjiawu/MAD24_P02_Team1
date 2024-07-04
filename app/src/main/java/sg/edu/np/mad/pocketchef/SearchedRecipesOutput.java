package sg.edu.np.mad.pocketchef;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import sg.edu.np.mad.pocketchef.Adapters.SearchedRecipesAdapter;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Listener.SearchRecipeListener;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipeApiResponse;

public class SearchedRecipesOutput extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Defining variables
    private ProgressBar progressBar;
    private RequestManager requestManager;
    private RecyclerView recyclerSearchedRecpies;

    //For navigation menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search;

    //For search inputs
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
    private String sort;
    private String sortDirection;
    private Button expandSearchButton;
    private TextView noRecipeFound;
    private Spinner sortBySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_searched_recipes_output);

        //Getting user inputs from the Advanced Search Activity
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
        sort = null; //Setting default to null
        sortDirection = "asc"; //Setting default to ascending

        //Request Manager Variable for API
        requestManager = new RequestManager(this);

        //Setting up views, listeners and fetching recipes
        setupViews();
        setupListeners();
        fetchSearchedRecipes();
    }

    //Setting up views
    private void setupViews() {
        // Getting all the variables from xml file
        recyclerSearchedRecpies = findViewById(R.id.recycler_searched_recipes);
        expandSearchButton = findViewById(R.id.expand_search_button);
        progressBar = findViewById(R.id.progressBar);
        noRecipeFound = findViewById(R.id.no_recipe_found);
        sortBySpinner = findViewById(R.id.sort_by_spinner);

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
        //Check if expandSearchButton has been clicked
        expandSearchButton.setOnClickListener(v -> { //If it has been clicked, o back to AdvancedSearchActivity
            Intent SearchedRecipeintent = new Intent(SearchedRecipesOutput.this, AdvancedSearchActivity.class);
            startActivity(SearchedRecipeintent);
        });

        //Check if user chooses to sort recipes
        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sort = sortBySpinner.getSelectedItem().toString();

                //Check how user wants to sort recipes
                if (sort.equals("Sort by")) {
                    sort = null;
                } else {
                    switch (sort) { // Replace sort and sort direction values with user's chosen option
                        case "Carbs (Asc)":
                            sort = "carbs";
                            sortDirection = "asc";
                            break;
                        case "Carbs (Desc)":
                            sort = "carbs";
                            sortDirection = "desc";
                            break;
                        case "Protein (Asc)":
                            sort = "protein";
                            sortDirection = "asc";
                            break;
                        case "Protein (Desc)":
                            sort = "protein";
                            sortDirection = "desc";
                            break;
                        case "Calories (Asc)":
                            sort = "calories";
                            sortDirection = "asc";
                            break;
                        case "Calories (Desc)":
                            sort = "calories";
                            sortDirection = "desc";
                            break;
                        default:
                            sort = null;
                            sortDirection = null;
                            break;
                    }
                }
                fetchSearchedRecipes(); //Fetch recipes again according to new sorting
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //Reading User Inputs and searching for the recipe
    public void fetchSearchedRecipes() {
        // Show a Snackbar message indicating that search is in progress
        Snackbar.make(findViewById(android.R.id.content), "Searching Recipes ...", Snackbar.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE); // Making the progress bar visible as the recipes get searched
        requestManager.getSearchedRecipes(new SearchRecipeListener() { //API Response

            // Fetching API response
            @Override
            public void didFetch(SearchedRecipeApiResponse response, String message) {
                // Log the API response
                if (response != null) {
                    Log.d("SearchActivity", "API Response: " + response);
                    // Process the response and update UI accordingly
                    setupSearchedRecipeRecyclerView(response);
                } else {
                    // Handle null response
                    Log.d("SearchActivity", "Response is null");
                    Toast.makeText(SearchedRecipesOutput.this, "Failed to fetch recipes", Toast.LENGTH_SHORT).show();
                }
            }

            // If got error
            @Override
            public void didError(String message) {
                // Handle API errors
                Log.d("SearchActivity", "API Error: " + message);
                Toast.makeText(SearchedRecipesOutput.this, "Failed to fetch recipes: " + message, Toast.LENGTH_SHORT).show();
            }
        }, query, excludeIngredients, minCarbs, maxCarbs, minProtein, maxProtein, minCalories, maxCalories, diet, intolerances, sort, sortDirection);
    }

    //For the menu
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Setting up recycler view to show searched recipes
    private void setupSearchedRecipeRecyclerView(SearchedRecipeApiResponse response) {
        recyclerSearchedRecpies.setHasFixedSize(true);

        // Check if response object is not null and if it contains recipes
        if (response != null && response.getRecipes() != null) {

            // Set the layout manager
            recyclerSearchedRecpies.setLayoutManager(new LinearLayoutManager(SearchedRecipesOutput.this));

            // Initialize adapter
            SearchedRecipesAdapter searchedRecipesAdapter = new SearchedRecipesAdapter(SearchedRecipesOutput.this, response.getRecipes(), recipeClickListener);

            // Check if there are no recipes found
            if (searchedRecipesAdapter.getItemCount() == 0){
                sortBySpinner.setVisibility(View.GONE); //Hide the sort by drop down
                noRecipeFound.setVisibility(View.VISIBLE); //Show the No Recipes Found Text
            }

            // Attach adapter
            recyclerSearchedRecpies.setAdapter(searchedRecipesAdapter);

            // Making the progress bar disappear after recipes get searched
            progressBar.setVisibility(View.GONE);

        } else {
            Log.d(TAG, "Response or recipes are null"); // Log for recipes or response that is null
        }
    }

    //To see recipe details
    private final RecipeClickListener recipeClickListener = id -> startActivity(new Intent(SearchedRecipesOutput.this, RecipeDetailsActivity.class)
            .putExtra("id", id));

    //For menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(SearchedRecipesOutput.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent2 = new Intent(SearchedRecipesOutput.this, RecipeActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(SearchedRecipesOutput.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            // Nothing Happens
        } else if (itemId == R.id.nav_profile) {
            Intent intent4 = new Intent(SearchedRecipesOutput.this, ProfileActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(SearchedRecipesOutput.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}