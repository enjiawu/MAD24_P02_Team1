package sg.edu.np.mad.pocketchef;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Adapters.NotificationAdapter;
import sg.edu.np.mad.pocketchef.Models.Notification;

public class NotificationsActivity extends AppCompatActivity {
    private final String TAG = "NotificationsActivity";
    RecyclerView notifRecyclerView;
    // Database
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference postsRef, mUserRef, postRef;
    StorageReference storageReference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViews();
        //setUpListeners();
    }

    private void findViews(){
        // Get the recycler view for the notifications
        notifRecyclerView = findViewById(R.id.notification_recycler_view);

        //Firebase database setup
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("PostImages");
        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");

        // Get current user
        currentUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference("users");

        // Load all the notifications
        loadNotifications(currentUser.getUid());
    }

    private void loadNotifications(String userId) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("notifications");

        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Notification> notifications = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Notification notification = dataSnapshot.getValue(Notification.class);
                        if (notification != null) {
                            notifications.add(notification);
                        }
                    }
                    // Do something with the list of notifications
                    displayNotifications(notifications);
                } else {
                    Log.d(TAG, "No notifications found for user.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });
    }

    // Method to display notifications (implement as needed)
    private void displayNotifications(List<Notification> notifications) {
        if (notifications.isEmpty()){

        }
        NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationsActivity.this, notifications);

        notifRecyclerView.setAdapter(notificationAdapter);
        notifRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}