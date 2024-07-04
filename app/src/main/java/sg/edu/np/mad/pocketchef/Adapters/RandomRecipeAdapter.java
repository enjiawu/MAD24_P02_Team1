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
    // Context object to access resources and layout inflator
    final Context context;
    final List<Recipe> list;
    final RecipeClickListener listener;

    public RandomRecipeAdapter(Context context, ArrayList<Recipe> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    //Override method
    public RandomRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RandomRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false));
    }

    //Override method to input items from api to RandomRecipeViewHolder
    @Override
    public void onBindViewHolder(@NonNull RandomRecipeViewHolder holder, int position) {
        holder.textView_title.setText(list.get(position).title);
        holder.textView_title.setSelected(true);
        holder.textView_upvote.setText(list.get(position).aggregateLikes + " Upvotes");
        holder.textView_servings.setText(list.get(position).servings + " Servings");
        holder.textView_time.setText(list.get(position).readyInMinutes + " Minutes");
        // Use picasso to load images
        Picasso.get().load(list.get(position).image).into(holder.imageView_food);

        //Call card view
        holder.random_list_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
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

    // Assigning list_random_recipe xml elements to variables in adapater
    public RandomRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_servings = itemView.findViewById(R.id.textView_servings);
        textView_upvote = itemView.findViewById(R.id.textView_upvote);
        textView_time = itemView.findViewById(R.id.textView_time);
        imageView_food = itemView.findViewById(R.id.imageView_food);
    }
}
