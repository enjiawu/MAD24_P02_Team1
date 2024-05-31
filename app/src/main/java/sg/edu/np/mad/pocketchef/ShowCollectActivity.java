package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Models.CategoryBean;
import sg.edu.np.mad.pocketchef.Models.Recipe;
import sg.edu.np.mad.pocketchef.Models.RecipeDetailsC;
import sg.edu.np.mad.pocketchef.base.AppDatabase;
import sg.edu.np.mad.pocketchef.base.CommonAdapter;
import sg.edu.np.mad.pocketchef.databinding.ActivityShowCollectBinding;
import sg.edu.np.mad.pocketchef.databinding.ListRandomRecipeBinding;

public class ShowCollectActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityShowCollectBinding binding;
    MaterialToolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    MenuItem nav_home, nav_recipes, nav_search;

    private List<CategoryBean> otherCollect;
    private List<String> otherCollectname;

    private CommonAdapter<ListRandomRecipeBinding,RecipeDetailsC> commonAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityShowCollectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(ShowCollectActivity.this);
        navigationView.setCheckedItem(nav_home);
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        commonAdapter =new CommonAdapter<ListRandomRecipeBinding, RecipeDetailsC>(new ArrayList<>()) {
            @Override
            protected int getType(int position) {
                return 0;
            }

            @Override
            protected void show(ListRandomRecipeBinding holder, int position, RecipeDetailsC recipe) {
                Glide.with(ShowCollectActivity.this).load(recipe.imagPath)
                        .into(holder.imageViewFood);
                holder.textViewServings.setText(recipe.meal_servings);
                holder.textViewTitle.setText(recipe.meal_name);
                holder.textViewTime.setText(recipe.meal_price);
                holder.randomListContainer.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String title = "delete";
                        String[] name;
                        if(otherCollectname==null||otherCollectname.isEmpty()){
                            name =new String[0];
                        }else{
                            name = otherCollectname.toArray(new String[0]);
                        }
                        String[] mergedArray = new String[name.length + 1];
                        int i=0;
                        mergedArray[0] = title;
                        for (i = 1; i <=name.length; i++) {
                            mergedArray[i] = "move to " +name[i-1];
                        }
                        BottomMenu.show(mergedArray).setMessage("edit").setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                            @Override
                            public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                                if(index!=0){
                                    WaitDialog.show("loading...");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            recipe.categoryBeanId = otherCollectname.get(index-1);
                                            AppDatabase.getInstance(ShowCollectActivity.this)
                                                    .RecipeDetailsCDao().update(recipe);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    WaitDialog.dismiss();
                                                    data.remove(recipe);
                                                    commonAdapter.notifyDataSetChanged();
                                                    PopTip.show("success");
                                                }
                                            });
                                        }
                                    }).start();
                                }else if(index==0){
                                    WaitDialog.show("loading...");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppDatabase.getInstance(ShowCollectActivity.this)
                                                    .RecipeDetailsCDao().delete(recipe);
                                            data.remove(recipe);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    WaitDialog.dismiss();
                                                    commonAdapter.notifyDataSetChanged();
                                                    PopTip.show("success");
                                                }
                                            });
                                        }
                                    }).start();
                                }
                                return false;
                            }
                        });
                        return true;
                    }
                });
                holder.randomListContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =new Intent(ShowCollectActivity.this,RecipeDetailsActivity.class);
                        intent.putExtra(EXTRA_RECIPE_ID, recipe.recipeDetailsResponseId+"");
                        startActivity(intent);
                    }
                });
            }
        };
        binding.rv.setAdapter(commonAdapter);
        init();
    }
    List<RecipeDetailsC> data;
    private static final String EXTRA_RECIPE_ID = "id";
    private void init(){
        String name  = getIntent().getStringExtra("id");
        toolbar.setTitle(name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                data = AppDatabase.getInstance(ShowCollectActivity.this).RecipeDetailsCDao()
                        .getByCategoryBeanId(name);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commonAdapter.setData(data);
                    }
                });
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                otherCollectname = new ArrayList<>();
                otherCollect = AppDatabase.getInstance(ShowCollectActivity.this)
                        .categoryDao().getAllCategories();
                for(int i=0;i<otherCollect.size();i++){
                    if(!otherCollect.get(i).text.equals(name)){
                        String collect_name = otherCollect.get(i).text;
                        otherCollectname.add(collect_name);
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(ShowCollectActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent2 = new Intent(ShowCollectActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_search) {
            Intent intent2 = new Intent(ShowCollectActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent2);
        }else if(itemId == R.id.nav_favourites){
            Intent intent2 = new Intent(ShowCollectActivity.this, CreateCategoryActivity.class);
            finish();
            startActivity(intent2);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}