package sg.edu.np.mad.pocketchef;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
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
import java.util.List;

import sg.edu.np.mad.pocketchef.Models.Comment;
import sg.edu.np.mad.pocketchef.Models.Post;

public class AddPostActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> resultLauncher;
    private ImageView backButton, recipeImage, addImageIcon;
    private TextView addRecipeImageText;
    private LinearLayout inputLayout, instructionInputLayout, ingredientsInputLayout, equipmentInputLayout;
    private TextInputEditText recipeTitleInput, proteinInput, fatInput, caloriesInput, servingsInput, prepTimeInput, costPerServingInput;
    private TextInputLayout recipeTitleBox, fatInfoBox, proteinInfoBox, caloriesInfoBox, prepTimeInfoBox, costPerServingInfoBox, servingsInputBox;
    private Button addMoreStepsButton, addMoreIngredients, addMoreEquipment, postButton;
    private List<TextInputLayout> inputBoxes, instructionsInputBoxes  = new ArrayList<>(), ingredientsInputBoxes  = new ArrayList<>(), equipmentInputBoxes = new ArrayList<>();
    private Uri imageUri;
    private ProgressBar progressBar;
    private String currentUsername, currentUserId, currentProfilePictureUrl;

    // Database
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef, mUserRef;
    StorageReference storageReference;
    FirebaseUser currentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_post);

        //Register user to get gallery access
        registerUser();

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

        progressBar = findViewById(R.id.progressBar);

        // Add the initial input boxes and set up listeners for them
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

        //Firebase database setup
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("PostImages");
        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        myRef = database.getReference("posts").push();
        // Get current user
        currentUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference("users");
    }

    /*
    * Potential features to add:
    * Drag and drop step by step instructions for easier reorganisation
    * Allow users to save their post draft
    */

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
                     addPost();
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
                            Toast.makeText(AddPostActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
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
    private void addPost() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Access firebase storage
        final StorageReference imageFilePath = storageReference.child(imageUri.getLastPathSegment());
        imageFilePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageDownloadLink = uri.toString();

                        // Collate instructions, ingredients, and equipment into lists
                        List<String> instructions = new ArrayList<>();
                        for (TextInputLayout inputBox : instructionsInputBoxes) {
                            TextInputEditText editText = inputBox.findViewById(R.id.input);
                            String instruction = editText.getText().toString().trim();
                            if (!instruction.isEmpty()) {
                                instructions.add(instruction);
                            }
                        }

                        List<String> ingredients = new ArrayList<>();
                        for (TextInputLayout inputBox : ingredientsInputBoxes) {
                            TextInputEditText editText = inputBox.findViewById(R.id.input);
                            String ingredient = editText.getText().toString().trim();
                            if (!ingredient.isEmpty()) {
                                ingredients.add(ingredient);
                            }
                        }

                        List<String> equipment = new ArrayList<>();
                        for (TextInputLayout inputBox : equipmentInputBoxes) {
                            TextInputEditText editText = inputBox.findViewById(R.id.input);
                            String equipmentItem = editText.getText().toString().trim();
                            if (!equipmentItem.isEmpty()) {
                                equipment.add(equipmentItem);
                            }
                        }

                        // Checking what the saved equipment, instructions and ingredients are
                        Log.d("Add Post Activity", equipment.toString());
                        Log.d("Add Post Activity", instructions.toString());
                        Log.d("Add Post Activity", ingredients.toString());
                        mUserRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Retrieve data safely
                                    String username = snapshot.child("username").getValue(String.class);
                                    String profilePictureUrl = snapshot.child("profile-picture").getValue(String.class);

                                    // Get username and user id of the user who made the post
                                    currentUsername = username;
                                    currentUserId = currentUser.getUid();
                                    currentProfilePictureUrl = profilePictureUrl;

                                    // Create post object
                                    Post post = new Post(
                                            recipeTitleInput.getText().toString().trim(),
                                            imageDownloadLink,
                                            Float.parseFloat(proteinInput.getText().toString().trim()),
                                            Float.parseFloat(fatInput.getText().toString().trim()),
                                            Float.parseFloat(caloriesInput.getText().toString().trim()),
                                            Float.parseFloat(servingsInput.getText().toString().trim()),
                                            Float.parseFloat(prepTimeInput.getText().toString().trim()),
                                            Float.parseFloat(costPerServingInput.getText().toString().trim()),
                                            instructions,
                                            ingredients,
                                            equipment,
                                            currentUsername,
                                            new ArrayList<Comment>(),
                                            currentUserId,
                                            currentProfilePictureUrl
                                    );

                                    // Add post data to firebase database
                                    myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(AddPostActivity.this, "Post has been published", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    progressBar.setVisibility(View.GONE);

                                    new Handler().postDelayed(new Runnable() { // Delay by 2 seconds so they can see the message
                                        @Override
                                        public void run() {
                                            // Go to community activity and see new post
                                            Intent intent = new Intent(AddPostActivity.this, CommunityActivity.class);
                                            finish();
                                            startActivity(intent);
                                        }
                                    }, 1000);
                                } else {
                                    Log.w(TAG, "DataSnapshot does not exist");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "DatabaseError: " + error.getMessage()); // Handle database error
                            }
                        });


                    }
                });
            }
        });

    }
}



