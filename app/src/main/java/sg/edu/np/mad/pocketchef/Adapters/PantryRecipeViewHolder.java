package sg.edu.np.mad.pocketchef.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import sg.edu.np.mad.pocketchef.R;

// Timothy - Stage 2
public class PantryRecipeViewHolder extends RecyclerView.ViewHolder{
    int id;
    ImageView recipeImage;
    TextView noRecipeImage;
    TextView recipeName;
    TextView missingIngredients;
    MaterialCardView card;
    public PantryRecipeViewHolder(View recipeView) {
        super(recipeView);

        recipeImage = recipeView.findViewById(R.id.recipeImage);
        noRecipeImage = recipeView.findViewById(R.id.noRecipeImage);
        recipeName = recipeView.findViewById(R.id.recipeName);
        missingIngredients = recipeView.findViewById(R.id.missingIngredients);
        card = recipeView.findViewById(R.id.card);
    }
}
