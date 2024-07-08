package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComplexSearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "CombinedSearchActivity";
    private DrawerLayout drawerLayout;
    private ImageView imageView;
    private MaterialTextView resultTextView;
    private Interpreter tflite;
    private List<String> labels;
    private ExecutorService executorService;
    private boolean isClassifiedLabelUpdated = false; // Flag to track which data was updated last
    private String classifiedLabel;
    private Set<String> foodKeywords;

    CardView  cardView_open_camera, cardView_open_gallery, cardView_start_recognition, cardView_search_recipes;
    FrameLayout frameLayout_image_camera, frameLayout_image_gallery, frameLayout_image_voice, frameLayout_image_recipes;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    MenuItem nav_home, nav_recipes, nav_search, nav_logout, nav_profile, nav_favourites, nav_community, nav_pantry, nav_complex_search;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    assert extras != null;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        Bitmap resizedBitmap = resizeBitmap(imageBitmap);
                        imageView.setImageBitmap(resizedBitmap);
                        classifyImage(resizedBitmap);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri)) {
                            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                            Bitmap resizedBitmap = resizeBitmap(imageBitmap);
                            imageView.setImageBitmap(resizedBitmap);
                            classifyImage(resizedBitmap);
                        } catch (IOException e) {
                            Log.e(TAG, "Error loading image from gallery", e);
                        }
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> speechRecognitionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && !results.isEmpty()) {
                        String recognizedText = results.get(0);
                        resultTextView.setText(recognizedText);
                        filterFoodRelatedWords(recognizedText);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex_search);

        // Initialize views
        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);

        // Intialise drawable menu
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        nav_recipes = navigationView.getMenu().findItem(R.id.nav_recipes);
        nav_search = navigationView.getMenu().findItem(R.id.nav_search);
        nav_logout = navigationView.getMenu().findItem(R.id.nav_logout);
        nav_profile = navigationView.getMenu().findItem(R.id.nav_profile);
        nav_favourites = navigationView.getMenu().findItem(R.id.nav_favourites);
        nav_pantry = navigationView.getMenu().findItem(R.id.nav_pantry);
        nav_community = navigationView.getMenu().findItem(R.id.nav_community);
        nav_complex_search = navigationView.getMenu().findItem(R.id.nav_complex_search);

        // Intialise cardViews
        cardView_open_camera = findViewById(R.id.cardView_open_camera);
        cardView_open_gallery = findViewById(R.id.cardView_open_gallery);
        cardView_start_recognition = findViewById(R.id.cardView_start_recognition);
        cardView_search_recipes = findViewById(R.id.cardView_search_recipes);

        // Intialise frameLayout
        frameLayout_image_camera = findViewById(R.id.frameLayout_image_camera);
        frameLayout_image_gallery = findViewById(R.id.frameLayout_image_gallery);
        frameLayout_image_voice = findViewById(R.id.frameLayout_image_voice);
        frameLayout_image_recipes = findViewById(R.id.frameLayout_image_recipes);

        // Create executor service for background tasks
        executorService = Executors.newFixedThreadPool(2);

        // Load the TFLite model and labels in background
        loadModelAndLabels();

        // Load food keywords
        foodKeywords = loadFoodKeywords();

        // Set click listeners for search, camera, gallery, and voice recognition buttons
        cardView_open_camera.setOnClickListener(view -> {
            if (isEmulator()) {
                // Use a predefined image for testing on emulator
                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
                Bitmap resizedBitmap = resizeBitmap(imageBitmap);
                imageView.setImageBitmap(resizedBitmap);
                classifyImage(resizedBitmap);
            } else {
                openCamera();
            }
        });

        cardView_open_gallery.setOnClickListener(view -> openGallery());

        cardView_start_recognition.setOnClickListener(v -> startSpeechRecognition());

        cardView_search_recipes.setOnClickListener(v -> navigateToSearchedQueryRecipes());
        // Set up navigation view
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(ComplexSearchActivity.this);
        navigationView.setCheckedItem(nav_home);
    }


    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            speechRecognitionLauncher.launch(intent);
        }
    }

    private void filterFoodRelatedWords(String recognizedText) {
        String lowerCaseRecognizedText = recognizedText.toLowerCase(Locale.ROOT);
        for (String keyword : foodKeywords) {
            if (lowerCaseRecognizedText.contains(keyword.toLowerCase(Locale.ROOT))) {
                resultTextView.setText(keyword);
                isClassifiedLabelUpdated = false; // Recognized text was updated last
                return;
            }
        }
    }

    private void loadModelAndLabels() {
        executorService.execute(() -> {
            try {
                MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(ComplexSearchActivity.this, "food101_mobilenet_quant.tflite");
                tflite = new Interpreter(tfliteModel);
                labels = FileUtil.loadLabels(ComplexSearchActivity.this, "labels.txt");
            } catch (IOException e) {
                Log.e(TAG, "Error loading TFLite model or labels", e);
            }
        });
    }

    private void classifyImage(Bitmap bitmap) {
        executorService.execute(() -> {
            try {
                TensorImage tensorImage = new TensorImage();
                tensorImage.load(bitmap);
                tensorImage = new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR).apply(tensorImage);

                TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 101}, tensorImage.getDataType());
                tflite.run(tensorImage.getBuffer().rewind(), outputBuffer.getBuffer().rewind());

                float[] outputArray = outputBuffer.getFloatArray();
                int maxIndex = getMaxIndex(outputArray);
                classifiedLabel = labels.get(maxIndex);
                float confidenceValue = outputArray[maxIndex];
                float confidenceLevel = (confidenceValue / 255) * 100;
                String result = String.format("This is probably a %s : %.2f%% confidence level", classifiedLabel, confidenceLevel);

                runOnUiThread(() -> {
                    resultTextView.setText(result);
                    isClassifiedLabelUpdated = true; // Classified label was updated last
                });
            } catch (Exception e) {
                Log.e(TAG, "Error during image classification", e);
                runOnUiThread(() -> resultTextView.setText("Error classifying image"));
            }
        });
    }

    private int getMaxIndex(float[] array) {
        int maxIndex = -1;
        float maxValue = Float.MIN_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private boolean isEmulator() {
        return android.os.Build.MODEL.contains("sdk") || android.os.Build.MODEL.contains("Emulator");
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        int newWidth = 224;
        int newHeight = 224;
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private Set<String> loadFoodKeywords() {
        Set<String> keywords = new HashSet<>();
        try (InputStream inputStream = getResources().openRawResource(R.raw.ingredient_to_index);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length > 0) {
                    keywords.add(parts[0].trim());
                }
            }
        } catch (IOException e) {
            // Log the error for debugging purposes
            Log.e("VoiceSearchActivity", "Error loading food keywords", e);
        }
        return keywords;
    }

    private void navigateToSearchedQueryRecipes() {
        Intent intent = new Intent(ComplexSearchActivity.this, SearchedQueryRecipesOutput.class);
        String searchQuery;

        if (isClassifiedLabelUpdated) {
            // Classified label was updated last
            searchQuery = classifiedLabel;
        } else {
            // Recognized text was updated last
            searchQuery = resultTextView.getText().toString();
        }

        intent.putExtra("SEARCH_QUERY", searchQuery);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            // Nothing Happens
        } else if (itemId == R.id.nav_recipes) {
            Intent intent = new Intent(ComplexSearchActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_profile) {
            Intent intent2 = new Intent(ComplexSearchActivity.this, ProfileActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(ComplexSearchActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_pantry) {
            Intent intent3 = new Intent(ComplexSearchActivity.this, PantryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            Intent intent4 = new Intent(ComplexSearchActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(ComplexSearchActivity.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        } else if (itemId == R.id.nav_community) {
            Intent intent6 = new Intent(ComplexSearchActivity.this, CommunityActivity.class);
            finish();
            startActivity(intent6);
        } else if (itemId == R.id.nav_complex_search) {
            Intent intent7 = new Intent(ComplexSearchActivity.this, ComplexSearchActivity.class);
            finish();
            startActivity(intent7);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
