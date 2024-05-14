package sg.edu.np.mad.pocketchef;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

public class NutritionDialogActivity extends AppCompatActivity {
    private ImageView imageView_nutrition;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutrition_dialog);
        setUpViews();
        // Retrieve the recipe ID from the Intent
        recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        if (recipeId == -1) {
            // Handle the error: Recipe ID was not passed correctly
            finish();
            return;
        }
        // Load the nutrition label image
        loadNutritionLabelImage();
    }

    private void setUpViews() {
        imageView_nutrition = findViewById(R.id.imageView_nutrition);
        // Other view initialization...
    }

    private void loadNutritionLabelImage() {
        // Construct the URL for the nutrition label image
        String nutritionLabelUrl = "https://api.spoonacular.com/recipes/" + recipeId + "/nutritionLabel.png?apiKey=" + getString(R.string.api_key);
        // Load the image using Picasso
        Picasso.get().load(nutritionLabelUrl)
                .fit()
                .centerInside()
                .into(imageView_nutrition);
    }
}

