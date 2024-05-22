package sg.edu.np.mad.pocketchef;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad.pocketchef.Models.User;
import sg.edu.np.mad.pocketchef.Models.Utils;

public class EditProfileActivity extends AppCompatActivity {


    CircleImageView profile_image;
    EditText usernameEt, passwordEt;
    EditText nameEt, emailEt;
    TextView dobTv;
    Uri uri;
    ImageView btnSave, backIv;
    String dob;
    DatabaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        helper = new DatabaseHelper(this);

        backIv = findViewById(R.id.backIv);
        passwordEt = findViewById(R.id.passwordEt);
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
        String password = passwordEt.getText().toString();

        if (username.isEmpty()) {
            usernameEt.setError("Select Username");
            usernameEt.requestFocus();
        } else if (email.isEmpty()) {
            emailEt.setError("Select Email");
            emailEt.requestFocus();
        } else if (name.isEmpty()) {
            nameEt.setError("Select Name");
            nameEt.requestFocus();
        } else if (password.isEmpty()) {
            passwordEt.setError("Select Password");
            passwordEt.requestFocus();
        } else if (uri == null) {
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
        } else {
            User user = new User(1, name, username, email, password, dob, Utils.uriToBytes(EditProfileActivity.this, uri));

            if (!helper.loadUsers().isEmpty()) {
                boolean b = helper.updateUser(user);
                if (b) {
                    onBackPressed();
                    Toast.makeText(this, "Data Updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Data Not Updated!", Toast.LENGTH_SHORT).show();
                }
            } else {
                boolean b = helper.saveUser(user);
                if (b) {
                    onBackPressed();
                    Toast.makeText(this, "Data Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Data Not Saved!", Toast.LENGTH_SHORT).show();
                }
            }
        }

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