package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import sg.edu.np.mad.pocketchef.Adapters.SearchedRecipesAdapter;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Listener.SearchRecipeListener;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipeApiResponse;

public class AdvancedSearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //Defining variables
    private static final String EXTRA_RECIPE_ID = "id";
    private RequestManager requestManager;

    //For menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search;

    //For user input
    private Spinner dietSpinner;
    private Spinner intolerancesSpinner;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advanced_search);
        //Settting up views and listeners
        setupViews();
        setupListeners();
        requestManager = new RequestManager(this);
    }

    //Setting up views
    private void setupViews() {
        dietSpinner = findViewById(R.id.diet_spinner);
        intolerancesSpinner =findViewById(R.id.intolerances_spinner);
        searchButton = findViewById(R.id.searchButton);
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
    }

    // Setting up listeners
    public void setupListeners(){
        //Check if search button has been clicked
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

                //Data validation to see if there are inputs, if don't have assign default value
                try {
                    query = queryEdit.getText().toString();
                } catch (Exception ex) {
                    query = null;
                }

                try {
                    excludeIngredients = excludeIngredientsEdit.getText().toString();
                } catch (Exception ex) {
                    excludeIngredients = null;
                }

                try {
                    minCarbs = Integer.parseInt(minCarbsEdit.getText().toString());;
                } catch (Exception ex) {
                    minCarbs = 0;
                }

                try {
                    maxCarbs = Integer.parseInt(maxCarbsEdit.getText().toString());
                } catch (Exception ex) {
                    maxCarbs = 2147483647;
                }

                try {
                    minProtein = Integer.parseInt(minProteinEdit.getText().toString());
                } catch (Exception ex) {
                    minProtein = 0;
                }

                try {
                    maxProtein = Integer.parseInt(maxProteinEdit.getText().toString());
                } catch (Exception ex) {
                    maxProtein = 2147483647;
                }

                try {
                    minCalories = Integer.parseInt(minCaloriesEdit.getText().toString());
                } catch (Exception ex) {
                    minCalories = 0;
                }

                try {
                    maxCalories = Integer.parseInt(maxCaloriesEdit.getText().toString());
                } catch (Exception ex) {
                    maxCalories = 2147483647;
                }

                try {
                    diet = dietSpinner.getSelectedItem().toString();
                    if (diet == "None"){
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    diet = null;
                }

                try {
                    intolerances = intolerancesSpinner.getSelectedItem().toString();
                    if (intolerances == "None"){
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    intolerances = null;
                }

                //Sending the data to Advanced Search Activity
                Intent intent = new Intent(AdvancedSearchActivity.this, SearchedRecipesOutput.class);
                Bundle userInput = new Bundle();
                userInput.putString("query",query);
                userInput.putString("excludeIngredients",excludeIngredients);
                userInput.putInt("minCarbs",minCarbs);
                userInput.putInt("maxCarbs",maxCarbs);
                userInput.putInt("minProtein",minProtein);
                userInput.putInt("maxProtein",maxProtein);
                userInput.putInt("minCalories",minCalories);
                userInput.putInt("maxCalories",maxCalories);
                userInput.putString("diet",diet);
                userInput.putString("intolerances",intolerances);
                intent.putExtras(userInput);
                startActivity(intent);
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
    };

    //For menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(AdvancedSearchActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent2 = new Intent(AdvancedSearchActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_search) {
            // Nothing happens
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}