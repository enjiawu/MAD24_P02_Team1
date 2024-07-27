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
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.RandomRecipeAdapter;
import sg.edu.np.mad.pocketchef.Listener.RdmRecipeRespListener;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Models.RandomRecipeApiResponse;
import sg.edu.np.mad.pocketchef.databinding.ActivityRecipeBinding;

public class RecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String EXTRA_RECIPE_ID = "id";
    private static final int SCROLL_THRESHOLD = 2;
    private RequestManager requestManager;
    private final List<String> tags = new ArrayList<>();
    private SearchView searchView;
    private ProgressBar progressBar;
    private int previousScrollPosition = 0;

    // Binding object
    private ActivityRecipeBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize view binding
        bind = ActivityRecipeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        // Initialize views
        setupViews();
        setupListeners();
        requestManager = new RequestManager(this);
    }

    private void setupViews() {
        searchView = bind.searchViewHome;
        Spinner spinner = bind.spinnerTags;
        RecyclerView recyclerView = bind.recyclerRandomRecipes;
        progressBar = bind.progressBar;

        // Navigation Menu setup
        DrawerLayout drawerLayout = bind.drawerLayout;
        NavigationView navigationView = bind.navView;
        MaterialToolbar toolbar = bind.toolbar;

        // Spinner setup
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tags,
                R.layout.spinner_text
        );
        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        spinner.setAdapter(arrayAdapter);

        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(RecipeActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(RecipeActivity.this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    private void setupListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tags.clear();
                tags.add(query);
                fetchRandomRecipes();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        bind.spinnerTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tags.clear();
                tags.add(parent.getSelectedItem().toString());
                fetchRandomRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bind.recyclerRandomRecipes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && hasScrolledEnough()) { // Scrolling downwards
                    transitionMotionLayoutToEnd();
                } else if (dy < 0 && shouldTransitionToStart()) { // Scrolling upwards
                    transitionMotionLayoutToStart();
                }
            }
        });
    }

    // Methods for scrolling function, transition motion layout
    private boolean hasScrolledEnough() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) bind.recyclerRandomRecipes.getLayoutManager();
        if (layoutManager != null && bind.recyclerRandomRecipes.getAdapter() != null) {
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            return lastVisibleItemPosition >= SCROLL_THRESHOLD;
        }
        return false;
    }

    private boolean shouldTransitionToStart() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) bind.recyclerRandomRecipes.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            return firstVisibleItemPosition <= SCROLL_THRESHOLD;
        }
        return false;
    }

    private void transitionMotionLayoutToEnd() {
        MotionLayout motionLayout = bind.main;
        motionLayout.transitionToEnd();
    }

    private void transitionMotionLayoutToStart() {
        MotionLayout motionLayout = bind.main;
        motionLayout.transitionToStart();
    }

    // Methods to call API
    private void fetchRandomRecipes() {
        // Show a Snackbar message indicating that search is in progress
        Snackbar.make(findViewById(android.R.id.content), "Searching Random Recipes", Snackbar.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        requestManager.getRandomRecipes(new RdmRecipeRespListener() {
            @Override
            public void didFetch(RandomRecipeApiResponse response, String message) {
                progressBar.setVisibility(View.GONE);
                setupRandomRecipeRecyclerView(response);
            }

            @Override
            public void didError(String message) {
                progressBar.setVisibility(View.GONE);
                String additionalMessage = "Please check API Key Quota";
                Toast.makeText(RecipeActivity.this, message + ". " + additionalMessage, Toast.LENGTH_SHORT).show();
            }
        }, tags);
    }

    private void setupRandomRecipeRecyclerView(RandomRecipeApiResponse response) {
        RecyclerView recyclerView = bind.recyclerRandomRecipes;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(RecipeActivity.this, 1));
        RandomRecipeAdapter randomRecipeAdapter = new RandomRecipeAdapter(RecipeActivity.this, response.getRecipes(), recipeClickListener);
        recyclerView.setAdapter(randomRecipeAdapter);
    }

    private final RecipeClickListener recipeClickListener = id ->
            startActivity(new Intent(RecipeActivity.this, RecipeDetailsActivity.class).putExtra(EXTRA_RECIPE_ID, id));

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(RecipeActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            // Nothing happens
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(RecipeActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            Intent intent4 = new Intent(RecipeActivity.this, AdvancedSearchActivity.class);
        } else if (itemId == R.id.nav_profile) {
            Intent intent4 = new Intent(RecipeActivity.this, ProfileActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(RecipeActivity.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        } else if (itemId == R.id.nav_community) {
            Intent intent6 = new Intent(RecipeActivity.this, CommunityActivity.class);
            finish();
            startActivity(intent6);
        } else if (itemId == R.id.nav_complex_search) {
            Intent intent7 = new Intent(RecipeActivity.this, ComplexSearchActivity.class);
            finish();
            startActivity(intent7);
        } else if (itemId == R.id.nav_shoppinglist) {
            Intent intent8 = new Intent(RecipeActivity.this, ShopCartActivity.class);
            finish();
            startActivity(intent8);
        } else if (itemId == R.id.nav_locationfinder) {
            Intent intent9 = new Intent(RecipeActivity.this, LocationActivity.class);
            finish();
            startActivity(intent9);
        }
        bind.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
