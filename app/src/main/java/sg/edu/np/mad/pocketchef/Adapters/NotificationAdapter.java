package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.NotificationListener;
import sg.edu.np.mad.pocketchef.Models.InstructionsResponse;
import sg.edu.np.mad.pocketchef.Models.Notification;
import sg.edu.np.mad.pocketchef.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationsViewHolder> {
    String TAG  = "NoficationsAdapter";
    Context context;
    List<Notification> notifications;
    NotificationListener notificationListener;


    public NotificationAdapter(Context context, List<Notification> notifications, NotificationListener notificationListener) {
        this.context = context;
        this.notifications = notifications;
        this.notificationListener = notificationListener;
    }
    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_community_notifications, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        Notification notification = notifications.get(position); // Get the notification from the list

        // Setting up values
        holder.title.setText(notification.getTitle());
        holder.date.setText(notification.formatDate());
        holder.description.setText(notification.getMessage());

        //Check if user clicks on post, if they click then return the Id
        holder.notificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(notifications.get(holder.getAdapterPosition()).getId()));
                notificationListener.onNotificationClicked(String.valueOf(notifications.get(holder.getAdapterPosition()).getId()));
            }
        });
    }

    // Function to delete the notification
    public void removeItem(int position) {
        if (position >= 0 && position < notifications.size()) {
            // Remove item from the list
            Notification notification = notifications.get(position);
            notifications.remove(position);
            notifyItemRemoved(position);

            // Update the database
            DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("notifications")
                    .child(notification.getId());

            notificationRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    Log.d("NotificationAdapter", "Notification removed from database");
                } else {
                    Log.e("NotificationAdapter", "Failed to remove notification from database");
                }
            });
        } else {
            Log.e("NotificationAdapter", "Invalid position: " + position);
        }
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }
}

class NotificationsViewHolder extends RecyclerView.ViewHolder {
    TextView title, date, description;
    CardView notificationCardView;

    public NotificationsViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.notification_title);
        date = itemView.findViewById(R.id.notification_date);
        description = itemView.findViewById(R.id.notification_description);
        notificationCardView = itemView.findViewById(R.id.notifications_container);
    }
}
