package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class AdvancedSearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Defining variables
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
    private Button searchButton; //Button for user to search for recipe


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advanced_search);

        //Setting up views and listeners
        setupViews();
        setupListeners();
    }

    //Setting up views
    private void setupViews() {
        // Getting all the variables from xml file
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
        searchButton.setOnClickListener(v -> {
            // Getting user inputs
            queryEdit = findViewById(R.id.searchByName);
            excludeIngredientsEdit = findViewById(R.id.excludeIngredients);
            minCarbsEdit = findViewById(R.id.editMinCarbs);
            maxCarbsEdit = findViewById(R.id.editMaxCarbs);
            minProteinEdit = findViewById(R.id.editMinProtein);
            maxProteinEdit = findViewById(R.id.editMaxProtein);
            minCaloriesEdit = findViewById(R.id.editMinCalories);
            maxCaloriesEdit = findViewById(R.id.editMaxCalories);

            //Data validation to see if there are inputs, if don't have assign default values
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
                minCarbs = Integer.parseInt(minCarbsEdit.getText().toString());
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
                if (diet.equals("None")){ //If user doesn't select anything, throw error to make diet null
                    throw new Exception();
                }
            } catch (Exception ex) {
                diet = null;
            }

            try {
                intolerances = intolerancesSpinner.getSelectedItem().toString();
                if (intolerances.equals("None")){ //If user doesn't select anything, throw error to make intolerances null
                    throw new Exception();
                }
            } catch (Exception ex) {
                intolerances = null;
            }

            //Sending the data to Advanced Search Activity
            Intent intent = new Intent(AdvancedSearchActivity.this, SearchedRecipesOutput.class);
            Bundle userInput = new Bundle(); //Bundling all the userInputs after they've gone through data validation
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
        });
    }

    // For the menu
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
        }else if (itemId == R.id.nav_profile){
        Intent intent3 = new Intent(AdvancedSearchActivity.this, ProfileActivity.class);
        finish();
        startActivity(intent3);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}