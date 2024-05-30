    package sg.edu.np.mad.pocketchef;
    
    
    import android.content.Intent;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.text.format.DateFormat;
    import android.util.Log;
    import android.view.View;
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
    import androidx.core.splashscreen.SplashScreen;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    
    import com.google.android.material.button.MaterialButton;
    import com.google.android.material.datepicker.MaterialDatePicker;
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
    import java.util.Objects;
    
    public class LoginActivity extends AppCompatActivity {
    
        private FirebaseAuth mAuth;
        private String uid = "";
        private String username = "";
        private String email = "";
        private String password = "";
        private String confirmPassword = "";
        private String dob = "";
        private String profilePicture;
    
        private List<String> usedUsernames = new ArrayList<>();
        private List<String> usedEmails = new ArrayList<>();
    
        private Boolean validUsername = false;
        private Boolean validEmail = false;
        private Boolean validPassword = false;
        private Boolean validConfirmPassword = false;

    
        private String passwordLogInText = "";
        private String usernameEmailLogInText = "";

        ViewAnimator viewAnimator;
        MaterialButton signUp;
        MaterialButton createAccount;
        View logInView;
        MaterialButton cancelSignUp;
        TextInputEditText dobEditText;
        TextView setProfilePictureText;
        ImageView setProfilePicture;
        MaterialButton logIn;
        TextInputLayout usernameSignUp;
        TextInputLayout emailSignUp;
        TextInputLayout passwordSignUp;
        TextInputLayout confirmPasswordSignUp;
        TextInputLayout usernameEmailLogIn;
        TextInputLayout passwordLogIn;
        TextInputLayout nameSignUp;
        TextInputLayout profileDescriptionSignUp;
        MaterialButton startCooking;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            SplashScreen.installSplashScreen(this);
            setContentView(R.layout.activity_login);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

//            Initialise views
            FindViews();

//            ViewAnimator viewAnimator = findViewById(R.id.viewAnimator);
            viewAnimator.setAnimateFirstView(true);
            viewAnimator.setInAnimation(this, android.R.anim.slide_in_left);
    //        viewAnimator.setOutAnimation(this, android.R.anim.slide_out_right);
    
//            MaterialButton signUp = findViewById(R.id.signUp);
//            createAccount = findViewById(R.id.createAccount);
    
    //        Show next view
            class Next implements View.OnClickListener {
    
                @Override
                public void onClick(View v) {
                    viewAnimator.showNext();
                }
            }
    
    //        Show previous view
            class Back implements View.OnClickListener {
    
                @Override
                public void onClick(View v) {
                    viewAnimator.showPrevious();
                }
            }

            signUp.setOnClickListener(new Next());
    //        createAccount.setOnClickListener(new Next());
    
//            View logInView = findViewById(R.id.logInView);
    
    //        Sign up
    
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
    
                @Override
                public void handleOnBackPressed() {
    //                Prevent user from using back button to go past the log in view backward
                    if (!(viewAnimator.getCurrentView() == logInView)) viewAnimator.showPrevious();
                }
            });
    
//            MaterialButton cancelSignUp = findViewById(R.id.cancelSignUp);
            cancelSignUp.setOnClickListener(new Back());
            String defaultDate = "01-January-2000";
    
    //          Convert default date string to Long object
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MMMM-uuuu", Locale.ENGLISH);
            Long milliseconds = LocalDate.parse(defaultDate, dateFormatter)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli();
    
    //          Build date picker
            MaterialDatePicker<Long> dobPicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date of Birth")
                    .setSelection(milliseconds)
                    .build();
    
    
//            TextInputEditText dobEditText = findViewById(R.id.dobTextEdit);
            dobEditText.setOnClickListener(v -> {
    //                Show date picker when field pressed
                dobPicker.show(getSupportFragmentManager(), "tag");
            });
    
    //        Set chosen date
            dobPicker.addOnPositiveButtonClickListener(selection -> {
                String date = DateFormat.format("dd/MM/yyyy", new Date(selection)).toString();
                dobEditText.setText(date);
                dob = date;
            });
