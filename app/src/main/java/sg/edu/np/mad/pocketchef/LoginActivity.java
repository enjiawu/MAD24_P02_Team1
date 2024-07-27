package sg.edu.np.mad.pocketchef;


import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import sg.edu.np.mad.pocketchef.Models.App;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;
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
    MaterialButton signUp, createAccount, cancelSignUp, logIn, startCooking;
    View logInView;
    TextInputEditText dobEditText;
    TextView setProfilePictureText;
    ImageView setProfilePicture;
    TextInputLayout usernameSignUp, emailSignUp, passwordSignUp, confirmPasswordSignUp, usernameEmailLogIn, passwordLogIn, nameSignUp, profileDescriptionSignUp;
    MaterialDatePicker<Long> dobPicker;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

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

        String defaultDate = "01-January-2000";

        //          Convert default date string to Long object
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MMMM-uuuu", Locale.ENGLISH);
        Long milliseconds = LocalDate.parse(defaultDate, dateFormatter)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli();

        //          Build date picker
        dobPicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(milliseconds)
                .build();

        // Initialize App Check for token generation (Stage 2 - Enjia)
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck appCheck = FirebaseAppCheck.getInstance();
        appCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        mStorageRef = FirebaseStorage.getInstance().getReference().child("Images");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        myRef = database.getReference();

        SetUpListeners();


        viewAnimator.setAnimateFirstView(true);
        viewAnimator.setInAnimation(this, android.R.anim.slide_in_left);
        //        viewAnimator.setOutAnimation(this, android.R.anim.slide_out_right);


        //        Sign up

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                //                Prevent user from using back button to go past the log in view backward
                if (!(viewAnimator.getCurrentView() == logInView)) viewAnimator.showPrevious();
            }
        });


        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
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

//                Check if username is given
                if (username.isEmpty()) {
                    validUsername = false;
                    break;
                }

                if (username.contains(" ")) {
                    validUsername = false;
                    textInputLayout.setError("No spaces allowed");
                    break;
                }

                for (int i = 0; i < username.length(); i++) {
                    if (!Character.isLetterOrDigit(username.charAt(i))) {
                        validUsername = false;
                        textInputLayout.setError("No special characters allowed");
                        valid = false;
                        break;
                    }
                }

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

