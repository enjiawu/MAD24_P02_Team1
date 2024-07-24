package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import sg.edu.np.mad.pocketchef.Adapters.PantryIngredientAdapter;

public class PantryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView pantryRecyclerView;
    ExtendedFloatingActionButton availableRecipesButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    MenuItem navHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry);


        FindViews();
        SetUpListeners();

        ArrayList<String> ingredientList = new ArrayList<>();
        ingredientList.add("Salt");
        ingredientList.add("Sugar");
        ingredientList.add("Butter");
        ingredientList.add("Honey");

        PantryIngredientAdapter pantryAdapter = new PantryIngredientAdapter(ingredientList);

        LinearLayoutManager pantryLayoutManager = new LinearLayoutManager(this);

        pantryRecyclerView.setLayoutManager(pantryLayoutManager);
        pantryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pantryRecyclerView.setAdapter(pantryAdapter);
    }

    private void FindViews() {
        pantryRecyclerView = findViewById(R.id.pantryRecyclerView);
        availableRecipesButton = findViewById(R.id.availableRecipesButton);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        navHome = navigationView.getMenu().findItem(R.id.nav_home);
        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(PantryActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(PantryActivity.this);
        navigationView.setCheckedItem(navHome);
    }

    private void SetUpListeners() {
        availableRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent availableRecipes = new Intent(PantryActivity.this, PantryRecipesActivity.class);
                startActivity(availableRecipes);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(PantryActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent2 = new Intent(PantryActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(PantryActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            Intent intent4 = new Intent(PantryActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_profile) {
            Intent intent7 = new Intent(PantryActivity.this, ProfileActivity.class);
            finish();
            startActivity(intent7);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(PantryActivity.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        } else if (itemId == R.id.nav_community) {
            Intent intent6 = new Intent(PantryActivity.this, CommunityActivity.class);
            finish();
            startActivity(intent6);
        } else if (itemId == R.id.nav_pantry) {
            // Nothing Happens
        } else if (itemId == R.id.nav_complex_search) {
            Intent intent8 = new Intent(PantryActivity.this, ComplexSearchActivity.class);
            finish();
            startActivity(intent8);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}