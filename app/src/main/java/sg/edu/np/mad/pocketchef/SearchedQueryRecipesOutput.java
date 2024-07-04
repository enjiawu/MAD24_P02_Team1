package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import sg.edu.np.mad.pocketchef.Adapters.SearchedRecipesAdapter;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Listener.SearchRecipeQueryListener;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipeQueryApiResponse;

public class SearchedQueryRecipesOutput extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String EXTRA_RECIPE_ID = "id";
    private static final int SCROLL_THRESHOLD = 2;

    private RequestManager requestManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String searchQuery;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_searched_query_recipes_output);

        // Initialise views and listener
        setupViews();
        setupListeners();

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
        recyclerView = findViewById(R.id.recycler_query_recipes);
        progressBar = findViewById(R.id.progressBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupListeners() {
        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    }

    // Methods for scrolling function, transition motion layout
    private boolean hasScrolledEnough() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null && recyclerView.getAdapter() != null) {
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            return lastVisibleItemPosition >= SCROLL_THRESHOLD;
        }
        return false;
    }

    private boolean shouldTransitionToStart() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            return firstVisibleItemPosition <= SCROLL_THRESHOLD;
        }
        return false;
    }

    private void transitionMotionLayoutToEnd() {
        MotionLayout motionLayout = findViewById(R.id.main);
        if (motionLayout != null) {
            motionLayout.transitionToEnd();
        }
    }

    private void transitionMotionLayoutToStart() {
        MotionLayout motionLayout = findViewById(R.id.main);
        if (motionLayout != null) {
            motionLayout.transitionToStart();
        }
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
                setupRecipeRecyclerView(response);
            }

            @Override
            public void didError(String message) {
                progressBar.setVisibility(View.GONE);
                String additionalMessage = "Please check API Key Quota";
                Toast.makeText(SearchedQueryRecipesOutput.this, message + ". " + additionalMessage, Toast.LENGTH_SHORT).show();
            }
        }, query); // Passing only the query parameter
    }

    private void setupRecipeRecyclerView(SearchedRecipeQueryApiResponse response) {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

            SearchedRecipesAdapter searchedRecipesAdapter = new SearchedRecipesAdapter(this,
                    response.getRecipes(), recipeClickListener);

            recyclerView.setAdapter(searchedRecipesAdapter);
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
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
