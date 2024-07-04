package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSearchActivity extends AppCompatActivity {

    private static final String TAG = "ImageSearchActivity";
    private ImageView imageView;
    private MaterialTextView resultTextView;
    private Interpreter tflite;
    private List<String> labels;
    private ExecutorService executorService;
    private String classifiedLabel;
    private MaterialButton searchButton, galleryButton, cameraButton;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        // Initialize views
        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);
        searchButton = findViewById(R.id.searchButton);
        galleryButton = findViewById(R.id.galleryButton);
        cameraButton = findViewById(R.id.cameraButton);

        // Create executor service for background tasks
        executorService = Executors.newFixedThreadPool(2);

        // Load the TFLite model and labels in background
        loadModelAndLabels();

        // Set click listeners for search, camera and gallery buttons
        cameraButton.setOnClickListener(view -> {
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
        galleryButton.setOnClickListener(view -> openGallery());
        searchButton.setOnClickListener(view -> navigateToAnotherActivity());
    }

    // Open camera to capture image
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    // Open gallery to select image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown executor service
        executorService.shutdown();
        // Close TFLite interpreter
        if (tflite != null) {
            tflite.close();
        }
    }

    // Load TFLite model and labels in background
    private void loadModelAndLabels() {
        executorService.execute(() -> {
            try {
                MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(ImageSearchActivity.this, "food101_mobilenet_quant.tflite");
                tflite = new Interpreter(tfliteModel);
                labels = FileUtil.loadLabels(ImageSearchActivity.this, "labels.txt");
            } catch (IOException e) {
                Log.e(TAG, "Error loading TFLite model or labels", e);
            }
        });
    }

    // Classify the given image
    private void classifyImage(Bitmap bitmap) {
        executorService.execute(() -> {
            try {
                // Load bitmap into TensorImage and resize to required input size
                TensorImage tensorImage = new TensorImage();
                tensorImage.load(bitmap);
                tensorImage = new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR).apply(tensorImage);

                // Prepare output buffer for classification
                TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 101}, tensorImage.getDataType());
                // Run inference on TFLite interpreter
                tflite.run(tensorImage.getBuffer().rewind(), outputBuffer.getBuffer().rewind());

                // Retrieve output probabilities and find the index of maximum probability
                float[] outputArray = outputBuffer.getFloatArray();
                int maxIndex = getMaxIndex(outputArray);
                classifiedLabel = labels.get(maxIndex);
                float confidenceValue = outputArray[maxIndex];
                float confidenceLevel = (confidenceValue / 255) * 100;
                String result = String.format("This is probably a %s : %.2f%% confidence level", classifiedLabel, confidenceLevel);

                // Update result on the UI thread
                runOnUiThread(() -> resultTextView.setText(result));
            } catch (Exception e) {
                Log.e(TAG, "Error during image classification", e);
                runOnUiThread(() -> resultTextView.setText("Error classifying image"));
            }
        });
    }

    // Utility method to find index of maximum value in an array
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

    // Utility method to check if the app is running on an emulator
    private boolean isEmulator() {
        return android.os.Build.MODEL.contains("sdk") || android.os.Build.MODEL.contains("Emulator");
    }

    // Utility method to resize bitmap to required input size
    private Bitmap resizeBitmap(Bitmap bitmap) {
        int newWidth = 224;
        int newHeight = 224;
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    // Method to navigate to another activity
    private void navigateToAnotherActivity() {
        Intent intent = new Intent(this, SearchedQueryRecipesOutput.class);
        intent.putExtra("SEARCH_QUERY", classifiedLabel);
        startActivity(intent);
    }
}
