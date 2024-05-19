package sg.edu.np.mad.pocketchef;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditFavoriteActivity extends AppCompatActivity {
    EditText editTextRecipeName;
    Button buttonSave, buttonCancel;
    int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_favorite);

        editTextRecipeName = findViewById(R.id.edit_recipe_name);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);

        recipeId = getIntent().getIntExtra("recipeId", -1);

        // Load the recipe details for editing
        loadRecipeDetails(recipeId);

        buttonSave.setOnClickListener(view -> {
            saveRecipeDetails();
            Toast.makeText(EditFavoriteActivity.this, "Recipe updated", Toast.LENGTH_SHORT).show();
            finish();
        });

        buttonCancel.setOnClickListener(view -> finish());
    }

    private void loadRecipeDetails(int recipeId) {
        // Fetch the recipe details from the database or data source using the recipeId
        FavoriteRecipe recipe = Database.getInstance().getFavoriteRecipeById(recipeId);
        if (recipe != null) {
            editTextRecipeName.setText(recipe.getName());
            // Populate other fields if there are any
        }
    }

    private void saveRecipeDetails() {
        // Save the updated recipe details back to the database or data source
        String updatedName = editTextRecipeName.getText().toString().trim();
        if (!updatedName.isEmpty()) {
            Database.getInstance().updateFavoriteRecipe(recipeId, updatedName);
            // Update other fields if there are any
        }
    }
}
