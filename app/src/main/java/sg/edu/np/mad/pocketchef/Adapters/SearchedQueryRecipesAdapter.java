package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Models.SearchedQueryRecipe;
import sg.edu.np.mad.pocketchef.R;

public class SearchedQueryRecipesAdapter extends RecyclerView.Adapter<SearchedQueryRecipeViewHolder> {

    private final Context context;
    private final List<SearchedQueryRecipe> list;
    private final RecipeClickListener listener;

    public SearchedQueryRecipesAdapter(Context context, List<SearchedQueryRecipe> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchedQueryRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_query_recipe_item, parent, false);
        return new SearchedQueryRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedQueryRecipeViewHolder holder, int position) {
        SearchedQueryRecipe recipe = list.get(position);

        holder.textView_title.setText(recipe.title);
        holder.textView_title.setSelected(true);
        holder.textView_servings.setText(recipe.servings+ " Servings");
        holder.textView_time.setText(recipe.readyInMinutes + " min");
        holder.textView_money.setText(String.valueOf(recipe.pricePerServing));
        if (recipe.nutrition != null && recipe.nutrition.nutrients != null && recipe.nutrition.nutrients.size() >= 3) {
            //If have then format everything nicely
            holder.textView_calories.setText((int) recipe.nutrition.nutrients.get(0).amount + " " + recipe.nutrition.nutrients.get(0).unit);
            holder.textView_protein.setText((int) recipe.nutrition.nutrients.get(1).amount + " " + recipe.nutrition.nutrients.get(1).unit);
            holder.textView_carbs.setText((int) recipe.nutrition.nutrients.get(2).amount + " " + recipe.nutrition.nutrients.get(2).unit);
        } else {
            //If don't have, then set as "N/A"
            holder.textView_calories.setText("N/A");
            holder.textView_protein.setText("N/A");
            holder.textView_carbs.setText("N/A");
        }
        // Load image with Picasso or show fallback if URL is null or empty
        if (recipe.image != null && !recipe.image.isEmpty()) {
            Picasso.get().load(recipe.image).into(holder.imageView_food);
            holder.textView_no_image.setVisibility(View.GONE);
            holder.imageView_food.setVisibility(View.VISIBLE);
        } else {
            holder.textView_no_image.setVisibility(View.VISIBLE);
            holder.imageView_food.setVisibility(View.GONE);
        }

        // Set click listener
        holder.random_list_container.setOnClickListener(v -> {
            listener.onRecipeClicked(String.valueOf(recipe.id));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


// Preparing view holder
class SearchedQueryRecipeViewHolder extends RecyclerView.ViewHolder {
    final MaterialCardView random_list_container;
    final TextView textView_title;
    final TextView textView_servings;
    final TextView textView_time;
    final TextView textView_money;
    final TextView textView_calories;
    final TextView textView_protein;
    final TextView textView_carbs;
    final ImageView imageView_food;
    final TextView textView_no_image;

    public SearchedQueryRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_servings = itemView.findViewById(R.id.textView_servings);
        textView_time = itemView.findViewById(R.id.textView_time);
        textView_money = itemView.findViewById(R.id.textView_money);
        textView_calories = itemView.findViewById(R.id.textView_calories);
        textView_protein = itemView.findViewById(R.id.textView_protein);
        textView_carbs = itemView.findViewById(R.id.textView_carbs);
        imageView_food = itemView.findViewById(R.id.imageView_food);
        textView_no_image = itemView.findViewById(R.id.textView_no_image);

        // Set initial visibility
        textView_no_image.setVisibility(View.GONE);
        imageView_food.setVisibility(View.VISIBLE);
    }
}
