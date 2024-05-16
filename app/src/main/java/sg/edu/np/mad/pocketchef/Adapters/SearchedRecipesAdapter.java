package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.RecipeClickListener;
import sg.edu.np.mad.pocketchef.Models.Recipe;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipe;
import sg.edu.np.mad.pocketchef.R;

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
    public void onBindViewHolder(@NonNull SearchedRecipesViewHolder holder, int position){
        holder.textView_title.setText(list.get(position).title);
        holder.textView_title.setSelected(true);
        holder.textView_calories.setText((int) list.get(position).nutrition.get(0).amount + list.get(position).nutrition.get(0).unit);
        holder.textView_protein.setText((int) list.get(position).nutrition.get(1).amount + list.get(position).nutrition.get(1).unit);
        holder.textView_carbs.setText((int) list.get(position).nutrition.get(2).amount + list.get(position).nutrition.get(2).unit);

        // Use picasso to load images
        Picasso.get().load(list.get(position).image).into(holder.imageView_food);
        //Call card view
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
    }
}

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

