package sg.edu.np.mad.pocketchef;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.kongzue.dialogx.dialogs.PopTip;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sg.edu.np.mad.pocketchef.Models.ExtendedIngredient;
import sg.edu.np.mad.pocketchef.Models.ShoppingCart;
import sg.edu.np.mad.pocketchef.databinding.ActivityCreateShopListCardBinding;
import sg.edu.np.mad.pocketchef.databinding.ActivityShopListBinding;

public class CreateShopListCardActivity extends AppCompatActivity {

    ActivityCreateShopListCardBinding bind;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia2;
    private String path = "";
    private  int id;
    private ExecutorService executorService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bind = ActivityCreateShopListCardBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(bind.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        executorService = Executors.newCachedThreadPool();
        id = getIntent().getIntExtra("id",0);
        bind.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bind.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPicture();
            }
        });
        bind.btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = bind.edName.getText().toString();
                String unit = bind.edUnit.getText().toString();
                String samount = bind.edAmount.getText().toString();
                double amount = Double.parseDouble(samount);
                Logger.d("amount--"+amount);
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        ShoppingCart shoppingCart= FavoriteDatabase.getInstance(CreateShopListCardActivity.this)
                                .shoppingCartDao().getAllShoppingCartsForId(id);
                        Type ingredientListType = new TypeToken<List<ExtendedIngredient>>(){}.getType();
                        Gson gson = new Gson();
                        List<ExtendedIngredient>  ingredients = gson.fromJson(shoppingCart.items, ingredientListType);
                        if(ingredients==null){
                            ingredients = new ArrayList<>();
                        }
                        ExtendedIngredient extendedIngredient = new ExtendedIngredient();
                        extendedIngredient.image = path;
                        extendedIngredient.name = name;
                        extendedIngredient.amount = amount;
                        extendedIngredient.unit = unit;
                        ingredients.add(extendedIngredient);
                        shoppingCart.items = new Gson().toJson(ingredients);
                        FavoriteDatabase.getInstance(CreateShopListCardActivity.this).shoppingCartDao()
                                .update(shoppingCart);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PopTip.show("Add Success!!");
                            }
                        });
                    }
                });
            }
        });
        register();
    }
    private void openPicture() {
        pickMedia2.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }
    private void register() {
        pickMedia2 =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        path = uri.toString();
                        Logger.d(path);
                        Glide.with(this).load(path).into(bind.ivHead);
                    } else {
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}