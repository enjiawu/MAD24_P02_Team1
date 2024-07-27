package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.mad.pocketchef.Adapters.ExtendedIngredientAdapter;
import sg.edu.np.mad.pocketchef.Models.ExtendedIngredient;
import sg.edu.np.mad.pocketchef.Models.HomeShopItem;
import sg.edu.np.mad.pocketchef.Models.ProductResponse;
import sg.edu.np.mad.pocketchef.Models.ShoppingCart;
import sg.edu.np.mad.pocketchef.databinding.ActivityShopListBinding;
import sg.edu.np.mad.pocketchef.net.ApiClient;
import sg.edu.np.mad.pocketchef.net.ApiService;

public class ShopListActivity extends AppCompatActivity {

    ActivityShopListBinding bind;
    List<ExtendedIngredient> ingredients = new ArrayList<>();
    ExtendedIngredientAdapter adapter;
    ExecutorService executorService;
    ItemTouchHelper itemTouchHelper;

    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bind = ActivityShopListBinding.inflate(getLayoutInflater());
        setContentView(bind.main);
        id = getIntent().getIntExtra("id",0);
        bind.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bind.ivShopHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopListActivity.this, ShopHomeActivity.class);
                startActivity(intent);
            }
        });
        bind.ivScanf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ShopListActivity.this);
                integrator.setPrompt("Scan a code");
                integrator.initiateScan();
            }
        });
        bind.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopListActivity.this, CreateShopListCardActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        adapter = new ExtendedIngredientAdapter(ingredients,this,bind.num);
        bind.rv.setLayoutManager(new LinearLayoutManager(this));
        bind.rv.setAdapter(adapter);
        executorService = Executors.newCachedThreadPool();
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        ShoppingCart shoppingCart= FavoriteDatabase.getInstance(ShopListActivity.this)
                                .shoppingCartDao().getAllShoppingCartsForId(id);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.removePosition(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(0,position);
                                Gson gson = new Gson();
                                shoppingCart.items =gson.toJson(ingredients,
                                        new TypeToken<List<ExtendedIngredient>>() {}.getType());
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        FavoriteDatabase.getInstance(ShopListActivity.this)
                                                .shoppingCartDao().update(shoppingCart);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        itemTouchHelper.attachToRecyclerView(bind.rv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ShoppingCart shoppingCart= FavoriteDatabase.getInstance(ShopListActivity.this)
                        .shoppingCartDao().getAllShoppingCartsForId(id);
                Logger.d("item=="+shoppingCart.items);
                Type ingredientListType = new TypeToken<List<ExtendedIngredient>>(){}.getType();
                Gson gson = new Gson();
                ingredients = gson.fromJson(shoppingCart.items, ingredientListType);
                if(ingredients==null){
                    ingredients = new ArrayList<>();
                    return;
                }
                List<HomeShopItem> list = FavoriteDatabase.getInstance(ShopListActivity.this)
                        .homeShopItemDao().getAll();
                if(list==null){
                    list = new ArrayList<>();
                }
                adapter.setData(ingredients,list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bind.title.setText(shoppingCart.name);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String json = result.getContents();
                Logger.d("res=="+json);
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        ApiClient.getClient().create(ApiService.class).getProductByBarcode(json)
                                .enqueue(new Callback<ProductResponse>() {
                                    @Override
                                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                                        //product_name, product_quantity, product_quantity_unit, image_url
                                        ProductResponse productResponse = response.body();
                                        ExtendedIngredient extendedIngredient= new ExtendedIngredient();
                                        extendedIngredient.id = (int) System.currentTimeMillis();
                                        extendedIngredient.name = productResponse.getProduct().getProduct_name();
                                        extendedIngredient.image = productResponse.getProduct().getImage_url();
                                        extendedIngredient.unit = productResponse.getProduct().getProduct_quantity_unit();
                                        try {
                                            extendedIngredient.amount = Double.parseDouble(productResponse.getProduct().
                                                    getProduct_quantity());
                                        }catch (Exception e){
                                            extendedIngredient.amount = -1.0f;
                                        }
                                        ingredients.add(extendedIngredient);
                                        executorService.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                Gson gson = new Gson();
                                                ShoppingCart shoppingCart= FavoriteDatabase.getInstance(ShopListActivity.this)
                                                        .shoppingCartDao().getAllShoppingCartsForId(id);
                                                shoppingCart.items =gson.toJson(ingredients,
                                                        new TypeToken<List<ExtendedIngredient>>() {}.getType());
                                                FavoriteDatabase.getInstance(ShopListActivity.this)
                                                        .shoppingCartDao().update(shoppingCart);
                                                List<HomeShopItem> list = FavoriteDatabase.getInstance(ShopListActivity.this)
                                                                .homeShopItemDao().getAll();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.setData(ingredients,list);
                                                        adapter.notifyDataSetChanged();
                                                        Toast.makeText(ShopListActivity.this, "You had added a list from  " +
                                                                extendedIngredient.name, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Call<ProductResponse> call, Throwable throwable) {
                                                    throwable.printStackTrace();
                                    }
                                });
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}