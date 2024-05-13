package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Models.SimilarRecipeResponse;
import sg.edu.np.mad.pocketchef.R;

public class SimilarRecipeAdapter extends RecyclerView.Adapter<SimilarRecipeViewHolder> {
    Context context;
    List<SimilarRecipeResponse> list;
    RecipeClickListener listener;

    public SimilarRecipeAdapter(Context context, List<SimilarRecipeResponse> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    // Method
    @NonNull
    @Override
    public SimilarRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimilarRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_similar_recipe, parent, false));
    }

    // Method to change objects inside the CardView for the similar recipes
    @Override
    public void onBindViewHolder(@NonNull SimilarRecipeViewHolder holder, int position) {
        // Changing text in textView dependending if list.size > 1
        if (list.isEmpty()) {
            // If the list is empty, change text in Recycler View to show "No Similar Recipes Found"
            holder.textView_similar_recipe_title.setText(R.string.textView_similar_recipe_title_NoRecipes);
            holder.textView_similar_recipe_servings.setVisibility(View.GONE);
            holder.imageView_similar_recipe.setVisibility(View.GONE);
            holder.similar_recipe_holder.setClickable(false);
        } else {
            // Otherwise proceed normally
            SimilarRecipeResponse recipe = list.get(position);
            holder.textView_similar_recipe_title.setText(recipe.title);
            holder.textView_similar_recipe_servings.setText(recipe.servings + context.getString(R.string.textView_similar_recipe_servings_textEnd));
            Picasso.get().load("https://img.spoonacular.com/recipeImages/" + recipe.id + "-556x370." + recipe.imageType).into(holder.imageView_similar_recipe);
            holder.similar_recipe_holder.setOnClickListener(v -> listener.onRecipeClicked(String.valueOf(recipe.id)));
        }
    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 1 : list.size();
    }
}

class SimilarRecipeViewHolder extends RecyclerView.ViewHolder {
    CardView similar_recipe_holder;
    TextView textView_similar_recipe_title, textView_similar_recipe_servings;
    ImageView imageView_similar_recipe;

    public SimilarRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        similar_recipe_holder = itemView.findViewById(R.id.similar_recipe_holder);
        textView_similar_recipe_title = itemView.findViewById(R.id.textView_similar_recipe_title);
        textView_similar_recipe_servings = itemView.findViewById(R.id.textView_similar_recipe_servings);
        imageView_similar_recipe = itemView.findViewById(R.id.imageView_similar_recipe);
    }
}
