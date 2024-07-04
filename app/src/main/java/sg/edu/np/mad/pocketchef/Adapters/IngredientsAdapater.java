package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.ExtendedIngredient;
import sg.edu.np.mad.pocketchef.R;

// Recycler View Adapater
public class IngredientsAdapater extends RecyclerView.Adapter<IngredientsViewHolder> {
    final Context context;
    final List<ExtendedIngredient> list;

    public IngredientsAdapater(Context context, List<ExtendedIngredient> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    // Method to set ViewHolder with list of ingredients from API
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_meal_ingredients, parent, false));
    }

    // Method to alter information in viewholder, refer to documentation of Spoontacular for Get Recipe Information
    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        holder.textView_ingredients_name.setText(list.get(position).name);
        holder.textView_ingredients_name.setSelected(true);
        holder.textView_ingredients_quantity.setSelected(true);
        holder.textView_ingredients_quantity.setText(list.get(position).original);
        if (list.get(position) != null && list.get(position).image != null) {
            Picasso.get()
                    .load("https://spoonacular.com/cdn/ingredients_100x100/" + list.get(position).image)
                    .into(holder.imageView_ingredients);
        } else {
            // Optionally handle the case where the image URL is null
            holder.imageView_ingredients.setImageResource(R.drawable.pocketchef_logo_transparent);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

// Method to implement Recylcer View viewholder
class IngredientsViewHolder extends RecyclerView.ViewHolder {
    final TextView textView_ingredients_quantity;
    final TextView textView_ingredients_name;
    final ImageView imageView_ingredients;

    public IngredientsViewHolder(@NonNull View itemView) {
        super(itemView);
        // Intialising objects
        textView_ingredients_quantity = itemView.findViewById(R.id.textView_ingredients_quantity);
        textView_ingredients_name = itemView.findViewById(R.id.textView_ingredients_name);
        imageView_ingredients = itemView.findViewById(R.id.imageView_ingredients);
    }
}
