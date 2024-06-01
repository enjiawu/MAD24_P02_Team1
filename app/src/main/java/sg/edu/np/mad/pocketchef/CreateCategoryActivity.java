package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.dialogs.WaitDialog;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Models.CategoryBean;

import sg.edu.np.mad.pocketchef.Adapters.FavoriteAdapter;
import sg.edu.np.mad.pocketchef.databinding.ActivityCreateCategoryBinding;
import sg.edu.np.mad.pocketchef.databinding.ItemCreateCategoryBinding;

public class CreateCategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityCreateCategoryBinding binding;
    MenuItem nav_home, nav_recipes, nav_search;

    // Adapter for display categories
    private FavoriteAdapter
            <ItemCreateCategoryBinding, CategoryBean> favoriteAdapter;

    // layout and view elements
    DrawerLayout drawerLayout;
    MaterialToolbar toolbar;
    NavigationView navigationView;

    // data list for categories
    private List<CategoryBean> datalist;

    //activity result launcher for picking media
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enable edge to edge method
        EdgeToEdge.enable(this);

        // inflate using view binding
        binding = ActivityCreateCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialize toolbar, drawer layout and nav view
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // get menu items and set listener
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(CreateCategoryActivity.this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(CreateCategoryActivity.this);
        navigationView.setCheckedItem(nav_home);

        // initialize UI element and path variable
        init();
        path = "";

        // button click listener
        binding.bt.setOnClickListener(v -> {
            String et = editText.getText().toString();
            if (et.isEmpty()) {
                PopTip.show("Please enter the name of the new type");
                return;
            }
            if (path.isEmpty()) {
                path = "default";
            }
            CategoryBean categoryBean = new CategoryBean(path, et);
            new Thread(() -> {
                try {
                    FavoriteDatabase.getInstance(CreateCategoryActivity.this)
                            .categoryDao().insertCategory(categoryBean);
                } catch (Exception e) {
                    PopTip.show("Addition failed");
                    return;
                }
                runOnUiThread(() -> {
                    PopTip.show("Success");
                    datalist.remove(datalist.size() - 1);
                    datalist.add(categoryBean);
                    datalist.add(new CategoryBean("a", "a"));
                    favoriteAdapter.setData(datalist);
                    editText.setText("");
                    path = "";
                });

            }).start();
        });

        // register activity result launcher
        register();
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia2;
    private CategoryBean categoryBean;

    // initialize register activity result launcher
    private void register() {
        pickMedia2 =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        path = uri.toString();
                        Glide.with(CreateCategoryActivity.this)
                                .load(uri).into(iv);
                        favoriteAdapter.notifyDataSetChanged();
                    } else {

                    }
                });
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        categoryBean.imagePath = uri.toString();
                        new Thread(() -> {
                            FavoriteDatabase.getInstance(CreateCategoryActivity.this)
                                    .categoryDao().updateCategory(categoryBean);
                            runOnUiThread(() -> favoriteAdapter.notifyDataSetChanged());
                        }).start();
                    } else {

                    }
                });
    }

    private TextInputEditText editText;
    private ImageView iv;

    // initialize UI element and adapter
    private void init() {
        // set up recycleview
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.rv.setLayoutManager(gridLayoutManager);
        binding.rv.setItemViewCacheSize(100);
        favoriteAdapter = new FavoriteAdapter<ItemCreateCategoryBinding, CategoryBean>(new ArrayList<>()) {
            @Override
            protected int getType(int position) {
                if (datalist == null || datalist.isEmpty() || datalist.size() - 1 == position) {
                    return -1;
                } else {
                    return position;
                }
            }

            @Override
            protected void show(ItemCreateCategoryBinding holder, int position, CategoryBean cetegoryBean) {
                holder.categoryList.setVisibility(View.GONE);
                holder.addCategoryLayout.setVisibility(View.GONE);
                if (getItemViewType(position) == -1) {
                    holder.addCategoryLayout.setVisibility(View.VISIBLE);
                    holder.categoryList.setVisibility(View.GONE);
                    try {
                        holder.addPicture.setImageResource(R.drawable.baseline_add_24);
                    } catch (Exception e) {
                        Toast.makeText(CreateCategoryActivity.this, (CharSequence) e, Toast.LENGTH_SHORT).show();
                    }

                    holder.addPicture.setOnClickListener(v -> openPicture(holder.addPicture));
                    editText = holder.addCategoryName;
                } else {
                    holder.addCategoryLayout.setVisibility(View.GONE);
                    holder.categoryList.setVisibility(View.VISIBLE);
                    if (cetegoryBean.imagePath.equals("Favorite")) {
                        holder.tvCategoryText.setText("Favorite");
                        holder.ivCategoryImage.setImageResource(R.drawable.pocketchef_logo);
                        holder.categoryList.setOnClickListener(v -> {
                            Intent intent = new Intent(CreateCategoryActivity.this,
                                    ShowCollectActivity.class);
                            intent.putExtra("id", cetegoryBean.text);
                            finish();
                            startActivity(intent);
                        });
                    } else if (!cetegoryBean.imagePath.equals("a")) {
                        holder.tvCategoryText.setText(cetegoryBean.text);
                        if (cetegoryBean.imagePath.equals("default")) {
                            Glide.with(CreateCategoryActivity.this)
                                    .load(R.drawable.pocketchef_logo)
                                    .into(holder.ivCategoryImage);
                        } else {
                            Glide.with(CreateCategoryActivity.this)
                                    .load(cetegoryBean.imagePath)
                                    .into(holder.ivCategoryImage);
                        }

                        holder.categoryList.setOnLongClickListener(v -> {
                            String[] text = new String[]{"Edit images", "Edit category name"
                                    , "Delete category"};
                            BottomMenu.show(text)
                                    .setMessage(Html.fromHtml("<b>Edit Category</b>"))
                                    .setOnMenuItemClickListener((dialog, text1, index) -> {
                                        if (index == 0) {
                                            openPicture(cetegoryBean);
                                        } else if (index == 1) {
                                            setCategoryBeanName(cetegoryBean);
                                        } else if (index == 2) {
                                            WaitDialog.show("loading...");
                                            new Thread(() -> {
                                                FavoriteDatabase.getInstance(CreateCategoryActivity.this)
                                                        .RecipeDetailsCDao().deleteByCategoryBeanId(cetegoryBean.text);
                                                FavoriteDatabase.getInstance(CreateCategoryActivity.this
                                                ).categoryDao().deleteCategory(cetegoryBean);
                                                datalist.remove(cetegoryBean);
                                                runOnUiThread(() -> {
                                                    WaitDialog.dismiss();
                                                    favoriteAdapter.notifyItemRemoved(position);
                                                });
                                            }).start();
                                        }
                                        dialog.dismiss();
                                        return true;
                                    });
                            return true;
                        });
                        holder.categoryList.setOnClickListener(v -> {
                            Intent intent = new Intent(CreateCategoryActivity.this,
                                    ShowCollectActivity.class);
                            intent.putExtra("id", cetegoryBean.text);
                            finish();
                            startActivity(intent);
                        });
                    }
                }
            }
        };
        binding.rv.setAdapter(favoriteAdapter);
        new Thread(() -> {
            datalist = FavoriteDatabase.getInstance(CreateCategoryActivity.this)
                    .categoryDao().getAllCategories();
            if (datalist == null || datalist.isEmpty()) {
                datalist = new ArrayList<>();
            }
            CategoryBean categoryBean = new CategoryBean("a", "a");
            datalist.add(categoryBean);
            runOnUiThread(() -> favoriteAdapter.setData(datalist));

        }).start();
    }

    // set category name
    private void setCategoryBeanName(CategoryBean categoryBean) {
        new InputDialog("edit", "Please enter a new category name",
                "ok", "cancel", "")
                .setCancelable(false)
                .setOkButton((baseDialog, v, inputStr) -> {
                    if (inputStr.isEmpty()) {
                        PopTip.show("Input cannot be empty");
                        return true;
                    }
                    categoryBean.text = inputStr;
                    favoriteAdapter.notifyDataSetChanged();
                    new Thread(() -> FavoriteDatabase.getInstance(CreateCategoryActivity.this)
                            .categoryDao().updateCategory(categoryBean)).start();
                    return false;
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    // open picture for catergory
    private void openPicture(CategoryBean categoryBean) {
        this.categoryBean = categoryBean;

        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private String path = "";

    // open picture for adding a new category
    private void openPicture(ImageView iv) {
        this.iv = iv;
        pickMedia2.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    // handle menu nav item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.nav_home) {
            Intent intent = new Intent(CreateCategoryActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (itemId == R.id.nav_recipes) {
            Intent intent2 = new Intent(CreateCategoryActivity.this, RecipeActivity.class);
            finish();
            startActivity(intent2);
        } else if (itemId == R.id.nav_search) {
            Intent intent3 = new Intent(CreateCategoryActivity.this, AdvancedSearchActivity.class);
            finish();
            startActivity(intent3);
        } else if (itemId == R.id.nav_profile) {
            Intent intent4 = new Intent(CreateCategoryActivity.this, ProfileActivity.class);
            finish();
            startActivity(intent4);
        } else if (itemId == R.id.nav_favourites) {
            // Nothing Happens
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent5 = new Intent(CreateCategoryActivity.this, LoginActivity.class);
            finish();
            startActivity(intent5);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}