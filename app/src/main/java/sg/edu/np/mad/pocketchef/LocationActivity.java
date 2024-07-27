package sg.edu.np.mad.pocketchef;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.PredictionsAdapter;

public class LocationActivity extends AppCompatActivity {

    private PlacesClient placesClient;
    private PredictionsAdapter adapter;
    NavigationView navigationView;
    EditText searchEt;
    RecyclerView searchLocationRv;
    private AutocompleteSessionToken sessionToken;
    ImageView searchIv,backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        backIv = findViewById(R.id.backIv);
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

        backIv.setOnClickListener(v -> onBackPressed());


    }

    private void findPredictions(String query) {
        if (query.isEmpty()) {
            adapter.updatePredictions(new ArrayList<>());
            return;
        }

        // Create a RectangularBounds object for the search area (optional)
//        RectangularBounds singaporeBounds = RectangularBounds.newInstance(
//                new LatLng(1.1304753, 103.6920359), // Southwest corner of bounds
//                new LatLng(1.4504753, 104.0910359)  // Northeast corner of bounds
//        );


        // Create a FindAutocompletePredictionsRequest object
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