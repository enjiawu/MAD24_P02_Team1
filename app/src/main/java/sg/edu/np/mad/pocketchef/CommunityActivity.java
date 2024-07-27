package sg.edu.np.mad.pocketchef;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.WaitDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.CommunityAdapter;
import sg.edu.np.mad.pocketchef.Listener.PostClickListener;
import sg.edu.np.mad.pocketchef.Listener.PostLikeClickListener;
import sg.edu.np.mad.pocketchef.Listener.PostOnHoldListener;
import sg.edu.np.mad.pocketchef.Models.Comment;
import sg.edu.np.mad.pocketchef.Models.Notification;
import sg.edu.np.mad.pocketchef.Models.Post;

// Enjia - Stage 2
public class CommunityActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "CommunityActivity";

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
    private RecyclerView recyclerView;

    // Database
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference postsRef, mUserRef;
    StorageReference storageReference;
    FirebaseUser currentUser;

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
        recyclerView = findViewById(R.id.post_recycler_view);

        noPostsFound.setVisibility(View.GONE); // Make sure the no posts found is hidden

        //Firebase database setup
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("PostImages");
        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        postsRef = database.getReference("posts");
        currentUser = mAuth.getCurrentUser(); // Get current user
        mUserRef = FirebaseDatabase.getInstance().getReference("users");

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
                    noPostsFound.setVisibility(View.GONE); // Make sure the no posts found is shown
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
            Log.d(TAG, "Add Post");
            // Go to add post activity
            Intent intent = new Intent(CommunityActivity.this, AddPostActivity.class);
            finish();
            startActivity(intent);
        });
    }

    // Loading community posts
    public void setupSearchedRecipeRecyclerView() {
        progressBar.setVisibility(VISIBLE); // Making the progress bar visible as the posts get loaded

        // Initialize the RecyclerView and Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(CommunityActivity.this));
        adapter = new CommunityAdapter(CommunityActivity.this, new ArrayList<>(), postClickListener, postLikeClickListener, postOnHoldListener);
        recyclerView.setAdapter(adapter);

        // Add a listener for data changes
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setPostKey(postSnapshot.getKey()); // Set the postKey for each Post object

                    posts.add(post);
                }

                // Update the existing adapter's data
                adapter.setPosts(sortPostsByNewest(posts));
                adapter.notifyDataSetChanged();

                // Making the progress bar disappear after posts get loaded
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
            }
        });
    }

    private final PostClickListener postClickListener = postKey -> {
        Log.d(TAG, postKey);
        // To see recipe details
        Intent postDetails = new Intent(CommunityActivity.this, PostDetailsActivity.class)
                .putExtra("id", postKey);
        startActivity(postDetails);
    };

    private final PostLikeClickListener postLikeClickListener = (postKey, position) -> {
        mUserRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String userId = currentUser.getUid();

                    // Find the post with the matching postKey
                    postsRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Post post = snapshot.getValue(Post.class);
                            post.setPostKey(snapshot.getKey());

                            if (post != null && snapshot.getKey().equals(postKey)) {
                                if (post.getLikesUsers().contains(userId)) {
                                    post.getLikesUsers().remove(userId);
                                    post.setLikes(post.getLikes() - 1);

                                    // Delete the notification
                                    deleteNotification(post.getUserId(), post.getPostKey() + "_" + userId);
                                } else {
                                    post.getLikesUsers().add(userId);
                                    post.setLikes(post.getLikes() + 1);

                                    // Send notification to the post owner
                                    String title = "Someone liked your post!";
                                    String message = "Your post on '" + post.getTitle() + "' was liked by @" + username;
                                    sendNotificationToPostOwner(post.getUserId(), post.getPostKey(), title, message);
                                }

                                postsRef.child(postKey).setValue(post).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        adapter.notifyItemChanged(position);
                                    }
                                });
                            }

                            sortPosts(spinner.getSelectedItem().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });
    };

    private final PostOnHoldListener postOnHoldListener = (postKey, position) -> {

        postsRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);

                String currentUserId = mAuth.getCurrentUser().getUid();

                // Check if the current user is the owner of the post
                if (!post.getUserId().equals(currentUserId)) {
                    // If the user does not own the post, do nothing
                    return;
                }

                String[] options = {"Edit Post", "Delete Post"};

                BottomMenu.show(options)
                    .setMessage(Html.fromHtml("<b>Post Options</b>"))
                    .setOnMenuItemClickListener((dialog, text, index) -> {
                        if (index == 0) {
                            // Handle Edit Post
                            handleEditPost(postKey);
                        } else if (index == 1) {
                            // Handle Delete Post
                            handleDeletePost(postKey, position);
                        }
                        dialog.dismiss();
                        return true;
                    });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
            }
        });


    };

    // Handle notifications
    private void sendNotificationToPostOwner(String postOwnerId, String id, String notificationTitle, String notificationMessage) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(postOwnerId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG,  currentUser.getUid() + "_" + postOwnerId);
                if (!currentUser.getUid().equals(postOwnerId)){ // If the post belongs to the user, ignore notifications
                    long timestamp = System.currentTimeMillis(); // Get current time as timestamp

                    // Get a reference to the notifications node for this user
                    DatabaseReference notificationsRef = userRef.child("notifications");

                    // Push the new notification to the notifications list with a unique ID
                    String notificationId = id + "_" + currentUser.getUid()
                            ; // Unique ID based on post and user

                    // Create a new notification object
                    Notification notification = new Notification(notificationId, notificationTitle, notificationMessage, timestamp);


                    notificationsRef.child(notificationId).setValue(notification)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification successfully added"))
                            .addOnFailureListener(e -> Log.e(TAG, "Error adding notification: " + e.getMessage()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });
    }

    // Deleting a notification
    private void deleteNotification(String postOwnerId, String notificationId) {
        // Get a reference to the user's notifications node
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(postOwnerId)
                .child("notifications");

        // Remove the specific notification by its ID
        notificationsRef.child(notificationId).removeValue()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification successfully deleted"))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting notification: " + e.getMessage()));
    }


    // Function to edit post if it belongs to the user
    private void handleEditPost(String postKey) {
        Log.d(TAG, postKey);
        // Create an intent to start the AddPostActivity
        Intent intent = new Intent(CommunityActivity.this, EditPostActivity.class);
        // Pass the post ID to the intent
        intent.putExtra("postKey", postKey);
        // Finish the current activity and start the EditPostActivity
        finish();
        startActivity(intent);
    }

    // Function to delete post if it belongs to the user
    private void handleDeletePost(String postKey, int position) {
        // Confirm deletion with a dialog
        BottomMenu.show(Collections.singletonList("Are you sure you want to delete this post?"))
            .setOnMenuItemClickListener((dialog, text, index) -> {
                if (index == 0) { // User confirmed deletion
                    WaitDialog.show("Deleting...");

                    // Perform the deletion in a separate thread to avoid blocking the UI
                    postsRef.child(postKey).removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    runOnUiThread(() -> {
                                        adapter.removePost(position); // Custom method to remove the item from the adapter
                                        adapter.notifyItemRemoved(position);
                                        WaitDialog.dismiss();
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        // Handle the failure, e.g., show a Toast message
                                        WaitDialog.dismiss();
                                        Toast.makeText(CommunityActivity.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                }
                dialog.dismiss();
                return true;
            });
    }


    // Funciton to search for post
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

    // Function to sort posts based on chosen option
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

    // Sort the posts by chosen option (my posts, newest, oldest, popularity)
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
        posts.sort((post1, post2) -> Long.compare((long) post2.getTimeStamp(), (long) post1.getTimeStamp()));
        return posts;
    }

    private List<Post> sortPostsByOldest(List<Post> posts) {
        posts.sort((post1, post2) -> Long.compare((long) post1.getTimeStamp(), (long) post2.getTimeStamp()));
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

    // For searching posts and sorting
    private void updateRecyclerView(List<Post> posts) {
        adapter.setPosts(posts); // Make sure to have a method to set new data in the adapter
        adapter.notifyDataSetChanged();
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