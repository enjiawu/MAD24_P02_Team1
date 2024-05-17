package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Models.Recipe;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipe;
import sg.edu.np.mad.pocketchef.R;
import sg.edu.np.mad.pocketchef.SearchedRecipesOutput;

public class SearchedRecipesAdapter extends RecyclerView.Adapter<SearchedRecipesViewHolder>{
    //Content object to access resource and layout inflater

    Context context;
    List<SearchedRecipe> list;
    RecipeClickListener listener;

    public SearchedRecipesAdapter(Context context, List<SearchedRecipe> list, RecipeClickListener listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchedRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new SearchedRecipesViewHolder(LayoutInflater.from(context).inflate(R.layout.list_searched_recipe, parent, false));
    }

    //Override method to input items from api to SearchedRecipesViewHolder
    @Override
    public void onBindViewHolder(@NonNull SearchedRecipesViewHolder holder, int position) {
        SearchedRecipe recipe = list.get(position);

        holder.textView_title.setText(recipe.title);
        holder.textView_title.setSelected(true);

        if (recipe.nutrition != null && recipe.nutrition.nutrients != null && recipe.nutrition.nutrients.size() >= 3) { //Check if got nutrition information
            holder.textView_calories.setText((int) recipe.nutrition.nutrients.get(0).amount + " " + recipe.nutrition.nutrients.get(0).unit);
            holder.textView_protein.setText((int) recipe.nutrition.nutrients.get(1).amount + " " + recipe.nutrition.nutrients.get(1).unit);
            holder.textView_carbs.setText((int) recipe.nutrition.nutrients.get(2).amount + " " + recipe.nutrition.nutrients.get(2).unit);
        } else {
            holder.textView_calories.setText("N/A");
            holder.textView_protein.setText("N/A");
            holder.textView_carbs.setText("N/A");
        }

        Picasso.get().load(recipe.image).into(holder.imageView_food); //Use picasso to load images

        holder.searched_recipes_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    } //Make sure that item count can be empty since the API might not return recipes
}

// Preparing viewholder
class SearchedRecipesViewHolder extends RecyclerView.ViewHolder{
    CardView searched_recipes_container;
    TextView textView_title, textView_calories, textView_protein, textView_carbs;
    ImageView imageView_food;


    // Assigning list_searched_recipes xml elements to variables in adapter
    public SearchedRecipesViewHolder(@NonNull View itemView) {
        super(itemView);
        searched_recipes_container = itemView.findViewById(R.id.searched_recipes_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_calories = itemView.findViewById(R.id.textView_calories);
        textView_protein = itemView.findViewById(R.id.textView_protein);
        textView_carbs = itemView.findViewById(R.id.textView_carbs);
        imageView_food = itemView.findViewById(R.id.imageView_food);
    }
}

