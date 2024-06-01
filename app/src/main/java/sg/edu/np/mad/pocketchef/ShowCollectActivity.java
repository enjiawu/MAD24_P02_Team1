package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.dialogs.WaitDialog;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Models.CategoryBean;
import sg.edu.np.mad.pocketchef.Models.RecipeDetailsC;
import sg.edu.np.mad.pocketchef.Adapters.FavoriteAdapter;
import sg.edu.np.mad.pocketchef.databinding.ActivityShowCollectBinding;
import sg.edu.np.mad.pocketchef.databinding.ListRandomRecipeBinding;

public class ShowCollectActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // variables for show collect activity
    ActivityShowCollectBinding binding;
    MaterialToolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    MenuItem nav_home, nav_recipes, nav_search;

    private List<CategoryBean> otherCollect;
    private List<String> otherCollectname;

    private FavoriteAdapter<ListRandomRecipeBinding, RecipeDetailsC> favoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enable edge to edge display
        EdgeToEdge.enable(this);

        // inflate the layout and bind views
        binding = ActivityShowCollectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialize toolbar, drawer layout and nav view
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // find nav menu item
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        navigationView.bringToFront();

        // set up the ActionBarDrawerToggle for the nav drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // set nav item selection listener
        navigationView.setNavigationItemSelectedListener(ShowCollectActivity.this);
        navigationView.setCheckedItem(nav_home);

        // set recycleview layout manager
        binding.recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        // initialize the favorite adapter
        favoriteAdapter = new FavoriteAdapter<ListRandomRecipeBinding, RecipeDetailsC>(new ArrayList<>()) {
            @Override
            protected int getType(int position) {
                return 0;
            }

            @Override
            protected void show(ListRandomRecipeBinding holder, int position, RecipeDetailsC recipe) {
                // load image using Glide
                Glide.with(ShowCollectActivity.this).load(recipe.imagPath)
                        .into(holder.imageViewFood);

                // set text for serving, title and time
                holder.textViewServings.setText(recipe.meal_servings);
                holder.textViewTitle.setText(recipe.meal_name);
                holder.textViewTime.setText(recipe.meal_price);

                // set long click listener for each item
                holder.randomListContainer.setOnLongClickListener(v -> {
                    String title = "Remove this recipe";
                    String[] name;
                    if (otherCollectname == null || otherCollectname.isEmpty()) {
                        name = new String[0];
                    } else {
                        name = otherCollectname.toArray(new String[0]);
                    }
                    String[] mergedArray = new String[name.length + 1];
                    int i = 0;
                    mergedArray[0] = title;
                    for (i = 1; i <= name.length; i++) {
                        mergedArray[i] = "Move to " + name[i - 1];
                    }

                    // show buttom menu
                    BottomMenu.show(mergedArray).setMessage(Html.fromHtml("<b>Edit Recipe</b>")).setOnMenuItemClickListener((dialog, text, index) -> {
                        if (index != 0) {
                            // update category and notify adapter
                            WaitDialog.show("loading...");
                            new Thread(() -> {
                                recipe.categoryBeanId = otherCollectname.get(index - 1);
                                FavoriteDatabase.getInstance(ShowCollectActivity.this)
                                        .RecipeDetailsCDao().update(recipe);
                                runOnUiThread(() -> {
                                    WaitDialog.dismiss();
                                    data.remove(recipe);
                                    favoriteAdapter.notifyDataSetChanged();
                                    PopTip.show("Successfully edit!");
                                });
                            }).start();
                        } else if (index == 0) {
                            // delete item and notify adapter
                            WaitDialog.show("loading...");
                            new Thread(() -> {
                                FavoriteDatabase.getInstance(ShowCollectActivity.this)
                                        .RecipeDetailsCDao().delete(recipe);
                                data.remove(recipe);
                                runOnUiThread(() -> {
                                    WaitDialog.dismiss();
                                    favoriteAdapter.notifyDataSetChanged();
                                    PopTip.show("Successfully Delete!");
                                });
                            }).start();
                        }
                        return false;
                    });
                    return true;
                });

                //set click listener for each item
                holder.randomListContainer.setOnClickListener(v -> {
                    // start recipeDetailActivity
                    Intent intent = new Intent(ShowCollectActivity.this, RecipeDetailsActivity.class);
                    intent.putExtra(EXTRA_RECIPE_ID, recipe.recipeDetailsResponseId + "");
                    startActivity(intent);
                });
            }
        };

        // set adapter for recycleview
        binding.recyclerViewFavorites.setAdapter(favoriteAdapter);
        init(); // initialize data
    }

    List<RecipeDetailsC> data;
    private static final String EXTRA_RECIPE_ID = "id";

    private void init() {
        // get category name from intent
        String name = getIntent().getStringExtra("id");
        // set toolbar title
        toolbar.setTitle(name);

        // load data from database in a background thread
        new Thread(() -> {
            data = FavoriteDatabase.getInstance(ShowCollectActivity.this).RecipeDetailsCDao()
                    .getByCategoryBeanId(name);
            runOnUiThread(() -> favoriteAdapter.setData(data));
        }).start();

        // get other category names
        new Thread(() -> {
            otherCollectname = new ArrayList<>();
            otherCollect = FavoriteDatabase.getInstance(ShowCollectActivity.this)
                    .categoryDao().getAllCategories();
            for (int i = 0; i < otherCollect.size(); i++) {
                if (!otherCollect.get(i).text.equals(name)) {
                    String collect_name = otherCollect.get(i).text;
                    otherCollectname.add(collect_name);
                }
            }
        }).start();
    }

    // for nav menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(ShowCollectActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent2 = new Intent(ShowCollectActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_search) {
            Intent intent2 = new Intent(ShowCollectActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent2 = new Intent(ShowCollectActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(ShowCollectActivity.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}