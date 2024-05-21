package sg.edu.np.mad.pocketchef;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String uid = "";
    private String username = "";
    private String email = "";
    private String password = "";
    private String confirmPassword = "";
    private String name = "";
    private String dob = "";
    private String profilePicture;
    private String profileDescription = "";

    private List<String> usedUsernames = new ArrayList<>();
    private List<String> usedEmails = new ArrayList<>();

    private Boolean validUsername = false;
    private Boolean validEmail = false;
    private Boolean validPassword = false;
    private Boolean validConfirmPassword = false;
    private Button createAccount;

    private String passwordLogInText = "";
    private String usernameEmailLogInText = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewAnimator viewAnimator = findViewById(R.id.viewAnimator);
        viewAnimator.setAnimateFirstView(true);
        viewAnimator.setInAnimation(this, android.R.anim.slide_in_left);
//        viewAnimator.setOutAnimation(this, android.R.anim.slide_out_right);

        Button signUp = findViewById(R.id.signUp);
        createAccount = findViewById(R.id.createAccount);

        class Next implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                viewAnimator.showNext();
            }
        }

        signUp.setOnClickListener(new Next());
//        createAccount.setOnClickListener(new Next());

        View logInView = findViewById(R.id.logInView);

//        Sign up
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                if (!(viewAnimator.getCurrentView() == logInView)) viewAnimator.showPrevious();
            }
        });


        class Back implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                viewAnimator.showPrevious();
            }
        };


        Button cancelSignUp = findViewById(R.id.cancelSignUp);
        cancelSignUp.setOnClickListener(new Back());




//        dobConstraints = CalendarConstraints.Builder()
//                .setOpenAt()


        String defaultDate = "01-January-2000";

//        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
//
//        Date d = Date;
//        try {
//            d = f.parse(defaultDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Long milliseconds = d.getTime();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MMMM-uuuu", Locale.ENGLISH);
        Long milliseconds = LocalDate.parse(defaultDate, dateFormatter)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();


        MaterialDatePicker<Long> dobPicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(milliseconds)
                .build();

        TextInputEditText dobEditText = findViewById(R.id.dobTextEdit);
        dobEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dobPicker.show(getSupportFragmentManager(), "tag");
            }
        });

        dobPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                String date = DateFormat.format("dd/MM/yyyy", new Date(selection)).toString();
                dobEditText.setText(date);
                dob = date;
            }
        });









        TextView setProfilePictureText = findViewById(R.id.setProfilePictureText);
        ImageView setProfilePicture = findViewById(R.id.setProfilePicture);

        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
//            Callback invoked after user selects a media or closes picker
            if (uri != null) {
                profilePicture = uri.toString();
                setProfilePicture.setImageURI(uri);
                setProfilePictureText.setVisibility(View.GONE);
                Log.d("PhotoPicker", "Selected URI: " + uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

// Launch the photo picker and let the user choose images only.
//        pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());

        setProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the photo picker and let the user choose images only.
                pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());

            }
        });




        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();


        // Read from the database
        myRef.child("usernames").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                User value = dataSnapshot.getValue(User.class);

                usedUsernames = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    usedUsernames.add(childSnapshot.getValue(String.class));
                    Log.d("CHILD",childSnapshot.getValue(String.class));

                }


//                Log.d("READ", "Value is: " + dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("READ", "Failed to read value.", error.toException());
            }
        });

        // Read from the database
        myRef.child("emails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                User value = dataSnapshot.getValue(User.class);

                usedEmails = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    usedEmails.add(childSnapshot.getValue(String.class));
                    Log.d("CHILD",childSnapshot.getValue(String.class));

                }


//                Log.d("READ", "Value is: " + dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("READ", "Failed to read value.", error.toException());
            }
        });

        TextInputLayout usernameSignUp = findViewById(R.id.usernameSignUp);
        usernameSignUp.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                username = s.toString();
                Validate("username",usernameSignUp);

            }
        });

        TextInputLayout emailSignUp = findViewById(R.id.emailSignUp);
//        String email;

        emailSignUp.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                email = s.toString();
                Validate("email",emailSignUp);
            }
        });


        TextInputLayout passwordSignUp = findViewById(R.id.passwordSignUp);
        TextInputLayout confirmPasswordSignUp = findViewById(R.id.confirmPasswordSignUp);


        passwordSignUp.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                password = s.toString();
                Validate("password",passwordSignUp, confirmPasswordSignUp);

            }
        });
