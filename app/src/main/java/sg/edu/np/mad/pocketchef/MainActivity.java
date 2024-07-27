package sg.edu.np.mad.pocketchef;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.App;
import sg.edu.np.mad.pocketchef.Models.CategoryBean;
import sg.edu.np.mad.pocketchef.Models.Notification;
import sg.edu.np.mad.pocketchef.Models.Post;
import sg.edu.np.mad.pocketchef.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    boolean isReady = false;
    private MotionLayout motionLayout;
    TextView usernameTv;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference postsRef, mUserRef;
    private static final String TAG = "MainMenu";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String CHANNEL_ID = "pocket_chef";
    private static final float SWIPE_THRESHOLD = 10; // pixels, reduce for higher sensitivity
    private static final float SWIPE_VELOCITY_THRESHOLD = 50; // pixels/second, reduce for higher sensitivity
    private ActivityMainBinding bind;

    DrawerLayout drawerLayout;
    MaterialToolbar toolbar;
    CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6, cardView7, cardView8, cardView_popularPost, cardView_newestPost, cardView_newNotifications, cardView_myPosts;

    // In-app notifications (Stage 2 - Enjia)
    ImageView notificationButton;

    // Dashboard statistics and posts (Stage 2 - Enjia)
    TextView newNotifications, myPosts, popularPostTitle, newestPostTitle, popularUsername, newestUsername, textView_popularPost, textView_newestPost;
    ImageView popularProfilePicture, newestProfilePicture;
    CardView popularPostCV, newestPostCV;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isReady) {
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                dismissSplashScreen();
                return false;
            }
        });
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //For username
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        // Initialize view binding
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        // Initialize Firebase Database
        mUserRef = FirebaseDatabase.getInstance().getReference("users");
        usernameTv = findViewById(R.id.textView_username);
        postsRef = FirebaseDatabase.getInstance().getReference("posts");

        FindViews(); // Initialize views after setContentView()
        loadProfile(); //Load username
        setUpDashboard(); // Set up dashboard

        // Set toolbar as action bar
        setSupportActionBar(toolbar);

        // Set up Notifications
        notificationButton = findViewById(R.id.notification_button);
        notificationActivity();

        //Create default favourites to test functionalities
        CreateDefaultFavorites();

        // Custom setOnTouchListener for swipe gestures (in-built Gesture Detector is not working)
        motionLayout.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;
            private VelocityTracker velocityTracker;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        if (velocityTracker == null) {
                            velocityTracker = VelocityTracker.obtain();
                        } else {
                            velocityTracker.clear();
                        }
                        velocityTracker.addMovement(event);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        velocityTracker.addMovement(event);
                        break;

                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float endY = event.getY();
                        float diffX = endX - startX;
                        float diffY = endY - startY;
                        velocityTracker.addMovement(event);
                        velocityTracker.computeCurrentVelocity(1000);
                        float velocityX = velocityTracker.getXVelocity();
                        float velocityY = velocityTracker.getYVelocity();

                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                // Right swipe
                                motionLayout.transitionToEnd();
                            } else {
                                // Left swipe
                                motionLayout.transitionToStart();
                            }
                        }
                        velocityTracker.recycle();
                        velocityTracker = null;
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        if (velocityTracker != null) {
                            velocityTracker.recycle();
                            velocityTracker = null;
                        }
                        break;
                }
                return true;
            }
        });

        // Notifications Permission
        createNotificationChannel();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
    }


    private void CreateDefaultFavorites(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CategoryBean> list = FavoriteDatabase.getInstance(MainActivity.this).categoryDao().getAllCategories();
                if(list==null||list.isEmpty()){
                    CategoryBean categoryBean =new CategoryBean(App.user,"Favorite","Favorite");
                    FavoriteDatabase.getInstance(MainActivity.this).categoryDao().insertCategory(categoryBean);
                }
            }
        }).start();
    }

    //To get username
    private void loadProfile() {
        // Read data from Firebase Database
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve data safely
                    String name = snapshot.child("name").getValue(String.class);

                    // Update UI if values are not null or empty
                    if (name != null && !name.isEmpty()) {
                        usernameTv.setText(name);
                    } else {
                        String username = snapshot.child("username").getValue(String.class);
                        usernameTv.setText(username);
                    }
                } else {
                    Log.w(TAG, "DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });

        // Card View On CLick Listener for RecipeActivity
        cardView1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
            startActivity(intent);
        });
        // Card View On Click Listener for AdvancedSearchActivity
        cardView2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdvancedSearchActivity.class);
            startActivity(intent);
        });

        // Card View On Click Listener for Favourites
        cardView3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateCategoryActivity.class);
            startActivity(intent);
        });

        // Card View On Click Listener for ShopCartActivity
        cardView4.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShopCartActivity.class);
            startActivity(intent);
        });

        // Card View On Click Listener for ComplexSearch Activity
        cardView5.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ComplexSearchActivity.class);
            startActivity(intent);
        });

        // Card View On Click Listener for Community Activity
        cardView6.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
            startActivity(intent);
        });

        // Card View On Click Listener for Pantry Activity
        cardView7.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PantryActivity.class);
            startActivity(intent);
        });

        // Card View On Click Listener for Shopping List Activity
        cardView8.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
            startActivity(intent);
        });

    }

    // Stage 2 - Enjia
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pocket Chef";
            String description = "Community Page Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // In-app notifications - check if got any notifications
    public void notificationActivity(){
        // Check if the user has any notifications and count the total of notifications the user has
        mUserRef.child(mUser.getUid()).child("notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalNotifications = (int) snapshot.getChildrenCount(); // Count number of notifications and update the dashbaord
                    Log.d(TAG, "Notifications: " + totalNotifications);
                    newNotifications.setText(totalNotifications == 0 ? "0" : String.valueOf(totalNotifications));

                    boolean hasNotifications = false;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Notification notification = dataSnapshot.getValue(Notification.class);
                        if (notification != null) {
                            // User has notifications so the button will change
                            hasNotifications = true;
                            notificationButton.setImageResource(R.drawable.baseline_notifications_active_24);
                        }
                    }
                    if (hasNotifications) {
                        Log.d(TAG, "User has notifications.");
                    } else {
                        Log.d(TAG, "No notifications for user.");
                    }
                } else {
                    newNotifications.setText("0"); // User has no notifications
                    Log.d(TAG, "No notifications found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    // Setting up dashboard and posts (Stage 2 - Enjia)
    public void setUpDashboard(){
        // Get the total number of posts
        postsRef.orderByChild("userId").equalTo(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalPosts = (int) snapshot.getChildrenCount();
                Log.d(TAG, "Total posts: " + totalPosts);
                myPosts.setText(totalPosts == 0 ? "0" : String.valueOf(totalPosts));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPosts", error.toException());
            }
        });

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Get the post with the most number of likes
                Post mostLikedPost = null;
                int maxLikes = Integer.MIN_VALUE;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setPostKey(postSnapshot.getKey());

                    if (post != null) {
                        int likes = post.getLikes();
                        if (likes > maxLikes) {
                            mostLikedPost = post;
                            maxLikes = likes;
                        }
                    }
                }
                if (mostLikedPost != null) {
                    Log.d(TAG, "Most liked post: " + mostLikedPost.getTitle());
                    popularPostTitle.setText(mostLikedPost.getTitle());

                    // Get the user name and profile picture of the user who posted the most liked post
                    String userId = mostLikedPost.getUserId();
                    getUserInfo(userId, popularUsername, popularProfilePicture);

                    // Set up on click listener for the post
                    Post finalMostLikedPost = mostLikedPost;
                    Log.d(TAG, finalMostLikedPost.getPostKey());
                    popularPostCV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // To see recipe details
                            Intent postDetails = new Intent(MainActivity.this, PostDetailsActivity.class)
                                    .putExtra("id", finalMostLikedPost.getPostKey());
                            startActivity(postDetails);
                        }
                    });
                } else {
                    Log.d(TAG, "No posts found.");
                }

                // Get the newest post
                Post newestPost = null;
                long newestTimestamp = Long.MIN_VALUE;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setPostKey(postSnapshot.getKey());

                    if (post != null) {
                        long timestamp = (long) post.getTimeStamp();
                        if (timestamp > newestTimestamp) {
                            newestPost = post;
                            newestTimestamp = timestamp;
                        }
                    }
                }
                if (newestPost != null) {
                    Log.d(TAG, "Newest post: " + newestPost.getTitle());
                    newestPostTitle.setText(newestPost.getTitle());

                    // Get the user name and profile picture of the user who posted the newest post
                    String userId = newestPost.getUserId();
                    getUserInfo(userId, newestUsername, newestProfilePicture);

                    // Set up on click listener for the post
                    Post finalNewestPost = newestPost;
                    newestPostCV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, finalNewestPost.getPostKey());
                            // To see recipe details
                            Intent postDetails = new Intent(MainActivity.this, PostDetailsActivity.class)
                                    .putExtra("id", finalNewestPost.getPostKey());
                            startActivity(postDetails);
                        }
                    });
                } else {
                    Log.d(TAG, "No posts found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPosts", error.toException());
            }
        });
    }

    private void getUserInfo(String userId, TextView usernameTextView, ImageView profilePictureImageView) {
        mUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String postUsername = snapshot.child("username").getValue(String.class);
                    String Image = snapshot.child("Image").getValue(String.class);

                    // Load profile picture
                    if (Image != null && !Image.isEmpty()) {
                        Picasso.get().load(Image).into(profilePictureImageView);
                    } else {
                        profilePictureImageView.setImageResource(R.drawable.pocketchef_logo);
                    }

                    // Update the username
                    usernameTextView.setText(postUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "getUserInfo:onCancelled", error.toException());
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void FindViews() {
        motionLayout = findViewById(R.id.main_activity);
        // Navigation Menu setup
        DrawerLayout drawerLayout = bind.drawerLayout;
        NavigationView navigationView = bind.navView;
        MaterialToolbar toolbar = bind.toolbar;


        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        navigationView.setCheckedItem(R.id.nav_search);

        // Set Up Card Views
        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        cardView6 = findViewById(R.id.cardView6);
        cardView7 = findViewById(R.id.cardView7);
        cardView8 = findViewById(R.id.cardView8);
        cardView_newestPost = findViewById(R.id.cardView_newestPost);
        cardView_popularPost = findViewById(R.id.cardView_popularPost);
        cardView_newNotifications = findViewById(R.id.cardView_newNotifications);
        cardView_myPosts = findViewById(R.id.cardView_myPosts);
        textView_newestPost = findViewById(R.id.textView_newestPost);
        textView_popularPost = findViewById(R.id.textView_popularPost);

        // Dashboard statistics and posts (Stage 2 - Enjia)
        newNotifications = findViewById(R.id.textView_new_notifications);
        myPosts = findViewById(R.id.textView_myPosts);
        popularPostCV = findViewById(R.id.cardView_popularPost);
        newestPostCV = findViewById(R.id.cardView_newestPost);
        popularPostTitle = findViewById(R.id.textView_popularPostTitle);
        newestPostTitle  = findViewById(R.id.textView_newestPostTitle);
        newestUsername = findViewById(R.id.newest_username);
        popularUsername = findViewById(R.id.popular_username);
        newestProfilePicture = findViewById(R.id.newest_profile_picture);
        popularProfilePicture = findViewById(R.id.popular_profile_picture);
    }

    private void dismissSplashScreen() {
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.postDelayed(() -> isReady = true, 3000);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            // Nothing Happens
        } else if (itemId == R.id.nav_recipes) {
            Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_profile) {
            Intent intent2 = new Intent(MainActivity.this, ProfileActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(MainActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_pantry) {
            Intent intent3 = new Intent(MainActivity.this, PantryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            Intent intent4 = new Intent(MainActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        } else if (itemId == R.id.nav_community) {
            Intent intent6 = new Intent(MainActivity.this, CommunityActivity.class);
            finish();
            startActivity(intent6);
        } else if (itemId == R.id.nav_complex_search) {
            Intent intent7 = new Intent(MainActivity.this, ComplexSearchActivity.class);
            finish();
            startActivity(intent7);
        } else if (itemId == R.id.nav_shoppinglist) {
            Intent intent8 = new Intent(MainActivity.this, ShopCartActivity.class);
            finish();
            startActivity(intent8);
        } else if (itemId == R.id.nav_locationfinder) {
            Intent intent9 = new Intent(MainActivity.this, LocationActivity.class);
            finish();
            startActivity(intent9);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}