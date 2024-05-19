package sg.edu.np.mad.pocketchef;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

public class AddToFavoritesDialog extends Dialog {
    private Context context;
    private int recipeId;
    private Spinner categorySpinner;
    private EditText editTextNewCategory;
    private Button buttonSave, buttonCancel;

    public AddToFavoritesDialog(@NonNull Context context, int recipeId) {
        super(context);
        this.context = context;
        this.recipeId = recipeId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_to_favorites);

        categorySpinner = findViewById(R.id.spinner_categories);
        editTextNewCategory = findViewById(R.id.edit_new_category);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);

        loadCategories();

        buttonSave.setOnClickListener(v -> saveToFavorites());
        buttonCancel.setOnClickListener(v -> dismiss());
    }

    private void loadCategories() {
        // Fetch categories from the database or data source
        List<String> categories = Database.getInstance().getFavoriteCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void saveToFavorites() {
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String newCategory = editTextNewCategory.getText().toString().trim();

        if (!newCategory.isEmpty()) {
            // Add new category to database
            Database.getInstance().addFavoriteCategory(newCategory);
            selectedCategory = newCategory;
        }

        // Save recipe to the selected category
        Database.getInstance().addRecipeToCategory(recipeId, selectedCategory);
        Toast.makeText(context, "Recipe added to favorites", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
