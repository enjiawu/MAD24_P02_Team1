package sg.edu.np.mad.pocketchef.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import sg.edu.np.mad.pocketchef.FavoriteDatabase;
import sg.edu.np.mad.pocketchef.Models.ExtendedIngredient;
import sg.edu.np.mad.pocketchef.Models.HomeShopItem;
import sg.edu.np.mad.pocketchef.R;

public class ExtendedIngredientAdapter extends RecyclerView.Adapter<ExtendedIngredientAdapter.ViewHolder> {

    private List<ExtendedIngredient> ingredientList;
    private List<ExtendedIngredient> lastingredientList;
    private List<HomeShopItem> homeShopItemList;
    private TextView textView;
    private  Context context;
    public ExtendedIngredientAdapter(List<ExtendedIngredient> ingredientList,Context context,TextView textView) {
        this.ingredientList = ingredientList;
        lastingredientList = new ArrayList<>();
        homeShopItemList = new ArrayList<>();
        this.context = context;
        this.textView = textView;
    }

    public void removePosition(int position){
        if(position<ingredientList.size()){
            ingredientList.remove(position);
        }else{
            lastingredientList.remove(position-ingredientList.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position>ingredientList.size()){
            return 1;
        }else{
            return 0;
        }
    }

    public void setData(List<ExtendedIngredient> ingredientList,List<HomeShopItem> homeShopItemList){
        this.ingredientList = ingredientList;
        this.homeShopItemList = homeShopItemList;
        lastingredientList = new ArrayList<>();
        if(this.homeShopItemList==null){
            this.homeShopItemList = new ArrayList<>();
        }
        for(int i=0;i<ingredientList.size();i++){
            boolean ishome = isconts(ingredientList.get(i).name);
            if(ishome){
                lastingredientList.add(ingredientList.get(i));
                ingredientList.remove(i);
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.extended_ingredient_item,
                parent, false);
        return new ViewHolder(view);
    }

    private boolean isconts(String name){
        for(int i=0;i<homeShopItemList.size();i++){
            if(name.contains(homeShopItemList.get(i).name)){
                return true;
            }
        }
        return false;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExtendedIngredient ingredient;
        if (position < ingredientList.size()) {
            ingredient = ingredientList.get(position);
            holder.checkBox.setChecked(false);
        } else {
            ingredient = lastingredientList.get(position - ingredientList.size());
            holder.checkBox.setChecked(true);
        }
        holder.checkBox.setVisibility(View.VISIBLE);
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
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.checkBox.isChecked()){
                    lastingredientList.remove(ingredient);
                    notifyItemRemoved(position);
                    ingredientList.add(0,ingredient);
                    notifyItemRangeChanged(0, ingredientList.size()+lastingredientList.size());
                    int num1 = ingredientList.size();
                    int num2 = lastingredientList.size()+ingredientList.size();
                    textView.setText(num1+"/"+num2);
                }else{
                    ingredientList.remove(ingredient);
                    notifyItemRemoved(position);
                    lastingredientList.add(ingredient);
                    notifyItemRangeChanged(0, ingredientList.size()+lastingredientList.size());
                    int num1 = ingredientList.size();
                    int num2 = lastingredientList.size()+ingredientList.size();
                    textView.setText(num1+"/"+num2);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        if(ingredientList==null)
            return 0;
      if(ingredientList.size()!=0){
          int num1 = ingredientList.size();
          int num2 = lastingredientList.size()+ingredientList.size();
          textView.setText(num1+"/"+num2);
      }
    return ingredientList.size()+lastingredientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIngredient;
        TextView textViewName;
        TextView textViewAmount;
        TextView textViewUnit;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIngredient = itemView.findViewById(R.id.imageViewIngredient);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewUnit = itemView.findViewById(R.id.textViewUnit);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}