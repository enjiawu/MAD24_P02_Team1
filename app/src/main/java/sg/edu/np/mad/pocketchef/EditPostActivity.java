package sg.edu.np.mad.pocketchef;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.pocketchef.Models.Comment;
import sg.edu.np.mad.pocketchef.Models.Post;

// Enjia - Stage 2
public class EditPostActivity extends AppCompatActivity {
    private static final String TAG = "EditPostActivity";
    private ActivityResultLauncher<Intent> resultLauncher;
    private ImageView backButton, recipeImage, addImageIcon;
    private TextView addRecipeImageText, addPostTitle;
    private LinearLayout inputLayout, instructionInputLayout, ingredientsInputLayout, equipmentInputLayout;
    private TextInputEditText recipeTitleInput, proteinInput, fatInput, caloriesInput, servingsInput, prepTimeInput, costPerServingInput;
    private TextInputLayout recipeTitleBox, fatInfoBox, proteinInfoBox, caloriesInfoBox, prepTimeInfoBox, costPerServingInfoBox, servingsInputBox;
    private Button addMoreStepsButton, addMoreIngredients, addMoreEquipment, postButton;
    private List<TextInputLayout> inputBoxes, instructionsInputBoxes = new ArrayList<>(), ingredientsInputBoxes = new ArrayList<>(), equipmentInputBoxes = new ArrayList<>();
    private List<Comment> comments;
    private Uri imageUri;
    private ProgressBar progressBar;
    private String  currentUserId, postId, recipeImageUrl;
    private Post post;

