package sg.edu.np.mad.pocketchef;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.PantryIngredientAdapter;
import sg.edu.np.mad.pocketchef.Adapters.PantryRecipeAdapter;
import sg.edu.np.mad.pocketchef.Listener.IngredientsRecipesListener;
import sg.edu.np.mad.pocketchef.Listener.RdmRecipeRespListener;
import sg.edu.np.mad.pocketchef.Models.IngredientsRecipesResponse;
import sg.edu.np.mad.pocketchef.Models.RandomRecipeApiResponse;

public class PantryRecipesActivity extends AppCompatActivity {

    private RequestManager requestManager;
    RecyclerView pantryRecipesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry_recipes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        requestManager = new RequestManager(this);

        FindViews();

        fetchIngredientsRecipes();
    }

    private void fetchIngredientsRecipes() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("Salt");
        ingredients.add("Sugar");
        ingredients.add("Butter");
        ingredients.add("Honey");


        // Show a Snackbar message indicating that search is in progress
//        Snackbar.make(findViewById(android.R.id.content), "Searching Recipes...", Snackbar.LENGTH_SHORT).show();
//        progressBar.setVisibility(View.VISIBLE);
        requestManager.getRecipesByIngredients(new IngredientsRecipesListener() {
            @Override
            public void didFetch(ArrayList<IngredientsRecipesResponse> response, String message) {
//                progressBar.setVisibility(View.GONE);
//                setupRandomRecipeRecyclerView(response);
                Log.d("RESPONSE:", response.toString());
//                response.getRecipes();
                for (IngredientsRecipesResponse recipe : response) {


                    Log.d("RECIPE", recipe.title);
                }
                PantryRecipeAdapter pantryRecipeAdapter = new PantryRecipeAdapter(response, PantryRecipesActivity.this);

                LinearLayoutManager pantryRecipeLayoutManager = new LinearLayoutManager(PantryRecipesActivity.this);

                pantryRecipesRecyclerView.setLayoutManager(pantryRecipeLayoutManager);
                pantryRecipesRecyclerView.setItemAnimator(new DefaultItemAnimator());
                pantryRecipesRecyclerView.setAdapter(pantryRecipeAdapter);
            }

            @Override
            public void didError(String message) {
                Log.d("Message", message);
//                progressBar.setVisibility(View.GONE);
                String additionalMessage = "Please check API Key Quota";
                Toast.makeText(PantryRecipesActivity.this, message + ". " + additionalMessage, Toast.LENGTH_SHORT).show();
            }
        }, Arrays.toString(ingredients.toArray()));
    }

    private void FindViews() {
        pantryRecipesRecyclerView = findViewById(R.id.pantryRecipesRecyclerView);
    }

}