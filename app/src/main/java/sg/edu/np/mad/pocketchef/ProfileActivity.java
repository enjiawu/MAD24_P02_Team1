package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView usernameTv;
    TextView nameTv, EmailTv, dobTv;
    ImageView editProfile;

    DatabaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        helper = new DatabaseHelper(this);
        editProfile = findViewById(R.id.editProfile);
        profile_image = findViewById(R.id.profile_image);
        usernameTv = findViewById(R.id.usernameTv);
        nameTv = findViewById(R.id.nameTv);
        EmailTv = findViewById(R.id.EmailTv);
        dobTv = findViewById(R.id.dobTv);


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });


        loadProfile();

    }

    private void loadProfile() {
        List<User> list = helper.loadUsers();
        if (list != null && !list.isEmpty()) {
            usernameTv.setText(list.get(0).getUsername());
            nameTv.setText(list.get(0).getName());
            dobTv.setText(list.get(0).getDate());
            EmailTv.setText(list.get(0).getEmail());
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(list.get(0).getImage(), 0, list.get(0).getImage().length);
            profile_image.setImageBitmap(imageBitmap);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }
}