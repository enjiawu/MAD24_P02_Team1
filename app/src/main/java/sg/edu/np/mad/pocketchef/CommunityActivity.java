package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.CommunityAdapter;
import sg.edu.np.mad.pocketchef.Adapters.SearchedRecipesAdapter;
import sg.edu.np.mad.pocketchef.Listener.PostClickListener;
import sg.edu.np.mad.pocketchef.Models.Comment;
import sg.edu.np.mad.pocketchef.Models.Post;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipeApiResponse;

public class CommunityActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search, nav_logout, nav_profile, nav_favourites, nav_community, nav_pantry, nav_complex_search;

    // XML Variables
    private ProgressBar progressBar;
    private ImageView addPostButton;

    // Database
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference postsRef;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community);

        //Setting up views and listeners
        setupViews();
        setupListeners();
    }

    private void setupViews() {
        // Navigation Menu set up
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_recipes = navigationView.getMenu().findItem(R.id.nav_recipes);
        nav_search = navigationView.getMenu().findItem(R.id.nav_search);
        nav_pantry = navigationView.getMenu().findItem(R.id.nav_pantry);
        nav_complex_search = navigationView.getMenu().findItem(R.id.nav_complex_search);
        nav_logout = navigationView.getMenu().findItem(R.id.nav_logout);
        nav_profile = navigationView.getMenu().findItem(R.id.nav_profile);
        nav_favourites = navigationView.getMenu().findItem(R.id.nav_favourites);
        nav_community = navigationView.getMenu().findItem(R.id.nav_community);

        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(CommunityActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(CommunityActivity.this);
        navigationView.setCheckedItem(nav_home);

        //Getting all the variables from the xml file
        progressBar = findViewById(R.id.progressBar);
        addPostButton = findViewById(R.id.addPostButton);

        //Firebase database setup
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("PostImages");
        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        postsRef = database.getReference("posts");

        setupSearchedRecipeRecyclerView();
    }

    // Setting up listeners
    public void setupListeners() {
        // Check if add post button has been clicked
        addPostButton.setOnClickListener(v -> {
            Log.d("Community", "Working");
            // Go to add post activity
            Intent intent = new Intent(CommunityActivity.this, AddPostActivity.class);
            finish();
            startActivity(intent);
        });
    }

    // Loading community posts
    public void setupSearchedRecipeRecyclerView() {
        progressBar.setVisibility(View.VISIBLE); // Making the progress bar visible as the  posts get loaded

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    posts.add(post);
                }

                // Set the adapter to the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.post_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(CommunityActivity.this));
                CommunityAdapter adapter = new CommunityAdapter(CommunityActivity.this, posts, new PostClickListener() {
                    @Override
                    public void onPostClicked(String postKey) {
                        // Handle post click here
                    }
                });
                recyclerView.setAdapter(adapter);

                // Making the progress bar disappear after posts get loaded
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
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
            Intent intent = new Intent(CommunityActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent2 = new Intent(CommunityActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(CommunityActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            Intent intent4 = new Intent(CommunityActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_profile) {
            Intent intent5 = new Intent(CommunityActivity.this, ProfileActivity.class);
            finish();
            startActivity(intent5);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent6 = new Intent(CommunityActivity.this, LoginActivity.class);
            finish();
            startActivity(intent6);
        } else if (itemId == R.id.nav_community) {
            // Nothing Happens
        } else if (itemId == R.id.nav_complex_search) {
            Intent intent7 = new Intent(CommunityActivity.this, ComplexSearchActivity.class);
            finish();
            startActivity(intent7);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}