//        String password = passwordSignUp.getEditText().getText().toString();


        confirmPasswordSignUp.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmPassword = s.toString();
                Validate("confirmPassword",confirmPasswordSignUp);
            }
        });





        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EMAIL", email);
                Log.d("PASS", password);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            uid = user.getUid();
                            // Write user data to the database
                            myRef.child("users").child(uid).child("username").setValue(username);
                            myRef.child("users").child(uid).child("email").setValue(email);
                            myRef.child("usernames").child(uid).setValue(username);
                            myRef.child("emails").child(uid).setValue(email);
                            viewAnimator.showNext();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid email format.",
                                Toast.LENGTH_SHORT).show();
                        // Handle the error
                    }
                });


            }
        });




        TextInputLayout usernameEmailLogIn = findViewById(R.id.usernameEmailLogIn);
        usernameEmailLogIn.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameEmailLogInText = s.toString();
            }
        });



        TextInputLayout passwordLogIn = findViewById(R.id.passwordLogIn);
        passwordLogIn.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordLogInText = s.toString();
            }
        });
        Button logIn = findViewById(R.id.logIn);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEmailLogInText.strip().isEmpty())  {
                    Toast.makeText(LoginActivity.this, "Fill in your username/email.",
                            Toast.LENGTH_SHORT).show();

                } else if (passwordLogInText.strip().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fill in your account password.",
                            Toast.LENGTH_SHORT).show();

                } else if (usernameEmailLogInText.contains("@") && usernameEmailLogInText.contains(".")) {
                    mAuth.signInWithEmailAndPassword(usernameEmailLogInText, passwordLogInText).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {

                        } else {
                            Log.d("CONTAINS", String.valueOf(usedEmails.contains(usernameEmailLogInText)));

                            if(!usedEmails.contains(usernameEmailLogInText))
                                Toast.makeText(LoginActivity.this, "User does not exist.",
                                        Toast.LENGTH_SHORT).show();
                            else Toast.makeText(LoginActivity.this, "Invalid password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    myRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("LISTENER", String.valueOf(snapshot.getChildren()));
                            for(DataSnapshot user : snapshot.getChildren()) {
                                Log.d("LISTENER", user.child("username").getValue(String.class));
                                if(usernameEmailLogInText.equalsIgnoreCase(user.child("username").getValue(String.class))) {
                                    mAuth.signInWithEmailAndPassword(user.child("email").getValue(String.class), passwordLogInText).addOnCompleteListener(task -> {
                                        if(task.isSuccessful()) {

                                        } else {
                                            Log.d("CONTAINS U", String.valueOf(usedUsernames.contains(usernameEmailLogInText)));
                                            if(!usedUsernames.contains(usernameEmailLogInText)) {
                                                Toast.makeText(LoginActivity.this, "User does not exist.",
                                                        Toast.LENGTH_SHORT).show();
                                            } else Toast.makeText(LoginActivity.this, "Invalid Password.",
                                                    Toast.LENGTH_SHORT).show();


                                        }
                                    });
                                    break;
                                };
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {



                        }
                    });
                }

            }
        });


        TextInputLayout nameSignUp = findViewById(R.id.nameSignUp);

        TextInputLayout profileDescriptionSignUp = findViewById(R.id.profileDescriptionSignUp);

        Button startCooking = findViewById(R.id.startCooking);
        startCooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NEWLINE", String.valueOf(String.valueOf(profileDescriptionSignUp.getEditText().getText()).contains("\n")));
                myRef.child("users").child(uid).child("name").setValue(nameSignUp.getEditText().getText().toString());
                myRef.child("users").child(uid).child("date-of-birth").setValue(dob);
                myRef.child("users").child(uid).child("profile-picture").setValue(profilePicture);
                myRef.child("users").child(uid).child("profile-description").setValue(String.valueOf(profileDescriptionSignUp.getEditText().getText()));


            }
        });

        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hello, World!");
//        Log.d("DATABASE", "SET");

        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d("READ", "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w("READ", "Failed to read value.", error.toException());
//            }
//        });







    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }


    private void updateUI(FirebaseUser u) {

    }

    public void Validate(String type, TextInputLayout textInputLayout, TextInputLayout... passwordInputLayout) {
        Boolean valid;
        switch(type) {

            case "username":
                valid = true;
                for(String u : usedUsernames) {
                    if(username.equalsIgnoreCase(u)) {
                        validUsername = false;
                        textInputLayout.setError("Username is already taken");
                        valid = false;
                        break;
                    }
                }

                if(valid) {
                    textInputLayout.setError(null);
                    validUsername = true;
                    break;
                } else break;


            case "email":
                valid = true;
                for(String e : usedEmails) {
                    if(email.equalsIgnoreCase(e)) {
                        validEmail = false;
                        textInputLayout.setError("Email is already in use by another account");
                        valid = false;
                        break;
                    }
                }

                if(valid) {
                    textInputLayout.setError(null);
                    validEmail = true;
                    break;
                } else break;


            case "password":
                if(password.length() < 6) {
                    validPassword = false;
                    textInputLayout.setError("Password needs to have at least 6 characters");
                    break;

                } else {
                    textInputLayout.setError(null);
                    validPassword = true;
                }

                if(password.equals(confirmPassword)) {
                    textInputLayout.setError(null);
                    validPassword = true;
                    Log.d("VALIDPASSWORD", validPassword.toString());
                }


            case "confirmPassword":
                TextInputLayout confirmPasswordInputLayout = passwordInputLayout.length == 0 ? textInputLayout : passwordInputLayout[0];
                if(!confirmPassword.equals(password)) {
                    validConfirmPassword = false;
                    confirmPasswordInputLayout.setError("Passwords must be the same");
                    break;

                }

                confirmPasswordInputLayout.setError(null);
                validConfirmPassword = true;
                break;

        }

        if(validUsername && validEmail && validPassword && validConfirmPassword) {
            createAccount.setEnabled(true);
        } else {
            createAccount.setEnabled(false);
        }

    }






}