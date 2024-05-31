package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.kongzue.albumdialog.PhotoAlbumDialog;
import com.kongzue.albumdialog.util.DialogImplCallback;
import com.kongzue.albumdialog.util.SelectPhotoCallback;
import com.kongzue.dialogx.dialogs.BottomDialog;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.FullScreenDialog;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;

import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import sg.edu.np.mad.pocketchef.Models.CategoryBean;

import sg.edu.np.mad.pocketchef.base.AppDatabase;
import sg.edu.np.mad.pocketchef.base.CommonAdapter;
import sg.edu.np.mad.pocketchef.databinding.ActivityCreateCategoryBinding;
import sg.edu.np.mad.pocketchef.databinding.ItemCreatecategoryBinding;

public class CreateCategoryActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {

    private ActivityCreateCategoryBinding binding;
    MenuItem nav_home, nav_recipes, nav_search;
    private CommonAdapter<ItemCreatecategoryBinding, CategoryBean> commonAdapter;
    DrawerLayout drawerLayout;
    private List<CategoryBean> datalist;
    MaterialToolbar toolbar;
    NavigationView navigationView;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        nav_home = navigationView.getMenu().findItem(R.id.nav_home);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(CreateCategoryActivity.this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(CreateCategoryActivity.this);
        navigationView.setCheckedItem(nav_home);
        init();
        path="";
        binding.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ed = editText.getText().toString();
                if(ed.isEmpty()){
                    PopTip.show("Please enter the name of the new type");
                    return;
                }
                if(path.isEmpty()){
                    path = "default";
                }
                CategoryBean categoryBean =new CategoryBean(path,ed);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AppDatabase.getInstance(CreateCategoryActivity.this)
                                    .categoryDao().insertCategory(categoryBean);
                        }catch (Exception e){
                            PopTip.show("Addition failed");
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PopTip.show("Success");
                                datalist.remove(datalist.size()-1);
                                datalist.add(categoryBean);
                                datalist.add(new CategoryBean("a","a"));
                                commonAdapter.setData(datalist);
                                editText.setText("");
                                path="";
                            }
                        });

                    }
                }).start();
            }
        });
        register();
    }
    ActivityResultLauncher<PickVisualMediaRequest>    pickMedia2;
    private CategoryBean categoryBean;
    private void register(){
        pickMedia2 =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        path = uri.toString();
                        Glide.with(CreateCategoryActivity.this)
                                .load(uri).into(iv);
                        commonAdapter.notifyDataSetChanged();
                    } else {

                    }
                });
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        categoryBean.imagePath = uri.toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase.getInstance(CreateCategoryActivity.this)
                                        .categoryDao().updateCategory(categoryBean);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        commonAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).start();
                    } else {

                    }
                });
    }
    private TextInputEditText editText;
    private ImageView iv;
    private void init(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.rv.setLayoutManager(gridLayoutManager);
        binding.rv.setItemViewCacheSize(100);
        commonAdapter = new CommonAdapter<ItemCreatecategoryBinding, CategoryBean>(new ArrayList<>()) {
            @Override
            protected int getType(int position) {
                if(datalist==null||datalist.isEmpty()||datalist.size()-1==position){
                    return -1;
                }else{
                    return position;
                }
            }

            @Override
            protected void show(ItemCreatecategoryBinding holder, int position, CategoryBean cetegoryBean) {
                holder.cl.setVisibility(View.GONE);
                holder.addLl.setVisibility(View.GONE);
                if(getItemViewType(position)==-1){
                    holder.addLl.setVisibility(View.VISIBLE);
                    holder.cl.setVisibility(View.GONE);
                    try {
                        holder.addPicture.setImageResource(R.drawable.baseline_add_24);
                    }catch (Exception e){

                    }

                    holder.addPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openPicture(  holder.addPicture);
                        }
                    });
                    editText = holder.edAdd;
                }else{
                    holder.addLl.setVisibility(View.GONE);
                    holder.cl.setVisibility(View.VISIBLE);
                    if(cetegoryBean.imagePath.equals("Favorite")){
                        holder.tvStr.setText("Favorite");
                        holder.ivStr.setImageResource(R.drawable.pocketchef_logo);
                        holder.cl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent =new Intent(CreateCategoryActivity.this,
                                        ShowCollectActivity.class);
                                intent.putExtra("id",cetegoryBean.text);
                                finish();
                                startActivity(intent);
                            }
                        });
                    }else if(!cetegoryBean.imagePath.equals("a")){
                        holder.tvStr.setText(cetegoryBean.text);
                        if(cetegoryBean.imagePath.equals("default")){
                            Glide.with(CreateCategoryActivity.this)
                                    .load(R.drawable.pocketchef_logo)
                                    .into(holder.ivStr);
                        }else{
                            Glide.with(CreateCategoryActivity.this)
                                    .load(cetegoryBean.imagePath)
                                    .into(holder.ivStr);
                        }

                        holder.cl.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                String[] text = new String[]{"edit images", "edit category name"
                                        ,"delete category"};
                                BottomMenu.show(text)
                                        .setMessage("Edit")
                                        .setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                                            @Override
                                            public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                                                if(index == 0){
                                                    openPicture(cetegoryBean);
                                                }else if(index==1){
                                                    setCategoryBeanName(cetegoryBean);
                                                }else if(index==2){
                                                    WaitDialog.show("loading...");
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            AppDatabase.getInstance(CreateCategoryActivity.this)
                                                                    .RecipeDetailsCDao().deleteByCategoryBeanId(cetegoryBean.text);
                                                            AppDatabase.getInstance(CreateCategoryActivity.this
                                                            ).categoryDao().deleteCategory(cetegoryBean);
                                                            datalist.remove(cetegoryBean);
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    WaitDialog.dismiss();
                                                                    commonAdapter.notifyItemRemoved(position);
                                                                }
                                                            });
                                                        }
                                                    }).start();
                                                }
                                                dialog.dismiss();
                                                return true;
                                            }
                                        });
                                return true;
                            }
                        });
                        holder.cl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent =new Intent(CreateCategoryActivity.this,
                                        ShowCollectActivity.class);
                                intent.putExtra("id",cetegoryBean.text);
                                finish();
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        };
        binding.rv.setAdapter(commonAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                datalist= AppDatabase.getInstance(CreateCategoryActivity.this)
                        .categoryDao().getAllCategories();
                if(datalist==null||datalist.isEmpty()){
                    datalist =new ArrayList<>();
                }
                CategoryBean categoryBean =new CategoryBean("a","a");
                datalist.add(categoryBean);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commonAdapter.setData(datalist);
                    }
                });

            }
        }).start();
    }

    private void setCategoryBeanName(CategoryBean categoryBean){
        new InputDialog("edit", "Please enter a new category name",
                "ok", "cancel", "")
                .setCancelable(false)
                .setOkButton(new OnInputDialogButtonClickListener<InputDialog>() {
                    @Override
                    public boolean onClick(InputDialog baseDialog, View v, String inputStr) {
                        if(inputStr.isEmpty()){
                            PopTip.show("Input cannot be empty");
                            return true;
                        }
                        categoryBean.text = inputStr;
                        commonAdapter.notifyDataSetChanged();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                AppDatabase.getInstance(CreateCategoryActivity.this)
                                        .categoryDao().updateCategory(categoryBean);
                            }
                        }).start();
                        return false;
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void openPicture(CategoryBean categoryBean){
        this.categoryBean = categoryBean;

        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private String path="";
    private void openPicture(ImageView iv){
        this.iv = iv;
        pickMedia2.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    //For menu
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
        } else if (itemId == R.id.nav_favourites){
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