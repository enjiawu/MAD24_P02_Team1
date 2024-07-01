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
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    // Declare UI elements
    CircleImageView profile_image;
    EditText usernameEt, nameEt, emailEt;
    TextView dobTv;
    Uri uri;
    ImageView btnSave, backIv;
    String dob = null;

    // Firebase Authentication and References
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference mUserRef;
    StorageReference mStorageRef;
    private static final String TAG = "ProfileActivity";


    ProgressDialog progressDialog;

    TextView changePasswordTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call the superclass onCreate method to ensure any superclass initialization is done
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_edit_profile);

        // Initialize progress dialog (The "Updating" Status when user is saving)
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        // Initialize Firebase Database reference for user data
        mUserRef = FirebaseDatabase.getInstance().getReference("users");
        // Initialize Firebase Storage reference for images
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Images");

        // Bind UI elements to their respective views
        backIv = findViewById(R.id.backIv);

        changePasswordTv = findViewById(R.id.changePasswordTv);
        btnSave = findViewById(R.id.btnSave);
        profile_image = findViewById(R.id.profile_image);
        usernameEt = findViewById(R.id.usernameEt);
        nameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailEt);
        dobTv = findViewById(R.id.dobTv);

        // Set click listeners for UI elements

        // When profile image is clicked, launch image selection from gallery
        profile_image.setOnClickListener(v -> selectImageFromGallery());

        // When save button is clicked, save user data
        btnSave.setOnClickListener(v -> saveData());

        // When date of birth text view is clicked, allow user to select date
        dobTv.setOnClickListener(v -> selectDat());

        // When back button is clicked, navigate back to previous activity
        backIv.setOnClickListener(v -> onBackPressed());

        // When change password text view is clicked, initiate password change process
        changePasswordTv.setOnClickListener(v -> changePassword());
        // Load user profile data from Firebase
        loadProfile();
    }

    // Method to change user password
    private void changePassword() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        dialogBuilder.setView(dialogView);

        // Get references to EditTexts and Button in the dialog layout
        EditText newPasswordEt = dialogView.findViewById(R.id.newPasswordEt);
        EditText oldPasswordEt = dialogView.findViewById(R.id.oldPasswordEt);
        Button buttonChangePassword = dialogView.findViewById(R.id.buttonChangePassword);

        // Create the AlertDialog
        AlertDialog alertDialog = dialogBuilder.create();


        // Set click listener for the "Change Password" button
        buttonChangePassword.setOnClickListener(v -> {
            // Retrieve the new and old passwords entered by the user
            String newPassword = newPasswordEt.getText().toString();
            String oldPassword = oldPasswordEt.getText().toString();
            // Check if the new password is not empty
            if (!TextUtils.isEmpty(newPassword)) {
                // Call the changePassword method to change the user's password
                // Dismiss the dialog after changing the password
                changePassword(oldPassword, newPassword, alertDialog);
                alertDialog.dismiss();
            } else {
                // Show a toast message if the new password is empty
                Toast.makeText(getApplicationContext(), "Please enter a new password", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();

    }

    // Method to change user password in Firebase
    private void changePassword(String oldPassword, String newPassword, AlertDialog alertDialog) {
        FirebaseUser user = mAuth.getCurrentUser();

        // Check if the user is not null (i.e., user is signed in)
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


    // Method to load user profile data from Firebase
    private void loadProfile() {
        // Read data from Firebase Database for the current user
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            // Method called when data changes in the database
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the snapshot (data) exists
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
                        Picasso.get().load(Image).into(profile_image); // Load and set profile image using Picasso library
                    }

                    if (username != null && !username.isEmpty()) {
                        usernameEt.setText(username);
                    }
                } else {
                    Log.w(TAG, "DataSnapshot does not exist"); // Log warning if snapshot does not exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });
    }

    // Method to select a date using DatePickerDialog (The Calendar)
    private void selectDat() {
        // Get the current instance of Calendar
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR); // Get the current year from the Calendar instance
        int month = calendar.get(Calendar.MONTH); // Get the current month from the Calendar instance
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // Get the current day of the month from the Calendar instance

        // Create a new DatePickerDialog with the current date as default
        // Method called when a date is set by the user
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, // Set an OnDateSetListener to handle the date selection event
                (view, year1, month1, dayOfMonth1) -> {
                    // Do something with the selected date
                    // Store the selected date in the variable dob
                    dob = dayOfMonth1 + "/" + (month1 + 1) + "/" + year1;
                    // Update the dobTv TextView to display the selected date
                    dobTv.setText(dob);
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // Method to save profile data
    private void saveData() {
        // Retrieve user input from EditText fields
        String username = usernameEt.getText().toString();
        String email = emailEt.getText().toString();
        String name = nameEt.getText().toString();

        // Check if all required fields are empty and no image is selected
        if (username.isEmpty() && email.isEmpty() && name.isEmpty() && uri == null) {
            // If all fields are empty, show a toast message prompting the user to add information
            Toast.makeText(this, "Please Add All Information", Toast.LENGTH_SHORT).show();
        } else if (uri == null) {
            // If no image is selected but other fields are filled, proceed to save the text data only
            progressDialog.show(); // Show progress dialog
            saveText(username, name, email, dob, null); // Call method to save text data
        } else {
            // If an image is selected, save both the image and text data
            progressDialog.show();
            mStorageRef.child(mUser.getUid()).putFile(uri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // If image upload is successful, get the download URL of the uploaded image
                    mStorageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
                        // Once download URL is obtained, save both the text and image data
                        saveText(username, name, email, dob, uri.toString());
                    });
                } else {
                    // If image upload fails, dismiss the progress dialog and show an error message
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to save text data to Firebase using a HashMap
    private void saveText(String username, String name, String email, String dateofbirth, String image) {
        HashMap hashMap = new HashMap();

        // Check if the username is not null and not empty
        if (username != null && !username.isEmpty()) {
            hashMap.put("username", username); // If username is not empty, put it into the HashMap with key "username"
        }

        // Check if the name is not null and not empty
        if (name != null && !name.isEmpty()) {
            hashMap.put("name", name); // If name is not empty, put it into the HashMap with key "name"
        }

        // Check if the email is not null and not empty
        if (email != null && !email.isEmpty()) {
            hashMap.put("email", email); // If email is not empty, put it into the HashMap with key "email"
        }

        // Check if the date of birth is not null and not empty
        if (dateofbirth != null && !dateofbirth.isEmpty()) {
            hashMap.put("date-of-birth", dateofbirth); // If date of birth is not empty, put it into the HashMap with key "date-of-birth"
        }

        // Check if the image URL is not null and not empty
        if (image != null && !image.isEmpty()) {
            hashMap.put("Image", image); // If image URL is not empty, put it into the HashMap with key "Image"
        }


        mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss(); // Dismiss the progress dialog if the update was successful
                Toast.makeText(EditProfileActivity.this, "Updated Profile!", Toast.LENGTH_SHORT).show(); // Show a success message to the user
                onBackPressed();
            } else {
                progressDialog.dismiss(); // Dismiss the progress dialog if the update failed
                Toast.makeText(EditProfileActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show(); // Show an error message to the user with the exception message
            }
        });

    }


    // Method to open the gallery and select an image
    private void selectImageFromGallery() {
        Intent intent = new Intent(); // Create a new Intent object
        intent.setType("image/*"); // Set the type of data to be retrieved to images
        intent.setAction(Intent.ACTION_GET_CONTENT); // Set the action to be performed to get content (retrieve data from another activity)
        activityResultLauncher.launch(intent); // Launch the intent using the activityResultLauncher to get the result (selected image)
    }

    // ActivityResultLauncher to handle the result of the image selection
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            assert o.getData() != null; // Ensure that the result data is not null
            uri = o.getData().getData(); // Get the URI of the selected image from the result data
            profile_image.setImageURI(uri); // Set the selected image URI to the profile_image ImageView to display it
        }
    });
}