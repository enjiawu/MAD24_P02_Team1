package sg.edu.np.mad.pocketchef;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sg.edu.np.mad.pocketchef.Adapters.IngredientsAdapater;
import sg.edu.np.mad.pocketchef.Adapters.InstructionsAdapter;
import sg.edu.np.mad.pocketchef.Adapters.SimilarRecipeAdapter;
import sg.edu.np.mad.pocketchef.Listener.InstructionsListener;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Listener.RecipeDetailsListener;
import sg.edu.np.mad.pocketchef.Listener.SimilarRecipesListener;
import sg.edu.np.mad.pocketchef.Models.CategoryBean;
import sg.edu.np.mad.pocketchef.Models.InstructionsResponse;
import sg.edu.np.mad.pocketchef.Models.RecipeDetailsC;
import sg.edu.np.mad.pocketchef.Models.RecipeDetailsResponse;
import sg.edu.np.mad.pocketchef.Models.SimilarRecipeResponse;
import sg.edu.np.mad.pocketchef.Models.SummaryParser;
import sg.edu.np.mad.pocketchef.FavoriteDatabase;

public class RecipeDetailsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Global variables for activity
    private static final long API_REQUEST_DELAY = 1000;
    int recipeId;
    TextView textView_meal_name, textView_meal_source, textView_meal_servings, textView_meal_ready, textView_meal_price,
            textView_protein_value, textView_fat_value, textView_calories_value, textView_daily_requirements_coverage_value;
    ImageView imageView_meal_image, imageView_nutrition;
    RecyclerView recycler_meal_ingredients, recycler_meal_similar, recycler_meal_instructions;
    RequestManager manager;
    ProgressBar progressBar;
    IngredientsAdapater ingredientsAdapater;
    SimilarRecipeAdapter similarRecipeAdapter;
    InstructionsAdapter instructionsAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search;
    ConstraintLayout recipeDetailsLayout, nutritionLabelLayout;
    MaterialButton buttonNutritionLabel;
    RecipeDetailsC recipeDetailsC;
    ImageView btnFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViews();
        recipeId = Integer.parseInt(getIntent().getStringExtra("id"));
        // Utilising RequestManager class methods
        loadRecipeDetailsWithStaggeredApiCalls();
        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(RecipeDetailsActivity.this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(RecipeDetailsActivity.this);
        navigationView.setCheckedItem(nav_home);
        loadNutritionLabelImage(); // Load the image when the layout becomes visible
        // Set up the OnClickListener for the button
        buttonNutritionLabel.setOnClickListener(v -> {
            if (nutritionLabelLayout.getVisibility() == View.VISIBLE) {
                nutritionLabelLayout.setVisibility(View.GONE);
            } else {
                nutritionLabelLayout.setVisibility(View.VISIBLE);
            }
        });

        // Set up the OnClickListener for the Favorite button
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recipeDetailsC==null){
                    showFavoriteDialog();
                } else{
                    // if recipe is already favorited, confirm unfavorite
                    MessageDialog.show("Unfavorite recipe","Are you sure you want to unfavorite this recipe?","Yes"
                            ,"No").setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            WaitDialog.show("loading...");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // delete recipe from favorites
                                    FavoriteDatabase.getInstance(RecipeDetailsActivity.this)
                                            .RecipeDetailsCDao().delete(recipeDetailsC);
                                    recipeDetailsC=null; // update fsvorite status
                                    runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // update favorite button appearance
                                                Glide.with(RecipeDetailsActivity.this).load(R.drawable.ic_btn_star).into(btnFavorite);
                                                btnFavorite.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                                                WaitDialog.dismiss();
                                            }
                                    });
                                }
                            }).start();

                            return false;
                        }
                    });
                }

            }
        });
    }
    // Intialise objects
    private void findViews() {
        // Intialise Progress Bar
        progressBar = findViewById(R.id.progressBar);
        // Intialise Text Views
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textView_meal_source = findViewById(R.id.textView_meal_source);
        // Text Views in Grid Layout
        textView_protein_value = findViewById(R.id.textView_protein_value);
        textView_fat_value = findViewById(R.id.textView_fat_value);
        textView_calories_value = findViewById(R.id.textView_calories_value);
        textView_daily_requirements_coverage_value = findViewById(R.id.textView_daily_requirements_coverage_value);
        // Text Views for below Grid Layout
        textView_meal_servings = findViewById(R.id.textView_meal_servings);
        textView_meal_ready = findViewById(R.id.textView_meal_ready);
        textView_meal_price = findViewById(R.id.textView_meal_price);
        // Intialise Image Views
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        // Intialise Recycler Viewsbtn_favorite
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients);
        recycler_meal_similar = findViewById(R.id.recycler_meal_similar);
        recycler_meal_instructions = findViewById(R.id.recycler_meal_instructions);
        // Navigation Menu set up
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_recipes = navigationView.getMenu().findItem(R.id.nav_recipes);
        nav_search = navigationView.getMenu().findItem(R.id.nav_search);
        recipeDetailsLayout = findViewById(R.id.recipe_details);
        nutritionLabelLayout = findViewById(R.id.nutrition_dialog_layout);
        //Intialise Nutritional Image
        imageView_nutrition = findViewById(R.id.imageView_nutrition);
        // Intialise Button
        buttonNutritionLabel = findViewById(R.id.button_Nutrition_Label);
        // Intialise favorite button
        btnFavorite = findViewById(R.id.btn_favorite);
    }

    // load recipe details with staggered APL calls
    private void loadRecipeDetailsWithStaggeredApiCalls() {
        progressBar.setVisibility(View.VISIBLE);
        manager = new RequestManager(this);
        InstructionsManager instructionsManager = new InstructionsManager();
        instructionsManager.fetchInstructionsWithDelay(manager, instructionsListener, recipeId, 0);
        instructionsManager.fetchRecipeDetailsWithDelay(manager, recipeDetailsListener, recipeId, API_REQUEST_DELAY);
        instructionsManager.fetchSimilarRecipesWithDelay(manager, similarRecipesListener, recipeId, API_REQUEST_DELAY * 2);
        new  Thread(new Runnable() {
            @Override
            public void run() {
                // check if recipe is favorited
                recipeDetailsC =  FavoriteDatabase.getInstance(RecipeDetailsActivity.this)
                        .RecipeDetailsCDao().getByRecipeDetailsResponseId(recipeId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // if favorited, update favorite button appearance
                        if(recipeDetailsC!=null){
                            Glide.with(RecipeDetailsActivity.this).load(R.drawable.ic_collect).into(btnFavorite);
                            btnFavorite.setImageTintList(ColorStateList.valueOf(Color.YELLOW));
                        }
                    }
                });
            }
        }).start();
    }
    // Implementing the recipeDetailsListener
    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            progressBar.setVisibility(View.GONE);
            // Replace with API response json data
            textView_meal_name.setText(response.title);
            textView_meal_source.setText(response.sourceName);
            textView_meal_servings.setText(MessageFormat.format("{0}{1}", response.servings, getString(R.string.textView_meal_servings_textEnd)));
            textView_meal_ready.setText(MessageFormat.format("{0}{1}", response.readyInMinutes, getString(R.string.textView_meal_ready_textEnd)));
            textView_meal_price.setText(MessageFormat.format("{0}{1}", response.pricePerServing, getString(R.string.textView_meal_price_textEnd)));
            // Parsing recipe details using Summary Parser into Grid Layout
            String summary = SummaryParser.parseRecipeDetails(response.summary);
            String[] details = summary.split("\n");
            // Null Validation
            textView_protein_value.setText(details[0] != null ? details[0] : "N/A");
            textView_fat_value.setText(details[1] != null ? details[1] : "N/A");
            textView_calories_value.setText(details[2] != null ? details[2] : "N/A");
            textView_daily_requirements_coverage_value.setText(details[3] != null ? details[3] : "N/A");
            // Loading image using Picasso
            Picasso.get().load(response.image).into(imageView_meal_image);
            path = response.image;
            // Use custom layout for recycler view
            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            // Use adapater
            ingredientsAdapater = new IngredientsAdapater(RecipeDetailsActivity.this, response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapater);

        }

        @Override
        public void didError(String message) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private String path;

    // Implementing the similarRecipesListener
    private final SimilarRecipesListener similarRecipesListener = new SimilarRecipesListener() {
        @Override
        public void didFetch(List<SimilarRecipeResponse> response, String message) {
            recycler_meal_similar.setHasFixedSize(true);
            recycler_meal_similar.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            similarRecipeAdapter = new SimilarRecipeAdapter(RecipeDetailsActivity.this, response, recipeClickListener);
            recycler_meal_similar.setAdapter(similarRecipeAdapter);
        }

        // This is to check functionality first before implementation
        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, "message", Toast.LENGTH_SHORT).show();
        }
    };

    // This is to check functionality first before implementation
    private final RecipeClickListener recipeClickListener = id -> Toast.makeText(RecipeDetailsActivity.this, "id", Toast.LENGTH_SHORT).show();

    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsResponse> response, String message) {
            recycler_meal_instructions.setHasFixedSize(true);
            recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
            instructionsAdapter = new InstructionsAdapter(RecipeDetailsActivity.this, response);
            recycler_meal_instructions.setAdapter(instructionsAdapter);
        }

        @Override
        public void didError(String message) {
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(RecipeDetailsActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent = new Intent(RecipeDetailsActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_search) {
            Intent intent2 = new Intent(RecipeDetailsActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent2);
        }else if (itemId == R.id.nav_favourites) {
            Intent intent2 = new Intent(RecipeDetailsActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent2);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Loading nutrition image
    private void loadNutritionLabelImage() {
        // Construct the URL for the nutrition label image
        String nutritionLabelUrl = "https://api.spoonacular.com/recipes/" + recipeId + "/nutritionLabel.png?apiKey=" + getString(R.string.api_key);
        Log.d("RecipeDetailsActivity", "Nutrition Label URL: " + nutritionLabelUrl);
        // Load the image using Picasso
        Picasso.get().load(nutritionLabelUrl)
                .into(imageView_nutrition);
    }




    // Add to favorite list
    List<String> categories;
    Spinner spinnerCategories;

    // show dialog to add to favorites
    private void showFavoriteDialog() {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_to_favorites, null);
        spinnerCategories = dialogView.findViewById(R.id.spinner_categories);
        Button btTextNewCategory = dialogView.findViewById(R.id.bt_new_category);
        Button buttonSave = dialogView.findViewById(R.id.button_save);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        getCategories();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        btTextNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new InputDialog("Add New Category", "Enter a name for the new category", "Save","Cancel")
                        .setCancelButton(new OnInputDialogButtonClickListener<InputDialog>() {
                            @Override
                            public boolean onClick(InputDialog inputDialog, View view, String s) {
                                inputDialog.dismiss();
                                return false;
                            }
                        })
                        .setOkButton(new OnInputDialogButtonClickListener<InputDialog>() {
                            @Override
                            public boolean onClick(InputDialog inputDialog, View view, String s) {
                                if(TextUtils.isEmpty(s)){
                                    PopTip.show("Input cannot be empty");
                                    return false;
                                }

                                WaitDialog.show("loading.....");
                                ExecutorService executorService = Executors.newSingleThreadExecutor();
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        CategoryBean categoryBean =new CategoryBean("default",s);
                                        FavoriteDatabase.getInstance(RecipeDetailsActivity.this)
                                                .categoryDao().insertCategory(categoryBean);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                WaitDialog.dismiss();
                                                PopTip.show("Successfully Added!");
                                            }
                                        });
                                    }
                                });
                                return false;
                            }
                        }).show();
            }
        });

        buttonSave.setOnClickListener(v -> {
            if(path==null||path.isEmpty()){
                PopTip.show("Loading still ongoing, please wait!");
                return;
            }
            RecipeDetailsC recipeDetailsC1 =new RecipeDetailsC();
            recipeDetailsC1.recipeDetailsResponseId = recipeId;
            Log.d("run",recipeId+"");
            recipeDetailsC1.categoryBeanId = spinnerCategories.getSelectedItem().toString();
            recipeDetailsC1.meal_servings =textView_meal_servings.getText().toString();
            recipeDetailsC1.meal_ready =  textView_meal_ready.getText().toString();
            recipeDetailsC1.meal_price = textView_meal_price.getText().toString();
            recipeDetailsC1.meal_name = textView_meal_name.getText().toString();
            recipeDetailsC1.imagPath = path;
            recipeDetailsC = recipeDetailsC1;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    WaitDialog.show("loading...");
                    FavoriteDatabase.getInstance(RecipeDetailsActivity.this)
                            .RecipeDetailsCDao().insert(recipeDetailsC1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WaitDialog.dismiss();
                            PopTip.show("Successfully Favorite!");
                            Glide.with(RecipeDetailsActivity.this).load(R.drawable.ic_collect).into(btnFavorite);
                            btnFavorite.setImageTintList(ColorStateList.valueOf(Color.YELLOW));
                        }
                    });
                }
            }).start();
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    List<String> list;

    // retrieve categories from database
    private void getCategories() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CategoryBean> data = FavoriteDatabase.getInstance(RecipeDetailsActivity.this)
                        .categoryDao().getAllCategories();
                List<String> categories = new ArrayList<>();
                for(int i=0;i<data.size();i++){
                    categories.add(data.get(i).text);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // populate spinner with categories
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(RecipeDetailsActivity.this,
                                android.R.layout.simple_spinner_item, categories);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategories.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }
}