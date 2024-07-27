package sg.edu.np.mad.pocketchef.Adapters;


import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import sg.edu.np.mad.pocketchef.Models.IngredientsRecipesResponse;
import sg.edu.np.mad.pocketchef.Models.Ingredient;


import sg.edu.np.mad.pocketchef.PantryRecipesActivity;
import sg.edu.np.mad.pocketchef.R;
import sg.edu.np.mad.pocketchef.RecipeActivity;
import sg.edu.np.mad.pocketchef.RecipeDetailsActivity;

// Timothy - Stage 2
public class PantryRecipeAdapter extends RecyclerView.Adapter<PantryRecipeViewHolder> {
    ArrayList<IngredientsRecipesResponse> data;
    PantryRecipesActivity pantryRecipesActivity;

    public PantryRecipeAdapter(ArrayList<IngredientsRecipesResponse> input, PantryRecipesActivity activity) {
        data = input;
        pantryRecipesActivity = activity;
    }
    public PantryRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recipe = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_pantry_recipe, parent, false);
        return new PantryRecipeViewHolder(recipe);
    }


    public void onBindViewHolder(PantryRecipeViewHolder holder, int position) {
        IngredientsRecipesResponse recipe = data.get(position);

        // Load image with Picasso or show fallback if URL is null or empty
        if (recipe.image != null && !recipe.image.isEmpty()) {
            Picasso.get().load(recipe.image).into(holder.recipeImage);
            holder.noRecipeImage.setVisibility(View.GONE);
            holder.recipeImage.setVisibility(View.VISIBLE);
        } else {
            holder.noRecipeImage.setVisibility(View.VISIBLE);
            holder.recipeImage.setVisibility(View.GONE);
        }

        holder.recipeName.setText(recipe.title);

        String missingIngredientsString = "";
        for (Ingredient ingredient : recipe.missedIngredients) {
            missingIngredientsString = missingIngredientsString + " " + ingredient.name + ",";
        }
        holder.missingIngredients.append(missingIngredientsString.substring(0, missingIngredientsString.length() - 1));

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pantryRecipesActivity, RecipeDetailsActivity.class);
                intent.putExtra("id", Integer.toString(recipe.id));
                startActivity(pantryRecipesActivity, intent, null);

            }
        });
    }


    public int getItemCount() {
        return data.size();
    }
}