//            TextView setProfilePictureText = findViewById(R.id.setProfilePictureText);
//            ImageView setProfilePicture = findViewById(R.id.setProfilePicture);
    
            // Registers a photo picker activity launcher in single-select mode.
            ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
    //            Callback invoked after user selects an image or closes picker
                if (uri != null) {
    //                Set chosen image
                    profilePicture = uri.toString();
                    setProfilePicture.setImageURI(uri);
                    setProfilePictureText.setVisibility(View.GONE);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });
    
    
            setProfilePicture.setOnClickListener(v -> {
                // Launch the photo picker and let the user choose images only
                pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
    
            });
    
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
    
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference myRef = database.getReference();
    
            // Get list of usernames in use by other users
            myRef.child("usernames").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("USernamesUsed", usedUsernames.toString());
    
    //                User value = dataSnapshot.getValue(User.class);
    
                    usedUsernames = new ArrayList<>();
    
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        usedUsernames.add(childSnapshot.getValue(String.class));
                        Log.d("CHILD", Objects.requireNonNull(childSnapshot.getValue(String.class)));
    
                    }
    
    //                Log.d("READ", "Value is: " + dataSnapshot.getChildren());
                }
    
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Failed to read value
                    Log.w("READ", "Failed to read value.", error.toException());
                }
            });

            Log.d("TEST","HELLO");
    
            // Get list of emails in use by other users
            myRef.child("emails").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    
    
                    usedEmails = new ArrayList<>();
    
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        usedEmails.add(childSnapshot.getValue(String.class));
                        Log.d("CHILD", Objects.requireNonNull(childSnapshot.getValue(String.class)));
    
                    }
    
    
    //                Log.d("READ", "Value is: " + dataSnapshot.getChildren());
                }
    
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Failed to read value
                    Log.w("READ", "Failed to read value.", error.toException());
                }
            });
    
    //        Login
            //        Log in the user
//            MaterialButton logIn = findViewById(R.id.login);
            logIn.setOnClickListener(v -> {
    //                Make sure fields are not empty
                if (usernameEmailLogInText.strip().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fill in your username/email.",
                            Toast.LENGTH_SHORT).show();
    
                } else if (passwordLogInText.strip().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fill in your account password.",
                            Toast.LENGTH_SHORT).show();
    
    //                    If email is entered
                } else if (usernameEmailLogInText.contains("@") && usernameEmailLogInText.contains(".")) {
                    mAuth.signInWithEmailAndPassword(usernameEmailLogInText, passwordLogInText).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
    //                            Go to main
                            Intent logIn1 = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(logIn1);
    
                        } else {
                            Log.d("CONTAINS", String.valueOf(usedEmails.contains(usernameEmailLogInText)));
    
                            if (!usedEmails.contains(usernameEmailLogInText))
                                Toast.makeText(LoginActivity.this, "User does not exist.",  // If user does not exist in database
                                        Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(LoginActivity.this, "Invalid password.",    // If user exists but incorrect password
                                        Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
    //                    If username entered
                    myRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("LISTENER", String.valueOf(snapshot.getChildren()));
    //                            Iterate through list of usernames in use
                            for (DataSnapshot user : snapshot.getChildren()) {
                                Log.d("LISTENER", Objects.requireNonNull(user.child("username").getValue(String.class)));
    //                                Find the email that is associated to the same account as the username entered
                                if (usernameEmailLogInText.equalsIgnoreCase(user.child("username").getValue(String.class))) {
                                    mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.child("email").getValue(String.class)), passwordLogInText).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
    //                                            Go to main
                                            Intent logIn1 = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(logIn1);
    
                                        } else {
                                            Log.d("CONTAINS U", String.valueOf(usedUsernames.contains(usernameEmailLogInText)));
                                            if (!usedUsernames.contains(usernameEmailLogInText)) {
                                                Toast.makeText(LoginActivity.this, "User does not exist.",  // If user does not exist in database
                                                        Toast.LENGTH_SHORT).show();
                                            } else
                                                Toast.makeText(LoginActivity.this, "Invalid Password.",  // If user exists but incorrect password
                                                        Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
    //        Signup
    //        Validate username
//            TextInputLayout usernameSignUp = findViewById(R.id.usernameSignUp);
            Objects.requireNonNull(usernameSignUp.getEditText()).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
    
                    username = s.toString();
                    Validate("username", usernameSignUp);
                }
            });
    
    //        Validate email
//            TextInputLayout emailSignUp = findViewById(R.id.emailSignUp);
            Objects.requireNonNull(emailSignUp.getEditText()).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    email = s.toString();
                    Validate("email", emailSignUp);
                }
            });
    //        Validate passwords
//            TextInputLayout passwordSignUp = findViewById(R.id.passwordSignUp);
//            TextInputLayout confirmPasswordSignUp = findViewById(R.id.confirmPasswordSignUp);
            Objects.requireNonNull(passwordSignUp.getEditText()).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    password = s.toString();
                    Validate("password", passwordSignUp, confirmPasswordSignUp);
                }
            });
            Objects.requireNonNull(confirmPasswordSignUp.getEditText()).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    confirmPassword = s.toString();
                    Validate("confirmPassword", confirmPasswordSignUp);
                }
            });
    //        Create new account on button press
            createAccount.setOnClickListener(v -> {
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
            });
    
    //        Keep track of username / email entered
