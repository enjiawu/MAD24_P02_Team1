package sg.edu.np.mad.pocketchef;

import static android.content.ContentValues.TAG;
import static android.content.Intent.ACTION_SEND;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.squareup.picasso.Picasso;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sg.edu.np.mad.pocketchef.Adapters.PostCommentsAdapter;
import sg.edu.np.mad.pocketchef.Adapters.PostInfoAdapter;
import sg.edu.np.mad.pocketchef.Models.CategoryBean;
import sg.edu.np.mad.pocketchef.Models.Comment;
import sg.edu.np.mad.pocketchef.Models.Post;
import sg.edu.np.mad.pocketchef.Models.RecipeDetailsC;

public class PostDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    String postKey;

    //Favourites
    RecipeDetailsC recipeDetailsC;
    ProgressBar progressBar;

    // Post details
    ImageButton shareButton, favouriteButton, likeButton, addCommentButton;
    TextView recipeName, username, protein, fat, calories, servings, prepTime, costPerServing, date, noEquipmentText, noCommentsText;
    ImageView recipeImage, profilePicture;
    RecyclerView instructionsRecyclerView, ingredientsRecyclerView, equipmentRecyclerView, commentsRecyclerView;
    TextInputEditText commentInput;

    // Database
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef, mUserRef, commentsRef;
    StorageReference storageReference;
    FirebaseUser currentUser;

    //For navigation menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search, nav_logout, nav_profile, nav_favourites, nav_community, nav_pantry, nav_complex_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViews();
        setUpListeners();
        addComment();

        handleDynamicLink();

        postKey = getIntent().getStringExtra("id");

        loadPostDetails();

        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(PostDetailsActivity.this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(PostDetailsActivity.this);
        navigationView.setCheckedItem(nav_home);

        /*
        // Set up the OnClickListener for the Favorite button
        btnFavorite.setOnClickListener(v -> {
            if (recipeDetailsC == null) {
                showFavoriteDialog();
            } else {
                // if recipe is already favorite, confirm favorite
                MessageDialog.show("Unfavorite recipe", "Are you sure you want to unfavorite this recipe?", "Yes"
                        , "No").setOkButtonClickListener((dialog, v1) -> {
                    WaitDialog.show("loading...");
                    new Thread(() -> {
                        // delete recipe from favorites
                        FavoriteDatabase.getInstance(PostDetailsActivity.this)
                                .RecipeDetailsCDao().delete(recipeDetailsC);
                        recipeDetailsC = null; // update favorite status
                        runOnUiThread(() -> {
                            // update favorite button appearance
                            Glide.with(PostDetailsActivity.this).load(R.drawable.ic_btn_star).into(btnFavorite);
                            btnFavorite.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                            WaitDialog.dismiss();
                        });
                    }).start();

                    return false;
                });
            }
        });*/


    }

    // Initialize objects
    private void findViews() {
        // Initialise Progress Bar
        progressBar = findViewById(R.id.progressBar);
        // Initialise buttons
        favouriteButton = findViewById(R.id.btn_favorite);
        shareButton = findViewById(R.id.btn_share);
        likeButton = findViewById(R.id.btn_like);
        addCommentButton = findViewById(R.id.btn_addComment);
        // Initialise text views
        recipeName = findViewById(R.id.textView_meal_name);
        username = findViewById(R.id.username);
        protein = findViewById(R.id.textView_protein_value);
        fat = findViewById(R.id.textView_fat_value);
        calories = findViewById(R.id.textView_calories_value);
        servings = findViewById(R.id.textView_meal_servings);
        prepTime = findViewById(R.id.textView_meal_ready);
        costPerServing = findViewById(R.id.textView_meal_price);
        date = findViewById(R.id.date);
        noEquipmentText = findViewById(R.id.noEquipmentText);
        noCommentsText = findViewById(R.id.noCommentsText);
        //Initialize image views
        recipeImage = findViewById(R.id.imageView_meal_image);
        profilePicture = findViewById(R.id.profile_picture);
        //Initialize recycler view
        instructionsRecyclerView = findViewById(R.id.recycler_meal_instructions);
        ingredientsRecyclerView = findViewById(R.id.recycler_meal_ingredients);
        equipmentRecyclerView = findViewById(R.id.recycler_meal_equipment);
        commentsRecyclerView = findViewById(R.id.recycler_comments);
        //Initialize comment input
        commentInput = findViewById(R.id.addCommentInput);

        //Firebase database setup
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("PostImages");
        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        myRef = database.getReference("posts").push();
        // Get current user
        currentUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference("users");

        // Navigation Menu set up
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        // Initialise Menu
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_recipes = navigationView.getMenu().findItem(R.id.nav_recipes);
        nav_search = navigationView.getMenu().findItem(R.id.nav_search);
        nav_logout = navigationView.getMenu().findItem(R.id.nav_logout);
        nav_profile = navigationView.getMenu().findItem(R.id.nav_profile);
        nav_favourites = navigationView.getMenu().findItem(R.id.nav_favourites);
        nav_community = navigationView.getMenu().findItem(R.id.nav_community);
        nav_pantry = navigationView.getMenu().findItem(R.id.nav_pantry);
        nav_complex_search = navigationView.getMenu().findItem(R.id.nav_complex_search);
    };

    // Handle dynamic link in firebase to load post when link is clicked
    private void handleDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            postKey = deepLink.getLastPathSegment();
                            Log.d("DynamicLink", "Deep link received: " + deepLink.toString());
                            Log.d("DynamicLink", "Post key extracted: " + postKey);
                            loadPostDetails();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DynamicLink", "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void loadPostDetails(){
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        DatabaseReference postRef = postsRef.child(getIntent().getStringExtra("id"));
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    // Update your UI with the post information
                    recipeName.setText(post.getTitle());
                    servings.setText(post.getServings() + " Servings");
                    prepTime.setText(post.getPrepTime() + " mins");
                    costPerServing.setText(String.format("%.2f", post.getCostPerServing()) + " per Serving(s)");
                    protein.setText(post.getProtein() + " g");
                    fat.setText(post.getFat() + " g");
                    calories.setText(post.getCalories() + " kcal");
                    username.setText("@" + post.getUsername());
                    date.setText(post.formatDate());

                    // Load post image
                    Picasso.get().load(post.getRecipeImage()).into(recipeImage);

                    // Load profile picture
                    if (post.getProfilePicture() != null) {
                        Picasso.get().load(post.getProfilePicture()).into(profilePicture);
                    } else {
                        profilePicture.setImageResource(R.drawable.pocketchef_logo);
                    }


                    // Load the ingredients, instructions, and comments
                    loadInstructions(post.getInstructions());
                    loadIngredients(post.getIngredients());
                    if(post.getEquipment() != null){
                        loadEquipment(post.getEquipment());
                        noEquipmentText.setVisibility(View.GONE);
                    }
                    else{
                        noEquipmentText.setVisibility(View.VISIBLE);
                    }
                    if(post.getComments() != null){
                        loadComments();
                        noCommentsText.setVisibility(View.GONE);
                    }
                    else{
                        noCommentsText.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Handle the case where the post is null
                    Log.e("Post Details", "Post is null");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("Post Details", "Error loading post details", error.toException());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadInstructions(List<String> instructions) {
        PostInfoAdapter adapter = new PostInfoAdapter(this, instructions, "instructions");
        instructionsRecyclerView.setAdapter(adapter);
        instructionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadIngredients(List<String> ingredients) {
        PostInfoAdapter adapter = new PostInfoAdapter(this, ingredients, "ingredients");
        ingredientsRecyclerView.setAdapter(adapter);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadEquipment(List<String> equipment) {
        PostInfoAdapter adapter = new PostInfoAdapter(this, equipment, "equipment");
        equipmentRecyclerView.setAdapter(adapter);
        equipmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadComments() {
        commentsRef = FirebaseDatabase.getInstance().getReference("posts").child(postKey);
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    List<Comment> comments = post.getComments();
                    PostCommentsAdapter adapter = new PostCommentsAdapter(PostDetailsActivity.this, comments);
                    commentsRecyclerView.setAdapter(adapter);
                    commentsRecyclerView.setLayoutManager(new LinearLayoutManager(PostDetailsActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Post Details", "loadComments:onCancelled", error.toException());
            }
        });
    }

    private void setUpListeners(){
        // Share post function
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDynamicLink();
            }
        });
    }

    // Share post function
    public void createDynamicLink() {
        String postDetailsUrl = "https://www.sg.edu.np.mad.pocketchef.com/post/" + postKey;  // This URL is for informational purposes only

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse(postDetailsUrl))  // Use the informational URL here
                .setDomainUriPrefix("https://pocketchef.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(getPackageName()).build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.d("DynamicLink", "Generated link: " + dynamicLinkUri.toString());

        sharePost(dynamicLinkUri.toString());
    }
    private void sharePost(String dynamicLink) {
        Intent intent = new Intent();
        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Learn how to make " + recipeName.getText() + " by " + username.getText() + " on Pocket Chef!");
        intent.putExtra(Intent.EXTRA_TEXT, "Pocket Chef - Recipes at your fingertips.\n" + dynamicLink);

        startActivity(Intent.createChooser(intent, "Share via..."));
    }

    private void addComment() {
        progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(getIntent().getStringExtra("id"));

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = String.valueOf(commentInput.getText());

                if (TextUtils.isEmpty(commentText)) {
                    Toast.makeText(PostDetailsActivity.this, "Enter a comment!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mUserRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String username = snapshot.child("username").getValue(String.class);
                            String profilePictureUrl = snapshot.child("profile-picture").getValue(String.class);
                            String userId = currentUser.getUid();

                            Comment comment = new Comment(commentText, userId, username, profilePictureUrl);

                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Post post = dataSnapshot.getValue(Post.class);
                                    if (post != null) {
                                        List<Comment> comments = post.getComments();
                                        comments.add(comment);
                                        post.setComments(comments);
                                        postRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(PostDetailsActivity.this, "Comment has been published", Toast.LENGTH_SHORT).show();
                                                loadComments(); // Refresh the comments section
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "DatabaseError: " + databaseError.getMessage());
                                }
                            });

                            commentInput.setText("");
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.w(TAG, "DataSnapshot does not exist");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "DatabaseError: " + error.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(PostDetailsActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent = new Intent(PostDetailsActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_search) {
            Intent intent2 = new Intent(PostDetailsActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent4 = new Intent(PostDetailsActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_profile) {
            Intent intent4 = new Intent(PostDetailsActivity.this, ProfileActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(PostDetailsActivity.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        } else if (itemId == R.id.nav_community) {
            Intent intent6 = new Intent(PostDetailsActivity.this, CommunityActivity.class);
            finish();
            startActivity(intent6);
        } else if (itemId == R.id.nav_pantry) {
            Intent intent7 = new Intent(PostDetailsActivity.this, PantryActivity.class);
            finish();
            startActivity(intent7);
        } else if (itemId == R.id.nav_complex_search) {
            Intent intent8 = new Intent(PostDetailsActivity.this, ComplexSearchActivity.class);
            finish();
            startActivity(intent8);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    // Add to favorite list
    Spinner spinnerCategories;
    private String path;

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
        btTextNewCategory.setOnClickListener(v -> {
            dialog.dismiss();
            new InputDialog("Add New Category", "Enter a name for the new category", "Save", "Cancel")
                    .setCancelButton((inputDialog, view, s) -> {
                        inputDialog.dismiss();
                        return false;
                    })
                    .setOkButton((inputDialog, view, s) -> {
                        if (TextUtils.isEmpty(s)) {
                            PopTip.show("Input cannot be empty");
                            return false;
                        }

                        WaitDialog.show("loading.....");
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.execute(() -> {
                            CategoryBean categoryBean = new CategoryBean("default", s);
                            FavoriteDatabase.getInstance(PostDetailsActivity.this)
                                    .categoryDao().insertCategory(categoryBean);
                            runOnUiThread(() -> {
                                WaitDialog.dismiss();
                                PopTip.show("Successfully Added!");
                            });
                        });
                        return false;
                    }).show();
        });

        buttonSave.setOnClickListener(v -> {
            if (path == null || path.isEmpty()) {
                PopTip.show("Loading still ongoing, please wait!");
                return;
            }
            RecipeDetailsC recipeDetailsC1 = new RecipeDetailsC();
            //recipeDetailsC1.recipeDetailsResponseId = postId;
            Log.d("run", postId + "");
            recipeDetailsC1.categoryBeanId = spinnerCategories.getSelectedItem().toString();
            recipeDetailsC1.meal_servings = textView_meal_servings.getText().toString();
            recipeDetailsC1.meal_ready = textView_meal_ready.getText().toString();
            recipeDetailsC1.meal_price = textView_meal_price.getText().toString();
            recipeDetailsC1.meal_name = textView_meal_name.getText().toString();
            recipeDetailsC1.imagPath = path;
            recipeDetailsC = recipeDetailsC1;

            new Thread(() -> {
                WaitDialog.show("loading...");
                FavoriteDatabase.getInstance(PostDetailsActivity.this)
                        .RecipeDetailsCDao().insert(recipeDetailsC1);
                runOnUiThread(() -> {
                    WaitDialog.dismiss();
                    PopTip.show("Successfully Favorite!");
                    Glide.with(PostDetailsActivity.this).load(R.drawable.ic_collect).into(btnFavorite);
                    btnFavorite.setImageTintList(ColorStateList.valueOf(Color.YELLOW));
                });
            }).start();
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    // retrieve categories from database
    private void getCategories() {
        new Thread(() -> {
            List<CategoryBean> data = FavoriteDatabase.getInstance(PostDetailsActivity.this)
                    .categoryDao().getAllCategories();
            List<String> categories = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                categories.add(data.get(i).text);
            }
            runOnUiThread(() -> {
                // populate spinner with categories
                ArrayAdapter<String> adapter = new ArrayAdapter<>(PostDetailsActivity.this,
                        android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategories.setAdapter(adapter);
            });
        }).start();
    }*/
}