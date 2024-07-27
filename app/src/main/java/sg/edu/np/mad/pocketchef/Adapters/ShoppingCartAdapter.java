package sg.edu.np.mad.pocketchef.Adapters;


import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sg.edu.np.mad.pocketchef.Models.ExtendedIngredient;
import sg.edu.np.mad.pocketchef.Models.ShoppingCart;
import sg.edu.np.mad.pocketchef.Models.Utils;
import sg.edu.np.mad.pocketchef.R;
import sg.edu.np.mad.pocketchef.ShopCartActivity;
import sg.edu.np.mad.pocketchef.ShopListActivity;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartViewHolder> {

    private List<ShoppingCart> shoppingCartList;
    private Context context;
    ExecutorService executorService = Executors.newCachedThreadPool();

    public ShoppingCartAdapter(Context context, List<ShoppingCart> shoppingCartList) {
        this.context = context;
        this.shoppingCartList = shoppingCartList;
    }

    public void setData(List<ShoppingCart> shoppingCartList){
        this.shoppingCartList = shoppingCartList;

    }



    @NonNull
    @Override
    public ShoppingCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_cart_item, parent, false);
        return new ShoppingCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingCartViewHolder holder, int position) {
        ShoppingCart shoppingCart = shoppingCartList.get(position);
        holder.bind(shoppingCart,context);
    }

    @Override
    public int getItemCount() {
        return shoppingCartList.size();
    }

    public  class ShoppingCartViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private TextView textViewUser;

        private TextView textViewCreateTime;
        private MaterialCardView linearLayout;
        private ImageView imageView,iv2;

        public ShoppingCartViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewUser = itemView.findViewById(R.id.textViewUser);
            imageView = itemView.findViewById(R.id.iv_share);
            textViewCreateTime = itemView.findViewById(R.id.textViewCreateTime);
            linearLayout = itemView.findViewById(R.id.ll_shop);
            iv2 = itemView.findViewById(R.id.iv_shop);
        }

        public void bind(ShoppingCart shoppingCart,Context context) {
            Type ingredientListType = new TypeToken<List<ExtendedIngredient>>(){}.getType();
            Gson gson = new Gson();
            List<ExtendedIngredient> ingredients = gson.fromJson(shoppingCart.items, ingredientListType);
            textViewName.setText(shoppingCart.name);
            if(ingredients==null){
                textViewUser.setText("0 "+"Products ");
            }else{
                textViewUser.setText(ingredients.size()+" Products ");
            }
            if(shoppingCart.imagurl!=null){
                Glide.with(context).load(shoppingCart.imagurl).into(iv2);
            }
            textViewCreateTime.setText("create time:"+shoppingCart.createTime);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            ShoppingCart shoppingCart1 = new ShoppingCart();
                            shoppingCart1.user = shoppingCart.user;
                            shoppingCart1.name = shoppingCart.name;
                            shoppingCart1.id = shoppingCart.id;
                            Bitmap bitmap= Utils.Create2DCode(new Gson().toJson(shoppingCart1));
                            Uri contentUri = Utils.createImageContentUri(bitmap,context);
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                            shareIntent.setType("image/*");
                            // 启动分享对话框
                            context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                        }
                    });
                }
            });
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShopListActivity.class);
                    intent.putExtra("id",shoppingCart.id);
                    context.startActivity(intent);
                }
            });
        }
    }
}