//            TextInputLayout usernameEmailLogIn = findViewById(R.id.usernameEmailLogIn);
            Objects.requireNonNull(usernameEmailLogIn.getEditText()).addTextChangedListener(new TextWatcher() {
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

    //        Keep track of password entered
//            TextInputLayout passwordLogIn = findViewById(R.id.passwordLogIn);
            Objects.requireNonNull(passwordLogIn.getEditText()).addTextChangedListener(new TextWatcher() {
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

//            TextInputLayout nameSignUp = findViewById(R.id.nameSignUp);
//            TextInputLayout profileDescriptionSignUp = findViewById(R.id.profileDescriptionSignUp);
//            MaterialButton startCooking = findViewById(R.id.startCooking);
            startCooking.setOnClickListener(v -> {
    //                Update additional profile details
    //                Log.d("NEWLINE", String.valueOf(String.valueOf(profileDescriptionSignUp.getEditText().getText()).contains("\n")));
                myRef.child("users").child(uid).child("name").setValue(Objects.requireNonNull(nameSignUp.getEditText()).getText().toString());
                myRef.child("users").child(uid).child("date-of-birth").setValue(dob);
                myRef.child("users").child(uid).child("profile-picture").setValue(profilePicture);
                myRef.child("users").child(uid).child("profile-description").setValue(String.valueOf(Objects.requireNonNull(profileDescriptionSignUp.getEditText()).getText()));
    
                Intent logIn12 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(logIn12);
    
    
            });
        }
        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                currentUser.reload();
            }
        }
        //    Validate various user inputs
        public void Validate(String type, TextInputLayout textInputLayout, TextInputLayout... passwordInputLayout) {
            boolean valid;
            switch (type) {
    
                case "username":
                    valid = true;
    //                Check if username is already in use
                    for (String u : usedUsernames) {
                        if (username.equalsIgnoreCase(u)) {
                            validUsername = false;
                            textInputLayout.setError("Username is already taken");
                            valid = false;
                            break;
                        }
                    }
    //                Remove error
                    if (valid) {
                        textInputLayout.setError(null);
                        validUsername = true;
                    }
                    break;
    
                case "email":
                    valid = true;
    //                Check if email is already in use
                    for (String e : usedEmails) {
                        if (email.equalsIgnoreCase(e)) {
                            validEmail = false;
                            textInputLayout.setError("Email is already in use by another account");
                            valid = false;
                            break;
                        }
                    }
    //                Remove error
                    if (valid) {
                        textInputLayout.setError(null);
                        validEmail = true;
                    }
                    break;
    
                case "password":
    //                Password needs to be at least 6 characters
                    if (password.length() < 6) {
                        validPassword = false;
                        textInputLayout.setError("Password needs to have at least 6 characters");
                        break;
    //                Remove error
                    } else {
                        textInputLayout.setError(null);
                        validPassword = true;
                    }
    //                Check if passwords match
                    if (password.equals(confirmPassword)) {
                        textInputLayout.setError(null);
                        validPassword = true;
                        Log.d("VALIDPASSWORD", validPassword.toString());
                    }
    
                case "confirmPassword":
    //                Optional parameter defaults to the same textInputLayout
                    TextInputLayout confirmPasswordInputLayout = passwordInputLayout.length == 0 ? textInputLayout : passwordInputLayout[0];
    //                Check if passwords match
                    if (!confirmPassword.equals(password)) {
                        validConfirmPassword = false;
                        confirmPasswordInputLayout.setError("Passwords must be the same");
                        break;
    
                    }
    //                Remove error
                    confirmPasswordInputLayout.setError(null);
                    validConfirmPassword = true;
                    break;
    
            }
    
    //        Allow account creation if all inputs are valid
            createAccount.setEnabled(validUsername && validEmail && validPassword && validConfirmPassword);
    
        }

        private void FindViews() {
            viewAnimator = findViewById(R.id.viewAnimator);
            signUp = findViewById(R.id.signUp);
            createAccount = findViewById(R.id.createAccount);
            logInView = findViewById(R.id.logInView);
            cancelSignUp = findViewById(R.id.cancelSignUp);
            dobEditText = findViewById(R.id.dobTextEdit);
            setProfilePictureText = findViewById(R.id.setProfilePictureText);
            setProfilePicture = findViewById(R.id.setProfilePicture);
            logIn = findViewById(R.id.login);
            usernameSignUp = findViewById(R.id.usernameSignUp);
            emailSignUp = findViewById(R.id.emailSignUp);
            passwordSignUp = findViewById(R.id.passwordSignUp);
            confirmPasswordSignUp = findViewById(R.id.confirmPasswordSignUp);
            usernameEmailLogIn = findViewById(R.id.usernameEmailLogIn);
            passwordLogIn = findViewById(R.id.passwordLogIn);
            nameSignUp = findViewById(R.id.nameSignUp);
            profileDescriptionSignUp = findViewById(R.id.profileDescriptionSignUp);
            startCooking = findViewById(R.id.startCooking);
        }
    }