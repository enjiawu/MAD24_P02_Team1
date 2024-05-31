package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
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

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad.pocketchef.Models.FavoriteRecipe;
import sg.edu.np.mad.pocketchef.Models.User;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //".write": "auth !== null && auth.uid === $uid"
    // Declare UI elements
    CircleImageView profile_image;
    TextView usernameTv;
    TextView nameTv, EmailTv, dobTv;
    ImageView editProfile;

    // Firebase Authentication and References
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference mUserRef;
    private static final String TAG = "ProfileActivity";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Database
        mAuth = FirebaseAuth.getInstance();
        // Get current user
        mUser = mAuth.getCurrentUser();
        // Initialize Firebase Database reference for user data
        mUserRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        editProfile = findViewById(R.id.editProfile);
        profile_image = findViewById(R.id.profile_image);
        usernameTv = findViewById(R.id.usernameTv);
        nameTv = findViewById(R.id.nameTv);
        EmailTv = findViewById(R.id.EmailTv);
        dobTv = findViewById(R.id.dobTv);

        // Set click listener for edit profile button
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open EditProfileActivity when edit profile button is clicked
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        // Load user profile data from Firebase
        loadProfile();
        setupViews();
    }

    private void setupViews() {
        // Navigation Menu set up
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_recipes = navigationView.getMenu().findItem(R.id.nav_recipes);
        nav_search = navigationView.getMenu().findItem(R.id.nav_search);
        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ProfileActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(ProfileActivity.this);
        navigationView.setCheckedItem(nav_home);
    }

    // Method to load user profile data from Firebase
    private void loadProfile() {
        // Read data from Firebase Database
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve data safely
                    String date_of_birth = snapshot.child("date-of-birth").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String profile_description = snapshot.child("profile_description").getValue(String.class);
                    String profile_picture = snapshot.child("profile_picture").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String Image = snapshot.child("Image").getValue(String.class);

                    // Log retrieved values for debugging
                    Log.d(TAG, "date_of_birth: " + date_of_birth);
                    Log.d(TAG, "email: " + email);
                    Log.d(TAG, "name: " + name);
                    Log.d(TAG, "profile_description: " + profile_description);
                    Log.d(TAG, "profile_picture: " + profile_picture);
                    Log.d(TAG, "username: " + username);

                    // Update UI if values are not null or empty
                    if (date_of_birth != null && !date_of_birth.isEmpty()) {
                        dobTv.setText(date_of_birth);
                    }

                    if (email != null && !email.isEmpty()) {
                        EmailTv.setText(email);
                    }

                    if (name != null && !name.isEmpty()) {
                        nameTv.setText(name);
                    }

                    if (Image != null && !Image.isEmpty()) {
                        Picasso.get().load(Image).into(profile_image);
                    }

                    if (username != null && !username.isEmpty()) {
                        usernameTv.setText(username);
                    }
                } else {
                    Log.w(TAG, "DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage()); // Handle database error
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile data when activity resumes
        loadProfile();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent2 = new Intent(ProfileActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(ProfileActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            Intent intent4 = new Intent(ProfileActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_profile) {
            // Nothing Happens
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(ProfileActivity.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}