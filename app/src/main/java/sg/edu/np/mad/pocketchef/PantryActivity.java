package sg.edu.np.mad.pocketchef;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import sg.edu.np.mad.pocketchef.Adapters.PantryIngredientAdapter;

// Timothy - Stage 2
public class PantryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference mPantryRef;
    FirebaseUser mUser;

    RecyclerView pantryRecyclerView;
    ExtendedFloatingActionButton availableRecipesButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    MenuItem navHome;
    BottomAppBar bottomAppBar;
    ArrayList<String> ingredientList = new ArrayList<>();
    ArrayList<String> selectedIngredients = new ArrayList<>();
    EditText addIngredientEditText;
    PantryIngredientAdapter pantryAdapter;
    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");

        mUser = mAuth.getCurrentUser();
        // Get pantry reference
        mPantryRef = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid());

        FindViews();
        SetUpListeners();

        // Load user pantry
        mPantryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve data safely
                    String pantry = snapshot.child("pantry").getValue(String.class);
                    Log.d("PA",pantry);
                    // Remove the square brackets and split the string by commas
                    String[] words = pantry.substring(1, pantry.length() - 1).split(", ");

                    Log.d("W", Arrays.toString(words));
                    // Create an ArrayList from the array of words
                    ingredientList = new ArrayList<>(Arrays.asList(words));
                    pantryAdapter = new PantryIngredientAdapter(ingredientList, PantryActivity.this);

                    LinearLayoutManager pantryLayoutManager = new LinearLayoutManager(PantryActivity.this);

                    pantryRecyclerView.setLayoutManager(pantryLayoutManager);
                    pantryRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    pantryRecyclerView.setAdapter(pantryAdapter);

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                    itemTouchHelper.attachToRecyclerView(pantryRecyclerView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });





    }

    public void SelectIngredient(String s) {
        selectedIngredients.add(s);
    }

    public void RemoveIngredient(String s) {
        selectedIngredients.remove(s);
    }

    private void FindViews() {
        pantryRecyclerView = findViewById(R.id.pantryRecyclerView);
        availableRecipesButton = findViewById(R.id.availableRecipesButton);
        addIngredientEditText = findViewById(R.id.addIngredientEditText);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        navHome = navigationView.getMenu().findItem(R.id.nav_home);
        bottomAppBar = findViewById(R.id.bottomAppBar);

        background = findViewById(R.id.background);

        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(PantryActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(PantryActivity.this);
        navigationView.setCheckedItem(navHome);
    }

    private void SetUpListeners() {
        // Add ingredient edittext
        addIngredientEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !addIngredientEditText.getText().toString().trim().isEmpty()) {
                    ingredientList.add(addIngredientEditText.getText().toString());
                    mPantryRef.child("pantry").setValue(Arrays.toString(ingredientList.toArray()));


                    pantryAdapter.notifyItemInserted(ingredientList.size() - 1);
                    addIngredientEditText.setText(null);
                }
            }
        });

        availableRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent availableRecipes = new Intent(PantryActivity.this, PantryRecipesActivity.class);
                availableRecipes.putStringArrayListExtra("ingredients", selectedIngredients);
                startActivity(availableRecipes);
            }
        });


        bottomAppBar.setOnMenuItemClickListener (new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.deleteIngredient) {
                    Log.d("CLICK","click");
                    if (!selectedIngredients.isEmpty()) {
                        // Dialog to confirm delete
                        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(PantryActivity.this);
                        alert.setTitle("Deleting " + selectedIngredients.size() + " ingredients");
                        alert.setMessage("Are you sure you want to delete these items? This action cannot be undone.");

                        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int count = selectedIngredients.size();
                                for (String i : selectedIngredients) {
                                    int position = ingredientList.indexOf(i);
                                    ingredientList.remove(i);
                                    pantryAdapter.notifyItemRemoved(position);

                                }
                                selectedIngredients.clear();

                                mPantryRef.child("pantry").setValue(Arrays.toString(ingredientList.toArray()));
                                Toast.makeText(PantryActivity.this, count + "ingredients deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                        alert.show();

                    }
                }


                return true;
            }
        });


    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,  ItemTouchHelper.START | ItemTouchHelper.END) {


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            // Re-order pantry items
            int fromPosition = viewHolder.getAdapterPosition();
            Log.d("FROM", String.valueOf(fromPosition));
            int toPosition = target.getAdapterPosition();
            Log.d("TO", String.valueOf(toPosition));
            Collections.swap(ingredientList,fromPosition, toPosition);
            mPantryRef.child("pantry").setValue(Arrays.toString(ingredientList.toArray()));
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Swipe to delete
            if (direction == ItemTouchHelper.START) {
                // Remove ingredient from selected ingredients
                if (selectedIngredients.contains(ingredientList.get(viewHolder.getAdapterPosition()))) {
                    selectedIngredients.remove(ingredientList.get(viewHolder.getAdapterPosition()));
                }
                ingredientList.remove(viewHolder.getAdapterPosition());
                mPantryRef.child("pantry").setValue(Arrays.toString(ingredientList.toArray()));
                pantryAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

            } else if (direction == ItemTouchHelper.END) { // Swipe to edit
                EditText updatedIngredient = new EditText(PantryActivity.this); // EditText to enter new ingredient

                // Dialog to edit ingredient
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(PantryActivity.this);
                alert.setTitle("Editing ingredient");
                alert.setView(updatedIngredient);
                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ingredientList.set(viewHolder.getAdapterPosition(), updatedIngredient.getText().toString());
                        mPantryRef.child("pantry").setValue(Arrays.toString(ingredientList.toArray()));
                        pantryAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pantryAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        return;
                    }
                });
                alert.show();

            }

        }
    };

    // Event to clear focus when user clicks outside edittext
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
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