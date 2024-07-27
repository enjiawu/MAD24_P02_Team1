package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sg.edu.np.mad.pocketchef.Adapters.HomeShopAdapter;
import sg.edu.np.mad.pocketchef.Models.ExtendedIngredient;
import sg.edu.np.mad.pocketchef.Models.HomeShopItem;
import sg.edu.np.mad.pocketchef.Models.ProductResponse;
import sg.edu.np.mad.pocketchef.Models.ShoppingCart;
import sg.edu.np.mad.pocketchef.databinding.ActivityShopHomeBinding;
import sg.edu.np.mad.pocketchef.net.ApiClient;
import sg.edu.np.mad.pocketchef.net.ApiService;

public class ShopHomeActivity extends AppCompatActivity {

    ActivityShopHomeBinding bind;
    ExecutorService executorService = Executors.newCachedThreadPool();
    List<HomeShopItem> homeShopItemList= new ArrayList<>();
    private HomeShopAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bind = ActivityShopHomeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(bind.main,(v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bind.rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeShopAdapter(new ArrayList<>());
        bind.rv.setAdapter(adapter);
        bind.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                homeShopItemList = FavoriteDatabase.getInstance(ShopHomeActivity.this)
                        .homeShopItemDao().getAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setData(homeShopItemList);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        bind.ivScanf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ShopHomeActivity.this);
                integrator.setPrompt("Scan a code");
                integrator.initiateScan();
            }
        });
        ItemTouchHelper  itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(0,position);
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        FavoriteDatabase.getInstance(ShopHomeActivity.this)
                                                .homeShopItemDao().delete(homeShopItemList.get(position));
                                        homeShopItemList.remove(position);
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
                                        HomeShopItem homeShopItem = new HomeShopItem();
                                        homeShopItem.name = productResponse.getProduct().getProduct_name();
                                        homeShopItem.image = productResponse.getProduct().getImage_url();
                                        homeShopItem.unit = productResponse.getProduct().getProduct_quantity_unit();
                                        try {
                                            homeShopItem.amount = Double.parseDouble(productResponse.getProduct().
                                                    getProduct_quantity());
                                        }catch (Exception e){
                                            homeShopItem.amount = 0.0;
                                        }
                                        if(homeShopItemList==null){
                                            homeShopItemList = new ArrayList<>();
                                        }
                                        homeShopItemList.add(homeShopItem);
                                        executorService.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                FavoriteDatabase.getInstance(ShopHomeActivity.this)
                                                                .homeShopItemDao().insert(homeShopItem);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.setData(homeShopItemList);
                                                        adapter.notifyDataSetChanged();
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