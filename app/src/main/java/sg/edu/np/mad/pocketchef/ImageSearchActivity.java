package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;
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

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;
    private ImageView imageView;
    private TextView resultTextView;
    private Interpreter tflite;
    private List<String> labels;
    private ExecutorService executorService;
    private GpuDelegate gpuDelegate;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                    new ClassifyImageTask().execute(imageBitmap);
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
                            imageView.setImageBitmap(imageBitmap);
                            new ClassifyImageTask().execute(imageBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
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

        // Create executor service for background tasks
        executorService = Executors.newFixedThreadPool(2);

        // Load the TFLite model and labels in background
        new LoadModelTask().execute();

        // Set click listeners for camera and gallery buttons
        Button cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(view -> {
            if (isEmulator()) {
                // Use a predefined image for testing on emulator
                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
                imageBitmap = resizeBitmap(imageBitmap);
                imageView.setImageBitmap(imageBitmap);
                new ClassifyImageTask().execute(imageBitmap);
            } else {
                openCamera();
            }
        });

        findViewById(R.id.galleryButton).setOnClickListener(view -> openGallery());
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
        // Close GPU delegate and TFLite interpreter
        if (gpuDelegate != null) {
            gpuDelegate.close();
        }
        if (tflite != null) {
            tflite.close();
        }
    }

    // AsyncTask to load TFLite model and labels
    private class LoadModelTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Load TFLite model from file
                MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(ImageSearchActivity.this, "food101_mobilenet_quant.tflite");
                try {
                    // Initialize GPU delegate for accelerated inference
                    gpuDelegate = new GpuDelegate();
                    Interpreter.Options options = new Interpreter.Options().addDelegate(gpuDelegate);
                    tflite = new Interpreter(tfliteModel, options);
                } catch (Exception e) {
                    // Fallback to CPU interpreter if GPU delegate initialization fails
                    if (gpuDelegate != null) {
                        gpuDelegate.close();
                        gpuDelegate = null;
                    }
                    tflite = new Interpreter(tfliteModel);
                }
                // Load labels from file
                labels = FileUtil.loadLabels(ImageSearchActivity.this, "labels.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // AsyncTask to perform image classification
    private class ClassifyImageTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
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
            // Return the predicted label and confidence score
            return labels.get(maxIndex) + ": " + outputArray[maxIndex];
        }

        @Override
        protected void onPostExecute(String result) {
            // Update result text view with classification result
            resultTextView.setText(result);
        }
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
}
