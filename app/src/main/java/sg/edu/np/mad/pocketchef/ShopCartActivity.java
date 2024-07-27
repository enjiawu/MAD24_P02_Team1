package sg.edu.np.mad.pocketchef;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.orhanobut.logger.Logger;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sg.edu.np.mad.pocketchef.Adapters.ShoppingCartAdapter;
import sg.edu.np.mad.pocketchef.Models.App;
import sg.edu.np.mad.pocketchef.Models.ShoppingCart;
import sg.edu.np.mad.pocketchef.databinding.ActivityShopCartBinding;

public class ShopCartActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;
    ActivityShopCartBinding bind;
    List<ShoppingCart> shoppingCartList = new ArrayList<>();
    MenuItem nav_home;
    ItemTouchHelper itemTouchHelper;
    ShoppingCartAdapter adapter;
    ExecutorService executorService = Executors.newCachedThreadPool();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bind = ActivityShopCartBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        navigationView = bind.navView;
        bind.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopCartActivity.this, CreateShopCardActivity.class);
                startActivity(intent);
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, bind.drawerLayout,
                bind.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        bind.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        navigationView.setCheckedItem(nav_home);
        bind.rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShoppingCartAdapter(this, shoppingCartList);
        bind.rv.setAdapter(adapter);

        bind.ivScnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ShopCartActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan a QR code");
                integrator.initiateScan();
            }
        });

         itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                 ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
             @Override
             public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                 return false;
             }

             @Override
             public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                 int position = viewHolder.getAdapterPosition();
                 ShoppingCart shoppingCart = shoppingCartList.get(position);
                 executorService.execute(new Runnable() {
                     @Override
                     public void run() {
                         FavoriteDatabase.getInstance(ShopCartActivity.this)
                                 .shoppingCartDao().delete(shoppingCart);
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 shoppingCartList.remove(position);
                                 adapter.notifyItemRemoved(position);
                                 adapter.notifyItemRangeChanged(0,position);
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
                shoppingCartList = FavoriteDatabase.getInstance(ShopCartActivity.this)
                        .shoppingCartDao().getAllShoppingCartsForUser(App.user);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setData(shoppingCartList);
                        adapter.notifyDataSetChanged();
                        itemTouchHelper.attachToRecyclerView(bind.rv);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String json = result.getContents();
                ShoppingCart shoppingCart = new Gson().fromJson(json,ShoppingCart.class);
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Logger.d("shopcart=="+shoppingCart.id);
                        try {
                            ShoppingCart shareCard = FavoriteDatabase.getInstance(ShopCartActivity.this)
                                    .shoppingCartDao().getAllShoppingCartsForId(shoppingCart.id);
                            ShoppingCart insertShareCard = new ShoppingCart();
                            insertShareCard.name = shareCard.name;
                            insertShareCard.items = shareCard.items;
                            insertShareCard.createTime = shareCard.createTime;
                            insertShareCard.user = shareCard.user;
                            shoppingCartList.add(insertShareCard);
                            FavoriteDatabase.getInstance(ShopCartActivity.this).shoppingCartDao()
                                    .insert(insertShareCard);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemInserted(shoppingCartList.size()-1);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                Toast.makeText(this, "You had added a list from  " + shoppingCart.user, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            // Nothing Happens
        } else if (itemId == R.id.nav_recipes) {
            Intent intent = new Intent(this, RecipeActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_profile) {
            Intent intent2 = new Intent(this, ProfileActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_favourites) {
            Intent intent3 = new Intent(this, CreateCategoryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_pantry) {
            Intent intent3 = new Intent(this, PantryActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_search) {
            Intent intent4 = new Intent(this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent5);
        } else if (itemId == R.id.nav_community) {
            Intent intent6 = new Intent(this, CommunityActivity.class);
            finish();
            startActivity(intent6);
        } else if (itemId == R.id.nav_shoppinglist) {
            Intent intent7 = new Intent(this, ShopCartActivity.class);
            finish();
            startActivity(intent7);
        }
        bind.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}