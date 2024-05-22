package sg.edu.np.mad.pocketchef;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import sg.edu.np.mad.pocketchef.Models.FavoriteRecipe;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    boolean isReady = false;
    private MotionLayout motionLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search;

    CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
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
        FindViews(); // Initialize views after setContentView()
        // Set toolbar as action bar
        setSupportActionBar(toolbar);
        // Hide and Show Items
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_logout).setVisible(false);
        menu.findItem(R.id.nav_shoppinglist).setVisible(false);
        // Set up navigation view
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(nav_home);
        // Custom setOnTouchListener for swipe gestures (in-built Gesture Detector is not working)
        motionLayout.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private VelocityTracker velocityTracker;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        if (velocityTracker == null) {
                            velocityTracker = VelocityTracker.obtain();
                        } else {
                            velocityTracker.clear();
                        }
                        velocityTracker.addMovement(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        velocityTracker.addMovement(event);
                        velocityTracker.computeCurrentVelocity(1000);
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float diffX = endX - startX;
                        if (Math.abs(diffX) > 10 && velocityTracker != null) {
                            velocityTracker.computeCurrentVelocity(1000);
                            float velocityX = velocityTracker.getXVelocity();
                            if (Math.abs(velocityX) > 100) {
                                if (diffX > 0) {
                                    // Right swipe (forward)
                                    motionLayout.transitionToEnd();
                                } else {
                                    // Left swipe (backward)
                                    motionLayout.transitionToStart();
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return true;
            }
        });

        // Card View On CLick Listener for RecipeActivity
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                startActivity(intent);
            }
        });
        // Card View On Click Listener for AdvancedSearchActivity
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdvancedSearchActivity.class);
                startActivity(intent);
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoriteRecipesActivity.class);
                startActivity(intent);
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
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
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_recipes = navigationView.getMenu().findItem(R.id.nav_recipes);
        nav_search = navigationView.getMenu().findItem(R.id.nav_search);
        cardView1 =findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        cardView6 = findViewById(R.id.cardView6);
    }

    private void dismissSplashScreen() {
        new Handler().postDelayed(() -> isReady = true, 3000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            // Nothing happens
        } else if (itemId == R.id.nav_recipes) {
            Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_search) {
            Intent intent2 = new Intent(MainActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent2);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}