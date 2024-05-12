package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.RandomRecipeAdapter;
import sg.edu.np.mad.pocketchef.Listener.RdmRecipeRespListener;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Models.RandomRecipeApiResponse;

public class RecipeActivity extends AppCompatActivity {
    private static final String EXTRA_RECIPE_ID = "id";
    private static final int SCROLL_THRESHOLD = 2;
    private RequestManager requestManager;
    private RecyclerView recyclerView;
    private Spinner spinner;
    private final List<String> tags = new ArrayList<>();
    private SearchView searchView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.recipe_activity);
        // Intialise views and listener
        setupViews();
        setupListeners();
        requestManager = new RequestManager(this);
    }

    private void setupViews() {
        searchView = findViewById(R.id.searchView_home);
        spinner = findViewById(R.id.spinner_tags);
        recyclerView = findViewById(R.id.recycler_random_recipes);
        progressBar = findViewById(R.id.progressBar);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tags,
                R.layout.spinner_text
        );
        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        spinner.setAdapter(arrayAdapter);
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (hasScrolledEnough()) {
                    transitionMotionLayoutToEnd();
                }
            }
        });
    }

    private boolean hasScrolledEnough() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null && recyclerView.getAdapter() != null) {
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            return lastVisibleItemPosition >= SCROLL_THRESHOLD;
        }
        return false;
    }

    private void fetchRandomRecipes() {
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

    private void transitionMotionLayoutToEnd() {
        MotionLayout motionLayout = findViewById(R.id.main);
        motionLayout.transitionToEnd();
    }
    private final RecipeClickListener recipeClickListener = id -> startActivity(new Intent(RecipeActivity.this, RecipeDetailsActivity.class)
            .putExtra(EXTRA_RECIPE_ID, id));
}