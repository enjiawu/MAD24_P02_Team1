package sg.edu.np.mad.pocketchef;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// Enjia - Stage 2
// Setting up push notification service for community posts
public class PushNotificationService extends FirebaseMessagingService {
    private DatabaseReference myRef;
    private String uid;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onNewToken(String token) { // Automatically refresh token when needed
        Log.d("FCM_TOKEN", "Refreshed token: " + token);

        // Store the new token in your database
        myRef.child("users").child(uid).child("fcmToken").setValue(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if(message.getNotification() != null){
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            NotificationHelper.showNotification(getApplicationContext(), title, body);
        }
    }
}
