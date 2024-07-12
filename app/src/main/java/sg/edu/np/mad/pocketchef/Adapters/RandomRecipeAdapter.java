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

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Models.Recipe;
import sg.edu.np.mad.pocketchef.R;


public class RandomRecipeAdapter extends RecyclerView.Adapter<RandomRecipeViewHolder> {

    private final Context context;
    private final List<Recipe> list;
    private final RecipeClickListener listener;

    public RandomRecipeAdapter(Context context, List<Recipe> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RandomRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false);
        return new RandomRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RandomRecipeViewHolder holder, int position) {
        Recipe recipe = list.get(position);

        holder.textView_title.setText(recipe.title);
        holder.textView_title.setSelected(true);
        holder.textView_upvote.setText(recipe.aggregateLikes + " Upvotes");
        holder.textView_servings.setText(recipe.servings + " Servings");
        holder.textView_time.setText(recipe.readyInMinutes + " Minutes");

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

class RandomRecipeViewHolder extends RecyclerView.ViewHolder {
    final CardView random_list_container;
    final TextView textView_title;
    final TextView textView_servings;
    final TextView textView_upvote;
    final TextView textView_time;
    final ImageView imageView_food;
    final TextView textView_no_image;

    public RandomRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_servings = itemView.findViewById(R.id.textView_servings);
        textView_upvote = itemView.findViewById(R.id.textView_upvote);
        textView_time = itemView.findViewById(R.id.textView_time);
        imageView_food = itemView.findViewById(R.id.imageView_food);
        textView_no_image = itemView.findViewById(R.id.textView_no_image);

        // Set initial visibility
        textView_no_image.setVisibility(View.GONE);
        imageView_food.setVisibility(View.VISIBLE);
    }
}