    // Database
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef, mUserRef;
    StorageReference storageReference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_post);

        // Get post ID from intent
        postId = getIntent().getStringExtra("postKey");
        Log.d(TAG, postId);

        // Register user to get gallery access
        registerUser();

        // Setting up views and listeners
        setupViews();
        setupListeners();

        // Load existing post data if postId is not null
        if (postId != null) {
            loadPostData(postId);
        }
    }

    private void setupViews() {
        // Initializing views as before...
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

        progressBar = findViewById(R.id.progressBar);

        addPostTitle = findViewById(R.id.addPostTitle);
        postButton = findViewById(R.id.postRecipeButton);

        addPostTitle.setText("Update Post"); // Change the text
        postButton.setText("Update Post");

        // Add the initial input boxes and set up listeners for them
        setupInitialInputBoxes();

        // Firebase database setup
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("PostImages");
        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        myRef = database.getReference("posts").child(postId);
        currentUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference("users");
    }

    // Set up the input boxes
    private void setupInitialInputBoxes() {
        TextInputLayout InstructionsInputBox = (TextInputLayout) getLayoutInflater().inflate(R.layout.input_box, null);
        instructionInputLayout.addView(InstructionsInputBox);
        instructionsInputBoxes.add(InstructionsInputBox);
        TextInputEditText instructionsEditText = InstructionsInputBox.findViewById(R.id.input);
        textChangeListener(instructionsEditText);
        textDelete(instructionsEditText, instructionInputLayout, instructionsInputBoxes);

        TextInputLayout IngredientInputBox = (TextInputLayout) getLayoutInflater().inflate(R.layout.input_box, null);
        IngredientInputBox.setHint("Enter Ingredient");
        ingredientsInputLayout.addView(IngredientInputBox);
        ingredientsInputBoxes.add(IngredientInputBox);
        TextInputEditText ingredientsEditText = IngredientInputBox.findViewById(R.id.input);
        textChangeListener(ingredientsEditText);
        textDelete(ingredientsEditText, ingredientsInputLayout, ingredientsInputBoxes);

        TextInputLayout EquipmentInputBox = (TextInputLayout) getLayoutInflater().inflate(R.layout.input_box, null);
        EquipmentInputBox.setHint("Enter Equipment");
        equipmentInputLayout.addView(EquipmentInputBox);
        equipmentInputBoxes.add(EquipmentInputBox);
        TextInputEditText equipmentEditText = EquipmentInputBox.findViewById(R.id.input);
        textChangeListener(equipmentEditText);
        textDelete(equipmentEditText, equipmentInputLayout, equipmentInputBoxes);
    }

    // Load the post data
    private void loadPostData(String postId) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    post = snapshot.getValue(Post.class);
                    if (post != null) {
                        populateFields(post);
                        comments = post.getComments();
                    }
                } else {
                    Log.w(TAG, "Post does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage()); // Handle database error
            }
        });
    }

    // Populate fields using the post data
    private void populateFields(Post post) {
        recipeTitleInput.setText(post.getTitle());
        proteinInput.setText(String.valueOf(post.getProtein()));
        fatInput.setText(String.valueOf(post.getFat()));
        caloriesInput.setText(String.valueOf(post.getCalories()));
        servingsInput.setText(String.valueOf(post.getServings()));
        prepTimeInput.setText(String.valueOf(post.getPrepTime()));
        costPerServingInput.setText(String.valueOf(post.getCostPerServing()));

        // Load image
        Picasso.get().load(post.getRecipeImage()).into(recipeImage);
        imageUri = Uri.parse(post.getRecipeImage());
        addImageIcon.setVisibility(View.GONE);
        addRecipeImageText.setVisibility(View.GONE);

        recipeImageUrl = post.getRecipeImage();

        currentUserId = post.getUserId();

        // Populate instructions, ingredients, and equipment
        populateList(instructionsInputBoxes, post.getInstructions(), instructionInputLayout);
        populateList(ingredientsInputBoxes, post.getIngredients(), ingredientsInputLayout);
        if (post.getEquipment() != null){
            populateList(equipmentInputBoxes, post.getEquipment(), equipmentInputLayout);
        }
    }

    // Populate the list for each item in equipment, ingredients or instructions
    private void populateList(List<TextInputLayout> inputBoxes, List<String> dataList, LinearLayout inputLayout) {
        inputLayout.removeAllViews();
        inputBoxes.clear();

        for (String data : dataList) {
            TextInputLayout inputBox = (TextInputLayout) getLayoutInflater().inflate(R.layout.input_box, null);
            TextInputEditText editText = inputBox.findViewById(R.id.input);
            editText.setText(data);
            inputLayout.addView(inputBox);
            inputBoxes.add(inputBox);
            if(inputLayout == ingredientsInputLayout){ // Change the hints
                inputBox.setHint("Enter Ingredient");
            } else if (inputLayout == equipmentInputLayout){
                inputBox.setHint("Enter Equipment");
            }
        }
    }

    // Setting up listeners
    public void setupListeners() {
        // Check if back button has been clicked
        backButton.setOnClickListener(v -> {
            // Go to community activity
            Intent intent = new Intent(EditPostActivity.this, CommunityActivity.class);
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
                    updatePost(post);
                }
            }
        });

        recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });;

        // Setting up text change listeners for the input fields
        textChangeListener(recipeTitleInput);
        textChangeListener(proteinInput);
        textChangeListener(fatInput);
        textChangeListener(caloriesInput);
        textChangeListener(servingsInput);
        textChangeListener(prepTimeInput);
        textChangeListener(costPerServingInput);
    }

    // Function to add new input box
    private void addInputBox(String type) {
        String hint = null;
        switch(type){
            case "instructions":
                inputBoxes = instructionsInputBoxes;
                inputLayout = instructionInputLayout;
                hint = "Instructions for this step: ";
                break;
            case "ingredients":
                inputBoxes = ingredientsInputBoxes;
                inputLayout = ingredientsInputLayout;
                hint = "Enter Ingredient: ";
                break;
            case "equipment":
                inputBoxes = equipmentInputBoxes;
                inputLayout = equipmentInputLayout;
                hint = "Enter Equipment:  ";
                break;
            default:
                // Nothing Happens
        }

        boolean isValid = true;

        // Check if subsequent input boxes have been filled
        for (TextInputLayout inputBox : inputBoxes) {
            TextInputEditText editText = inputBox.findViewById(R.id.input);
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
            inputBox.setHint(hint);

            TextInputEditText editText = inputBox.findViewById(R.id.input);
            textChangeListener(editText);
            textDelete(editText, inputLayout, inputBoxes);
        }
    }

    private void textDelete(TextInputEditText editText, LinearLayout inputLayout, List<TextInputLayout> inputBoxes){ // Function to check if the input in the input box has been deleted for instructions, ingredients and equipment

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

    private void textChangeListener(TextInputEditText editText){ {
        // Function to check if the input in the input box has been changed
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
    }
    }

    // Function to allow user to select image from gallery
    private void selectImageFromGallery() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    // Register user for image
    private void registerUser(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try{
                            // Saving image reference to a URI variable
                            imageUri = result.getData().getData();
                            recipeImage.setImageURI(imageUri);
                            // Make the text and icon invisible
                            addImageIcon.setVisibility(View.GONE);
                            addRecipeImageText.setVisibility(View.GONE);
                        }
                        catch (Exception e){ //Throw error if the user didnt select any image
                            Toast.makeText(EditPostActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
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
        if (recipeImage.getDrawable() == null) {
            Toast.makeText(this, "Please add a recipe image", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

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
        if (instructionsInputBoxes.get(0).getEditText().getText().toString().trim().isEmpty()) {
            instructionsInputBoxes.get(0).setError("Please add step-by-step instructions");
            isValid = false;
        } else {
            instructionsInputBoxes.get(0).setError(null);
            instructionsInputBoxes.get(0).setErrorEnabled(false);
        }

        // Check if ingredients have been added
        if (ingredientsInputBoxes.get(0).getEditText().getText().toString().trim().isEmpty()) {
            ingredientsInputBoxes.get(0).setError("Please add ingredients");
            isValid = false;
        } else {
            ingredientsInputBoxes.get(0).setError(null);
            ingredientsInputBoxes.get(0).setErrorEnabled(false);
        }
        return isValid;
    }

    // Function to add post
    private void updatePost(Post post) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Get the updated post data
        String title = recipeTitleInput.getText().toString().trim();
        float protein = 0;
        float fat = 0;
        float calories = 0;
        float servings = 0;
        float prepTime = 0;
        float costPerServing = 0;
        List<String> instructions = getInputBoxData(instructionsInputBoxes);
        List<String> ingredients = getInputBoxData(ingredientsInputBoxes);
        List<String> equipment = getInputBoxData(equipmentInputBoxes);

        if (!TextUtils.isEmpty(proteinInput.getText())) {
            protein = Float.parseFloat(proteinInput.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(fatInput.getText())) {
            fat = Float.parseFloat(fatInput.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(caloriesInput.getText())) {
            calories = Float.parseFloat(caloriesInput.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(servingsInput.getText())) {
            servings = Float.parseFloat(servingsInput.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(prepTimeInput.getText())) {
            prepTime = Float.parseFloat(prepTimeInput.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(costPerServingInput.getText())) {
            costPerServing = Float.parseFloat(costPerServingInput.getText().toString().trim());
        }

        // Create a map to hold only the updated values
        Map<String, Object> updates = new HashMap<>();

        // Compare each field and add to the map if it has changed
        if (!title.equals(post.getTitle())) {
            updates.put("title", title);
        }
        if (!recipeImageUrl.equals(post.getRecipeImage())) {
            updates.put("recipeImage", recipeImageUrl);
        }
        if (protein != post.getProtein()) {
            updates.put("protein", protein);
        }
        if (fat != post.getFat()) {
            updates.put("fat", fat);
        }
        if (calories != post.getCalories()) {
            updates.put("calories", calories);
        }
        if (servings != post.getServings()) {
            updates.put("servings", servings);
        }
        if (prepTime != post.getPrepTime()) {
            updates.put("prepTime", prepTime);
        }
        if (costPerServing != post.getCostPerServing()) {
            updates.put("costPerServing", costPerServing);
        }
        if (!instructions.equals(post.getInstructions())) {
            updates.put("instructions", instructions);
        }
        if (!ingredients.equals(post.getIngredients())) {
            updates.put("ingredients", ingredients);
        }
        // Remove the 'equipment' field if the list is empty
        if (equipment.isEmpty() || (equipment.size() == 1 && equipment.get(0).isEmpty())){
            myRef.child("equipment").removeValue().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(EditPostActivity.this, "Failed to remove " + "equipment", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (!equipment.equals(post.getEquipment())) {
            updates.put("equipment", equipment);
        }

        // Update the post in the Firebase Realtime Database
        myRef.updateChildren(updates)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Hide progress bar and show success message
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditPostActivity.this, "Post updated successfully", Toast.LENGTH_SHORT).show();

                        // Go back to the CommunityActivity
                        Intent intent = new Intent(EditPostActivity.this, CommunityActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Hide progress bar and show error message
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditPostActivity.this, "Failed to update post: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    // Helper method to extract data from a list of TextInputLayouts
    private List<String> getInputBoxData(List<TextInputLayout> inputBoxes) {
        List<String> dataList = new ArrayList<>();
        for (TextInputLayout inputBox : inputBoxes) {
            TextInputEditText editText = inputBox.findViewById(R.id.input);
            dataList.add(editText.getText().toString().trim());
        }
        return dataList;
    }
}
