package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad.pocketchef.Models.User;

public class ProfileActivity extends AppCompatActivity {
    //".write": "auth !== null && auth.uid === $uid"
    CircleImageView profile_image;
    TextView usernameTv;
    TextView nameTv, EmailTv, dobTv;
    ImageView editProfile;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference mUserRef;
    private static final String TAG = "ProfileActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        // Initialize Firebase Database
        mUserRef = FirebaseDatabase.getInstance().getReference("users");


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
        // Read data from Firebase Database
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve data safely
                    String date_of_birth = snapshot.child("date-of-birth").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String profile_description = snapshot.child("profile_description").getValue(String.class);
                    String profile_picture = snapshot.child("profile_picture").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String Image = snapshot.child("Image").getValue(String.class);

                    // Log retrieved values for debugging
                    Log.d(TAG, "date_of_birth: " + date_of_birth);
                    Log.d(TAG, "email: " + email);
                    Log.d(TAG, "name: " + name);
                    Log.d(TAG, "profile_description: " + profile_description);
                    Log.d(TAG, "profile_picture: " + profile_picture);
                    Log.d(TAG, "username: " + username);

                    // Update UI if values are not null or empty
                    if (date_of_birth != null && !date_of_birth.isEmpty()) {
                        dobTv.setText(date_of_birth);
                    }

                    if (email != null && !email.isEmpty()) {
                        EmailTv.setText(email);
                    }

                    if (name != null && !name.isEmpty()) {
                        nameTv.setText(name);
                    }

                    if (Image != null && !Image.isEmpty()) {
                        Picasso.get().load(Image).into(profile_image);
                    }

                    if (username != null && !username.isEmpty()) {
                        usernameTv.setText(username);
                    }
                } else {
                    Log.w(TAG, "DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }
}