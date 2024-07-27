
package sg.edu.np.mad.pocketchef;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.snackbar.Snackbar;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sg.edu.np.mad.pocketchef.databinding.ActivityCommunityBinding;
import sg.edu.np.mad.pocketchef.databinding.ActivityComplexSearchBinding;

public class ComplexSearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ComplexSearchActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final int PERMISSION_NONE = 0;
    private static final int PERMISSION_CAMERA = 1;
    private static final int PERMISSION_GALLERY = 2;
    private static final int PERMISSION_MICROPHONE = 3;
    private int permissionRequested = PERMISSION_NONE;
    private ImageView imageView_classify;
    private MaterialTextView resultTextView;
    private Interpreter tflite;
    private List<String> labels;
    private ExecutorService executorService;
    private boolean isClassifiedLabelUpdated = false;
    private boolean isRecognizedTextUpdated = false;
    private String classifiedLabel;
    private Set<String> foodKeywords;

    CardView cardView_open_camera, cardView_open_gallery, cardView_start_recognition, cardView_search_recipes;
    FrameLayout frameLayout_image_camera, frameLayout_image_gallery, frameLayout_image_voice, frameLayout_image_recipes;
    private ActivityComplexSearchBinding bind;
    // Launcher for camera activity to capture images, for user to take a photo
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            // Specify contract for strating activity, expects a result
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Check if result is ok and data is not null
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Retrieve captured image as a bitmap from extras
                    Bundle extras = result.getData().getExtras();
                    assert extras != null;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        // Resize the Bitmap to the desired size (224x224)
                        Bitmap resizedBitmap = resizeBitmap(imageBitmap);
                        // Set the resized Bitmap to the ImageView
                        imageView_classify.setImageBitmap(resizedBitmap);
                        // Classify the image using TFLite model
                        classifyImage(resizedBitmap);
                    }
                }
            }
    );
    // Launcher for gallery activity, for user to pick an image from gallery
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            // Check if the result is ok and the data is not null
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Get the URI of selected image from the result data
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri)) {
                            // Decode the image from the input stream to a Bitmap
                            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                            // Resize the Bitmap to the desired size (224x224)
                            Bitmap resizedBitmap = resizeBitmap(imageBitmap);
                            // Set the resized Bitmap to the ImageView
                            imageView_classify.setImageBitmap(resizedBitmap);
                            // Classify the image using TFLite model
                            classifyImage(resizedBitmap);
                        } catch (IOException e) {
                            Log.e(TAG, "Error loading image from gallery", e);
                        }
                    }
                }
            }
    );
    // Launcher for speech recognition activity, for user to speak and recognize text
    private final ActivityResultLauncher<Intent> speechRecognitionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Check if result is ok and data is not null
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Get the list of recognised words from the result data
                    ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && !results.isEmpty()) {
                        // Display the first recognised word
                        String recognizedText = results.get(0);
                        // Display the recognized text in the TextView
                        resultTextView.setText(recognizedText);
                        // Filter the recognized text based on food keywords
                        filterFoodRelatedWords(recognizedText);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex_search);
        // Initialize view binding
        bind = ActivityComplexSearchBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        setUpViews();
        // Create executor service for background tasks
        executorService = Executors.newFixedThreadPool(2);

        // Load the TFLite model and labels in background
        loadModelAndLabels();

        // Load food keywords
        foodKeywords = loadFoodKeywords();

        // onClickListener for open camera function
        cardView_open_camera.setOnClickListener(view -> {
            if (isEmulator()) {
                // Use a predefined image for testing on emulator for testing
                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test4);
                Bitmap resizedBitmap = resizeBitmap(imageBitmap);
                imageView_classify.setImageBitmap(resizedBitmap);
                classifyImage(resizedBitmap);
            } else {
                if (checkAndRequestCameraPermissions()) {
                    openCamera();
                }
            }
        });

        // onClickListener for open gallery function
        cardView_open_gallery.setOnClickListener(view -> {
            if (checkAndRequestGalleryPermissions()) {
                openGallery();
            }
        });

        // onClickListener for start voice recognition function
        cardView_start_recognition.setOnClickListener(view -> {
            if (checkAndRequestVoicePermissions()) {
                startSpeechRecognition();
            }
        });

        // onClickListener for search recipes function
        cardView_search_recipes.setOnClickListener(v -> navigateToSearchedQueryRecipes());

    }

    private void setUpViews() {
        // Initialize views
        imageView_classify = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);

        // Navigation Menu setup
        DrawerLayout drawerLayout = bind.drawerLayout;
        NavigationView navigationView = bind.navView;
        MaterialToolbar toolbar = bind.toolbar;

        // Set up nav menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ComplexSearchActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(ComplexSearchActivity.this);
        navigationView.setCheckedItem(R.id.nav_complex_search);

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
    }

    // Function to check for camera permissions, prompts if required
    private boolean checkAndRequestCameraPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            permissionRequested = PERMISSION_CAMERA;
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
            return false; // Permissions requested
        }
        return true; // Permissions already granted
    }

    // Function to check for gallery permissions, prompts if required
    private boolean checkAndRequestGalleryPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            permissionRequested = PERMISSION_GALLERY;
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
            return false; // Permissions requested
        }
        return true; // Permissions already granted
    }

    // Function to check for voice permissions, prompts if required
    private boolean checkAndRequestVoicePermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            permissionRequested = PERMISSION_MICROPHONE;
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
            return false; // Permissions requested
        }
        return true; // Permissions already granted
    }

    // Function to open camera intent
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    // Function to open gallery intent
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    // Function to start speech recognition
    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            speechRecognitionLauncher.launch(intent);
        }
    }

    // Function to handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_MEDIA_IMAGES, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);

            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }

            boolean allPermissionsGranted = true;
            for (int value : perms.values()) {
                if (value != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                Log.e(TAG, "Some permissions are not granted!");
            } else {
                Log.d(TAG, "All permissions granted");
                // Open the respective feature based on the permission requested
                switch (permissionRequested) {
                    case PERMISSION_CAMERA:
                        openCamera();
                        break;
                    case PERMISSION_GALLERY:
                        openGallery();
                        break;
                    case PERMISSION_MICROPHONE:
                        startSpeechRecognition();
                        break;
                }
                permissionRequested = PERMISSION_NONE;
            }
        }
    }

    // Function to filter food related words
    private void filterFoodRelatedWords(String recognizedText) {
        // Split the recognized text into words
        String[] words = recognizedText.split("\\s+");
        // Create a list to store filtered words
        List<String> filteredWords = new ArrayList<>();
        // Iterate through each word in the list
        for (String word : words) {
            // Check if the word contains any of the food keywords
            if (foodKeywords.contains(word.toLowerCase())) {
                // Add the word to the filtered list
                filteredWords.add(word);
            }
        }
        // Join the filtered words back into a single string
        String filteredResult = String.join(" ", filteredWords);
        // Update the TextView with the filtered result
        updateUIWithFilteredResult(filteredResult);
    }

    // Function to update UI with filtered result
    private void updateUIWithFilteredResult(String filteredResult) {
        runOnUiThread(() -> {
            String displayMessage = filteredResult;
            if (displayMessage == null || displayMessage.trim().isEmpty()) {
                displayMessage = "Please try another image or voice search";
            } else {
                isRecognizedTextUpdated = true;
            }
            resultTextView.setText(displayMessage);
            resultTextView.setVisibility(View.VISIBLE);
        });
    }

    // Function to load TFLite model and labels
    private void loadModelAndLabels() {
        // Execute the loading process in a background thread
        executorService.execute(() -> {
            try {
                // Load TFLite model from assets folder
                MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(ComplexSearchActivity.this, "food101_mobilenet_quant.tflite");
                // Intialise the TFLite interpreter with the loaded model
                tflite = new Interpreter(tfliteModel);
                // Load labels from assets folder
                labels = FileUtil.loadLabels(ComplexSearchActivity.this, "labels.txt");
            } catch (IOException e) {
                // Log error if issue loading model or labels
                Log.e(TAG, "Error loading TFLite model or labels", e);
            }
        });
    }

    // Function to classify image using TFLite model
    private void classifyImage(Bitmap bitmap) {
        // Execute the classification process in a background thread
        executorService.execute(() -> {
            String resultMessage;
            boolean showSnackbar = false;
            try {
                // Load the bitmap into a TensorImage object
                TensorImage tensorImage = new TensorImage();
                tensorImage.load(bitmap);
                // Resize the image to the expected input size of the model (224, 224)
                tensorImage = new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR).apply(tensorImage);
                // Create output buffer to hold model prediction from classification result
                TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 101}, tensorImage.getDataType());
                // Run model with input image, store result in output buffer
                tflite.run(tensorImage.getBuffer().rewind(), outputBuffer.getBuffer().rewind());
                // Extract float array from output buffer
                float[] outputArray = outputBuffer.getFloatArray();
                // Get the index of the highest confidence in the output array (class with highest probability)
                int maxIndex = getMaxIndex(outputArray);
                // Map the index to the corresponding label
                classifiedLabel = labels.get(maxIndex);
                // Clean the classified label
                classifiedLabel = classifiedLabel.replace("_", " ");
                // Get the confidence value from the output array
                float confidenceValue = outputArray[maxIndex];
                // Convert confidence value to percentage, max confidence value is 255
                float confidenceLevel = (confidenceValue / 255) * 100;
                // Check if confidence value is less than 5%
                if (confidenceLevel < 5) {
                    resultMessage = "TensorFlow model unable to classify image";
                    showSnackbar = true;
                } else {
                    // Format the result message
                    resultMessage = String.format("This is probably a %s : %.2f%% confidence level", classifiedLabel, confidenceLevel);
                }
            } catch (Exception e) {
                // Log error if issue classifying image
                Log.e(TAG, "Error during image classification", e);
                resultMessage = "Error classifying image";
                showSnackbar = true;
            }
            // Update UI with the classification result
            updateUIWithClassificationResult(resultMessage, showSnackbar);
        });
    }

    // Function to update UI with classification result
    private void updateUIWithClassificationResult(String resultMessage, boolean showSnackbar) {
        runOnUiThread(() -> {
            String displayMessage = resultMessage;
            if (displayMessage == null || displayMessage.trim().isEmpty()) {
                displayMessage = "Error classifying image";
            }
            if (showSnackbar) {
                Snackbar.make(resultTextView, "Please try image classification again", Snackbar.LENGTH_LONG).show();
            } else {
                isClassifiedLabelUpdated = true;
            }
            resultTextView.setText(displayMessage);
            resultTextView.setVisibility(View.VISIBLE);
        });
    }

    // Function to get the index of the highest confidence in the output array
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

    // Function to check if the device is running on an emulator, streamlines testing due to no web camera
    private boolean isEmulator() {
        return android.os.Build.MODEL.contains("sdk") || android.os.Build.MODEL.contains("Emulator");
    }

    // Function to resize bitmap to 224x224
    private Bitmap resizeBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, 224, 224, true);
    }

    // Function to load food keywords from raw resource
    private Set<String> loadFoodKeywords() {
        // Initialize an empty set to store keywords
        Set<String> keywords = new HashSet<>();

        try {
            // Open the raw resource file for reading
            InputStream inputStream = getResources().openRawResource(R.raw.ingredient_to_index);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            // Read each line from the file
            String line;
            while ((line = reader.readLine()) != null) {
                // Split each line by ":" and add the first part (before ":") to the set of keywords
                String[] parts = line.split(":");
                if (parts.length > 0) {
                    keywords.add(parts[0].trim());
                }
            }

            // Close the reader and input stream
            reader.close();
            inputStream.close();

        } catch (IOException e) {
            // Log the error if there's an issue loading the keywords
            Log.e("VoiceSearchActivity", "Error loading food keywords", e);
        }

        // Return the set of loaded keywords
        return keywords;
    }

    private void navigateToSearchedQueryRecipes() {
        String searchQuery = null;
        if (isClassifiedLabelUpdated) {
            // Classified label was updated last
            searchQuery = classifiedLabel;
        } else if (isRecognizedTextUpdated) {
            // Recognized text was updated last
            searchQuery = resultTextView.getText().toString();
        }

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            // Show dialog to notify user
            showNotificationDialog();
        } else {
            // Proceed to start activity with the search query
            startSearchedQueryRecipesActivity(searchQuery);
        }
    }

    private void showNotificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Required");
        builder.setMessage("To search for recipes, please provide a search query.");

        // Set up the button
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle OK button click (optional)
            dialog.dismiss(); // Dismiss the dialog
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startSearchedQueryRecipesActivity(String searchQuery) {
        Intent intent = new Intent(ComplexSearchActivity.this, SearchedQueryRecipesOutput.class);
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
            // Nothing happens
        } else if (itemId == R.id.nav_shoppinglist) {
            Intent intent8 = new Intent(ComplexSearchActivity.this, ShopCartActivity.class);
            finish();
            startActivity(intent8);
        } else if (itemId == R.id.nav_locationfinder) {
            Intent intent9 = new Intent(ComplexSearchActivity.this, LocationActivity.class);
            finish();
            startActivity(intent9);
        }
        bind.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
