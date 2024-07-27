package sg.edu.np.mad.pocketchef;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.dialogs.WaitDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sg.edu.np.mad.pocketchef.Models.App;
import sg.edu.np.mad.pocketchef.Models.ShoppingCart;
import sg.edu.np.mad.pocketchef.Models.Utils;
import sg.edu.np.mad.pocketchef.databinding.ActivityCreateShopCardBinding;

public class CreateShopCardActivity extends AppCompatActivity {

    ActivityCreateShopCardBinding bind;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia2;
    private String path = "";
    ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bind = ActivityCreateShopCardBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(bind.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                if(TextUtils.isEmpty(name)){
                    PopTip.show("name is empty!");
                    return;
                }
                ShoppingCart shoppingCart = new ShoppingCart();
                shoppingCart.name = name;
                shoppingCart.user = App.user;
                shoppingCart.createTime = Utils.getLocalTime();
                if(TextUtils.isEmpty(path)){
                    shoppingCart.imagurl = "";
                }else {
                    shoppingCart.imagurl = path;
                }
                WaitDialog.show("loading...");
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        WaitDialog.dismiss();
                        FavoriteDatabase.getInstance(CreateShopCardActivity.this)
                                .shoppingCartDao().insert(shoppingCart);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PopTip.show("create success!");
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
                        Glide.with(this).load(path).into(bind.ivHead);
                    } else {
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}