package sg.edu.np.mad.pocketchef;

import static android.content.ContentValues.TAG;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.CommunityAdapter;
import sg.edu.np.mad.pocketchef.Adapters.SearchedRecipesAdapter;
import sg.edu.np.mad.pocketchef.Listener.PostClickListener;
import sg.edu.np.mad.pocketchef.Listener.PostLikeClickListener;
import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
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
    private TextView noPostsFound;

    private CommunityAdapter adapter;
    private SearchView searchView;
    private Spinner spinner;

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
        spinner = findViewById(R.id.sort_by_spinner);
        searchView = findViewById(R.id.searchView_post);
        noPostsFound = findViewById(R.id.noPostsFound);

        noPostsFound.setVisibility(View.GONE); // Make sure the no posts found is hidden

        //Firebase database setup
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("PostImages");
        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        postsRef = database.getReference("posts");

        setupSearchedRecipeRecyclerView();
    }

    // Setting up listeners
    public void setupListeners() {
        // Search function
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()){
                    setupSearchedRecipeRecyclerView();
                    return false;
                }
                searchPost(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    setupSearchedRecipeRecyclerView();
                    return false;
                }
                searchPost(newText);
                return true;
            }
        });

        // Set up spinner for sorting of posts
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortPosts(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
        progressBar.setVisibility(VISIBLE); // Making the progress bar visible as the posts get loaded

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setPostKey(postSnapshot.getKey()); // Set the postKey for each Post object
                    posts.add(post);
                }

                // Set the adapter to the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.post_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(CommunityActivity.this));
                adapter = new CommunityAdapter(CommunityActivity.this, posts, new PostClickListener() {
                    @Override
                    public void onPostClicked(String postKey) {
                        Log.d("Community", postKey);
                        //To see recipe details
                        Intent postDetails = new Intent(CommunityActivity.this, PostDetailsActivity.class)
                                .putExtra("id", postKey);
                        startActivity(postDetails);
                    }
                }, new PostLikeClickListener() {
                    @Override
                    public void onLikeClicked(String postKey, int position) {
                        // Find the post with the matching postKey
                        String userId = mAuth.getCurrentUser().getUid();
                        postsRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Post post = snapshot.getValue(Post.class);

                                if (post != null && snapshot.getKey().equals(postKey)) {
                                    if (post.getLikesUsers().contains(userId)) {
                                        post.getLikesUsers().remove(userId);
                                        post.setLikes(post.getLikes() - 1);
                                    } else {
                                        post.getLikesUsers().add(userId);
                                        post.setLikes(post.getLikes() + 1);
                                    }

                                    postsRef.child(postKey).setValue(post).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            adapter.notifyItemChanged(position);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle potential errors
                            }
                        });
                    }
                });

                recyclerView.setAdapter(adapter);

                // Making the progress bar disappear after posts get loaded
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
            }
        });
    }

    public void searchPost(String query) {
        progressBar.setVisibility(VISIBLE); // Show progress bar while searching

        if (query.isEmpty()){ // If user doesnt enter anything
            setupSearchedRecipeRecyclerView();
        }

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> searchedPosts = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setPostKey(postSnapshot.getKey()); // Set the postKey for each Post object

                    // Check if the query is present in the post's recipeName or description
                    if (post.getTitle().toLowerCase().contains(query.toLowerCase())){
                        searchedPosts.add(post);
                    }
                }

                if (searchedPosts.isEmpty()){
                    noPostsFound.setVisibility(View.VISIBLE); // Make sure the no posts found is shown
                }
                else{
                    noPostsFound.setVisibility(View.GONE); // Make sure the no posts found is shown
                }

                // Update the RecyclerView with the search results
                updateRecyclerView(searchedPosts);

                // Hide progress bar after search results are loaded
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
                progressBar.setVisibility(View.GONE);
                noPostsFound.setVisibility(View.GONE); // Make sure the no posts found is shown
            }
        });
    }

    public void sortPosts(String sortOption) {
        progressBar.setVisibility(View.VISIBLE); // Show progress bar while sorting

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setPostKey(postSnapshot.getKey()); // Set the postKey for each Post object
                    posts.add(post);
                }

                // Sort the posts based on the selected sort option
                switch (sortOption) {
                    case "My Posts":
                        posts = sortMyPosts(posts);
                        break;
                    case "Newest":
                        posts = sortPostsByNewest(posts);
                        break;
                    case "Oldest":
                        posts = sortPostsByOldest(posts);
                        break;
                    case "Popularity (Asc)":
                        posts = sortPostsByPopularityAsc(posts);
                        break;
                    case "Popularity (Desc)":
                        posts = sortPostsByPopularityDesc(posts);
                        break;
                }

                // Update the RecyclerView with the sorted posts
                updateRecyclerView(posts);

                // Hide progress bar after sorting is done
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private List<Post> sortMyPosts(List<Post> posts) {
        String userId = mAuth.getCurrentUser().getUid();
        List<Post> myPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.getUserId().equals(userId)) {
                myPosts.add(post);
            }
        }
        return myPosts;
    }

    private List<Post> sortPostsByNewest(List<Post> posts) {
        posts.sort((post1, post2) -> Long.compare(post2.getTimeStamp(), post1.getTimeStamp()));
        return posts;
    }

    private List<Post> sortPostsByOldest(List<Post> posts) {
        posts.sort((post1, post2) -> Long.compare(post1.getTimeStamp(), post2.getTimeStamp()));
        return posts;
    }

    private List<Post> sortPostsByPopularityAsc(List<Post> posts) {
        posts.sort((post1, post2) -> Integer.compare(post1.getLikes(), post2.getLikes()));
        return posts;
    }

    private List<Post> sortPostsByPopularityDesc(List<Post> posts) {
        posts.sort((post1, post2) -> Integer.compare(post2.getLikes(), post1.getLikes()));
        return posts;
    }

    private void updateRecyclerView(List<Post> posts) {
        RecyclerView recyclerView = findViewById(R.id.post_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommunityActivity.this));
        adapter = new CommunityAdapter(CommunityActivity.this, posts, new PostClickListener() {
            @Override
            public void onPostClicked(String postKey) {
                Log.d("Community", postKey);
                // To see recipe details
                Intent postDetails = new Intent(CommunityActivity.this, PostDetailsActivity.class)
                        .putExtra("id", postKey);
                startActivity(postDetails);
            }
        }, new PostLikeClickListener() {
            @Override
            public void onLikeClicked(String postKey, int position) {
                // Find the post with the matching postKey
                String userId = mAuth.getCurrentUser().getUid();
                postsRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);

                        if (post != null && snapshot.getKey().equals(postKey)) {
                            if (post.getLikesUsers().contains(userId)) {
                                post.getLikesUsers().remove(userId);
                                post.setLikes(post.getLikes() - 1);
                            } else {
                                post.getLikesUsers().add(userId);
                                post.setLikes(post.getLikes() + 1);
                            }

                            postsRef.child(postKey).setValue(post).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    adapter.notifyItemChanged(position);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors
                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);
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