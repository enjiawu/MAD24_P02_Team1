package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class VoiceSearchActivity extends AppCompatActivity {

    private static final String LANGUAGE_MODEL = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
    private TextView textRecognizedInput;
    private Button buttonStartRecognition;
    private Set<String> foodKeywords;

    private final ActivityResultLauncher<Intent> speechRecognitionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && !results.isEmpty()) {
                        String recognizedText = results.get(0);
                        textRecognizedInput.setText(recognizedText);
                        filterFoodRelatedWords(recognizedText);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_search);

        textRecognizedInput = findViewById(R.id.text_recognized_input);
        buttonStartRecognition = findViewById(R.id.button_start_recognition);

        foodKeywords = loadFoodKeywords();

        buttonStartRecognition.setOnClickListener(v -> startSpeechRecognition());
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            speechRecognitionLauncher.launch(intent);
        }
    }

    private void filterFoodRelatedWords(String recognizedText) {
        String lowerCaseRecognizedText = recognizedText.toLowerCase();
        for (String keyword : foodKeywords) {
            if (lowerCaseRecognizedText.contains(keyword.toLowerCase())) {
                textRecognizedInput.setText(keyword);
                break;
            }
        }
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
            e.printStackTrace();
        }
        return keywords;
    }
}
