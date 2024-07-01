package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {
    private ImageView backButton, recipeImage, addImageIcon;
    private TextView addRecipeImageText;
    private LinearLayout inputLayout, instructionInputLayout, ingredientsInputLayout, equipmentInputLayout;
    private TextInputEditText recipeTitleInput, proteinInput, fatInput, caloriesInput, servingsInput, prepTimeInput, costPerServingInput;
    private TextInputLayout recipeTitleBox, fatInfoBox, proteinInfoBox, caloriesInfoBox, prepTimeInfoBox, costPerServingInfoBox, servingsInputBox;
    private Button addMoreStepsButton, addMoreIngredients, addMoreEquipment, postButton;
    private List<TextInputLayout> inputBoxes, instructionsInputBoxes  = new ArrayList<>(), ingredientsInputBoxes  = new ArrayList<>(), equipmentInputBoxes = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_post);

        //Setting up views and listeners
        setupViews();
        setupListeners();
    }

    private void setupViews() {
        //Getting all the variables from the xml file
        backButton = findViewById(R.id.backIv);

        instructionInputLayout = findViewById(R.id.instruction_input_layout);
        ingredientsInputLayout = findViewById(R.id.ingredients_input_layout);
        equipmentInputLayout = findViewById(R.id.equipment_input_layout);
        addMoreStepsButton = findViewById(R.id.addMoreSteps);
        addMoreIngredients = findViewById(R.id.addMoreIngredients);
        addMoreEquipment = findViewById(R.id.addEquipment);
        postButton = findViewById(R.id.postRecipeButton);

        recipeTitleInput = findViewById(R.id.recipeTitleInput);
        recipeTitleBox = findViewById(R.id.recipeTitleBox);
        proteinInput = findViewById(R.id.proteinInput);
        proteinInfoBox = findViewById(R.id.proteinInfoBox);
        fatInput = findViewById(R.id.fatInput);
        fatInfoBox = findViewById(R.id.fatInfoBox);
        caloriesInput = findViewById(R.id.caloriesInput);
        caloriesInfoBox = findViewById(R.id.caloriesInfoBox);
        prepTimeInput = findViewById(R.id.prepTimeInfo);
        prepTimeInfoBox = findViewById(R.id.prepTimeBox);
        servingsInput = findViewById(R.id.servingsInput);
        servingsInputBox = findViewById(R.id.servingsInfoBox);
        costPerServingInput = findViewById(R.id.costPerServingInput);
        costPerServingInfoBox = findViewById(R.id.costPerServingBox);

        recipeImage = findViewById(R.id.recipeImage);
        addImageIcon = findViewById(R.id.addRecipeImageIcon);
        addRecipeImageText = findViewById(R.id.addRecipeImageText);

        // Add the initial input boxes
        TextInputLayout InstructionsInputBox = (TextInputLayout) getLayoutInflater().inflate(R.layout.input_box, null);
        instructionInputLayout.addView(InstructionsInputBox);
        instructionsInputBoxes.add(InstructionsInputBox);

        TextInputLayout IngredientInputBox = (TextInputLayout) getLayoutInflater().inflate(R.layout.input_box, null);
        IngredientInputBox.setHint("Enter Ingredient");
        ingredientsInputLayout.addView(IngredientInputBox);
        ingredientsInputBoxes.add(IngredientInputBox);

        TextInputLayout EquipmentInputBox = (TextInputLayout) getLayoutInflater().inflate(R.layout.input_box, null);
        EquipmentInputBox.setHint("Enter Equipment");
        equipmentInputLayout.addView(EquipmentInputBox);
        equipmentInputBoxes.add(EquipmentInputBox);
    }

    // Setting up listeners
    public void setupListeners() {
        // Check if back button has been clicked
        backButton.setOnClickListener(v -> {
            // Go to community activity
            Intent intent = new Intent(AddPostActivity.this, CommunityActivity.class);
            finish();
            startActivity(intent);
        });

        // Check if addMoreStepsButton button has been clicked
        addMoreStepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInputBox("instructions"); // Call add input box function if it has
            }
        });

        // Check if addMoreIngredients button has been clicked
        addMoreIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInputBox("ingredients"); // Call add input box function if it has
            }
        });

        // Check if addMoreEquipment button has been clicked
        addMoreEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInputBox("equipment"); // Call add input box function if it has
            }
        });

        // Check if post button has been clicked
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (validateData()){
                     postRecipe();
                 }
            }
        });

        recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectImageFromGallery();
            }
        });;

    }

    // Function to add new input box
    private void addInputBox(String type) {
        switch(type){
            case "instructions":
                inputBoxes = instructionsInputBoxes;
                inputLayout = instructionInputLayout;
                break;
            case "ingredients":
                inputBoxes = ingredientsInputBoxes;
                inputLayout = ingredientsInputLayout;
                break;
            case "equipment":
                inputBoxes = equipmentInputBoxes;
                inputLayout = equipmentInputLayout;
                break;
            default:
                // Nothing Happens
        }

        boolean isValid = true;

        // Check if subsequent input boxes have been filled
        for (TextInputLayout inputBox : inputBoxes) {
            TextInputEditText editText = inputBox.findViewById(R.id.instructionsInput);
            if (editText.getText().toString().trim().isEmpty()) {
                inputBox.setError("Please fill this field");
                isValid = false;
            } else {
                inputBox.setError(null);
                inputBox.setErrorEnabled(false);
            }
        }

        if (isValid) {
            TextInputLayout inputBox = (TextInputLayout) getLayoutInflater().inflate(R.layout.input_box, null);
            inputLayout.addView(inputBox);
            inputBoxes.add(inputBox);

            TextInputEditText editText = inputBox.findViewById(R.id.instructionsInput);
            textChangeListener(editText, inputLayout, inputBoxes);
        }
    }

    private void textChangeListener(TextInputEditText editText, LinearLayout inputLayout, List<TextInputLayout> inputBoxes){ // Function to check if the input in the input box has been changed
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputLayout parentLayout = (TextInputLayout) editText.getParent().getParent();
                if (s.toString().trim().isEmpty()) {
                    parentLayout.setError("Please fill this field");
                } else {
                    parentLayout.setError(null);
                    parentLayout.setErrorEnabled(false);
                }
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editText.getText().toString().trim().isEmpty()) {
                        if (inputBoxes.size() > 1) {
                            TextInputLayout parentLayout = (TextInputLayout) editText.getParent().getParent();
                            inputBoxes.remove(parentLayout);
                            inputLayout.removeView(parentLayout);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    // Function to allow user to select image from gallery
    private void selectImageFromGallery() {


    }

    private boolean validateData() {
        boolean isValid = true;

        // Check if recipe title has been filled
        if (recipeTitleInput.getText().toString().trim().isEmpty()) {
            recipeTitleBox.setError("Please fill this field");
            isValid = false;
        } else {
            recipeTitleBox.setError(null);
            recipeTitleBox.setErrorEnabled(false);
        }

        // Check if image has been added

        // Check if protein has been filled
        if (proteinInput.getText().toString().trim().isEmpty()) {
            proteinInfoBox.setError("Please fill this field");
            isValid = false;
        } else {
            proteinInfoBox.setError(null);
            proteinInfoBox.setErrorEnabled(false);
        }

        // Check if fat has been filled
        if (fatInput.getText().toString().trim().isEmpty()) {
            fatInfoBox.setError("Please fill this field");
            isValid = false;
        } else {
            fatInfoBox.setError(null);
            fatInfoBox.setErrorEnabled(false);
        }

        // Check if calories has been filled
        if (caloriesInput.getText().toString().trim().isEmpty()) {
            caloriesInfoBox.setError("Please fill this field");
            isValid = false;
        } else {
            caloriesInfoBox.setError(null);
            caloriesInfoBox.setErrorEnabled(false);
        }

        // Check if servings has been filled
        if (servingsInput.getText().toString().trim().isEmpty()) {
            servingsInputBox.setError("Please fill this field");
            isValid = false;
        } else {
            servingsInputBox.setError(null);
            servingsInputBox.setErrorEnabled(false);
        }

        // Check if prep time has been filled
        if (prepTimeInput.getText().toString().trim().isEmpty()) {
            prepTimeInfoBox.setError("Please fill this field");
            isValid = false;
        } else {
            prepTimeInfoBox.setError(null);
            prepTimeInfoBox.setErrorEnabled(false);
        }

        // Check if cost per serving has been filled
        if (costPerServingInput.getText().toString().trim().isEmpty()) {
            costPerServingInfoBox.setError("Please fill this field");
            isValid = false;
        } else {
            costPerServingInfoBox.setError(null);
            costPerServingInfoBox.setErrorEnabled(false);
        }

        // Check if step-by-step instructions have been added
        if (instructionsInputBoxes.isEmpty()) {
            Toast.makeText(this, "Please add step-by-step instructions", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Check if ingredients have been added
        if (ingredientsInputBoxes.isEmpty()) {
            Toast.makeText(this, "Please add ingredients", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    // Function to post recipe
    private void postRecipe(){}

}

