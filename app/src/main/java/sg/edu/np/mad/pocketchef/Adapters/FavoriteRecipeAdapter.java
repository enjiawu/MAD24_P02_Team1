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

import sg.edu.np.mad.pocketchef.Models.FavoriteRecipe;
import sg.edu.np.mad.pocketchef.FavoriteRecipesActivity;
import sg.edu.np.mad.pocketchef.R;

public class FavoriteRecipeAdapter extends RecyclerView.Adapter<FavoriteRecipeAdapter.ViewHolder> {
    private Context context;
    private List<FavoriteRecipe> favoriteRecipes;
    private FavoriteRecipesActivity activity;

    public FavoriteRecipeAdapter(Context context, List<FavoriteRecipe> favoriteRecipes) {
        this.context = context;
        this.favoriteRecipes = favoriteRecipes;
        this.activity = (FavoriteRecipesActivity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteRecipe recipe = favoriteRecipes.get(position);
        holder.textViewRecipeName.setText(recipe.getName());
        Picasso.get().load(recipe.getImageUrl()).into(holder.imageViewRecipe);

        holder.buttonEdit.setOnClickListener(view -> activity.onEditFavorite(recipe));
        holder.buttonDelete.setOnClickListener(view -> activity.onDeleteFavorite(recipe));
    }

    @Override
    public int getItemCount() {
        return favoriteRecipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecipeName;
        ImageView imageViewRecipe;
        ImageView buttonEdit;
        ImageView buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRecipeName = itemView.findViewById(R.id.text_view_recipe_name);
            imageViewRecipe = itemView.findViewById(R.id.image_view_recipe);
            buttonEdit = itemView.findViewById(R.id.button_edit);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}