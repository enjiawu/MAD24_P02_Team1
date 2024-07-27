package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.PredictionsAdapter;
import sg.edu.np.mad.pocketchef.databinding.ActivityLocationBinding;

public class LocationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private PlacesClient placesClient;
    private PredictionsAdapter adapter;
    private AutocompleteSessionToken sessionToken;
    EditText searchEt;
    RecyclerView searchLocationRv;
    private ActivityLocationBinding bind;
    ImageView searchIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // Initialize view binding
        bind = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        // Set toolbar as action bar
        setSupportActionBar(bind.toolbar);
        // Navigation Menu setup
        DrawerLayout drawerLayout = bind.drawerLayout;
        NavigationView navigationView = bind.navView;
        MaterialToolbar toolbar = bind.toolbar;


        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(LocationActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(LocationActivity.this);
        navigationView.setCheckedItem(R.id.nav_locationfinder);

        searchEt = findViewById(R.id.searchEt);
        searchLocationRv = findViewById(R.id.searchLocationRv);
        searchIv = findViewById(R.id.searchIv);
        searchLocationRv.setLayoutManager(new LinearLayoutManager(this));


        Places.initialize(getApplicationContext(), getString(R.string.s_map_api));


        // Initialize the Places API
        String apiKey = getString(R.string.s_map_api);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);

        sessionToken = AutocompleteSessionToken.newInstance();

        adapter = new PredictionsAdapter(new ArrayList<>(), LocationActivity.this);
        searchLocationRv.setAdapter(adapter);

        // Set up search input

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    adapter.updatePredictions(new ArrayList<>());
                } else {
                    findPredictions(s.toString());
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            // Nothing Happens
        } else if (itemId == R.id.nav_recipes) {
            Intent intent = new Intent(LocationActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_profile) {
            Intent intent2 = new Intent(LocationActivity.this, ProfileActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(LocationActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_pantry) {
            Intent intent3 = new Intent(LocationActivity.this, PantryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            Intent intent4 = new Intent(LocationActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(LocationActivity.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        } else if (itemId == R.id.nav_community) {
            Intent intent6 = new Intent(LocationActivity.this, CommunityActivity.class);
            finish();
            startActivity(intent6);
        } else if (itemId == R.id.nav_complex_search) {
            Intent intent7 = new Intent(LocationActivity.this, ComplexSearchActivity.class);
            finish();
            startActivity(intent7);
        } else if (itemId == R.id.nav_shoppinglist) {
            Intent intent8 = new Intent(LocationActivity.this, ShopCartActivity.class);
            finish();
            startActivity(intent8);
        } else if (itemId == R.id.nav_locationfinder) {
            Intent intent9 = new Intent(LocationActivity.this, LocationActivity.class);
            finish();
            startActivity(intent9);
        }
        bind.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            bind.drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);

    }

    private void findPredictions(String query) {
        if (query.isEmpty()) {
            adapter.updatePredictions(new ArrayList<>());
            return;
        }

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("SG")
                .setSessionToken(sessionToken)
                .setQuery(query)
                .build();
        // Execute the request
        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            List<Place> places = new ArrayList<>();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                fetchPlaceDetails(prediction.getPlaceId(), places);
            }
        }).addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Toast.makeText(this, "Place not found: " + apiException.getStatusCode(), Toast.LENGTH_SHORT).show();
                //Log.e("PlacesAPI", "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    private void fetchPlaceDetails(String placeId, List<Place> placeList) {
        // Specify the fields to return
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.TYPES);

        // Construct a request object, passing the place ID and fields array
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .setSessionToken(sessionToken)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            LatLng latLng = place.getLatLng();
            placeList.add(place);
            adapter.updatePredictions(placeList);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
            }
        });
    }
}