package sg.edu.np.mad.pocketchef.Adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Models.ExtendedIngredient;
import sg.edu.np.mad.pocketchef.Models.HomeShopItem;
import sg.edu.np.mad.pocketchef.R;

public class HomeShopAdapter extends RecyclerView.Adapter<HomeShopAdapter.ViewHolder> {

    private List<HomeShopItem> ingredientList;


    public HomeShopAdapter(List<HomeShopItem> ingredientList) {
        this.ingredientList = ingredientList;

    }


    public void setData(List<HomeShopItem> ingredientList){
        this.ingredientList = ingredientList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homeshop_item,
                parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeShopItem ingredient;
        ingredient = ingredientList.get(position);
        holder.textViewName.setText("Name:"+ingredient.name);
        holder.textViewAmount.setText("Amount:"+ingredient.amount);
        holder.textViewUnit.setText("Unit:"+ingredient.unit);
        if(TextUtils.isEmpty(ingredient.image)){
            Picasso.get().load(R.mipmap.ic_launcher).into(holder.imageViewIngredient);
        }else{
            Logger.d("image=="+ingredient.image);
            if(ingredient.image.contains("https")){
                Picasso.get().load(ingredient.image).into(holder.imageViewIngredient);
            }else if(ingredient.image.contains("com.android.providers")){
                Picasso.get().load(ingredient.image).into(holder.imageViewIngredient);
            } else{
                Picasso.get()
                        .load("https://spoonacular.com/cdn/ingredients_100x100/" +ingredient.image)
                        .into(holder.imageViewIngredient);
            }
        }
    }


    @Override
    public int getItemCount() {
        if(ingredientList==null)
            return 0;
    return ingredientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIngredient;
        TextView textViewName;
        TextView textViewAmount;
        TextView textViewUnit;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIngredient = itemView.findViewById(R.id.imageViewIngredient);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewUnit = itemView.findViewById(R.id.textViewUnit);

        }
    }
}