//                Check if email is given
                if (email.isEmpty()) {
                    validEmail = false;
                }
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
//                    Log.d("VALIDPASSWORD", validPassword.toString());
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

    //        Show next view
    class Next implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            viewAnimator.showNext();
            ClearInputs();
        }
    }

    //        Show previous view
    class Back implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            viewAnimator.showPrevious();
            ClearInputs();
        }
    }

    private void SetUpListeners() {
        signUp.setOnClickListener(new Next());
        cancelSignUp.setOnClickListener(new Back());
        dobEditText.setOnClickListener(v -> {
            //                Show date picker when field pressed
            dobPicker.show(getSupportFragmentManager(), "tag");
        });
        dobPicker.addOnPositiveButtonClickListener(selection -> {
            String date = DateFormat.format("dd/MM/yyyy", new Date(selection)).toString();
            dobEditText.setText(date);
            dob = date;
        });
        setProfilePicture.setOnClickListener(v -> {
            // Launch the photo picker and let the user choose images only
            pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
        });
        myRef.child("usernames").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Log.d("UsernamesUsed", usedUsernames.toString());

                //                User value = dataSnapshot.getValue(User.class);

                usedUsernames = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    usedUsernames.add(childSnapshot.getValue(String.class));
//                        Log.d("CHILD", Objects.requireNonNull(childSnapshot.getValue(String.class)));

                }

                //                Log.d("READ", "Value is: " + dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("READ", "Failed to read value.", error.toException());
            }
        });
        myRef.child("emails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                usedEmails = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    usedEmails.add(childSnapshot.getValue(String.class));
//                        Log.d("CHILD", Objects.requireNonNull(childSnapshot.getValue(String.class)));

                }


                //                Log.d("READ", "Value is: " + dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("READ", "Failed to read value.", error.toException());
            }
        });

        logIn.setOnClickListener(v -> {
            if (usernameEmailLogInText.trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Fill in your username/email.",
                        Toast.LENGTH_SHORT).show();
            } else if (passwordLogInText.trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Fill in your account password.",
                        Toast.LENGTH_SHORT).show();
                //                    If email is entered
            } else if (usernameEmailLogInText.contains("@") && usernameEmailLogInText.contains(".")) {
                mAuth.signInWithEmailAndPassword(usernameEmailLogInText,
                        passwordLogInText).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        App.user = usernameEmailLogInText;
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
                    boolean usernameFound = false;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Log.d("LISTENER", String.valueOf(snapshot.getChildren()));
                        //                            Iterate through list of usernames in use
                        for (DataSnapshot user : snapshot.getChildren()) {
//                            Log.d("LISTENER", Objects.requireNonNull(user.child("username").getValue(String.class)));
                            //                                Find the email that is associated to the same account as the username entered
                            if (usernameEmailLogInText.equalsIgnoreCase(user.child("username").getValue(String.class))) {
                                mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.child("email").getValue(String.class)), passwordLogInText).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        App.user = usernameEmailLogInText;
                                        Intent logIn1 = new Intent(LoginActivity.this, MainActivity.class);

                                        startActivity(logIn1);

                                    } else {
//                                        Log.d("CONTAINS U", String.valueOf(usedUsernames.contains(usernameEmailLogInText)));
                                        if (!usedUsernames.contains(usernameEmailLogInText)) {
                                            Toast.makeText(LoginActivity.this, "User does not exist.",  // If user does not exist in database
                                                    Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(LoginActivity.this, "Invalid Password.",  // If user exists but incorrect password
                                                    Toast.LENGTH_SHORT).show();
                                    }
                                });
                                usernameFound = true;
                                break;
                            }
                        }

                        if (!usernameFound) {
                            Toast.makeText(LoginActivity.this, "User does not exist.",  // If user exists but incorrect password
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

//                Log.d("HELLO","TEST");
            }
        });
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
                        myRef.child("users").child(uid).child("pantry").setValue("[]");
                        myRef.child("usernames").child(uid).setValue(username);
                        myRef.child("emails").child(uid).setValue(email);

                        // Enjia - Stage 2 (Not working due to limitations - must pay)
                        // Generate a new FCM token for the user to send notifications
                        /*
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(fcmTask -> {
                                    if (fcmTask.isSuccessful()) {
                                        String fcmToken = fcmTask.getResult();
                                        Log.d("FCM_TOKEN", fcmToken);
                                        // Store the FCM token in your database
                                        myRef.child("users").child(uid).child("fcmToken").setValue(fcmToken)
                                                .addOnCompleteListener(databaseTask -> {
                                                    if (databaseTask.isSuccessful()) {
                                                        Log.d("FCM_TOKEN", "FCM token added to database");
                                                    } else {
                                                        Log.w("FCM_TOKEN", "Failed to add FCM token to database", databaseTask.getException());
                                                    }
                                                });
                                    } else {
                                        Log.w("FCM_TOKEN", "Failed to get FCM token", fcmTask.getException());
                                    }
                                });*/

                        viewAnimator.showNext();
                        ClearInputs();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email format.",
                            Toast.LENGTH_SHORT).show();
                    // Handle the error
                }
            });
        });
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
        startCooking.setOnClickListener(v -> {
            //                Update additional profile details
            //                Log.d("NEWLINE", String.valueOf(String.valueOf(profileDescriptionSignUp.getEditText().getText()).contains("\n")));
            myRef.child("users").child(uid).child("name").setValue(Objects.requireNonNull(nameSignUp.getEditText()).getText().toString());
            myRef.child("users").child(uid).child("date-of-birth").setValue(dob);

//                myRef.child("users").child(uid).child("profile-picture").setValue(profilePicture);
            myRef.child("users").child(uid).child("profile-description").setValue(String.valueOf(Objects.requireNonNull(profileDescriptionSignUp.getEditText()).getText()));

            if (profilePicture != null) {
                mStorageRef.child(mAuth.getCurrentUser().getUid()).putFile(Uri.parse(profilePicture)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        Toast.makeText(LoginActivity.this, "Unable to upload profile picture", Toast.LENGTH_SHORT).show();
                    }
                });
            }


            Intent logIn12 = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(logIn12);


        });
    }

    private void ClearInputs() {
        usernameEmailLogIn.getEditText().getText().clear();
        passwordLogIn.getEditText().getText().clear();
        usernameSignUp.getEditText().getText().clear();
        emailSignUp.getEditText().getText().clear();
        passwordSignUp.getEditText().getText().clear();
        confirmPasswordSignUp.getEditText().getText().clear();
        nameSignUp.getEditText().getText().clear();
        dobEditText.getText().clear();
        setProfilePicture.setImageURI(null);
        setProfilePictureText.setVisibility(View.VISIBLE);
        profileDescriptionSignUp.getEditText().getText().clear();
    }

}

