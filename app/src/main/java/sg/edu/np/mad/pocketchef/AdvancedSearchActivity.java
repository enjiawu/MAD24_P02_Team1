package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import sg.edu.np.mad.pocketchef.Models.RandomRecipeApiResponse;

public class AdvancedSearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ProgressBar progressBar;
    private Spinner dietSpinner;
    private Spinner intolerencesSpinner;
    private RequestManager requestManager;
    private RecyclerView recyclerSearchedRecpies;

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
            requestManager = new RequestManager(this);
            return insets;
        });
    }

    private void setupViews() {
        progressBar = findViewById(R.id.progressBar);
        dietSpinner = findViewById(R.id.diet_spinner);
        intolerencesSpinner = findViewById(R.id.intolerences_spinner);
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
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tags,
                R.layout.spinner_text
        );
        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        dietSpinner.setAdapter(arrayAdapter);
        intolerencesSpinner.setAdapter(arrayAdapter);
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
        requestManager.getRandomRecipes(new RdmRecipeRespListener() {
            @Override
            public void didFetch(RandomRecipeApiResponse response, String message) {
                progressBar.setVisibility(View.GONE);
                setupRandomRecipeRecyclerView(response);
            }

            @Override
            public void didError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RecipeActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }, tags);
    }

    private void setupRandomRecipeRecyclerView(RandomRecipeApiResponse response) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(RecipeActivity.this, 1));
        RandomRecipeAdapter randomRecipeAdapter = new RandomRecipeAdapter(RecipeActivity.this, response.recipes, recipeClickListener);
        recyclerView.setAdapter(randomRecipeAdapter);
    }

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