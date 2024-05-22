package sg.edu.np.mad.pocketchef;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad.pocketchef.Models.User;
import sg.edu.np.mad.pocketchef.Models.Utils;

public class EditProfileActivity extends AppCompatActivity {


    CircleImageView profile_image;
    EditText usernameEt;
    EditText nameEt, emailEt;
    TextView dobTv;
    Uri uri;
    ImageView btnSave, backIv;
    String dob = null;


    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference mUserRef;
    StorageReference mStorageRef;
    private static final String TAG = "ProfileActivity";


    ProgressDialog progressDialog;

    TextView changePasswordTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        // Initialize Firebase Database
        mUserRef = FirebaseDatabase.getInstance().getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Images");

        backIv = findViewById(R.id.backIv);

        changePasswordTv = findViewById(R.id.changePasswordTv);
        btnSave = findViewById(R.id.btnSave);
        profile_image = findViewById(R.id.profile_image);
        usernameEt = findViewById(R.id.usernameEt);
        nameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailEt);
        dobTv = findViewById(R.id.dobTv);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        dobTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDat();
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        changePasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        loadProfile();
    }

    private void changePassword() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        dialogBuilder.setView(dialogView);

        EditText newPasswordEt = dialogView.findViewById(R.id.newPasswordEt);
        EditText oldPasswordEt = dialogView.findViewById(R.id.oldPasswordEt);
        Button buttonChangePassword = dialogView.findViewById(R.id.buttonChangePassword);

        AlertDialog alertDialog = dialogBuilder.create();


        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEt.getText().toString();
                String oldPassword = oldPasswordEt.getText().toString();
                if (!TextUtils.isEmpty(newPassword)) {
                    changePassword(oldPassword, newPassword, alertDialog);
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a new password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();

    }


    private void changePassword(String oldPassword, String newPassword, AlertDialog alertDialog) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Create a credential using the user's email and current password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            // Re-authenticate the user
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // User successfully re-authenticated, now update the password
                    user.updatePassword(newPassword).addOnCompleteListener(passwordUpdateTask -> {
                        if (passwordUpdateTask.isSuccessful()) {
                            // Password updated successfully
                            alertDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Password update failed
                            Toast.makeText(EditProfileActivity.this, "Failed to update password: " + passwordUpdateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            passwordUpdateTask.getException().printStackTrace();
                        }
                    });
                } else {
                    // Re-authentication failed
                    Toast.makeText(EditProfileActivity.this, "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    task.getException().printStackTrace();
                }
            });
        } else {
            // User is not signed in
            Toast.makeText(EditProfileActivity.this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
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
                        emailEt.setText(email);
                    }

                    if (name != null && !name.isEmpty()) {
                        nameEt.setText(name);
                    }

                    if (Image != null && !Image.isEmpty()) {
                        Picasso.get().load(Image).into(profile_image);
                    }

                    if (username != null && !username.isEmpty()) {
                        usernameEt.setText(username);
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

    private void selectDat() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Do something with the selected date
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dob = selectedDate;
                        dobTv.setText(dob);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void saveData() {
        String username = usernameEt.getText().toString();
        String email = emailEt.getText().toString();
        String name = nameEt.getText().toString();


        if (username.isEmpty() && email.isEmpty() && name.isEmpty() && uri == null) {
            Toast.makeText(this, "Please Add All Information", Toast.LENGTH_SHORT).show();
        } else if (uri == null) {
            progressDialog.show();
            saveText(username, name, email, dob, null);
        } else {
            progressDialog.show();
            mStorageRef.child(mUser.getUid()).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        mStorageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                saveText(username, name, email, dob, uri.toString());
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void saveText(String username, String name, String email, String dateofbirth, String image) {
        HashMap hashMap = new HashMap();
        if (username != null && !username.isEmpty()) {
            hashMap.put("username", username);
        }

        if (name != null && !name.isEmpty()) {
            hashMap.put("name", name);
        }


        if (email != null && !email.isEmpty()) {
            hashMap.put("email", email);
        }

        if (dateofbirth != null && !dateofbirth.isEmpty()) {
            hashMap.put("date-of-birth", dateofbirth);
        }

        if (image != null && !image.isEmpty()) {
            hashMap.put("Image", image);
        }


        mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Updated Profile!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intent);
    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            assert o.getData() != null;
            uri = o.getData().getData();
            profile_image.setImageURI(uri);
        }
    });
}