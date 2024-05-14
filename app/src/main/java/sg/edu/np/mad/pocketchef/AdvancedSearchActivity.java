package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import sg.edu.np.mad.pocketchef.Adapters.RandomRecipeAdapter;
import sg.edu.np.mad.pocketchef.Listener.RdmRecipeRespListener;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Listener.SearchRecipeListener;
import sg.edu.np.mad.pocketchef.Models.RandomRecipeApiResponse;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipeApiResponse;

public class AdvancedSearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String EXTRA_RECIPE_ID = "id";
    private ProgressBar progressBar;
    private Spinner dietSpinner;
    private Spinner intolerancesSpinner;
    private RequestManager requestManager;
    private RecyclerView recyclerSearchedRecpies;
    private EditText queryEdit;
    private EditText excludeIngredientsEdit;
    private EditText minCarbsEdit;
    private EditText maxCarbsEdit;
    private EditText minProteinEdit;
    private EditText maxProteinEdit;
    private EditText minCaloriesEdit;
    private EditText maxCaloriesEdit;
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
    private Button searchButton;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advanced_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            setupViews();
            setupListeners();
            requestManager = new RequestManager(this);
            return insets;
        });
    }

    private void setupViews() {
        searchButton = findViewById(R.id.searchButton);
        progressBar = findViewById(R.id.progressBar);
        dietSpinner = findViewById(R.id.diet_spinner);
        intolerancesSpinner = findViewById(R.id.intolerances_spinner);
        recyclerSearchedRecpies = findViewById(R.id.recycler_searched_recipes);
        // Navigation Menu set up
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_recipes = navigationView.getMenu().findItem(R.id.nav_recipes);
        nav_search = navigationView.getMenu().findItem(R.id.nav_search);
        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(AdvancedSearchActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(AdvancedSearchActivity.this);
        navigationView.setCheckedItem(nav_home);
        // Setting up spinner
        ArrayAdapter<CharSequence> dietArrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.dietArray,
                R.layout.spinner_text
        );
        dietArrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        ArrayAdapter<CharSequence> intolerancesArrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.intolerancesArray,
                R.layout.spinner_text
        );
        intolerancesArrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        dietSpinner.setAdapter(dietArrayAdapter);
        intolerancesSpinner.setAdapter(intolerancesArrayAdapter);
    }

    // Setting up listeners
    public void setupListeners(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting user inputs
                queryEdit = findViewById(R.id.searchByName);
                excludeIngredientsEdit = findViewById(R.id.excludeIngredients);
                minCarbsEdit = findViewById(R.id.editMinCarbs);
                maxCarbsEdit = findViewById(R.id.editMaxCarbs);
                minProteinEdit = findViewById(R.id.editMinProtein);
                maxProteinEdit = findViewById(R.id.editMaxProtein);
                minCaloriesEdit = findViewById(R.id.editMinCalories);
                maxCaloriesEdit = findViewById(R.id.editMaxCalories);

                try{
                    query = queryEdit.getText().toString();
                    excludeIngredients = excludeIngredientsEdit.getText().toString();
                    minCarbs = Integer.parseInt(minCarbsEdit.getText().toString());
                    maxCarbs = Integer.parseInt(maxCarbsEdit.getText().toString());
                    minProtein = Integer.parseInt(minProteinEdit.getText().toString());
                    maxProtein = Integer.parseInt(maxProteinEdit.getText().toString());
                    minCalories = Integer.parseInt(minCaloriesEdit.getText().toString());
                    maxCalories = Integer.parseInt(maxCaloriesEdit.getText().toString());
                    diet = dietSpinner.getSelectedItem().toString();
                    intolerances = intolerancesSpinner.getSelectedItem().toString();
                }
                catch(Exception e){
                    Log.d("pocketchef", "error with the inputs");
                }


                //User input validation

                fetchSearchedRecipes();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Reading User Inputs and searching for the recipe
    public void fetchSearchedRecipes(){
        progressBar.setVisibility(View.VISIBLE); // Making the progress bar visible as the recipes get searched
        requestManager.getSearchedRecipes(new SearchRecipeListener() {
            @Override
            public void didFetch(SearchedRecipeApiResponse response, String message) {
                progressBar.setVisibility(View.GONE);
                setupSearchedRecipeRecyclerView(response);
                Log.d("pocketchefAPI", message);
            }

            @Override
            public void didError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdvancedSearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }, query, excludeIngredients, minCarbs, maxCarbs, minProtein, maxProtein, minCalories, maxCalories, diet, intolerances);
    }

    private void setupSearchedRecipeRecyclerView(SearchedRecipeApiResponse response) {
        recyclerSearchedRecpies.setHasFixedSize(true);
        recyclerSearchedRecpies.setLayoutManager(new GridLayoutManager(AdvancedSearchActivity.this, 1));
        RandomRecipeAdapter randomRecipeAdapter = new RandomRecipeAdapter(AdvancedSearchActivity.this, response.recipes, recipeClickListener);
        recyclerSearchedRecpies.setAdapter(randomRecipeAdapter);
    }

    private final RecipeClickListener recipeClickListener = id -> startActivity(new Intent(AdvancedSearchActivity.this, RecipeDetailsActivity.class)
            .putExtra(EXTRA_RECIPE_ID, id));

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(AdvancedSearchActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            // Nothing happens
        } else if (itemId == R.id.nav_search) {
            Intent intent2 = new Intent(AdvancedSearchActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent2);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}