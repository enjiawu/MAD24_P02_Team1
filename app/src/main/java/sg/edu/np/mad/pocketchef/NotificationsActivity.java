package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import sg.edu.np.mad.pocketchef.Listener.NotificationListener;
import sg.edu.np.mad.pocketchef.Listener.PostClickListener;
import sg.edu.np.mad.pocketchef.Models.Notification;

// Enjia - Stage 2
public class NotificationsActivity extends AppCompatActivity {
    private final String TAG = "NotificationsActivity";

    //XML Values
    RecyclerView notifRecyclerView;
    ImageView backButton;
    TextView noNotifications;

    // Database
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference mUserRef;
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
        setUpListeners();

        setUpSwipeToDelete(); // Set up swipe to delete
    }

    private void findViews(){
        // Get the recycler view for the notifications
        notifRecyclerView = findViewById(R.id.notification_recycler_view);

        // Get the back button
        backButton = findViewById(R.id.backIv);

        // No notifications text view
        noNotifications = findViewById(R.id.noNotifications);

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

    // Function to load notifications for the user
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

    // Set up listeners for the back button to the main activity
    private void setUpListeners(){
        backButton.setOnClickListener(v -> {
            // Go to main activity
            Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        });
    }

    // Method to display notifications (implement as needed)
    private void displayNotifications(List<Notification> notifications) {
        if (notifications.isEmpty()){ // If there no notifications, show the no notifications text
            noNotifications.setVisibility(View.VISIBLE);
        }
        noNotifications.setVisibility(View.GONE); // Hide the no notifications text

        // Set up recycler view to display notifications
        NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationsActivity.this, notifications, notificationClickListener);

        notifRecyclerView.setAdapter(notificationAdapter);
        notifRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private final NotificationListener notificationClickListener = notifId -> {
        Log.d(TAG, notifId);

        // Split the notifId to get the postId
        String[] parts = notifId.split("_"); // Split it because the post key is formmatted at the back
        if (parts.length == 2) {
            String postId = parts[0]; // Get the post key which is the first element from the split
            Log.d(TAG, postId);

            // To see recipe details
            Intent postDetails = new Intent(NotificationsActivity.this, PostDetailsActivity.class)
                    .putExtra("id", postId);
            startActivity(postDetails);
        } else {
            Log.e(TAG, "Invalid notification ID format");
        }
    };


    // Function to delete notification by swiping
    private void setUpSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Not used, only interested in swipe
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                NotificationAdapter adapter = (NotificationAdapter) notifRecyclerView.getAdapter();
                if (adapter != null) {
                    if (position >= 0 && position < adapter.getItemCount()) {
                        adapter.removeItem(position);
                        if(adapter.getItemCount() == 0){
                            noNotifications.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e(TAG, "Invalid position on swipe: " + position);
                    }
                } else {
                    Log.e(TAG, "Adapter is null");
                }
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(notifRecyclerView);
    }
}